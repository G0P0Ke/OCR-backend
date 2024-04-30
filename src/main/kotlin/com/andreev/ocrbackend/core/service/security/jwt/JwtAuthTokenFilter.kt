package com.andreev.ocrbackend.core.service.security.jwt

import com.andreev.ocrbackend.core.service.security.UserDetailsServiceImpl
import mu.KLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthTokenFilter(
    val tokenProvider: JwtProvider,
    val userDetailService: UserDetailsServiceImpl
) : OncePerRequestFilter() {

    companion object : KLogging()

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain)  {
        try {
            val jwt = getJwt(request)
            if (tokenProvider.validateJwtToken(jwt)) {
                val username = tokenProvider.getUserNameFromJwtToken(jwt)
                val userDetails = userDetailService.loadUserByUsername(username)
                val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
                request.setAttribute("currentUser", username)
            } else {
                logger.warn("Can't validate JWT token")
            }
        } catch (e: Exception) {
            logger.error("Can NOT set user authentication", e)
        }

        filterChain.doFilter(request, response)
    }

    private fun getJwt(request: HttpServletRequest): String? {
        val cookies = request.cookies
        cookies?.forEach { cookie ->
            if (cookie.name == "accessToken") return cookie.value
        }
        return null
    }
}
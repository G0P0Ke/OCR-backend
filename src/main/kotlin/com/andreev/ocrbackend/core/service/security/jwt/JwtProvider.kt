package com.andreev.ocrbackend.core.service.security.jwt

import com.andreev.ocrbackend.core.service.domain.security.UserPrinciple
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import mu.KLogging
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.io.Serializable
import java.security.SignatureException
import java.time.Instant
import java.time.temporal.ChronoUnit.SECONDS
import java.util.Date

@Component
class JwtProvider(
    @Value("3600")
    private val jwtExpiration: Int
) {

    companion object : KLogging() {
        private val JWT_SIGN_SECRET : String = RandomStringUtils.random(32, true, true)
    }

    fun generateJwtToken(authentication: Authentication): String {
        val userPrincipal: UserPrinciple = authentication.principal as UserPrinciple
        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(jwtExpiration.toLong(), SECONDS)))
            .claim("currentUser", toJsonString(userPrincipal))
            .signWith(SignatureAlgorithm.HS512, JWT_SIGN_SECRET)
            .compact()
    }

    fun getUserNameFromJwtToken(token: String?): String {
        return Jwts.parser()
            .setSigningKey(JWT_SIGN_SECRET)
            .parseClaimsJws(token)
            .body
            .subject
    }

    fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(JWT_SIGN_SECRET).parseClaimsJws(authToken)
            return true
        } catch (e: SignatureException) {
            logger.warn("Invalid JWT signature -> Message: {} ", e.message)
        } catch (e: MalformedJwtException) {
            logger.warn("Invalid JWT token -> Message: {}", e.message)
        } catch (e: ExpiredJwtException) {
            logger.warn("Expired JWT token -> Message: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            logger.warn("Unsupported JWT token -> Message: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.warn("JWT claims string is empty -> Message: {}", e.message)
        }
        return false
    }

    private fun toJsonString(`object`: Serializable): String? {
        val writer = ObjectMapper().writer()
        return try {
            writer.writeValueAsString(`object`)
        } catch (e: JsonProcessingException) {
            throw IllegalStateException(String.format("Could not transform object '%s' to JSON: ", `object`), e)
        }
    }
}
package com.andreev.ocrbackend.core.service

import com.andreev.ocrbackend.core.model.User
import com.andreev.ocrbackend.core.repository.UserRepository
import com.andreev.ocrbackend.core.service.domain.security.JwtResponse
import com.andreev.ocrbackend.core.service.security.jwt.JwtProvider
import com.andreev.ocrbackend.dto.EntryDto
import mu.KLogging
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val jwtProvider: JwtProvider
) {
    companion object : KLogging() {
        private var passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
    }

    fun getUserByEmail(email: String) = userRepository.findUserByEmail(email)

    @Transactional
    fun register(email: String, password: String): User {
        logger.info { "Saving user with email: $email" }
        val user = User(email = email, password = passwordEncoder.encode(password))
        val result = userRepository.save(user)
        logger.info { "Saved user $result" }
        return result
    }

    fun login(entryDto: EntryDto): Pair<Boolean, JwtResponse> {
        val exists = existsUser(entryDto.email)
        if (exists) {
            val authToken = UsernamePasswordAuthenticationToken(entryDto.email, entryDto.password)
            val authentication: Authentication = authenticationManager.authenticate(authToken)
            SecurityContextHolder.getContext().authentication = authentication

            val jwtToken: String = jwtProvider.generateJwtToken(authentication)

            return true to JwtResponse(jwtToken)
        } else {
            return false to JwtResponse("")
        }
    }

    fun existsUser(email: String): Boolean = userRepository.existsByEmail(email)
}
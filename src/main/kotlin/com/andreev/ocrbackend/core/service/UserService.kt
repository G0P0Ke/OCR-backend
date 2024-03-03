package com.andreev.ocrbackend.core.service

import com.andreev.ocrbackend.core.model.User
import com.andreev.ocrbackend.core.repository.UserRepository
import mu.KLogging
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {
    companion object : KLogging() {
        private var passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
    }

    @Transactional
    fun register(email: String, password: String) : User {
        logger.info { "Saving user with email: $email" }
        val user = User(email = email, password = passwordEncoder.encode(password))
        val result = userRepository.save(user)
        logger.info { "Saved user $result" }
        return result
    }

    fun existsUser(email: String) : Boolean = userRepository.existsByEmail(email)
}
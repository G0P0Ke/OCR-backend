package com.andreev.ocrbackend.core.service

import com.andreev.ocrbackend.UserAlreadyExistsException
import com.andreev.ocrbackend.UserNotFoundException
import com.andreev.ocrbackend.core.model.User
import com.andreev.ocrbackend.core.model.security.RoleName
import com.andreev.ocrbackend.core.repository.RoleRepository
import com.andreev.ocrbackend.core.repository.UserRepository
import com.andreev.ocrbackend.core.service.domain.security.JwtResponse
import com.andreev.ocrbackend.core.service.domain.security.UserPrinciple
import com.andreev.ocrbackend.core.service.security.jwt.JwtProvider
import com.andreev.ocrbackend.dto.EntryDto
import com.andreev.ocrbackend.dto.UserUpdateRequest
import mu.KLogging
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val authenticationManager: AuthenticationManager,
    private val jwtProvider: JwtProvider
) {
    companion object : KLogging() {
        private var passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
    }

    fun getAllUsers(authentication: Authentication): MutableList<User> {
        val admin = authentication.principal as UserPrinciple
        logger.info { "Admin ${admin.username} get all users" }
        return userRepository.findAll()
    }

    @Transactional
    fun updateUser(userId: UUID, request: UserUpdateRequest): User {
        val user = findById(id = userId)
        logger.info { "Try to update $user" }
        with(request) {
            email?.let { user.email = email }
            name?.let { user.name = name }
            surname?.let { user.surname = surname }
            company?.let { user.company = company }
            password?.let { user.password = passwordEncoder.encode(password) }
        }
        val savedUser = userRepository.save(user)
        logger.info { "Successfully update user info: $savedUser" }
        return savedUser
    }

    @Transactional
    fun deleteUser(userId: UUID) {
        logger.info { "Try to delete user with id: $userId" }
        userRepository.deleteById(userId)
    }

    fun findUsersByCompany(company: String, authentication: Authentication): Collection<User> {
        val manager = authentication.principal as UserPrinciple
        return userRepository
            .findUsersByCompany(company)
            .filter { user -> user.email != manager.username }
    }

    fun getUserByEmail(email: String) = userRepository.findUserByEmail(email)

    fun findById(id: UUID): User {
        val user = userRepository.findById(id)
        if (user.isEmpty) {
            logger.error { "User with id: $id not found" }
            throw UserNotFoundException(id)
        }
        return user.get()
    }

    @Transactional
    fun register(entryDto: EntryDto, roleName: RoleName): User {
        val exists = existsUser(entryDto.email)
        val role = roleRepository.findByName(roleName)
        if (exists) {
            logger.info { "User ${entryDto.email} already exists" }
            throw UserAlreadyExistsException("User ${entryDto.email} already exists")
        }
        logger.info { "Saving user with email: ${entryDto.email}" }
        val user = User(
            email = entryDto.email,
            password = passwordEncoder.encode(entryDto.password),
            company = entryDto.company,
            name = entryDto.name,
            surname = entryDto.surname,
            role = role
        )
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
            logger.info { "User: ${entryDto.email} logged in" }
            val user = getUserByEmail(entryDto.email)

            return true to JwtResponse(
                accessToken = jwtToken,
                id = user.id,
                email = user.email,
                name = user.name,
                surname = user.surname,
                company = user.company,
                role = user.role?.name?.name
            )
        } else {
            logger.info { "User ${entryDto.email} doesn't exist" }
            return false to JwtResponse("")
        }
    }

    fun existsUser(email: String): Boolean = userRepository.existsByEmail(email)
}
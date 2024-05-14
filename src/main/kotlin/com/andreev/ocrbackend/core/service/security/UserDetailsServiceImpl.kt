package com.andreev.ocrbackend.core.service.security

import com.andreev.ocrbackend.core.model.User
import com.andreev.ocrbackend.core.model.security.RoleName
import com.andreev.ocrbackend.core.repository.UserRepository
import com.andreev.ocrbackend.core.service.domain.security.UserPrinciple
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        return try {
            val user: User = userRepository.findUserByEmail(email)
            if (user.role != null && user.role.name == RoleName.ROLE_ADMIN) {
                UserPrinciple.build(user, user.role.name)
            } else {
                UserPrinciple.build(user, RoleName.ROLE_USER)
            }
        } catch (e: EntityNotFoundException) {
            throw UsernameNotFoundException("User not found", e)
        }
    }
}
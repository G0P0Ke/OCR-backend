package com.andreev.ocrbackend.core.repository

import com.andreev.ocrbackend.core.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findUserByEmail(email: String): User

    fun existsByEmail(email: String): Boolean

    fun findUsersByCompany(company: String): Collection<User>
}
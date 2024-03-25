package com.andreev.ocrbackend.core.repository

import com.andreev.ocrbackend.core.model.security.Role
import com.andreev.ocrbackend.core.model.security.RoleName
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, Long> {

    fun findByName(name: RoleName): Role
}
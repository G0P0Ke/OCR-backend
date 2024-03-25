package com.andreev.ocrbackend.core.service

import com.andreev.ocrbackend.core.model.security.RoleName
import com.andreev.ocrbackend.core.repository.RoleRepository
import org.springframework.stereotype.Service

@Service
class RoleService(
    private val roleRepository: RoleRepository
) {

    fun findRoleByName(name: RoleName) = roleRepository.findByName(name)
}
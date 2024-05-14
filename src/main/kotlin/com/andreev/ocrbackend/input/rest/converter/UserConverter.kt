package com.andreev.ocrbackend.input.rest.converter

import com.andreev.ocrbackend.core.model.User
import com.andreev.ocrbackend.core.model.security.Role
import com.andreev.ocrbackend.dto.UserResponse
import org.springframework.stereotype.Service

@Service
class UserConverter {

    fun toResponse(user: User) = UserResponse(
        id = user.id,
        email = user.email,
        name = user.name,
        surname = user.surname,
        company = user.company,
        role = user.role?.name?.name
    )

    fun toResponseWithRole(user: User, role: Role) = UserResponse(
        id = user.id,
        email = user.email,
        name = user.name,
        surname = user.surname,
        role = role.name.name,
        company = user.company
    )

    fun collectionToResponse(collection: Collection<User>) = collection.map { user -> toResponse(user) }

}
package com.andreev.ocrbackend.input.rest.converter

import com.andreev.ocrbackend.core.model.User
import com.andreev.ocrbackend.dto.UserResponse
import org.springframework.stereotype.Service

@Service
class UserConverter {

    fun toResponse(user: User) = UserResponse(
        id = user.id,
        email = user.email,
        name = user.name,
        surname = user.surname,
    )
}
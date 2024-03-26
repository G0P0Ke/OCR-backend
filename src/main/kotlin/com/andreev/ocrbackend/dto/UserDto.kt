package com.andreev.ocrbackend.dto

import java.util.UUID

data class UserResponse(
    val id: UUID,
    val email: String,
    val name: String?,
    val surname: String?,
    val role: String? = null
)
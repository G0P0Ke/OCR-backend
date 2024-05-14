package com.andreev.ocrbackend.dto

import java.util.UUID
import javax.validation.constraints.Size

data class UserResponse(
    val id: UUID,
    val email: String,
    val name: String?,
    val surname: String?,
    val role: String? = null,
    val company: String? = null
)

data class UserUpdateRequest(
    val email: String?,
    val name: String?,
    val surname: String?,
    @field:[Size(max = 255)]
    val company: String?,
    val password: String?,
)
package com.andreev.ocrbackend.core.service.domain.security

import java.util.UUID

data class JwtResponse(
    val accessToken: String,
    val type: String = "Bearer",
    val id: UUID? = null,
    val email: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val company: String? = null
)

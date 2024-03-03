package com.andreev.ocrbackend.core.service.domain.security

data class JwtResponse(
    val accessToken: String,
    val type: String = "Bearer",
)

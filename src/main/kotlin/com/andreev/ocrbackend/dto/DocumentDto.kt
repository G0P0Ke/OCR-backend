package com.andreev.ocrbackend.dto

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDateTime
import java.util.UUID

data class DocumentCreateRequest(
    val urlPath: String,
)

data class DocumentUpdateRequest(
    val labels: JsonNode?,
    val isLabeled: Boolean?,
    val assessor: UUID?,
)

data class DocumentResponse(
    val id: UUID,
    val isLearnt: Boolean?,
    val isValid: Boolean?,
    val isLabeled: Boolean,
    val type: String?,
    val urlPath: String?,
    val createdAt: LocalDateTime,
    val assessors: List<UserResponse>
)
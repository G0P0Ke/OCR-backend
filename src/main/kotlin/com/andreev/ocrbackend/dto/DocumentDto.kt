package com.andreev.ocrbackend.dto

import com.andreev.ocrbackend.input.rest.validator.EnumValue
import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDateTime
import java.util.UUID

data class DocumentCreateRequest(
    val urlPath: String,
)

data class DocumentUploadRequest(
    @field:EnumValue(DocumentType::class)
    val type: String
)

enum class DocumentType {
    TEMPLATE, // документ с разметкой менеджером
    DATASET, // документы для обучения модели
    FREE, // документы, добавляемые в проект после обучения модели
}

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
    val labels: JsonNode?,
    val type: String?,
    val urlPath: String?,
    val createdAt: LocalDateTime,
    val assessors: List<UserResponse>
)
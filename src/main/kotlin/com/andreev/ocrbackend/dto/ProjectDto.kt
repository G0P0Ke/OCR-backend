package com.andreev.ocrbackend.dto

import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime
import java.util.UUID
import javax.validation.constraints.Size

data class CreateProjectRequest(
    @field:[NotNull Size(min = 1, max = 255)]
    val name: String
)

data class ProjectResponse(
    val id: UUID,
    val name: String,
    val createdAt: LocalDateTime
)
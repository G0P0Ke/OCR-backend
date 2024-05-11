package com.andreev.ocrbackend.dto

import com.andreev.ocrbackend.core.model.security.RoleName
import com.andreev.ocrbackend.input.rest.validator.EnumValue
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime
import java.util.UUID
import javax.validation.constraints.Size

data class CreateProjectRequest(
    @field:[NotNull Size(min = 1, max = 255)]
    val name: String,
    @field:[NotNull Size(min = 1, max = 255)]
    val description: String,
    val participants: Set<ParticipantAdd>?
)

data class UpdateProjectRequest(
    @field:[Size(max = 255)]
    val name: String?,
    @field:[Size(min = 1, max = 255)]
    val description: String?,
    val participants: Set<ParticipantAdd>?,
)

data class ParticipantAdd(
    val userId: UUID,
    @field:EnumValue(ProjectRole::class)
    val role: String,
) {
    enum class ProjectRole {
        ROLE_MODERATOR,
        ROLE_MANAGER, // users with this role can add assessors
        ROLE_ASSESSOR // users with this role can start labeling
    }
}

data class ProjectResponse(
    val id: UUID,
    val name: String,
    val description: String,
    val createdAt: LocalDateTime,
    val documents: List<DocumentResponse>,
    val participants: List<UserResponse>,
    val modelId: UUID? = null
)

data class ProjectResponseWithoutDocuments(
    val userRole: RoleName,
    val id: UUID,
    val name: String,
    val description: String,
    val totalDocuments: Long,
    val labeledDocuments: Long,
    val datasetDocuments: Long,
    val isTemplateLabeled: Boolean,
    val createdAt: LocalDateTime,
    val previewURL: String?
)
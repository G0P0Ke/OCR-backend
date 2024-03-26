package com.andreev.ocrbackend.dto

import com.andreev.ocrbackend.input.rest.validator.EnumValue
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime
import java.util.UUID
import javax.validation.constraints.Size

data class CreateProjectRequest(
    @field:[NotNull Size(min = 1, max = 255)]
    val name: String
)

data class UpdateProjectRequest(
    @field:[Size(max = 255)]
    val name: String?,
    val participants: Set<ParticipantAdd>?,
    val documents: Set<DocumentCreateRequest>?
) {
    data class ParticipantAdd(
        val userId: UUID,
        @field:EnumValue(ProjectRole::class)
        val role: String,
    ) {
        enum class ProjectRole {
            ROLE_MODERATOR, ROLE_MANAGER, ROLE_ASSESSOR
        }
    }
}

data class ProjectResponse(
    val id: UUID,
    val name: String,
    val createdAt: LocalDateTime,
    val document: List<DocumentResponse>,
    val participants: List<UserResponse>
)
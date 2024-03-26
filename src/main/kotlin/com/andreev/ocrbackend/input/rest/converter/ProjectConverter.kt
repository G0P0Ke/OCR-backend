package com.andreev.ocrbackend.input.rest.converter

import com.andreev.ocrbackend.core.model.Project
import com.andreev.ocrbackend.dto.ProjectResponse
import org.springframework.stereotype.Component

@Component
class ProjectConverter(
    private val documentConverter: DocumentConverter,
    private val userConverter: UserConverter,
) {

    fun toResponse(project: Project) = with(project) {
        ProjectResponse(
            id = id,
            name = name,
            createdAt = createdAt,
            document = documents?.map { document ->
                documentConverter.toResponse(document)
            } ?: emptyList(),
            participants = project.participants?.map { userProjectAgent ->
                userConverter.toResponseWithRole(
                    user = userProjectAgent.user,
                    role = userProjectAgent.role
                )
            } ?: emptyList()
        )
    }
}
package com.andreev.ocrbackend.input.rest.converter

import com.andreev.ocrbackend.core.model.Project
import com.andreev.ocrbackend.core.model.security.RoleName
import com.andreev.ocrbackend.dto.ProjectResponse
import com.andreev.ocrbackend.dto.ProjectResponseWithoutDocuments
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
            description = project.description,
            documents = documents?.map { document ->
                documentConverter.toResponse(document)
            } ?: emptyList(),
            participants = project.participants?.map { userProjectAgent ->
                userConverter.toResponseWithRole(
                    user = userProjectAgent.user,
                    role = userProjectAgent.role
                )
            } ?: emptyList(),
            modelId = project.model?.id
        )
    }

    fun roleWithProjectResponse(triple: List<Triple<RoleName, Project, String?>>) = triple.map { projectByUserRole ->
        ProjectResponseWithoutDocuments(
            userRole = projectByUserRole.first,
            id = projectByUserRole.second.id,
            name = projectByUserRole.second.name,
            description = projectByUserRole.second.description,
            createdAt = projectByUserRole.second.createdAt,
            previewURL = projectByUserRole.third
        )
    }
}
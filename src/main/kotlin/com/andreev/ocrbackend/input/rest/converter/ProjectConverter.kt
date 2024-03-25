package com.andreev.ocrbackend.input.rest.converter

import com.andreev.ocrbackend.core.model.Project
import com.andreev.ocrbackend.dto.ProjectResponse
import org.springframework.stereotype.Component

@Component
class ProjectConverter {

    fun projectToResponse(project: Project) = with(project) {
        ProjectResponse(
            id = id,
            name = name,
            createdAt = createdAt
        )
    }
}
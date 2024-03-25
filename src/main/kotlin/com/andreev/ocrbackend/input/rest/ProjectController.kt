package com.andreev.ocrbackend.input.rest

import com.andreev.ocrbackend.core.service.ProjectService
import com.andreev.ocrbackend.dto.CreateProjectRequest
import com.andreev.ocrbackend.dto.ProjectResponse
import com.andreev.ocrbackend.input.rest.converter.ProjectConverter
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1")
class ProjectController(
    private val projectService: ProjectService,
    private val projectConverter: ProjectConverter
) {

    @PostMapping("/project", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createProject(
        @RequestBody @Valid request: CreateProjectRequest,
        authentication: Authentication,
    ): ResponseEntity<ProjectResponse> {
        val result = projectService.createProject(request, authentication)
        return ResponseEntity.ok(projectConverter.projectToResponse(result))
    }
}
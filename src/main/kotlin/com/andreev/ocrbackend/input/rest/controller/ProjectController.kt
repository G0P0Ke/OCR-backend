package com.andreev.ocrbackend.input.rest.controller

import com.andreev.ocrbackend.core.service.ProjectService
import com.andreev.ocrbackend.dto.CreateProjectRequest
import com.andreev.ocrbackend.dto.ProjectResponse
import com.andreev.ocrbackend.dto.UpdateProjectRequest
import com.andreev.ocrbackend.input.rest.converter.ProjectConverter
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/v1/project")
class ProjectController(
    private val projectService: ProjectService,
    private val projectConverter: ProjectConverter
) {

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createProject(
        authentication: Authentication,
        @RequestBody @Valid request: CreateProjectRequest,
    ): ResponseEntity<ProjectResponse> {
        val result = projectService.createProject(authentication, request)
        return ResponseEntity.ok(projectConverter.toResponse(result))
    }

    @PatchMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProject(
        @PathVariable id: UUID,
        @RequestBody @Valid request: UpdateProjectRequest,
    ): ResponseEntity<ProjectResponse> {
        val result = projectService.updateProject(id, request)
        return ResponseEntity.ok(projectConverter.toResponse(result))
    }

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getProject(
        @PathVariable id: UUID
    ): ResponseEntity<ProjectResponse> {
        val result = projectService.findById(id)
        return ResponseEntity.ok(projectConverter.toResponse(result))
    }

    @PostMapping("/{id}:execute", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun executeProject(
        @PathVariable id: UUID
    ): ResponseEntity<String> {
        projectService.executeProject(id)
        return ResponseEntity.ok("Success. Project with id: $id was sent to execute")
    }
}
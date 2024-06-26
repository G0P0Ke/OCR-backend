package com.andreev.ocrbackend.input.rest.controller

import com.andreev.ocrbackend.core.service.ProjectService
import com.andreev.ocrbackend.dto.CreateProjectRequest
import com.andreev.ocrbackend.dto.DocumentUploadRequest
import com.andreev.ocrbackend.dto.ProjectResponse
import com.andreev.ocrbackend.dto.ProjectResponseWithoutDocuments
import com.andreev.ocrbackend.dto.UpdateProjectRequest
import com.andreev.ocrbackend.input.rest.converter.ProjectConverter
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/v1/project")
class ProjectController(
    private val projectService: ProjectService,
    private val projectConverter: ProjectConverter
) {

    @GetMapping("/{id}/markup", produces = ["text/csv"])
    @Operation(summary = "Получение размеченных документов в формате csv",)
    fun getDocumentsWithLabels(@PathVariable id: UUID): ResponseEntity<String> {
        val csvData = projectService.getMarkupDocuments(projectId = id)
        val headers = HttpHeaders().apply {
            contentType = MediaType.parseMediaType("text/csv")
            add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=markupDocuments.csv")
        }
        return ResponseEntity(csvData, headers, HttpStatus.OK)
    }

    @GetMapping("")
    @Operation(
        summary = "Получение всех проектов пользователя по userId",
    )
    fun getByCompany(
        @RequestParam(name = "userId", required = true) userId: UUID
    ): ResponseEntity<List<ProjectResponseWithoutDocuments>> {
        val projectList = projectService.getProjectsByUserId(userId)
        return ResponseEntity.ok(projectList)
    }

    @PostMapping("", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Создание нового проекта",
        description = "Добавление проекта: (name, description) и список assessor'ov"
    )
    fun createProject(
        authentication: Authentication,
        @RequestBody @Valid request: CreateProjectRequest,
    ): ResponseEntity<ProjectResponse> {
        val result = projectService.createProject(authentication, request)
        return ResponseEntity.ok(projectConverter.toResponse(result))
    }

    @PatchMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Обновление проекта по id",
        description = "Добавление assessor'ov, документов, изменение имени"
    )
    fun updateProject(
        @PathVariable id: UUID,
        @RequestBody @Valid request: UpdateProjectRequest,
        authentication: Authentication
    ): ResponseEntity<ProjectResponse> {
        val result = projectService.updateProject(id, request, authentication)
        return ResponseEntity.ok(projectConverter.toResponse(result))
    }

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Получение проекта по id",
    )
    fun getProject(
        @PathVariable id: UUID
    ): ResponseEntity<ProjectResponse> {
        val result = projectService.findById(id)
        return ResponseEntity.ok(projectConverter.toResponse(result))
    }

    @PostMapping("/{id}:execute", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Отправка команды EXECUTE по id проекта",
        description = "Отправка всего проекта на обучение в модуль машинного обучения"
    )
    fun executeProject(
        @PathVariable id: UUID
    ): ResponseEntity<String> {
        projectService.executeProject(id)
        return ResponseEntity.ok("Success. Project with id: $id was sent to execute")
    }

    @PostMapping("/{id}:upload-documents")
    fun uploadDocuments(
        @PathVariable id: UUID,
        @RequestPart("documents") documents: List<MultipartFile>,
        @RequestPart("request") @Valid request: DocumentUploadRequest
    ): ResponseEntity<String> {
        projectService.uploadDocuments(id = id, documents = documents, request = request)
        return ResponseEntity.ok("Success. Added documents to Project with id: $id")
    }

    @DeleteMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Удаление проекта по id",
    )
    fun deleteDocument(
        @PathVariable id: UUID
    ): ResponseEntity<String> {
        projectService.deleteProject(id)
        return ResponseEntity.ok("Successfully delete project with id: $id")
    }
}
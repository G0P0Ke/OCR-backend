package com.andreev.ocrbackend.input.rest.controller

import com.andreev.ocrbackend.core.service.DocumentService
import com.andreev.ocrbackend.core.service.JsonValidationService
import com.andreev.ocrbackend.dto.DocumentResponse
import com.andreev.ocrbackend.dto.DocumentUpdateRequest
import com.andreev.ocrbackend.input.rest.converter.DocumentConverter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid

@RestController
@RequestMapping("/v1/document")
class DocumentController(
    private val documentService: DocumentService,
    private val jsonValidationService: JsonValidationService,
    private val documentConverter: DocumentConverter,
) {

    companion object {
        private const val VALIDATION_SCHEMA_LABELS = "patch-document-labels-schema.json"
        private val objectMapper: ObjectMapper = jacksonObjectMapper()
    }

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Получение документа по id"
    )
    fun getDocument(
        @PathVariable id: UUID
    ): ResponseEntity<DocumentResponse> {
        val result = documentService.getDocumentById(id)
        return ResponseEntity.ok(documentConverter.toResponse(document = result))
    }

    @PatchMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Обновление документа по id",
        description = "Отправка разметки по документу и информации о assessor'e"
    )
    fun updateDocument(
        @PathVariable id: UUID,
        @RequestBody @Valid request: JsonNode
    ): ResponseEntity<DocumentResponse> {
        jsonValidationService.validate(jsonNode = request, schemaPath = VALIDATION_SCHEMA_LABELS)
        val documentUpdateRequest = objectMapper.convertValue<DocumentUpdateRequest>(request)
        val result = documentService.updateDocument(id = id, request = documentUpdateRequest)
        return ResponseEntity.ok(documentConverter.toResponse(document = result))
    }

    
    @DeleteMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Удаление документа по id",
    )
    fun deleteDocument(
        @PathVariable id: UUID
    ): ResponseEntity<String> {
        documentService.deleteDocument(id)
        return ResponseEntity.ok("Successfully deleted document with id: $id")
    }
}
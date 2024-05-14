package com.andreev.ocrbackend.input.rest.converter

import com.andreev.ocrbackend.core.model.Document
import com.andreev.ocrbackend.dto.DocumentResponse
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component

@Component
class DocumentConverter(
    private val userConverter: UserConverter
) {

    fun toResponse(document: Document) = DocumentResponse(
        id = document.id,
        projectId = document.project.id,
        isLearnt = document.isLearnt,
        isValid = document.isValid,
        isLabeled = document.isLabeled,
        labels = document.labels,
        type = document.type,
        urlPath = document.urlPath,
        createdAt = document.createdAt,
        assessors = document.assessors?.map { userDocumentAgent ->
            userConverter.toResponse(userDocumentAgent.user)
        } ?: emptyList()
    )

    fun toResponseForLabeling(document: Document, templateLabels: JsonNode?) = DocumentResponse(
        id = document.id,
        projectId = document.project.id,
        isLearnt = document.isLearnt,
        isValid = document.isValid,
        isLabeled = document.isLabeled,
        labels = document.labels,
        templateLabels = templateLabels,
        type = document.type,
        urlPath = document.urlPath,
        createdAt = document.createdAt,
        assessors = document.assessors?.map { userDocumentAgent ->
            userConverter.toResponse(userDocumentAgent.user)
        } ?: emptyList()
    )
}
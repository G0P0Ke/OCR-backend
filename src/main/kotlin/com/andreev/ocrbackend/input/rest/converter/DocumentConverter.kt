package com.andreev.ocrbackend.input.rest.converter

import com.andreev.ocrbackend.core.model.Document
import com.andreev.ocrbackend.dto.DocumentResponse
import org.springframework.stereotype.Component

@Component
class DocumentConverter(
    private val userConverter: UserConverter
) {

    fun toResponse(document: Document) = DocumentResponse(
        id = document.id,
        isLearnt = document.isLearnt,
        isValid = document.isValid,
        isLabeled = document.isLabeled,
        type = document.type,
        urlPath = document.urlPath,
        createdAt = document.createdAt,
        assessors = document.assessors?.map { userDocumentAgent ->
            userConverter.toResponse(userDocumentAgent.user)
        } ?: emptyList()
    )
}
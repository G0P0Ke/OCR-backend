package com.andreev.ocrbackend

import java.util.UUID
import javax.persistence.EntityNotFoundException

class ProjectNotFoundException(
    private val projectId: UUID,
    override val cause: Throwable? = null,
) : EntityNotFoundException() {
    override val message: String
        get() = "Project with id $projectId not found"
}

class UserNotFoundException(
    private val userId: UUID,
    override val cause: Throwable? = null,
) : EntityNotFoundException() {
    override val message: String
        get() = "User with id $userId not found "
}

class ModelNotFoundException(
    private val modelId: UUID,
    override val cause: Throwable? = null,
) : EntityNotFoundException() {
    override val message: String
        get() = "Model with id $modelId not found"
}

class DocumentNotFoundException(
    private val documentId: UUID,
    override val cause: Throwable? = null,
) : EntityNotFoundException() {
    override val message: String
        get() = "Document with id $documentId not found"
}

class UserAlreadyExistsException(
    override val message: String?
) : RuntimeException()

class JsonValidationException(errorMessages: List<String>) :
    RuntimeException("JSON validation failed: Errors: ${errorMessages.joinToString()}")
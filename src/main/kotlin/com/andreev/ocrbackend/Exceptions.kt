package com.andreev.ocrbackend

import javax.persistence.EntityNotFoundException

class ProjectNotFoundException(
    private val projectId: String,
    override val cause: Throwable? = null,
) : EntityNotFoundException() {
    override val message: String
        get() = "Project with id $projectId not found"
}

class UserNotFoundException(
    private val userId: String,
    override val cause: Throwable? = null,
) : EntityNotFoundException() {
    override val message: String
        get() = "User with id $userId not found "
}

class ModelNotFoundException(
    private val modelId: String,
    override val cause: Throwable? = null,
) : EntityNotFoundException() {
    override val message: String
        get() = "Model with id $modelId not found"
}

class UserAlreadyExistsException(
    override val message: String?
) : RuntimeException()
package com.andreev.ocrbackend.core.repository

import com.andreev.ocrbackend.core.model.Document
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DocumentRepository : JpaRepository<Document, UUID>
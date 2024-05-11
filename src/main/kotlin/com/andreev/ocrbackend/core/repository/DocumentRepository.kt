package com.andreev.ocrbackend.core.repository

import com.andreev.ocrbackend.core.model.Document
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface DocumentRepository : JpaRepository<Document, UUID> {

    @Query(
        value = """
        SELECT 
            COUNT(*) AS total_documents,
            COUNT(CASE is_labeled WHEN true THEN 1 END) AS labeled_documents,
            COUNT(CASE type WHEN 'FREE' THEN 1 END) AS free_documents
        FROM document
        WHERE id_project = :projectId
    """, nativeQuery = true
    )
    fun countDocumentsAndLabeledDocuments(@Param("projectId") projectId: UUID): Map<String, Any>
}
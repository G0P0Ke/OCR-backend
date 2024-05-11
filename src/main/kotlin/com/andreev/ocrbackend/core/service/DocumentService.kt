package com.andreev.ocrbackend.core.service

import com.andreev.ocrbackend.DocumentNotFoundException
import com.andreev.ocrbackend.core.model.Document
import com.andreev.ocrbackend.core.model.Project
import com.andreev.ocrbackend.core.repository.DocumentRepository
import com.andreev.ocrbackend.dto.DocumentCreateRequest
import com.andreev.ocrbackend.dto.DocumentStatisticDto
import com.andreev.ocrbackend.dto.DocumentUpdateRequest
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.UUID
import javax.transaction.Transactional

@Service
class DocumentService(
    private val documentRepository: DocumentRepository,
    private val userDocumentAgentService: UserDocumentAgentService,
    private val userService: UserService,
) {

    companion object : KLogging()

    fun analyticsOfDocsInProject(projectId: UUID): DocumentStatisticDto {
        val result = documentRepository.countDocumentsAndLabeledDocuments(projectId)
        return DocumentStatisticDto(
            totalDocuments = result["total_documents"].toString().toLong(),
            labeledDocuments = result["labeled_documents"].toString().toLong()
        )
    }

    fun getDocumentById(id: UUID): Document {
        val document = documentRepository.findById(id)
        if (document.isEmpty) {
            logger.error { "Document with id: $id not found" }
            throw DocumentNotFoundException(id)
        }
        return document.get()
    }

    @Transactional
    fun createDocument(
        project: Project,
        request: DocumentCreateRequest,
        type: String
    ): Document {
        val document = Document(
            urlPath = request.urlPath,
            project = project,
            type = type
        )
        val savedDocument = documentRepository.save(document)
        logger.info { "Successfully created $savedDocument" }
        return savedDocument
    }

    @Transactional
    fun updateDocument(id: UUID, request: DocumentUpdateRequest): Document {
        val document = getDocumentById(id)
        with(request) {
            labels?.let {
                document.labels = it
            }
            isLabeled?.let {
                document.isLabeled = isLabeled
            }
            assessor?.let { userAssessorId ->
                val user = userService.findById(userAssessorId)
                userDocumentAgentService.createUserDocumentAgent(
                    user = user,
                    document = document
                )
            }
        }

        val savedDocument = documentRepository.save(document)
        logger.info { "Successfully updated $savedDocument" }
        return savedDocument
    }

    @Transactional
    fun deleteDocument(id: UUID) {
        val exist = documentRepository.existsById(id)
        if (!exist) {
            logger.error { "Document with id: $id not found" }
            throw DocumentNotFoundException(id)
        }
        documentRepository.deleteById(id)
        logger.info { "Successfully deleted document with id: $id" }
    }
}
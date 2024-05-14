package com.andreev.ocrbackend.core.service

import com.andreev.ocrbackend.DocumentNotFoundException
import com.andreev.ocrbackend.LabelingPermissionException
import com.andreev.ocrbackend.core.model.Document
import com.andreev.ocrbackend.core.model.Project
import com.andreev.ocrbackend.core.repository.DocumentRepository
import com.andreev.ocrbackend.core.service.domain.security.UserPrinciple
import com.andreev.ocrbackend.dto.DocumentCreateRequest
import com.andreev.ocrbackend.dto.DocumentStatisticDto
import com.andreev.ocrbackend.dto.DocumentType
import com.andreev.ocrbackend.dto.DocumentUpdateRequest
import com.fasterxml.jackson.databind.JsonNode
import mu.KLogging
import org.springframework.security.core.Authentication
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

    fun getDocumentsWithLabelsAsCsv(projectId: UUID): String {
        logger.info { "Get documents with labels != null and type != FREE" }
        val documents = documentRepository.findDocumentsWithLabels(projectId)
        return documents.joinToString(separator = "\n") { document ->
            "${document.id},${document.labels},${document.isLearnt},${document.isValid},${document.isLabeled},${document.type},${document.urlPath},${document.createdAt}"
        }
    }

    fun analyticsOfDocsInProject(projectId: UUID): DocumentStatisticDto {
        val result = documentRepository.countDocumentsAndLabeledDocuments(projectId)
        val freeDocuments = result["free_documents"].toString().toLong()
        val totalDocuments = result["total_documents"].toString().toLong()
        return DocumentStatisticDto(
            totalDocuments = totalDocuments,
            labeledDocuments = result["labeled_documents"].toString().toLong(),
            freeDocuments = freeDocuments,
            datasetDocuments = totalDocuments - freeDocuments
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

    fun getDocumentForLabeling(id: UUID): Pair<Document, JsonNode?> {
        val document = getDocumentById(id)
        val documentCollection = document.project.documents

        val templateDocLabels = if (documentCollection.isNullOrEmpty()) {
            null
        } else {
            documentCollection.firstOrNull { it.type == DocumentType.TEMPLATE.name }?.labels
        }

        return document to templateDocLabels
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
    fun updateDocument(
        id: UUID,
        request: DocumentUpdateRequest,
        authentication: Authentication
    ): Document {
        val document = getDocumentById(id)
        with(request) {
            labels?.let {
                isAllowedLabeling(document, authentication)
                document.labels = it
            }
            isLabeled?.let {
                document.isLabeled = isLabeled
            }
            isValid?.let {
                document.isValid = isValid
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

    fun isAllowedLabeling(document: Document, authentication: Authentication) : Boolean {
        val assessorsEmails = document.assessors?.map { it.user.email }
        if (assessorsEmails.isNullOrEmpty()) {
            return true
        }
        val assessor = authentication.principal as UserPrinciple
        if (assessor.username !in assessorsEmails) {
            logger.info { "Document was already labeled by other users: ${assessorsEmails.joinToString()}" }
            throw LabelingPermissionException("Document was already labeled by ${assessorsEmails.first()}")
        }

        return true
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
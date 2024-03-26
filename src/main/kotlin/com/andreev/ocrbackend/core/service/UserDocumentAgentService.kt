package com.andreev.ocrbackend.core.service

import com.andreev.ocrbackend.core.model.Document
import com.andreev.ocrbackend.core.model.Project
import com.andreev.ocrbackend.core.model.User
import com.andreev.ocrbackend.core.model.UserDocumentAgent
import com.andreev.ocrbackend.core.model.UserDocumentAgentId
import com.andreev.ocrbackend.core.model.UserProjectAgent
import com.andreev.ocrbackend.core.model.UserProjectAgentId
import com.andreev.ocrbackend.core.model.security.Role
import com.andreev.ocrbackend.core.model.security.RoleName
import com.andreev.ocrbackend.core.repository.UserDocumentAgentRepository
import mu.KLogging
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserDocumentAgentService(
    private val userDocumentAgentRepository: UserDocumentAgentRepository
) {

    companion object : KLogging()

    @Transactional
    fun createUserDocumentAgent(
        user: User,
        document: Document
    ) {
        val userDocumentAgent = UserDocumentAgent(
            id = UserDocumentAgentId(idUser = user.id, document.id),
            user = user,
            document = document
        )
        userDocumentAgentRepository.save(userDocumentAgent)
        logger.info { "Successfully added $user to $document as ${RoleName.ROLE_ASSESSOR}" }
    }
}
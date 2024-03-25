package com.andreev.ocrbackend.core.service

import com.andreev.ocrbackend.core.model.Project
import com.andreev.ocrbackend.core.model.User
import com.andreev.ocrbackend.core.model.UserProjectAgent
import com.andreev.ocrbackend.core.model.UserProjectAgentId
import com.andreev.ocrbackend.core.model.security.Role
import com.andreev.ocrbackend.core.repository.UserProjectAgentRepository
import mu.KLogging
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserProjectAgentService(
    private val userProjectAgentRepository: UserProjectAgentRepository
) {

    companion object : KLogging()

    @Transactional
    fun createUserProjectAgent(
        user: User,
        project: Project,
        role: Role
    ) {
        val userProjectAgent = UserProjectAgent(
            id = UserProjectAgentId(idUser = user.id, idProject = project.id, idRole = role.id),
            user = user,
            project = project,
            role = role
        )
        userProjectAgentRepository.save(userProjectAgent)
        logger.info { "Successfully added $user to $project with ${role.name.name}" }
    }
}
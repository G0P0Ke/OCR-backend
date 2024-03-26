package com.andreev.ocrbackend.core.service

import com.andreev.ocrbackend.ProjectNotFoundException
import com.andreev.ocrbackend.core.model.Project
import com.andreev.ocrbackend.core.model.security.RoleName
import com.andreev.ocrbackend.core.repository.ProjectRepository
import com.andreev.ocrbackend.core.service.domain.security.UserPrinciple
import com.andreev.ocrbackend.dto.CreateProjectRequest
import com.andreev.ocrbackend.dto.UpdateProjectRequest
import mu.KLogging
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.UUID
import javax.transaction.Transactional

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val userProjectAgentService: UserProjectAgentService,
    private val userService: UserService,
    private val roleService: RoleService,
    private val modelService: ModelService,
    private val documentService: DocumentService,
) {

    companion object : KLogging()

    fun findById(id: UUID): Project {
        val project = projectRepository.findById(id)
        if (project.isEmpty) {
            logger.error { "Project with id: $id not found" }
            throw ProjectNotFoundException(id)
        }
        return project.get()
    }

    @Transactional
    fun createProject(authentication: Authentication, request: CreateProjectRequest): Project {
        logger.info { "Try to create project with name: ${request.name}" }
        val creator = authentication.principal as UserPrinciple
        val user = userService.getUserByEmail(creator.username)

        val project = Project(name = request.name)
        val savedProject = projectRepository.save(project)
        modelService.createModel(savedProject)

        val role = roleService.findRoleByName(RoleName.ROLE_MANAGER)

        userProjectAgentService.createUserProjectAgent(
            user = user,
            project = project,
            role = role
        )

        logger.info { "Successfully created $savedProject" }

        return savedProject
    }

    @Transactional
    fun updateProject(id: UUID, request: UpdateProjectRequest): Project {
        val project = findById(id)
        with(request) {
            name?.let { project.name = name }
            participants?.map { userAdd ->
                val user = userService.findById(userAdd.userId)
                val role = roleService.findRoleByName(RoleName.valueOf(userAdd.role))
                userProjectAgentService.createUserProjectAgent(
                    user = user,
                    project = project,
                    role = role
                )
            }
            documents?.map { documentCreate ->
                documentService.createDocument(
                    project = project,
                    request = documentCreate
                )
            }
        }

        val savedProject = projectRepository.save(project)
        logger.info { "Successfully updated $savedProject" }
        return savedProject
    }
}
package com.andreev.ocrbackend.core.service

import com.andreev.ocrbackend.core.model.Project
import com.andreev.ocrbackend.core.model.security.RoleName
import com.andreev.ocrbackend.core.repository.ProjectRepository
import com.andreev.ocrbackend.core.service.domain.security.UserPrinciple
import com.andreev.ocrbackend.dto.CreateProjectRequest
import mu.KLogging
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val userProjectAgentService: UserProjectAgentService,
    private val userService: UserService,
    private val roleService: RoleService,
    private val modelService: ModelService,
) {

    companion object : KLogging()

    @Transactional
    fun createProject(request: CreateProjectRequest, authentication: Authentication): Project {
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
}
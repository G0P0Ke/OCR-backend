package com.andreev.ocrbackend.core.service

import com.andreev.ocrbackend.ProjectNotFoundException
import com.andreev.ocrbackend.Rabbit
import com.andreev.ocrbackend.core.enums.Command
import com.andreev.ocrbackend.core.model.Model
import com.andreev.ocrbackend.core.model.Project
import com.andreev.ocrbackend.core.model.security.RoleName
import com.andreev.ocrbackend.core.repository.ProjectRepository
import com.andreev.ocrbackend.core.service.domain.security.UserPrinciple
import com.andreev.ocrbackend.dto.CreateProjectRequest
import com.andreev.ocrbackend.dto.DocumentCreateRequest
import com.andreev.ocrbackend.dto.ModelMessage
import com.andreev.ocrbackend.dto.UpdateProjectRequest
import com.andreev.ocrbackend.output.rabbit.RabbitSender
import mu.KLogging
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
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
    private val s3StorageService: YandexStorageService,
    private val rabbitSender: RabbitSender
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

        val project = Project(name = request.name, description = request.description)
        val savedProject = projectRepository.save(project)
        modelService.createModel(savedProject)

        val role = roleService.findRoleByName(RoleName.ROLE_MANAGER)

        userProjectAgentService.createUserProjectAgent(
            user = user,
            project = project,
            role = role
        )

        request.participants?.map { userAdd ->
            val userToProject = userService.findById(userAdd.userId)
            val roleOfNewParticipants = roleService.findRoleByName(RoleName.valueOf(userAdd.role))
            userProjectAgentService.createUserProjectAgent(
                user = userToProject,
                project = project,
                role = roleOfNewParticipants
            )
        }

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

    @Transactional
    fun executeProject(id: UUID) {
        val project = findById(id)
        val model = modelService.getModelByProjectId(project)
        val message = ModelMessage(modelId = model.id.toString(), command = Command.EXECUTE)
        modelService.updateStatusModel(status = Model.Status.IN_PROGRESS, model = model)

        rabbitSender.send(
            exchange = Rabbit.Exchange.OCRBACKEND_TX_X_HSE_LEARNING_COMMANDS_V1,
            message = message,
            routingKey = Rabbit.RoutingKey.OCRBACKEND_COMMAND_EXECUTE_MODEL
        )
    }

    @Transactional
    fun uploadDocuments(id: UUID, documents: List<MultipartFile>) {
        val project = findById(id)
        val urlPathList: List<String> = s3StorageService.uploadToS3Storage(documents = documents.map { it.bytes })
        urlPathList.map { urlPath ->
            documentService.createDocument(
                project = project,
                request = DocumentCreateRequest(urlPath = urlPath)
            )
        }
        logger.info { "Successfully added documents to $project" }
    }

}
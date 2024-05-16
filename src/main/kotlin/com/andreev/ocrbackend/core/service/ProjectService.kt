package com.andreev.ocrbackend.core.service

import com.andreev.ocrbackend.ProjectAlreadyExistsException
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
import com.andreev.ocrbackend.dto.DocumentStatisticDto
import com.andreev.ocrbackend.dto.DocumentType
import com.andreev.ocrbackend.dto.DocumentUploadRequest
import com.andreev.ocrbackend.dto.ModelMessage
import com.andreev.ocrbackend.dto.ParticipantAdd
import com.andreev.ocrbackend.dto.ProjectResponseWithoutDocuments
import com.andreev.ocrbackend.dto.UpdateProjectRequest
import com.andreev.ocrbackend.input.rest.converter.ProjectConverter
import com.andreev.ocrbackend.output.mail.MailSender
import com.andreev.ocrbackend.output.rabbit.RabbitSender
import mu.KLogging
import org.apache.commons.io.FilenameUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID
import javax.transaction.Transactional


@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val projectConverter: ProjectConverter,
    private val userProjectAgentService: UserProjectAgentService,
    private val userService: UserService,
    private val roleService: RoleService,
    private val modelService: ModelService,
    private val documentService: DocumentService,
    private val s3StorageService: YandexStorageService,
    private val rabbitSender: RabbitSender,
    private val mailSender: MailSender,
    @Value("\${machineLearning.batchSize}")
    private val batchSize: Int = 2,
    @Value("\${machineLearning.modelType}")
    private val modelType: String = "YOLO8",
    @Value("\${machineLearning.epoch}")
    private val epoch: Int = 50
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

    fun getMarkupDocuments(projectId: UUID) = documentService.getDocumentsWithLabelsAsCsv(projectId = projectId)

    fun getTemplateDocUrlPath(project: Project): String? {
        val documentCollection = project.documents
        if (documentCollection.isNullOrEmpty()) {
            return null
        }
        return documentCollection.firstOrNull { it.type == DocumentType.TEMPLATE.name }?.urlPath
    }

    fun getProjectsByUserId(userId: UUID): List<ProjectResponseWithoutDocuments> {
        val user = userService.findById(userId)
        logger.info { "Try to find all projects of $user" }
        val userProjectAgentList = userProjectAgentService.getAgentByUser(user)
        val result = userProjectAgentList.map { agent ->
            val numberOfDocsAndLabeled: DocumentStatisticDto =
                documentService.analyticsOfDocsInProject(projectId = agent.project.id)
            projectConverter.toProjectResponseWithoutDocuments(
                role = agent.role.name,
                project = agent.project,
                previewUrl = getTemplateDocUrlPath(agent.project),
                analytics = numberOfDocsAndLabeled,
                isTemplateLabeled = agent.project.isTemplateLabeled()
            )
        }
        if (result.isEmpty()) {
            logger.info { "$user doesn't have any project" }
        }

        return result
    }

    @Transactional
    fun deleteProject(id: UUID) {
        val project = findById(id)
        logger.info { "Try to delete $project with all documents" }
        projectRepository.deleteById(id)
        logger.info { "Successfully deleted project with id: $id" }
    }

    fun existsProjectByName(name: String): Boolean = projectRepository.existsByName(name)

    @Transactional
    fun createProject(authentication: Authentication, request: CreateProjectRequest): Project {
        val exists = existsProjectByName(request.name)
        if (exists) {
            logger.info { "Project with title ${request.name} already exists" }
            throw ProjectAlreadyExistsException("Project with title ${request.name} already exists")
        }

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

        request.participants?.let { addParticipantToProject(it, project, manager = creator) }

        logger.info { "Successfully created $savedProject" }

        return savedProject
    }

    @Transactional
    fun updateProject(
        id: UUID,
        request: UpdateProjectRequest,
        authentication: Authentication
    ): Project {
        val project = findById(id)
        val manager = authentication.principal as UserPrinciple
        with(request) {
            name?.let { project.name = name }
            description?.let { project.description = description }

            participants?.let { addParticipantToProject(it, project, manager) }
        }

        val savedProject = projectRepository.save(project)
        logger.info { "Successfully updated $savedProject" }
        return savedProject
    }

    fun addParticipantToProject(
        participants: Set<ParticipantAdd>,
        project: Project,
        manager: UserPrinciple,
    ) {
        participants.let { users ->
            val assessors = users.map { userAdd ->
                val user = userService.findById(userAdd.userId)
                val role = roleService.findRoleByName(RoleName.valueOf(userAdd.role))
                userProjectAgentService.createUserProjectAgent(
                    user = user,
                    project = project,
                    role = role
                )
                user.email to "${user.name} ${user.surname}"
            }.toSet()

            sendInviteMessageTo(assessors, projectName = project.name, managerEmail = manager.username)
        }
    }

    fun sendInviteMessageTo(
        assessors: Set<Pair<String, String>>,
        projectName: String,
        managerEmail: String,
    ) {
        val subject = "Invite to project: $projectName"
        assessors.forEach { (mail, fullName) ->
            val message = "Hello, $fullName!\n" +
                "Your manager $managerEmail invite you to the project: $projectName"
            logger.info { "Sending invite message to $fullName" }
            mailSender.sendTo(
                emailTo = mail,
                subject = subject,
                message = message
            )
        }
    }

    @Transactional
    fun executeProject(id: UUID) {
        val project = findById(id)
        val model = modelService.getModelByProjectId(project)
        val message = ModelMessage(
            modelId = model.id.toString(),
            command = Command.TRAIN.name,
            batch_size = batchSize,
            model_type = modelType,
            epoch = epoch
        )
        modelService.updateStatusModel(status = Model.Status.IN_PROGRESS, model = model)

        rabbitSender.send(
            exchange = Rabbit.Exchange.OCRBACKEND_TX_X_HSE_LEARNING_COMMANDS_V1,
            message = message.toJson(),
            routingKey = Rabbit.RoutingKey.OCRBACKEND_COMMAND_TRAIN_MODEL
        )
    }

    @Transactional
    fun uploadDocuments(id: UUID, documents: List<MultipartFile>, request: DocumentUploadRequest) {
        val project = findById(id)
        val extensionByDoc: List<Pair<ByteArray, String>> = documents.map {
            it.bytes to FilenameUtils.getExtension(it.originalFilename)
        }
        val urlPathList: List<String> = s3StorageService.uploadToS3Storage(extensionByDoc = extensionByDoc)
        urlPathList.map { urlPath ->
            documentService.createDocument(
                project = project,
                request = DocumentCreateRequest(urlPath = urlPath),
                type = request.type
            )
        }
        logger.info { "Successfully added documents to $project" }
    }

}
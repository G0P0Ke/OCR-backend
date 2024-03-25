package com.andreev.ocrbackend.core.service

import com.andreev.ocrbackend.core.model.Model
import com.andreev.ocrbackend.core.model.Project
import com.andreev.ocrbackend.core.repository.ModelRepository
import mu.KLogging
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class ModelService(
    private val modelRepository: ModelRepository
) {

    companion object : KLogging()

    @Transactional
    fun createModel(project: Project) {
        val newModel = Model(project = project)
        val savedModel = modelRepository.save(newModel)
        logger.info { "Successfully created Model: $savedModel for Project: ${project.id}" }
    }
}
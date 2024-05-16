package com.andreev.ocrbackend.input.rabbit

import com.andreev.ocrbackend.Rabbit.Inputs.OCRBACKEND_MACHINE_LEARNING_RESULT
import com.andreev.ocrbackend.core.service.JsonUtilService
import com.andreev.ocrbackend.dto.ModelResult
import mu.KLogging
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.messaging.Message
import org.springframework.stereotype.Service

@Service
class MachineLearningHandler(
    private val jsonUtilService: JsonUtilService
) {

    companion object : KLogging()

    @StreamListener(OCRBACKEND_MACHINE_LEARNING_RESULT)
    fun handleMessage(message: Message<String>) {
        val receivedMessage = jsonUtilService.extractMessage(message, ModelResult::class.java)
        logger.info { "Received message: $receivedMessage" }
    }
}
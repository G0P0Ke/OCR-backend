package com.andreev.ocrbackend.core.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.springframework.messaging.Message
import org.springframework.stereotype.Service

@Service
class JsonUtilService(private val objectMapper: ObjectMapper) {

    companion object : KLogging()

    fun <T> extractMessage(message: Message<String>, clazz: Class<T>) = extractMessage(message.payload, clazz)

    fun <T> extractMessage(message: String, clazz: Class<T>): T? {
        return try {
            objectMapper.readValue(message, clazz)
        } catch (e: JsonProcessingException) {
            logger.error(e) {
                "An error occurred during massage deserialization : $message"
            }
            null
        }
    }
}
package com.andreev.ocrbackend.dto

import com.fasterxml.jackson.databind.ObjectMapper

data class ModelMessage(
    val modelId: String,
    val command: String,
    val batch_size: Int,
    val model_type: String,
    val epoch: Int,
) {
    fun toJson(): String {
        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(this)
    }
}

data class ModelResult(
    val modelId: String,
    val status: String
)


data class ModelMessageInference(
    val modelId: String,
    val documentId: String,
    val command: String
) {
    fun toJson(): String {
        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(this)
    }
}
package com.andreev.ocrbackend.dto

import com.andreev.ocrbackend.core.enums.Command

data class ModelMessage(
    val modelId: String,
    val command: Command
)
package com.andreev.ocrbackend

object Rabbit {
    object Exchange {
       const val OCRBACKEND_TX_X_HSE_LEARNING_COMMANDS_V1 = "ocrbackend-tx-x-hse-learning-commands-v1"
    }

    object RoutingKey {
        const val OCRBACKEND_COMMAND_EXECUTE_MODEL = "ocrBackend.command.executeModel"
    }
}
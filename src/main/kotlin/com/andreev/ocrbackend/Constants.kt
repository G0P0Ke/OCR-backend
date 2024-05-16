package com.andreev.ocrbackend

object Rabbit {
    object Exchange {
       const val OCRBACKEND_TX_X_HSE_LEARNING_COMMANDS_V1 = "ocrbackend-tx-x-hse-learning-commands-v1"
    }

    object RoutingKey {
        const val OCRBACKEND_COMMAND_TRAIN_MODEL = "ocrBackend.command.trainModel"
        const val OCRBACKEND_COMMAND_INFERENCE_MODEL = "ocrBackend.command.inference"
    }

    object Inputs {
        const val OCRBACKEND_MACHINE_LEARNING_RESULT = "ocrBackendMachineLearningResult-in-0"
    }
}
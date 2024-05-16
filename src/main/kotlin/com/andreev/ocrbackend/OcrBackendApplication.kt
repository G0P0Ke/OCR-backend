package com.andreev.ocrbackend

import com.andreev.ocrbackend.Rabbit.Inputs.OCRBACKEND_MACHINE_LEARNING_RESULT
import com.andreev.ocrbackend.configuration.YandexStorageConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.messaging.SubscribableChannel

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(YandexStorageConfig::class)
@EnableBinding(RabbitSource::class)
class OcrBackendApplication

fun main(args: Array<String>) {
    runApplication<OcrBackendApplication>(*args)
}

interface RabbitSource {
    @Input(OCRBACKEND_MACHINE_LEARNING_RESULT)
    fun ocrBackendMachineLearningResultInput(): SubscribableChannel

}
package com.andreev.ocrbackend

import com.andreev.ocrbackend.configuration.YandexStorageConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(YandexStorageConfig::class)
class OcrBackendApplication

fun main(args: Array<String>) {
    runApplication<OcrBackendApplication>(*args)
}

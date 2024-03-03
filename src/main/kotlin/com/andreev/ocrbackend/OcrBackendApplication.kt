package com.andreev.ocrbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class OcrBackendApplication

fun main(args: Array<String>) {
    runApplication<OcrBackendApplication>(*args)
}

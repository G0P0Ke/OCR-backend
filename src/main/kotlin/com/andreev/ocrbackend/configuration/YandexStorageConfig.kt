package com.andreev.ocrbackend.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "yandex-s3")
@ConstructorBinding
data class YandexStorageConfig(
    val accessKey: String,
    val secretKey: String,
    val bucket: String,
    val serviceEndpoint: String,
    val signingRegion: String,
)

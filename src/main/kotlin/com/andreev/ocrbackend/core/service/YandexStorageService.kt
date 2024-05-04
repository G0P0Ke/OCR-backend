package com.andreev.ocrbackend.core.service

import com.amazonaws.SdkClientException
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.andreev.ocrbackend.configuration.YandexStorageConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue


@Service
class YandexStorageService(
    private val configs: YandexStorageConfig,
) {

    companion object : KLogging()

    private lateinit var s3Client: AmazonS3

    init {
        try {
            s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                    AwsClientBuilder.EndpointConfiguration(
                        configs.serviceEndpoint,
                        configs.signingRegion
                    )
                )
                .withCredentials(
                    AWSStaticCredentialsProvider(
                        BasicAWSCredentials(
                            configs.accessKey,
                            configs.secretKey
                        )
                    )
                )
                .build()
        } catch (e: SdkClientException) {
            logger.error("Error creating client for Object Storage via AWS SDK. Reason: {}", e.message)
            throw SdkClientException(e.message)
        }
    }

    fun uploadToS3Storage(extensionByDoc: List<Pair<ByteArray, String>>): List<String> {
        val urls = ConcurrentLinkedQueue<String>()

        runBlocking {
            val jobs = extensionByDoc.map {
                val (documentBytes, extension) = it
                async(Dispatchers.IO) {
                    val fileName = generateUniqueName() + "." + extension
                    val metadata = ObjectMetadata()
                    metadata.contentLength = documentBytes.size.toLong()

                    ByteArrayInputStream(documentBytes).use { inputStream ->
                        try {
                            s3Client.putObject(configs.bucket, fileName, inputStream, metadata)
                            logger.info("Upload Document to S3. Added file: $fileName to bucket: ${configs.bucket}")

                            val url = s3Client.getUrl(configs.bucket, fileName).toURI().toString()
                            urls.add(url)
                        } catch (e: Exception) {
                            logger.error("Failed to upload file $fileName", e)
                        }
                    }
                }
            }
            jobs.forEach { it.await() }
        }
        return urls.toList()
    }

    private fun generateUniqueName(): String {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }
}
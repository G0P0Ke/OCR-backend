package com.andreev.ocrbackend.core.service

import com.andreev.ocrbackend.JsonValidationException
import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.main.JsonSchemaFactory
import org.springframework.stereotype.Service

@Service
class JsonValidationService {

    fun validate(jsonNode: JsonNode, schemaPath: String) {
        val report = JsonSchemaFactory
            .byDefault()
            .getJsonSchema(
                JsonLoader.fromResource("/schemas/$schemaPath")
            )
            .validate(jsonNode)

        if (!report.isSuccess) {
            throw JsonValidationException(report.map { it.message })
        }

    }
}
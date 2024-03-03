package com.andreev.ocrbackend.core.model.converter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import javax.persistence.AttributeConverter
import javax.persistence.Converter


@Converter
class JsonNodeToStringConverter(
    val objectMapper: ObjectMapper
) : AttributeConverter<JsonNode, String> {

    override fun convertToDatabaseColumn(p0: JsonNode?): String? {
        if (p0 == null) {
            return null;
        }
        return p0.toString();
    }

    override fun convertToEntityAttribute(p0: String?): JsonNode? {
        return if (p0 == null) {
            return null
        } else try {
            objectMapper.readTree(p0)
        } catch (e: IOException) {
            throw RuntimeException("Failed to deserialize JSON", e)
        }
    }

}
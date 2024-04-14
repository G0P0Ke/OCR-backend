package com.andreev.ocrbackend.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class EntryDto(
    @field:[NotBlank]
    val email: String,
    @field:[NotBlank]
    val password: String,
    @field:[Size(max = 255)]
    val company: String? = null
)
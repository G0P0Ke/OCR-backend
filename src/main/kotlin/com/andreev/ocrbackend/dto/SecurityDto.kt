package com.andreev.ocrbackend.dto

import javax.validation.constraints.NotBlank

data class EntryDto(
    @field:[NotBlank]
    val email: String,
    @field:[NotBlank]
    val password: String,
)
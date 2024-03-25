package com.andreev.ocrbackend.dto

data class ErrorResponse(val errors: Collection<ErrorDescription>)

data class ErrorDescription(val message: String)
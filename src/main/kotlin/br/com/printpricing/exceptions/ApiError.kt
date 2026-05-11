package br.com.printpricing.exceptions

import java.time.OffsetDateTime

data class ApiError(
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val details: Map<String, String>? = null
)

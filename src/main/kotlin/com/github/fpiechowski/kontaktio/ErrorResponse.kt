package com.github.fpiechowski.kontaktio

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
    val cause: ErrorResponse? = null,
    val throwable: String? = null
) {
    companion object {
        fun from(error: KontaktError): ErrorResponse = ErrorResponse(
            message = error.message,
            cause =  error.cause?.let { from(it) },
            throwable = error.throwable?.let { it::class.qualifiedName }
        )
    }
}
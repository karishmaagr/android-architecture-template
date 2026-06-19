package com.example.arch.core.common.result

sealed class ApiException(message: String) : Exception(message) {
    class BadRequest(message: String) : ApiException(message)
    class Unauthorized(message: String) : ApiException(message)
    class Forbidden(message: String) : ApiException(message)
    class NotFound(message: String) : ApiException(message)
    class Conflict(message: String) : ApiException(message)
    class ValidationError(
        message: String,
        val fieldErrors: Map<String, List<String>> = emptyMap(),
    ) : ApiException(message)
    class TooManyRequests(message: String) : ApiException(message)
    class ServerError(message: String) : ApiException(message)
    class NetworkError(message: String = "No internet connection") : ApiException(message)
    class Unknown(val code: Int, message: String) : ApiException(message)
}

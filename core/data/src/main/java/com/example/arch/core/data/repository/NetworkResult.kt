package com.example.arch.core.data.repository

import com.example.arch.core.common.result.Result
import com.example.arch.core.common.result.ApiException

sealed interface NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>
    data object NotModified : NetworkResult<Nothing>
    data class Failure(val exception: ApiException) : NetworkResult<Nothing>
}

fun <T> NetworkResult<T>.toResult(): Result<T> = when (this) {
    is NetworkResult.Success -> Result.Success(data)
    is NetworkResult.NotModified -> Result.Error(
        ApiException.Unknown(204, "Unexpected empty response"),
        "Unexpected empty response",
    )
    is NetworkResult.Failure -> Result.Error(exception, exception.message)
}

fun NetworkResult<Unit>.toUnitResult(): Result<Unit> = when (this) {
    is NetworkResult.Success -> Result.Success(Unit)
    is NetworkResult.NotModified -> Result.Success(Unit)
    is NetworkResult.Failure -> Result.Error(exception, exception.message)
}

fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> = when (this) {
    is NetworkResult.Success -> NetworkResult.Success(transform(data))
    is NetworkResult.NotModified -> NetworkResult.NotModified
    is NetworkResult.Failure -> this
}
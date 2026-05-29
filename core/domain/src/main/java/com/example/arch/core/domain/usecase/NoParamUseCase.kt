package com.example.arch.core.domain.usecase

import com.example.arch.core.common.coroutines.DispatcherProvider
import com.example.arch.core.common.result.Result
import kotlinx.coroutines.withContext

abstract class NoParamUseCase<R>(private val dispatchers: DispatcherProvider) {

    suspend operator fun invoke(): Result<R> =
        withContext(dispatchers.io) {
            try {
                Result.Success(execute())
            } catch (e: Exception) {
                Result.Error(e, e.message)
            }
        }

    protected abstract suspend fun execute(): R
}

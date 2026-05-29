package com.example.arch.core.domain.usecase

import com.example.arch.core.common.coroutines.DispatcherProvider
import com.example.arch.core.common.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

abstract class FlowUseCase<in P, R>(private val dispatchers: DispatcherProvider) {

    operator fun invoke(params: P): Flow<Result<R>> =
        execute(params)
            .map<R, Result<R>> { Result.Success(it) }
            .catch { emit(Result.Error(it)) }
            .flowOn(dispatchers.io)

    protected abstract fun execute(params: P): Flow<R>
}

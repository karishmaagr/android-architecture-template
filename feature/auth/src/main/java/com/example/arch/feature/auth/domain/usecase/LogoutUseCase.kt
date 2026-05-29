package com.example.arch.feature.auth.domain.usecase

import com.example.arch.core.common.coroutines.DispatcherProvider
import com.example.arch.core.common.result.Result
import com.example.arch.core.domain.usecase.NoParamUseCase
import com.example.arch.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
    dispatchers: DispatcherProvider,
) : NoParamUseCase<Unit>(dispatchers) {

    override suspend fun execute() {
        when (val result = repository.logout()) {
            is Result.Success -> Unit
            is Result.Error   -> throw result.exception
            is Result.Loading -> error("Unexpected loading state")
        }
    }
}

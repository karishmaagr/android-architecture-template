package com.example.arch.feature.auth.domain.usecase

import com.example.arch.core.common.coroutines.DispatcherProvider
import com.example.arch.core.common.result.Result
import com.example.arch.core.domain.usecase.UseCase
import com.example.arch.feature.auth.domain.model.AuthCredentials
import com.example.arch.feature.auth.domain.model.User
import com.example.arch.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
    dispatchers: DispatcherProvider,
) : UseCase<AuthCredentials, User>(dispatchers) {

    override suspend fun execute(params: AuthCredentials): User {
        require(params.email.isNotBlank()) { "Email must not be blank" }
        require(params.password.isNotBlank()) { "Password must not be blank" }

        return when (val result = repository.login(params)) {
            is Result.Success -> result.data
            is Result.Error   -> throw result.exception
            is Result.Loading -> error("Unexpected loading state")
        }
    }
}

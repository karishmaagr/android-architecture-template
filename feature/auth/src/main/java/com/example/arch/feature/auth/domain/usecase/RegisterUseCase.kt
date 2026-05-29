package com.example.arch.feature.auth.domain.usecase

import com.example.arch.core.common.coroutines.DispatcherProvider
import com.example.arch.core.common.result.Result
import com.example.arch.core.domain.usecase.UseCase
import com.example.arch.feature.auth.domain.model.RegisterCredentials
import com.example.arch.feature.auth.domain.model.User
import com.example.arch.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository,
    dispatchers: DispatcherProvider,
) : UseCase<RegisterCredentials, User>(dispatchers) {

    override suspend fun execute(params: RegisterCredentials): User {
        require(params.name.isNotBlank()) { "Name must not be blank" }
        require(params.email.isNotBlank()) { "Email must not be blank" }
        require(params.password.isNotBlank()) { "Password must not be blank" }

        return when (val result = repository.register(params)) {
            is Result.Success -> result.data
            is Result.Error   -> throw result.exception
            is Result.Loading -> error("Unexpected loading state")
        }
    }
}

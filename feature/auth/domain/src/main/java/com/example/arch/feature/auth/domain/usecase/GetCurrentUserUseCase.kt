package com.example.arch.feature.auth.domain.usecase

import com.example.arch.core.common.coroutines.DispatcherProvider
import com.example.arch.core.domain.usecase.FlowUseCase
import com.example.arch.feature.auth.domain.model.User
import com.example.arch.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository,
    dispatchers: DispatcherProvider,
) : FlowUseCase<Unit, User?>(dispatchers) {

    override fun execute(params: Unit): Flow<User?> = repository.getCurrentUser()
}

package com.example.arch.feature.profile.domain.usecase

import com.example.arch.core.common.coroutines.DispatcherProvider
import com.example.arch.core.common.result.Result
import com.example.arch.core.domain.usecase.UseCase
import com.example.arch.feature.profile.domain.model.Profile
import com.example.arch.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
    dispatchers: DispatcherProvider,
) : UseCase<String, Profile>(dispatchers) {

    override suspend fun execute(params: String): Profile =
        when (val r = repository.getProfile(params)) {
            is Result.Success -> r.data
            is Result.Error   -> throw r.exception
            is Result.Loading -> error("Unexpected loading state")
        }
}

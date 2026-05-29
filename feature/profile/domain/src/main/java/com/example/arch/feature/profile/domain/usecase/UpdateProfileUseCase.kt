package com.example.arch.feature.profile.domain.usecase

import com.example.arch.core.common.coroutines.DispatcherProvider
import com.example.arch.core.common.result.Result
import com.example.arch.core.domain.usecase.UseCase
import com.example.arch.feature.profile.domain.model.Profile
import com.example.arch.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
    dispatchers: DispatcherProvider,
) : UseCase<Profile, Profile>(dispatchers) {

    override suspend fun execute(params: Profile): Profile =
        when (val r = repository.updateProfile(params)) {
            is Result.Success -> r.data
            is Result.Error   -> throw r.exception
            is Result.Loading -> error("Unexpected")
        }
}

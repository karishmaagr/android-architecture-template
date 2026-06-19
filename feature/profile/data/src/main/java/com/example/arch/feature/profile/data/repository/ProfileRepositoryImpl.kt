package com.example.arch.feature.profile.data.repository

import com.example.arch.core.common.result.Result
import com.example.arch.core.common.result.map
import com.example.arch.core.data.repository.BaseRepository
import com.example.arch.core.data.repository.NetworkResult
import com.example.arch.core.data.repository.toResult
import com.example.arch.feature.profile.data.remote.ProfileApi
import com.example.arch.feature.profile.data.remote.model.UpdateProfileRequest
import com.example.arch.feature.profile.domain.model.Profile
import com.example.arch.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi,
) : ProfileRepository, BaseRepository() {

    override suspend fun getProfile(userId: String): Result<Profile> =
        safeApiCall { api.getProfile(userId) }.toResult().map { dto ->
            Profile(
                id             = dto.id,
                name           = dto.name,
                email          = dto.email,
                bio            = dto.bio,
                avatarUrl      = dto.avatarUrl,
                followersCount = dto.followersCount,
                followingCount = dto.followingCount,
            )
        }

    override suspend fun updateProfile(profile: Profile): Result<Profile> {
        val request = UpdateProfileRequest(name = profile.name, bio = profile.bio)
        return when (val result = safeApiCall { api.updateProfile(profile.id, request) }) {
            is NetworkResult.Success -> Result.Success(
                Profile(
                    id             = result.data.id,
                    name           = result.data.name,
                    email          = result.data.email,
                    bio            = result.data.bio,
                    avatarUrl      = result.data.avatarUrl,
                    followersCount = result.data.followersCount,
                    followingCount = result.data.followingCount,
                )
            )
            // 204: server accepted the update but returned no body — the input is the current state
            is NetworkResult.NotModified -> Result.Success(profile)
            is NetworkResult.Failure -> Result.Error(result.exception, result.exception.message)
        }
    }

}
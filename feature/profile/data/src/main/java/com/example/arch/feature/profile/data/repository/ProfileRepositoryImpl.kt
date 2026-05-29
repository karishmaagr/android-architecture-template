package com.example.arch.feature.profile.data.repository

import com.example.arch.core.common.result.Result
import com.example.arch.core.data.repository.BaseRepository
import com.example.arch.feature.profile.data.remote.ProfileApi
import com.example.arch.feature.profile.data.remote.model.UpdateProfileRequest
import com.example.arch.feature.profile.domain.model.Profile
import com.example.arch.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi,
) : ProfileRepository, BaseRepository() {

    override suspend fun getProfile(userId: String): Result<Profile> =
        safeApiCall { api.getProfile(userId) }.map { dto ->
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
        return safeApiCall { api.updateProfile(profile.id, request) }.map { dto ->
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
    }

    override suspend fun updateAvatar(localUri: String): Result<String> {
        // Multipart upload implementation
        TODO("Implement avatar upload with multipart form data")
    }
}

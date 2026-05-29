package com.example.arch.feature.profile.domain.repository

import com.example.arch.core.common.result.Result
import com.example.arch.feature.profile.domain.model.Profile

interface ProfileRepository {
    suspend fun getProfile(userId: String): Result<Profile>
    suspend fun updateProfile(profile: Profile): Result<Profile>
    suspend fun updateAvatar(localUri: String): Result<String>
}

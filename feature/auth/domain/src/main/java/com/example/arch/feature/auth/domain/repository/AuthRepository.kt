package com.example.arch.feature.auth.domain.repository

import com.example.arch.core.common.result.Result
import com.example.arch.feature.auth.domain.model.AuthCredentials
import com.example.arch.feature.auth.domain.model.RegisterCredentials
import com.example.arch.feature.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(credentials: AuthCredentials): Result<User>
    suspend fun register(credentials: RegisterCredentials): Result<User>
    suspend fun logout(): Result<Unit>
    fun getCurrentUser(): Flow<User?>
    suspend fun refreshToken(): Result<Unit>
}

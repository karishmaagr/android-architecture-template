package com.example.arch.feature.auth.data.remote.datasource

import com.example.arch.core.common.result.Result
import com.example.arch.feature.auth.data.remote.model.LoginRequest
import com.example.arch.feature.auth.data.remote.model.LoginResponseDto
import com.example.arch.feature.auth.data.remote.model.RegisterRequest

interface AuthRemoteDataSource {
    suspend fun login(request: LoginRequest): Result<LoginResponseDto>
    suspend fun register(request: RegisterRequest): Result<LoginResponseDto>
    suspend fun logout(): Result<Unit>
    suspend fun refreshToken(): Result<LoginResponseDto>
}

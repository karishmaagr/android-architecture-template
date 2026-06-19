package com.example.arch.feature.auth.data.remote.datasource

import com.example.arch.core.common.result.Result
import com.example.arch.core.data.repository.BaseRepository
import com.example.arch.core.data.repository.toResult
import com.example.arch.core.data.repository.toUnitResult
import com.example.arch.feature.auth.data.remote.AuthApi
import com.example.arch.feature.auth.data.remote.model.LoginRequest
import com.example.arch.feature.auth.data.remote.model.LoginResponseDto
import com.example.arch.feature.auth.data.remote.model.RegisterRequest
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: AuthApi,
) : AuthRemoteDataSource, BaseRepository() {

    override suspend fun login(request: LoginRequest): Result<LoginResponseDto> =
        safeApiCall { api.login(request) }.toResult()

    override suspend fun register(request: RegisterRequest): Result<LoginResponseDto> =
        safeApiCall { api.register(request) }.toResult()

    override suspend fun logout(): Result<Unit> =
        safeApiCall { api.logout() }.toUnitResult()

    override suspend fun refreshToken(): Result<LoginResponseDto> =
        safeApiCall { api.refreshToken() }.toResult()
}
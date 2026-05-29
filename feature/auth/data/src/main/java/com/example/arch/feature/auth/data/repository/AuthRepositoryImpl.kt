package com.example.arch.feature.auth.data.repository

import com.example.arch.core.common.result.Result
import com.example.arch.core.data.local.preferences.AppPreferences
import com.example.arch.feature.auth.data.local.datasource.AuthLocalDataSource
import com.example.arch.feature.auth.data.mapper.AuthMapper.toDomain
import com.example.arch.feature.auth.data.mapper.toDomain
import com.example.arch.feature.auth.data.remote.datasource.AuthRemoteDataSource
import com.example.arch.feature.auth.data.remote.model.LoginRequest
import com.example.arch.feature.auth.data.remote.model.RegisterRequest
import com.example.arch.feature.auth.domain.model.AuthCredentials
import com.example.arch.feature.auth.domain.model.RegisterCredentials
import com.example.arch.feature.auth.domain.model.User
import com.example.arch.core.data.auth.LogoutService
import com.example.arch.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
    private val prefs: AppPreferences,
) : AuthRepository, LogoutService {

    override suspend fun login(credentials: AuthCredentials): Result<User> {
        val request = LoginRequest(email = credentials.email, password = credentials.password)
        return when (val result = remoteDataSource.login(request)) {
            is Result.Success -> {
                val dto = result.data
                prefs.saveAuthToken(dto.tokens.accessToken)
                prefs.saveRefreshToken(dto.tokens.refreshToken)
                val user = dto.toDomain()
                localDataSource.saveUser(user)
                Result.Success(user)
            }
            is Result.Error   -> result
            is Result.Loading -> result
        }
    }

    override suspend fun register(credentials: RegisterCredentials): Result<User> {
        val request = RegisterRequest(name = credentials.name, email = credentials.email, password = credentials.password)
        return when (val result = remoteDataSource.register(request)) {
            is Result.Success -> {
                val dto = result.data
                prefs.saveAuthToken(dto.tokens.accessToken)
                prefs.saveRefreshToken(dto.tokens.refreshToken)
                val user = dto.toDomain()
                localDataSource.saveUser(user)
                Result.Success(user)
            }
            is Result.Error   -> result
            is Result.Loading -> result
        }
    }

    override suspend fun logout(): Result<Unit> {
        localDataSource.clearUser()
        return remoteDataSource.logout()
    }

    override fun getCurrentUser(): Flow<User?> = localDataSource.getCachedUser()

    override suspend fun refreshToken(): Result<Unit> =
        when (val result = remoteDataSource.refreshToken()) {
            is Result.Success -> {
                prefs.saveAuthToken(result.data.tokens.accessToken)
                prefs.saveRefreshToken(result.data.tokens.refreshToken)
                Result.Success(Unit)
            }
            is Result.Error   -> result
            is Result.Loading -> result
        }
}

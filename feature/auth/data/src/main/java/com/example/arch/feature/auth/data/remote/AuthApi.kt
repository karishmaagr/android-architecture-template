package com.example.arch.feature.auth.data.remote

import com.example.arch.core.network.model.ApiResponse
import com.example.arch.feature.auth.data.remote.model.LoginRequest
import com.example.arch.feature.auth.data.remote.model.LoginResponseDto
import com.example.arch.feature.auth.data.remote.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponseDto>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<LoginResponseDto>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @POST("auth/refresh")
    suspend fun refreshToken(): Response<ApiResponse<LoginResponseDto>>
}

package com.example.arch.feature.profile.data.remote

import com.example.arch.core.network.model.ApiResponse
import com.example.arch.feature.profile.data.remote.model.ProfileDto
import com.example.arch.feature.profile.data.remote.model.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileApi {
    @GET("users/{userId}/profile")
    suspend fun getProfile(@Path("userId") userId: String): Response<ApiResponse<ProfileDto>>

    @PUT("users/{userId}/profile")
    suspend fun updateProfile(
        @Path("userId") userId: String,
        @Body request: UpdateProfileRequest,
    ): Response<ApiResponse<ProfileDto>>
}

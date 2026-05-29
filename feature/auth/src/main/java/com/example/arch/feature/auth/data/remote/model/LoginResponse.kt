package com.example.arch.feature.auth.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthTokenResponse(
    @Json(name = "access_token")  val accessToken: String,
    @Json(name = "refresh_token") val refreshToken: String,
    @Json(name = "expires_in")    val expiresIn: Int,
)

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id")         val id: String,
    @Json(name = "email")      val email: String,
    @Json(name = "name")       val name: String,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "created_at") val createdAt: Long,
)

@JsonClass(generateAdapter = true)
data class LoginResponseDto(
    @Json(name = "user")   val user: UserDto,
    @Json(name = "tokens") val tokens: AuthTokenResponse,
)

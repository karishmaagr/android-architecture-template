package com.example.arch.feature.profile.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileDto(
    @Json(name = "id")              val id: String,
    @Json(name = "name")            val name: String,
    @Json(name = "email")           val email: String,
    @Json(name = "bio")             val bio: String,
    @Json(name = "avatar_url")      val avatarUrl: String?,
    @Json(name = "followers_count") val followersCount: Int,
    @Json(name = "following_count") val followingCount: Int,
)

@JsonClass(generateAdapter = true)
data class UpdateProfileRequest(
    @Json(name = "name") val name: String,
    @Json(name = "bio")  val bio: String,
)

package com.example.arch.feature.profile.domain.model

data class Profile(
    val id: String,
    val name: String,
    val email: String,
    val bio: String,
    val avatarUrl: String?,
    val followersCount: Int,
    val followingCount: Int,
)

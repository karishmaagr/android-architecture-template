package com.example.arch.feature.auth.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val avatarUrl: String?,
    val createdAt: Long,
)

package com.example.arch.feature.home.domain.model

data class Post(
    val id: String,
    val title: String,
    val body: String,
    val userId: String,
    val createdAt: Long,
)

package com.example.arch.feature.auth.domain.model

data class AuthCredentials(
    val email: String,
    val password: String,
)

data class RegisterCredentials(
    val name: String,
    val email: String,
    val password: String,
)

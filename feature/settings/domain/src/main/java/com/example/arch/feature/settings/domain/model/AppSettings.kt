package com.example.arch.feature.settings.domain.model

data class AppSettings(
    val themeMode: ThemeMode,
    val notificationsEnabled: Boolean,
    val language: String,
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }

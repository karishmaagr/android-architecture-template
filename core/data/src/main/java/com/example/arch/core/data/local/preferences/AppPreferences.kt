package com.example.arch.core.data.local.preferences

import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    val authToken: Flow<String?>
    val refreshToken: Flow<String?>
    val userId: Flow<String?>
    val themeMode: Flow<String>
    val isOnboardingDone: Flow<Boolean>

    suspend fun saveAuthToken(token: String)
    suspend fun saveRefreshToken(token: String)
    suspend fun saveUserId(id: String)
    suspend fun saveThemeMode(mode: String)
    suspend fun setOnboardingDone(done: Boolean)
    suspend fun clearAuthData()
}

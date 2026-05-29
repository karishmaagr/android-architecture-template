package com.example.arch.feature.settings.domain.repository

import com.example.arch.core.common.result.Result
import com.example.arch.feature.settings.domain.model.AppSettings
import com.example.arch.feature.settings.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updateThemeMode(mode: ThemeMode): Result<Unit>
    suspend fun updateNotificationsEnabled(enabled: Boolean): Result<Unit>
    suspend fun logout(): Result<Unit>
}

package com.example.arch.feature.settings.data.repository

import com.example.arch.core.common.result.Result
import com.example.arch.core.data.auth.LogoutService
import com.example.arch.core.data.local.preferences.AppPreferences
import com.example.arch.feature.settings.domain.model.AppSettings
import com.example.arch.feature.settings.domain.model.ThemeMode
import com.example.arch.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val logoutService: LogoutService,
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> =
        prefs.themeMode.map { themeStr ->
            AppSettings(
                themeMode            = ThemeMode.entries.firstOrNull { it.name.lowercase() == themeStr } ?: ThemeMode.SYSTEM,
                notificationsEnabled = true,
                language             = "en",
            )
        }

    override suspend fun updateThemeMode(mode: ThemeMode): Result<Unit> {
        prefs.saveThemeMode(mode.name.lowercase())
        return Result.Success(Unit)
    }

    override suspend fun updateNotificationsEnabled(enabled: Boolean): Result<Unit> =
        Result.Success(Unit)

    override suspend fun logout(): Result<Unit> = logoutService.logout()
}

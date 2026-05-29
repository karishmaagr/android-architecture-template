package com.example.arch.feature.settings.presentation

import com.example.arch.core.ui.mvi.UiEffect
import com.example.arch.core.ui.mvi.UiIntent
import com.example.arch.core.ui.mvi.UiState
import com.example.arch.feature.settings.domain.model.AppSettings
import com.example.arch.feature.settings.domain.model.ThemeMode

data class SettingsState(
    val settings: AppSettings? = null,
    val isLoading: Boolean     = false,
    val isLoggingOut: Boolean  = false,
) : UiState

sealed interface SettingsIntent : UiIntent {
    data class ThemeModeChanged(val mode: ThemeMode)   : SettingsIntent
    data class NotificationsChanged(val enabled: Boolean) : SettingsIntent
    data object LogoutClicked                           : SettingsIntent
    data object NavigateBack                            : SettingsIntent
}

sealed interface SettingsEffect : UiEffect {
    data object NavigateBack                         : SettingsEffect
    data object NavigateToLogin                      : SettingsEffect
    data class ShowSnackbar(val message: String)     : SettingsEffect
}

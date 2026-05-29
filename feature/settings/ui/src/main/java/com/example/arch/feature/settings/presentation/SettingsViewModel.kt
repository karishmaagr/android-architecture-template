package com.example.arch.feature.settings.presentation

import androidx.lifecycle.viewModelScope
import com.example.arch.core.common.result.Result
import com.example.arch.core.ui.mvi.MviViewModel
import com.example.arch.feature.settings.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
) : MviViewModel<SettingsState, SettingsIntent, SettingsEffect>() {

    override fun createInitialState() = SettingsState()

    init { observeSettings() }

    override fun handleIntent(intent: SettingsIntent) = when (intent) {
        is SettingsIntent.ThemeModeChanged     -> changeTheme(intent.mode)
        is SettingsIntent.NotificationsChanged -> changeNotifications(intent.enabled)
        is SettingsIntent.LogoutClicked        -> logout()
        is SettingsIntent.NavigateBack         -> sendEffect(SettingsEffect.NavigateBack)
    }

    private fun observeSettings() {
        repository.getSettings()
            .onEach { settings -> setState { copy(settings = settings, isLoading = false) } }
            .launchIn(viewModelScope)
    }

    private fun changeTheme(mode: com.example.arch.feature.settings.domain.model.ThemeMode) {
        viewModelScope.launch { repository.updateThemeMode(mode) }
    }

    private fun changeNotifications(enabled: Boolean) {
        viewModelScope.launch { repository.updateNotificationsEnabled(enabled) }
    }

    private fun logout() {
        viewModelScope.launch {
            setState { copy(isLoggingOut = true) }
            when (val result = repository.logout()) {
                is Result.Success -> sendEffect(SettingsEffect.NavigateToLogin)
                is Result.Error   -> {
                    setState { copy(isLoggingOut = false) }
                    sendEffect(SettingsEffect.ShowSnackbar(result.message ?: "Logout failed"))
                }
                is Result.Loading -> Unit
            }
        }
    }
}

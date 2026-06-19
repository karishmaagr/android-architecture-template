package com.example.arch.feature.profile.presentation

import com.example.arch.core.ui.mvi.UiEffect
import com.example.arch.core.ui.mvi.UiIntent
import com.example.arch.core.ui.mvi.UiState
import com.example.arch.feature.profile.domain.model.Profile

data class ProfileState(
    val profile: Profile?    = null,
    val isLoading: Boolean   = true,
    val isEditing: Boolean   = false,
    val isSaving: Boolean    = false,
    val error: String?       = null,
    val editName: String     = "",
    val editBio: String      = "",
) : UiState

sealed interface ProfileIntent : UiIntent {
    data class LoadProfile(val userId: String)   : ProfileIntent
    data object EditClicked                      : ProfileIntent
    data object CancelEdit                       : ProfileIntent
    data class NameChanged(val name: String)     : ProfileIntent
    data class BioChanged(val bio: String)       : ProfileIntent
    data object SaveProfile                      : ProfileIntent
    data object NavigateBack                     : ProfileIntent
}

sealed interface ProfileEffect : UiEffect {
    data object NavigateBack                     : ProfileEffect
    data object SessionExpired                   : ProfileEffect
    data class ShowSnackbar(val message: String) : ProfileEffect
}

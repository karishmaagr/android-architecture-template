package com.example.arch.feature.profile.presentation

import androidx.lifecycle.viewModelScope
import com.example.arch.core.common.result.Result
import com.example.arch.core.ui.mvi.MviViewModel
import com.example.arch.feature.profile.domain.usecase.GetProfileUseCase
import com.example.arch.feature.profile.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
) : MviViewModel<ProfileState, ProfileIntent, ProfileEffect>() {

    override fun createInitialState() = ProfileState()

    override fun handleIntent(intent: ProfileIntent) = when (intent) {
        is ProfileIntent.LoadProfile  -> loadProfile(intent.userId)
        is ProfileIntent.EditClicked  -> setState { copy(isEditing = true, editName = profile?.name ?: "", editBio = profile?.bio ?: "") }
        is ProfileIntent.CancelEdit   -> setState { copy(isEditing = false) }
        is ProfileIntent.NameChanged  -> setState { copy(editName = intent.name) }
        is ProfileIntent.BioChanged   -> setState { copy(editBio = intent.bio) }
        is ProfileIntent.SaveProfile  -> saveProfile()
        is ProfileIntent.NavigateBack -> sendEffect(ProfileEffect.NavigateBack)
    }

    private fun loadProfile(userId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            when (val result = getProfileUseCase(userId)) {
                is Result.Success -> setState { copy(isLoading = false, profile = result.data) }
                is Result.Error   -> setState { copy(isLoading = false, error = result.message) }
                is Result.Loading -> Unit
            }
        }
    }

    private fun saveProfile() {
        val profile = currentState.profile ?: return
        viewModelScope.launch {
            setState { copy(isSaving = true) }
            val updated = profile.copy(name = currentState.editName, bio = currentState.editBio)
            when (val result = updateProfileUseCase(updated)) {
                is Result.Success -> {
                    setState { copy(isSaving = false, isEditing = false, profile = result.data) }
                    sendEffect(ProfileEffect.ShowSnackbar("Profile updated!"))
                }
                is Result.Error -> {
                    setState { copy(isSaving = false) }
                    sendEffect(ProfileEffect.ShowSnackbar(result.message ?: "Update failed"))
                }
                is Result.Loading -> Unit
            }
        }
    }
}

package com.example.arch.feature.profile.presentation

import androidx.lifecycle.viewModelScope
import com.example.arch.core.common.result.ApiException
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
            val result = getProfileUseCase(userId)
            when (result) {
                is Result.Success -> setState { copy(isLoading = false, profile = result.data) }
                is Result.Error   -> handleLoadError(result.exception)
                is Result.Loading -> Unit
            }
        }
    }

    private fun saveProfile() {
        val profile = currentState.profile ?: return
        viewModelScope.launch {
            setState { copy(isSaving = true) }
            val updated = profile.copy(name = currentState.editName, bio = currentState.editBio)
            val result = updateProfileUseCase(updated)
            when (result) {
                is Result.Success -> {
                    setState { copy(isSaving = false, isEditing = false, profile = result.data) }
                    sendEffect(ProfileEffect.ShowSnackbar("Profile updated successfully."))
                }
                is Result.Error   -> handleSaveError(result.exception)
                is Result.Loading -> Unit
            }
        }
    }

    private fun handleLoadError(exception: Throwable) {
        when (exception) {
            is ApiException.Unauthorized -> sendEffect(ProfileEffect.SessionExpired)
            is ApiException.NetworkError -> setState { copy(isLoading = false, error = "You're offline. Check your connection and try again.") }
            is ApiException.NotFound     -> setState { copy(isLoading = false, error = "Profile not found.") }
            is ApiException.ServerError  -> setState { copy(isLoading = false, error = "Server error. Please try again later.") }
            else                         -> setState { copy(isLoading = false, error = exception.message ?: "Failed to load profile.") }
        }
    }

    private fun handleSaveError(exception: Throwable) {
        setState { copy(isSaving = false) }
        when (exception) {
            is ApiException.Unauthorized -> sendEffect(ProfileEffect.SessionExpired)
            is ApiException.ValidationError -> {
                val firstError = exception.fieldErrors.values.firstOrNull()?.firstOrNull()
                sendEffect(ProfileEffect.ShowSnackbar(firstError ?: exception.message ?: "Validation failed."))
            }
            is ApiException.NetworkError -> sendEffect(ProfileEffect.ShowSnackbar("No internet connection. Changes were not saved."))
            is ApiException.ServerError  -> sendEffect(ProfileEffect.ShowSnackbar("Server error. Please try again."))
            else                         -> sendEffect(ProfileEffect.ShowSnackbar(exception.message ?: "Update failed."))
        }
    }
}

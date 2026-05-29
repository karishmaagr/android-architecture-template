package com.example.arch.feature.auth.presentation.register

import com.example.arch.core.ui.mvi.UiEffect
import com.example.arch.core.ui.mvi.UiIntent
import com.example.arch.core.ui.mvi.UiState

data class RegisterState(
    val name: String           = "",
    val email: String          = "",
    val password: String       = "",
    val confirmPassword: String = "",
    val isLoading: Boolean     = false,
    val nameError: String?     = null,
    val emailError: String?    = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String?  = null,
) : UiState {
    val isFormValid: Boolean
        get() = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
}

sealed interface RegisterIntent : UiIntent {
    data class NameChanged(val name: String)                       : RegisterIntent
    data class EmailChanged(val email: String)                     : RegisterIntent
    data class PasswordChanged(val password: String)               : RegisterIntent
    data class ConfirmPasswordChanged(val password: String)        : RegisterIntent
    data object RegisterClicked                                    : RegisterIntent
    data object NavigateToLogin                                    : RegisterIntent
}

sealed interface RegisterEffect : UiEffect {
    data object NavigateToHome  : RegisterEffect
    data object NavigateToLogin : RegisterEffect
    data class ShowSnackbar(val message: String) : RegisterEffect
}

package com.example.arch.feature.auth.presentation.login

import com.example.arch.core.ui.mvi.UiEffect
import com.example.arch.core.ui.mvi.UiIntent
import com.example.arch.core.ui.mvi.UiState

// --- State ---
data class LoginState(
    val email: String        = "",
    val password: String     = "",
    val isLoading: Boolean   = false,
    val emailError: String?  = null,
    val passwordError: String? = null,
    val generalError: String? = null,
) : UiState {
    val isFormValid: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}

// --- Intent (user actions) ---
sealed interface LoginIntent : UiIntent {
    data class EmailChanged(val email: String)       : LoginIntent
    data class PasswordChanged(val password: String) : LoginIntent
    data object LoginClicked                         : LoginIntent
    data object ForgotPasswordClicked               : LoginIntent
    data object NavigateToRegister                  : LoginIntent
}

// --- Effect (one-shot side effects) ---
sealed interface LoginEffect : UiEffect {
    data object NavigateToHome           : LoginEffect
    data object NavigateToRegister       : LoginEffect
    data object NavigateToForgotPassword : LoginEffect
    data class ShowSnackbar(val message: String) : LoginEffect
}

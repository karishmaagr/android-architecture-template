package com.example.arch.feature.auth.presentation.login

import androidx.lifecycle.viewModelScope
import com.example.arch.core.common.extensions.isValidEmail
import com.example.arch.core.common.extensions.isValidPassword
import com.example.arch.core.common.result.Result
import com.example.arch.core.ui.mvi.MviViewModel
import com.example.arch.feature.auth.domain.model.AuthCredentials
import com.example.arch.feature.auth.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : MviViewModel<LoginState, LoginIntent, LoginEffect>() {

    override fun createInitialState() = LoginState()

    override fun handleIntent(intent: LoginIntent) = when (intent) {
        is LoginIntent.EmailChanged    -> setState { copy(email = intent.email, emailError = null) }
        is LoginIntent.PasswordChanged -> setState { copy(password = intent.password, passwordError = null) }
        is LoginIntent.LoginClicked    -> submitLogin()
        is LoginIntent.NavigateToRegister       -> sendEffect(LoginEffect.NavigateToRegister)
        is LoginIntent.ForgotPasswordClicked    -> sendEffect(LoginEffect.NavigateToForgotPassword)
    }

    private fun submitLogin() {
        if (!validateInputs()) return

        viewModelScope.launch {
            setState { copy(isLoading = true, generalError = null) }

            val result = loginUseCase(
                AuthCredentials(
                    email    = currentState.email.trim(),
                    password = currentState.password,
                )
            )

            when (result) {
                is Result.Success -> {
                    setState { copy(isLoading = false) }
                    sendEffect(LoginEffect.NavigateToHome)
                }
                is Result.Error -> {
                    setState {
                        copy(
                            isLoading    = false,
                            generalError = result.message ?: "Login failed. Please try again.",
                        )
                    }
                    sendEffect(LoginEffect.ShowSnackbar(result.message ?: "Login failed"))
                }
                is Result.Loading -> Unit
            }
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true
        val state = currentState

        if (!state.email.isValidEmail()) {
            setState { copy(emailError = "Please enter a valid email address") }
            valid = false
        }
        if (!state.password.isValidPassword()) {
            setState { copy(passwordError = "Password must be at least 8 characters with letters and numbers") }
            valid = false
        }
        return valid
    }
}

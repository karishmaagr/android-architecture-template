package com.example.arch.feature.auth.presentation.register

import androidx.lifecycle.viewModelScope
import com.example.arch.core.common.extensions.isValidEmail
import com.example.arch.core.common.extensions.isValidPassword
import com.example.arch.core.common.result.Result
import com.example.arch.core.ui.mvi.MviViewModel
import com.example.arch.feature.auth.domain.model.RegisterCredentials
import com.example.arch.feature.auth.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
) : MviViewModel<RegisterState, RegisterIntent, RegisterEffect>() {

    override fun createInitialState() = RegisterState()

    override fun handleIntent(intent: RegisterIntent) = when (intent) {
        is RegisterIntent.NameChanged            -> setState { copy(name = intent.name, nameError = null) }
        is RegisterIntent.EmailChanged           -> setState { copy(email = intent.email, emailError = null) }
        is RegisterIntent.PasswordChanged        -> setState { copy(password = intent.password, passwordError = null) }
        is RegisterIntent.ConfirmPasswordChanged -> setState { copy(confirmPassword = intent.password, confirmPasswordError = null) }
        is RegisterIntent.RegisterClicked        -> submitRegister()
        is RegisterIntent.NavigateToLogin        -> sendEffect(RegisterEffect.NavigateToLogin)
    }

    private fun submitRegister() {
        if (!validateInputs()) return

        viewModelScope.launch {
            setState { copy(isLoading = true, generalError = null) }

            val result = registerUseCase(
                RegisterCredentials(
                    name     = currentState.name.trim(),
                    email    = currentState.email.trim(),
                    password = currentState.password,
                )
            )

            when (result) {
                is Result.Success -> {
                    setState { copy(isLoading = false) }
                    sendEffect(RegisterEffect.NavigateToHome)
                }
                is Result.Error -> {
                    setState { copy(isLoading = false, generalError = result.message ?: "Registration failed") }
                    sendEffect(RegisterEffect.ShowSnackbar(result.message ?: "Registration failed"))
                }
                is Result.Loading -> Unit
            }
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true
        val s = currentState

        if (s.name.isBlank()) {
            setState { copy(nameError = "Name must not be empty") }
            valid = false
        }
        if (!s.email.isValidEmail()) {
            setState { copy(emailError = "Please enter a valid email") }
            valid = false
        }
        if (!s.password.isValidPassword()) {
            setState { copy(passwordError = "Password must be at least 8 characters with letters and numbers") }
            valid = false
        }
        if (s.password != s.confirmPassword) {
            setState { copy(confirmPasswordError = "Passwords do not match") }
            valid = false
        }
        return valid
    }
}

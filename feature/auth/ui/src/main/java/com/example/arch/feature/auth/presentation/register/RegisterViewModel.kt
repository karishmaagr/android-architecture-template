package com.example.arch.feature.auth.presentation.register

import androidx.lifecycle.viewModelScope
import com.example.arch.core.common.extensions.isValidEmail
import com.example.arch.core.common.extensions.isValidPassword
import com.example.arch.core.common.result.ApiException
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
            setState { copy(isLoading = true, generalError = null, nameError = null, emailError = null, passwordError = null) }
            val credentials = RegisterCredentials(name = currentState.name.trim(), email = currentState.email.trim(), password = currentState.password)
            val result = registerUseCase(credentials)
            when (result) {
                is Result.Success -> {
                    setState { copy(isLoading = false) }
                    sendEffect(RegisterEffect.NavigateToHome)
                }
                is Result.Error   -> handleRegisterError(result.exception)
                is Result.Loading -> Unit
            }
        }
    }

    private fun handleRegisterError(exception: Throwable) {
        when (exception) {
            is ApiException.Conflict ->
                setState { copy(isLoading = false, emailError = "An account with this email already exists.") }

            is ApiException.ValidationError ->
                setState {
                    copy(
                        isLoading     = false,
                        nameError     = exception.fieldErrors["name"]?.firstOrNull(),
                        emailError    = exception.fieldErrors["email"]?.firstOrNull(),
                        passwordError = exception.fieldErrors["password"]?.firstOrNull(),
                        generalError  = if (exception.fieldErrors.isEmpty()) exception.message else null,
                    )
                }

            is ApiException.NetworkError -> {
                setState { copy(isLoading = false) }
                sendEffect(RegisterEffect.ShowSnackbar("No internet connection. Please check your network."))
            }

            is ApiException.TooManyRequests ->
                setState { copy(isLoading = false, generalError = "Too many attempts. Please wait before trying again.") }

            is ApiException.ServerError ->
                setState { copy(isLoading = false, generalError = "Server error. Please try again later.") }

            else ->
                setState { copy(isLoading = false, generalError = exception.message ?: "Registration failed. Please try again.") }
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true
        if (currentState.name.isBlank()) {
            setState { copy(nameError = "Name must not be empty") }
            valid = false
        }
        if (!currentState.email.isValidEmail()) {
            setState { copy(emailError = "Please enter a valid email") }
            valid = false
        }
        if (!currentState.password.isValidPassword()) {
            setState { copy(passwordError = "Password must be at least 8 characters with letters and numbers") }
            valid = false
        }
        if (currentState.password != currentState.confirmPassword) {
            setState { copy(confirmPasswordError = "Passwords do not match") }
            valid = false
        }
        return valid
    }
}

package com.example.arch.feature.auth.presentation.login

import androidx.lifecycle.viewModelScope
import com.example.arch.core.common.extensions.isValidEmail
import com.example.arch.core.common.extensions.isValidPassword
import com.example.arch.core.common.result.ApiException
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
        is LoginIntent.EmailChanged          -> setState { copy(email = intent.email, emailError = null) }
        is LoginIntent.PasswordChanged       -> setState { copy(password = intent.password, passwordError = null) }
        is LoginIntent.LoginClicked          -> submitLogin()
        is LoginIntent.NavigateToRegister    -> sendEffect(LoginEffect.NavigateToRegister)
        is LoginIntent.ForgotPasswordClicked -> sendEffect(LoginEffect.NavigateToForgotPassword)
    }

    private fun submitLogin() {
        if (!validateInputs()) return
        viewModelScope.launch {
            setState { copy(isLoading = true, generalError = null, emailError = null, passwordError = null) }
            val credentials = AuthCredentials(email = currentState.email.trim(), password = currentState.password)
            val result = loginUseCase(credentials)
            when (result) {
                is Result.Success -> {
                    setState { copy(isLoading = false) }
                    sendEffect(LoginEffect.NavigateToHome)
                }
                is Result.Error   -> handleLoginError(result.exception)
                is Result.Loading -> Unit
            }
        }
    }

    private fun handleLoginError(exception: Throwable) {
        when (exception) {
            is ApiException.Unauthorized ->
                setState { copy(isLoading = false, generalError = "Invalid email or password.") }

            is ApiException.ValidationError ->
                setState {
                    copy(
                        isLoading     = false,
                        emailError    = exception.fieldErrors["email"]?.firstOrNull(),
                        passwordError = exception.fieldErrors["password"]?.firstOrNull(),
                        generalError  = if (exception.fieldErrors.isEmpty()) exception.message else null,
                    )
                }

            is ApiException.TooManyRequests ->
                setState { copy(isLoading = false, generalError = "Too many login attempts. Please wait before trying again.") }

            is ApiException.NetworkError -> {
                setState { copy(isLoading = false) }
                sendEffect(LoginEffect.ShowSnackbar("No internet connection. Please check your network."))
            }

            is ApiException.ServerError ->
                setState { copy(isLoading = false, generalError = "Server error. Please try again later.") }

            else ->
                setState { copy(isLoading = false, generalError = exception.message ?: "Login failed. Please try again.") }
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true
        if (!currentState.email.isValidEmail()) {
            setState { copy(emailError = "Please enter a valid email address") }
            valid = false
        }
        if (!currentState.password.isValidPassword()) {
            setState { copy(passwordError = "Password must be at least 8 characters with letters and numbers") }
            valid = false
        }
        return valid
    }
}

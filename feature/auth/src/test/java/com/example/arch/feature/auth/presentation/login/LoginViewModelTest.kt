package com.example.arch.feature.auth.presentation.login

import app.cash.turbine.test
import com.example.arch.core.common.coroutines.TestDispatcherProvider
import com.example.arch.core.common.result.Result
import com.example.arch.feature.auth.domain.model.AuthCredentials
import com.example.arch.feature.auth.domain.model.User
import com.example.arch.feature.auth.domain.usecase.LoginUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val loginUseCase   = mockk<LoginUseCase>()
    private lateinit var viewModel: LoginViewModel

    private val validUser = User(
        id = "1", email = "test@example.com", name = "Test", avatarUrl = null, createdAt = 0L,
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `initial state is empty and not loading`() {
        val state = viewModel.uiState.value
        assertThat(state.email).isEmpty()
        assertThat(state.password).isEmpty()
        assertThat(state.isLoading).isFalse()
    }

    @Test
    fun `EmailChanged updates email in state`() {
        viewModel.onIntent(LoginIntent.EmailChanged("user@test.com"))
        assertThat(viewModel.uiState.value.email).isEqualTo("user@test.com")
    }

    @Test
    fun `PasswordChanged updates password in state`() {
        viewModel.onIntent(LoginIntent.PasswordChanged("pass123"))
        assertThat(viewModel.uiState.value.password).isEqualTo("pass123")
    }

    @Test
    fun `LoginClicked with invalid email sets emailError`() = runTest(testDispatcher) {
        viewModel.onIntent(LoginIntent.EmailChanged("not-an-email"))
        viewModel.onIntent(LoginIntent.PasswordChanged("password1"))
        viewModel.onIntent(LoginIntent.LoginClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.emailError).isNotNull()
    }

    @Test
    fun `successful login emits NavigateToHome effect`() = runTest(testDispatcher) {
        coEvery { loginUseCase(any<AuthCredentials>()) } returns Result.Success(validUser)

        viewModel.effect.test {
            viewModel.onIntent(LoginIntent.EmailChanged("user@test.com"))
            viewModel.onIntent(LoginIntent.PasswordChanged("password1"))
            viewModel.onIntent(LoginIntent.LoginClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(awaitItem()).isInstanceOf(LoginEffect.NavigateToHome::class.java)
        }
    }

    @Test
    fun `failed login emits ShowSnackbar effect and sets generalError`() = runTest(testDispatcher) {
        coEvery { loginUseCase(any<AuthCredentials>()) } returns
            Result.Error(RuntimeException("Bad credentials"), "Bad credentials")

        viewModel.effect.test {
            viewModel.onIntent(LoginIntent.EmailChanged("user@test.com"))
            viewModel.onIntent(LoginIntent.PasswordChanged("password1"))
            viewModel.onIntent(LoginIntent.LoginClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertThat(effect).isInstanceOf(LoginEffect.ShowSnackbar::class.java)
        }

        assertThat(viewModel.uiState.value.generalError).isNotNull()
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `NavigateToRegister emits NavigateToRegister effect`() = runTest(testDispatcher) {
        viewModel.effect.test {
            viewModel.onIntent(LoginIntent.NavigateToRegister)
            assertThat(awaitItem()).isInstanceOf(LoginEffect.NavigateToRegister::class.java)
        }
    }
}

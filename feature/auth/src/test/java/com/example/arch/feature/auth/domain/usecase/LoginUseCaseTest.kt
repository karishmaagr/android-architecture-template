package com.example.arch.feature.auth.domain.usecase

import com.example.arch.core.common.coroutines.TestDispatcherProvider
import com.example.arch.core.common.result.Result
import com.example.arch.feature.auth.domain.model.AuthCredentials
import com.example.arch.feature.auth.domain.model.User
import com.example.arch.feature.auth.domain.repository.AuthRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers    = TestDispatcherProvider(testDispatcher)
    private val repository     = mockk<AuthRepository>()
    private lateinit var useCase: LoginUseCase

    private val validUser = User(
        id = "1", email = "test@example.com", name = "Test User", avatarUrl = null, createdAt = 0L,
    )

    @Before
    fun setUp() { useCase = LoginUseCase(repository, dispatchers) }

    @Test
    fun `returns Success when repository login succeeds`() = runTest(testDispatcher) {
        val creds = AuthCredentials("test@example.com", "password1")
        coEvery { repository.login(creds) } returns Result.Success(validUser)

        val result = useCase(creds)

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo(validUser)
        coVerify(exactly = 1) { repository.login(creds) }
    }

    @Test
    fun `returns Error when repository returns Error`() = runTest(testDispatcher) {
        val creds = AuthCredentials("test@example.com", "password1")
        val error = RuntimeException("Invalid credentials")
        coEvery { repository.login(creds) } returns Result.Error(error)

        val result = useCase(creds)

        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `returns Error when email is blank`() = runTest(testDispatcher) {
        val creds = AuthCredentials("", "password1")
        val result = useCase(creds)
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun `returns Error when password is blank`() = runTest(testDispatcher) {
        val creds = AuthCredentials("test@example.com", "")
        val result = useCase(creds)
        assertThat(result).isInstanceOf(Result.Error::class.java)
    }
}

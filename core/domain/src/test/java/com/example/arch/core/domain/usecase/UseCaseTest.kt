package com.example.arch.core.domain.usecase

import com.example.arch.core.common.coroutines.TestDispatcherProvider
import com.example.arch.core.common.result.Result
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = TestDispatcherProvider(testDispatcher)

    private inner class SuccessUseCase : UseCase<Int, String>(dispatchers) {
        override suspend fun execute(params: Int): String = "result_$params"
    }

    private inner class ThrowingUseCase : UseCase<Unit, String>(dispatchers) {
        override suspend fun execute(params: Unit): String = throw IllegalStateException("boom")
    }

    @Test
    fun `invoke returns Success on normal execution`() = runTest(testDispatcher) {
        val result = SuccessUseCase()(42)
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat((result as Result.Success).data).isEqualTo("result_42")
    }

    @Test
    fun `invoke returns Error when execution throws`() = runTest(testDispatcher) {
        val result = ThrowingUseCase()(Unit)
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat((result as Result.Error).exception).isInstanceOf(IllegalStateException::class.java)
    }
}

package com.example.arch.core.common.result

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResultTest {

    @Test
    fun `Success getOrNull returns data`() {
        val result: Result<Int> = Result.Success(42)
        assertThat(result.getOrNull()).isEqualTo(42)
    }

    @Test
    fun `Error getOrNull returns null`() {
        val result: Result<Int> = Result.Error(RuntimeException("fail"))
        assertThat(result.getOrNull()).isNull()
    }

    @Test
    fun `map transforms Success data`() {
        val result: Result<Int> = Result.Success(2)
        val mapped = result.map { it * 3 }
        assertThat((mapped as Result.Success).data).isEqualTo(6)
    }

    @Test
    fun `map preserves Error`() {
        val error = RuntimeException("oops")
        val result: Result<Int> = Result.Error(error)
        val mapped = result.map { it * 2 }
        assertThat(mapped).isInstanceOf(Result.Error::class.java)
        assertThat((mapped as Result.Error).exception).isEqualTo(error)
    }

    @Test
    fun `onSuccess callback invoked for Success`() {
        var called = false
        Result.Success("hello").onSuccess { called = true }
        assertThat(called).isTrue()
    }

    @Test
    fun `onSuccess callback not invoked for Error`() {
        var called = false
        Result.Error(RuntimeException()).onSuccess { called = true }
        assertThat(called).isFalse()
    }
}

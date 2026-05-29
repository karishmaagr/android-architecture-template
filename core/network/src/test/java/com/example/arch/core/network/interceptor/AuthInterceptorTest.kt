package com.example.arch.core.network.interceptor

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import javax.inject.Provider

class AuthInterceptorTest {

    private val server = MockWebServer()

    @Before fun setUp() { server.start() }
    @After  fun tearDown() { server.shutdown() }

    private fun buildClient(token: String?): OkHttpClient {
        val provider = mockk<Provider<String>>()
        every { provider.get() } returns (token ?: throw RuntimeException("no token"))
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(provider))
            .build()
    }

    @Test
    fun `adds Authorization header when token is present`() {
        server.enqueue(MockResponse().setResponseCode(200))
        val client = buildClient("my-token")
        client.newCall(Request.Builder().url(server.url("/")).build()).execute()
        val recorded = server.takeRequest()
        assertThat(recorded.getHeader("Authorization")).isEqualTo("Bearer my-token")
    }

    @Test
    fun `omits Authorization header when token is absent`() {
        server.enqueue(MockResponse().setResponseCode(200))
        val provider = mockk<Provider<String>>()
        every { provider.get() } throws RuntimeException("no token")
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(provider))
            .build()
        client.newCall(Request.Builder().url(server.url("/")).build()).execute()
        val recorded = server.takeRequest()
        assertThat(recorded.getHeader("Authorization")).isNull()
    }
}

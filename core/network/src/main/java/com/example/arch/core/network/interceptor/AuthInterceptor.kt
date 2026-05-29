package com.example.arch.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Provider

// Provider<String> breaks the circular dependency that would arise if we
// injected the token directly from a repository that itself needs Retrofit.
class AuthInterceptor @Inject constructor(
    private val tokenProvider: Provider<String>,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runCatching { tokenProvider.get() }.getOrNull()
        val request = chain.request().newBuilder().apply {
            if (!token.isNullOrBlank()) {
                header("Authorization", "Bearer $token")
            }
        }.build()
        return chain.proceed(request)
    }
}

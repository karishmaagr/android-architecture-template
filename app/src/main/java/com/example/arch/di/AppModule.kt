package com.example.arch.di

import com.example.arch.core.data.local.preferences.AppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Non-singleton: called fresh each time AuthInterceptor's tokenProvider.get() fires,
    // so the interceptor always reads the most recent token from DataStore.
    @Provides
    fun provideAuthToken(prefs: AppPreferences): String =
        runBlocking { prefs.authToken.firstOrNull() ?: "" }
}

package com.example.arch.core.data.di

import com.example.arch.core.common.coroutines.DefaultDispatcherProvider
import com.example.arch.core.common.coroutines.DispatcherProvider
import com.example.arch.core.data.local.preferences.AppPreferences
import com.example.arch.core.data.local.preferences.AppPreferencesImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds @Singleton
    abstract fun bindAppPreferences(impl: AppPreferencesImpl): AppPreferences

    companion object {
        @Provides @Singleton
        fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
    }
}

package com.example.arch.feature.auth.di

import com.example.arch.core.data.auth.LogoutService
import com.example.arch.feature.auth.data.local.datasource.AuthLocalDataSource
import com.example.arch.feature.auth.data.local.datasource.AuthLocalDataSourceImpl
import com.example.arch.feature.auth.data.remote.AuthApi
import com.example.arch.feature.auth.data.remote.datasource.AuthRemoteDataSource
import com.example.arch.feature.auth.data.remote.datasource.AuthRemoteDataSourceImpl
import com.example.arch.feature.auth.data.repository.AuthRepositoryImpl
import com.example.arch.feature.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindLogoutService(impl: AuthRepositoryImpl): LogoutService

    @Binds @Singleton
    abstract fun bindAuthRemoteDataSource(impl: AuthRemoteDataSourceImpl): AuthRemoteDataSource

    @Binds @Singleton
    abstract fun bindAuthLocalDataSource(impl: AuthLocalDataSourceImpl): AuthLocalDataSource

    companion object {
        @Provides @Singleton
        fun provideAuthApi(retrofit: Retrofit): AuthApi =
            retrofit.create(AuthApi::class.java)
    }
}

package com.example.arch.feature.home.di

import com.example.arch.core.data.sync.Syncable
import com.example.arch.feature.home.data.remote.HomeApi
import com.example.arch.feature.home.data.remote.datasource.HomeRemoteDataSource
import com.example.arch.feature.home.data.remote.datasource.HomeRemoteDataSourceImpl
import com.example.arch.feature.home.data.repository.HomeRepositoryImpl
import com.example.arch.feature.home.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    @Binds @Singleton
    abstract fun bindSyncable(impl: HomeRepositoryImpl): Syncable

    @Binds @Singleton
    abstract fun bindHomeRemoteDataSource(impl: HomeRemoteDataSourceImpl): HomeRemoteDataSource

    companion object {
        @Provides @Singleton
        fun provideHomeApi(retrofit: Retrofit): HomeApi = retrofit.create(HomeApi::class.java)
    }
}

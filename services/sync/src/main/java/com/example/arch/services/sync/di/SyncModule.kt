package com.example.arch.services.sync.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// SyncScheduler and SyncWorker are bound automatically by Hilt + HiltWorkerFactory.
// Add any sync-specific bindings here as the module grows.
@Module
@InstallIn(SingletonComponent::class)
object SyncModule

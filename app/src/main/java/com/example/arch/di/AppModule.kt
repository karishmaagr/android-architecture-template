package com.example.arch.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Top-level app module. Feature-specific bindings live in each feature's own DI module.
// This module wires cross-cutting concerns that span all features.
@Module
@InstallIn(SingletonComponent::class)
object AppModule

package com.example.arch.feature.auth.data.local.datasource

import com.example.arch.feature.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthLocalDataSource {
    fun getCachedUser(): Flow<User?>
    suspend fun saveUser(user: User)
    suspend fun clearUser()
}

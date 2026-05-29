package com.example.arch.feature.auth.data.local.datasource

import com.example.arch.core.data.local.preferences.AppPreferences
import com.example.arch.feature.auth.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class AuthLocalDataSourceImpl @Inject constructor(
    private val prefs: AppPreferences,
) : AuthLocalDataSource {

    // Reconstruct the User object from individual DataStore keys
    override fun getCachedUser(): Flow<User?> =
        combine(prefs.userId, prefs.authToken) { id, token ->
            if (id != null && token != null) User(
                id        = id,
                email     = "",
                name      = "",
                avatarUrl = null,
                createdAt = 0L,
            ) else null
        }

    override suspend fun saveUser(user: User) {
        prefs.saveUserId(user.id)
    }

    override suspend fun clearUser() {
        prefs.clearAuthData()
    }
}

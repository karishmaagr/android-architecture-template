package com.example.arch.core.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.arch.core.common.constants.AppConstants
import com.example.arch.core.common.constants.PreferenceKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = AppConstants.PREFS_NAME,
)

@Singleton
class AppPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AppPreferences {

    private val store = context.dataStore

    private object Keys {
        val AUTH_TOKEN      = stringPreferencesKey(PreferenceKeys.AUTH_TOKEN)
        val REFRESH_TOKEN   = stringPreferencesKey(PreferenceKeys.REFRESH_TOKEN)
        val USER_ID         = stringPreferencesKey(PreferenceKeys.USER_ID)
        val THEME_MODE      = stringPreferencesKey(PreferenceKeys.THEME_MODE)
        val ONBOARDING_DONE = booleanPreferencesKey(PreferenceKeys.ONBOARDING_DONE)
    }

    override val authToken: Flow<String?>     = store.data.map { it[Keys.AUTH_TOKEN] }
    override val refreshToken: Flow<String?>  = store.data.map { it[Keys.REFRESH_TOKEN] }
    override val userId: Flow<String?>        = store.data.map { it[Keys.USER_ID] }
    override val themeMode: Flow<String>      = store.data.map { it[Keys.THEME_MODE] ?: "system" }
    override val isOnboardingDone: Flow<Boolean> = store.data.map { it[Keys.ONBOARDING_DONE] ?: false }

    override suspend fun saveAuthToken(token: String)      { store.edit { it[Keys.AUTH_TOKEN] = token } }
    override suspend fun saveRefreshToken(token: String)   { store.edit { it[Keys.REFRESH_TOKEN] = token } }
    override suspend fun saveUserId(id: String)            { store.edit { it[Keys.USER_ID] = id } }
    override suspend fun saveThemeMode(mode: String)       { store.edit { it[Keys.THEME_MODE] = mode } }
    override suspend fun setOnboardingDone(done: Boolean)  { store.edit { it[Keys.ONBOARDING_DONE] = done } }

    override suspend fun clearAuthData() {
        store.edit { prefs ->
            prefs.remove(Keys.AUTH_TOKEN)
            prefs.remove(Keys.REFRESH_TOKEN)
            prefs.remove(Keys.USER_ID)
        }
    }
}

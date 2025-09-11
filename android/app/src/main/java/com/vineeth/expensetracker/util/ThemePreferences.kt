package com.vineeth.expensetracker.util

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val THEME_DATASTORE = "settings"

private val Context.dataStore by preferencesDataStore(name = THEME_DATASTORE)

object ThemePreferences {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    fun darkModeFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { prefs: Preferences ->
            prefs[DARK_MODE_KEY] ?: false
        }

    suspend fun setDarkMode(context: Context, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }
}



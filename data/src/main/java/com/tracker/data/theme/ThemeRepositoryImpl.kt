package com.tracker.data.theme

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.tracker.domain.theme.repository.ThemeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

/**
 * Implementation of ThemeRepository using DataStore
 */
@Singleton
class ThemeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ThemeRepository {

    private val isDarkThemeKey = booleanPreferencesKey("is_dark_theme")

    /**
     * Detect system theme based on device dark mode setting
     * This is the non-Composable equivalent of isSystemInDarkTheme()
     */
    private fun isSystemInDarkMode(): Boolean {
        val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightMode == Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * Observe the dark theme preference
     * Returns null when no preference is saved (use system default)
     */
    override val isDarkTheme: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[isDarkThemeKey]
    }

    /**
     * Toggle theme between light and dark
     */
    override suspend fun toggleTheme(currentIsDark: Boolean?) {
        val newValue = when (currentIsDark) {
            true -> false  // Dark -> Light
            false -> true  // Light -> Dark
            null -> !isSystemInDarkMode() // System -> Toggle from system state
        }
        context.dataStore.edit { preferences ->
            preferences[isDarkThemeKey] = newValue
        }
    }
}


package com.tracker.domain.theme.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for theme preferences
 */
interface ThemeRepository {
    /**
     * Observe the dark theme preference
     * Returns null when no preference is saved (use system default)
     */
    val isDarkTheme: Flow<Boolean?>

    /**
     * Toggle theme between light and dark
     */
    suspend fun toggleTheme(currentIsDark: Boolean?)
}


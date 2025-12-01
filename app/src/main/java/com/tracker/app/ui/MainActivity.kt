package com.tracker.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.tracker.app.ui.screen.TrackerScreen
import com.tracker.app.ui.theme.TrackerAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map

/**
 * Main Activity with Hilt injection
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            val isDarkTheme by mainViewModel.isDarkTheme
                .map { it ?: systemInDarkTheme }
                .collectAsState(initial = systemInDarkTheme)

            TrackerAppTheme(isDarkTheme = isDarkTheme) {
                TrackerScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { mainViewModel.toggleTheme() }
                )
            }
        }
    }
}


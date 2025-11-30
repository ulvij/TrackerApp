package com.tracker.app.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tracker.app.state.PriceTrackerState
import com.tracker.app.ui.components.StockListItem
import com.tracker.app.ui.components.TopBar
import com.tracker.app.ui.theme.ThemeViewModel

/**
 * Main screen for the Price Tracker app
 */
@Composable
fun PriceTrackerScreen(
    viewModel: PriceTrackerViewModel,
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    PriceTrackerContent(
        state = state,
        onToggleTracking = { viewModel.toggleTracking() },
        isDarkTheme = isDarkTheme,
        onThemeToggle = { themeViewModel.toggleTheme() },
        modifier = modifier
    )
}

@Composable
fun PriceTrackerContent(
    state: PriceTrackerState,
    onToggleTracking: () -> Unit,
    isDarkTheme: Boolean?,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopBar(
                connectionState = state.connectionState,
                onToggleTracking = onToggleTracking,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.stocks.isEmpty() && state.connectionState.isConnected.not() -> {
                    // Empty state
                    Text(
                        text = "Press Start to begin tracking stock prices",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp)
                    )
                }

                state.stocks.isEmpty() && state.connectionState.isConnected -> {
                    // Loading state
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    // Stock list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = state.stocks,
                            key = { it.symbol }
                        ) { stock ->
                            StockListItem(stock = stock)
                        }
                    }
                }
            }
        }
    }
}


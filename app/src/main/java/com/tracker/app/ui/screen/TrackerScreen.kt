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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tracker.app.R
import com.tracker.app.ui.TestTags
import com.tracker.app.ui.components.StockListItem
import com.tracker.app.ui.components.TopBar

/**
 * Main screen for the Price Tracker app
 */
@Composable
fun TrackerScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrackerViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    // Memoize the callback to prevent unnecessary recompositions
    val onConnectionToggle = remember(viewModel) {
        { viewModel.toggleTracking() }
    }

    TrackerContent(
        state = state,
        onConnectionToggle = onConnectionToggle,
        isDarkTheme = isDarkTheme,
        onThemeToggle = onThemeToggle,
        modifier = modifier
    )
}

@Composable
fun TrackerContent(
    state: TrackerUIState,
    isDarkTheme: Boolean,
    onConnectionToggle: () -> Unit,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopBar(
                connectionState = state.connectionState,
                onConnectionToggle = onConnectionToggle,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        },
        modifier = modifier
            .fillMaxSize()
            .testTag(TestTags.TRACKER_SCREEN)
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
                        text = stringResource(R.string.empty_message),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp)
                            .testTag(TestTags.EMPTY_MESSAGE)
                    )
                }

                state.stocks.isEmpty() && state.connectionState.isConnected -> {
                    // Loading state
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag(TestTags.LOADING_INDICATOR)
                    )
                }

                else -> {
                    // Stock list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag(TestTags.STOCK_LIST)
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


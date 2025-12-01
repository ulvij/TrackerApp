package com.tracker.app.ui.screen

import androidx.compose.runtime.Immutable
import com.tracker.app.ui.model.StockUIModel
import com.tracker.domain.connection.model.ConnectionState

/**
 * UI state for the Tracker screen
 * @Immutable annotation enables smart recomposition
 */
@Immutable
data class TrackerUIState(
    val stocks: List<StockUIModel> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.Disconnected
)
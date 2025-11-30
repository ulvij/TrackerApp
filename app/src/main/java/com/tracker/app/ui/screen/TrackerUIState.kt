package com.tracker.app.ui.screen

import com.tracker.domain.connection.model.ConnectionState
import com.tracker.domain.stock.model.Stock

/**
 * UI state for the Tracker screen
 */
data class TrackerUIState(
    val stocks: List<Stock> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.Disconnected
)
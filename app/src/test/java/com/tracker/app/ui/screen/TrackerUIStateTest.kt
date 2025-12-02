package com.tracker.app.ui.screen

import com.tracker.app.ui.model.StockUIModel
import com.tracker.domain.connection.model.ConnectionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Comprehensive test cases for TrackerUIState
 * Tests cover default values, immutability, state changes, and edge cases
 */
class TrackerUIStateTest {

    // ========== Default Values Tests ==========

    @Test
    fun `default state should have empty stocks list`() {
        // Given & When
        val state = TrackerUIState()

        // Then
        assertTrue(state.stocks.isEmpty())
    }

    @Test
    fun `default state should have disconnected connection state`() {
        // Given & When
        val state = TrackerUIState()

        // Then
        assertEquals(ConnectionState.Disconnected, state.connectionState)
        assertFalse(state.connectionState.isConnected)
    }

    // ========== State Creation Tests ==========

    @Test
    fun `should create state with custom stocks`() {
        // Given
        val stocks = listOf(
            StockUIModel(
                symbol = "AAPL",
                currentPrice = 150.0,
                previousPrice = 145.0,
                priceChange = 5.0,
                priceChangePercentage = 3.45,
                isPriceIncreased = true,
                logoUrl = "logo1",
                timestamp = 1000L
            )
        )

        // When
        val state = TrackerUIState(stocks = stocks)

        // Then
        assertEquals(1, state.stocks.size)
        assertEquals("AAPL", state.stocks[0].symbol)
    }

    @Test
    fun `should create state with custom connection state`() {
        // Given
        val connectionState = ConnectionState.Connected

        // When
        val state = TrackerUIState(connectionState = connectionState)

        // Then
        assertEquals(ConnectionState.Connected, state.connectionState)
        assertTrue(state.connectionState.isConnected)
    }

    @Test
    fun `should create state with both custom values`() {
        // Given
        val stocks = listOf(
            StockUIModel(
                symbol = "GOOG",
                currentPrice = 2800.0,
                previousPrice = 2750.0,
                priceChange = 50.0,
                priceChangePercentage = 1.82,
                isPriceIncreased = true,
                logoUrl = "logo2",
                timestamp = 2000L
            )
        )
        val connectionState = ConnectionState.Connecting

        // When
        val state = TrackerUIState(
            stocks = stocks,
            connectionState = connectionState
        )

        // Then
        assertEquals(1, state.stocks.size)
        assertEquals("GOOG", state.stocks[0].symbol)
        assertEquals(ConnectionState.Connecting, state.connectionState)
    }

    // ========== Immutability Tests ==========

    @Test
    fun `copy should create new instance with modified stocks`() {
        // Given
        val originalState = TrackerUIState()
        val newStocks = listOf(
            StockUIModel(
                symbol = "MSFT",
                currentPrice = 300.0,
                previousPrice = 295.0,
                priceChange = 5.0,
                priceChangePercentage = 1.69,
                isPriceIncreased = true,
                logoUrl = "logo3",
                timestamp = 3000L
            )
        )

        // When
        val newState = originalState.copy(stocks = newStocks)

        // Then
        assertTrue(originalState.stocks.isEmpty())
        assertEquals(1, newState.stocks.size)
        assertNotEquals(originalState, newState)
    }

    @Test
    fun `copy should create new instance with modified connection state`() {
        // Given
        val originalState = TrackerUIState()

        // When
        val newState = originalState.copy(connectionState = ConnectionState.Connected)

        // Then
        assertEquals(ConnectionState.Disconnected, originalState.connectionState)
        assertEquals(ConnectionState.Connected, newState.connectionState)
        assertNotEquals(originalState, newState)
    }

    @Test
    fun `copy should preserve unchanged properties`() {
        // Given
        val stocks = listOf(
            StockUIModel(
                symbol = "TSLA",
                currentPrice = 250.0,
                previousPrice = 240.0,
                priceChange = 10.0,
                priceChangePercentage = 4.17,
                isPriceIncreased = true,
                logoUrl = "logo4",
                timestamp = 4000L
            )
        )
        val originalState = TrackerUIState(stocks = stocks)

        // When
        val newState = originalState.copy(connectionState = ConnectionState.Connected)

        // Then
        assertEquals(stocks, newState.stocks)
        assertEquals(ConnectionState.Connected, newState.connectionState)
    }

    // ========== Equality Tests ==========

    @Test
    fun `two states with same values should be equal`() {
        // Given
        val stocks = listOf(
            StockUIModel(
                symbol = "AAPL",
                currentPrice = 150.0,
                previousPrice = 145.0,
                priceChange = 5.0,
                priceChangePercentage = 3.45,
                isPriceIncreased = true,
                logoUrl = "logo",
                timestamp = 1000L
            )
        )
        val state1 = TrackerUIState(stocks = stocks, connectionState = ConnectionState.Connected)
        val state2 = TrackerUIState(stocks = stocks, connectionState = ConnectionState.Connected)

        // Then
        assertEquals(state1, state2)
        assertEquals(state1.hashCode(), state2.hashCode())
    }

    @Test
    fun `two states with different stocks should not be equal`() {
        // Given
        val stocks1 = listOf(
            StockUIModel(
                symbol = "AAPL",
                currentPrice = 150.0,
                previousPrice = 145.0,
                priceChange = 5.0,
                priceChangePercentage = 3.45,
                isPriceIncreased = true,
                logoUrl = "logo1",
                timestamp = 1000L
            )
        )
        val stocks2 = listOf(
            StockUIModel(
                symbol = "GOOG",
                currentPrice = 2800.0,
                previousPrice = 2750.0,
                priceChange = 50.0,
                priceChangePercentage = 1.82,
                isPriceIncreased = true,
                logoUrl = "logo2",
                timestamp = 2000L
            )
        )
        val state1 = TrackerUIState(stocks = stocks1)
        val state2 = TrackerUIState(stocks = stocks2)

        // Then
        assertNotEquals(state1, state2)
    }

    @Test
    fun `two states with different connection states should not be equal`() {
        // Given
        val state1 = TrackerUIState(connectionState = ConnectionState.Connected)
        val state2 = TrackerUIState(connectionState = ConnectionState.Disconnected)

        // Then
        assertNotEquals(state1, state2)
    }

    // ========== Connection State Variations Tests ==========

    @Test
    fun `should handle connected state`() {
        // Given & When
        val state = TrackerUIState(connectionState = ConnectionState.Connected)

        // Then
        assertEquals(ConnectionState.Connected, state.connectionState)
        assertTrue(state.connectionState.isConnected)
    }

    @Test
    fun `should handle connecting state`() {
        // Given & When
        val state = TrackerUIState(connectionState = ConnectionState.Connecting)

        // Then
        assertEquals(ConnectionState.Connecting, state.connectionState)
        assertFalse(state.connectionState.isConnected)
    }

    @Test
    fun `should handle disconnected state`() {
        // Given & When
        val state = TrackerUIState(connectionState = ConnectionState.Disconnected)

        // Then
        assertEquals(ConnectionState.Disconnected, state.connectionState)
        assertFalse(state.connectionState.isConnected)
    }

    @Test
    fun `should handle error state`() {
        // Given
        val errorMessage = "Connection failed"

        // When
        val state = TrackerUIState(connectionState = ConnectionState.Error(errorMessage))

        // Then
        assertTrue(state.connectionState is ConnectionState.Error)
        assertEquals(errorMessage, (state.connectionState as ConnectionState.Error).message)
        assertFalse(state.connectionState.isConnected)
    }

    // ========== Stock List Variations Tests ==========

    @Test
    fun `should handle empty stock list`() {
        // Given & When
        val state = TrackerUIState(stocks = emptyList())

        // Then
        assertTrue(state.stocks.isEmpty())
        assertEquals(0, state.stocks.size)
    }

    @Test
    fun `should handle single stock`() {
        // Given
        val stocks = listOf(
            StockUIModel(
                symbol = "AAPL",
                currentPrice = 150.0,
                previousPrice = 145.0,
                priceChange = 5.0,
                priceChangePercentage = 3.45,
                isPriceIncreased = true,
                logoUrl = "logo",
                timestamp = 1000L
            )
        )

        // When
        val state = TrackerUIState(stocks = stocks)

        // Then
        assertEquals(1, state.stocks.size)
        assertEquals("AAPL", state.stocks[0].symbol)
    }

    @Test
    fun `should handle multiple stocks`() {
        // Given
        val stocks = listOf(
            StockUIModel("AAPL", "logo1", 150.0, 145.0, 5.0, 3.45, true, 1000L),
            StockUIModel("GOOG", "logo2", 2800.0, 2750.0, 50.0, 1.82, true, 2000L),
            StockUIModel("MSFT", "logo3", 300.0, 295.0, 5.0, 1.69, true, 3000L)
        )

        // When
        val state = TrackerUIState(stocks = stocks)

        // Then
        assertEquals(3, state.stocks.size)
        assertEquals("AAPL", state.stocks[0].symbol)
        assertEquals("GOOG", state.stocks[1].symbol)
        assertEquals("MSFT", state.stocks[2].symbol)
    }

    @Test
    fun `should handle large stock list`() {
        // Given
        val largeStockList = (1..100).map { index ->
            StockUIModel(
                symbol = "STOCK$index",
                logoUrl = "logo$index",
                currentPrice = 100.0 + index,
                previousPrice = 100.0,
                priceChange = index.toDouble(),
                priceChangePercentage = index.toDouble(),
                isPriceIncreased = true,
                timestamp = index.toLong()
            )
        }

        // When
        val state = TrackerUIState(stocks = largeStockList)

        // Then
        assertEquals(100, state.stocks.size)
        assertEquals("STOCK1", state.stocks[0].symbol)
        assertEquals("STOCK100", state.stocks[99].symbol)
    }

    // ========== Edge Cases Tests ==========

    @Test
    fun `should handle stocks with mixed price movements`() {
        // Given
        val stocks = listOf(
            StockUIModel("UP", "logo1", 150.0, 145.0, 5.0, 3.45, true, 1000L),
            StockUIModel("DOWN", "logo2", 145.0, 150.0, -5.0, -3.33, false, 2000L),
            StockUIModel("SAME", "logo3", 100.0, 100.0, 0.0, 0.0, false, 3000L)
        )

        // When
        val state = TrackerUIState(stocks = stocks)

        // Then
        assertEquals(3, state.stocks.size)
        assertTrue(state.stocks[0].isPriceIncreased)
        assertFalse(state.stocks[1].isPriceIncreased)
        assertFalse(state.stocks[2].isPriceIncreased)
    }

    @Test
    fun `should preserve stock order`() {
        // Given
        val stocks = listOf(
            StockUIModel("ZZZZ", "logo1", 100.0, 90.0, 10.0, 11.11, true, 1000L),
            StockUIModel("AAAA", "logo2", 200.0, 190.0, 10.0, 5.26, true, 2000L),
            StockUIModel("MMMM", "logo3", 300.0, 290.0, 10.0, 3.45, true, 3000L)
        )

        // When
        val state = TrackerUIState(stocks = stocks)

        // Then
        assertEquals("ZZZZ", state.stocks[0].symbol)
        assertEquals("AAAA", state.stocks[1].symbol)
        assertEquals("MMMM", state.stocks[2].symbol)
    }

    // ========== State Transition Tests ==========

    @Test
    fun `should support disconnected to connecting transition`() {
        // Given
        val initialState = TrackerUIState(connectionState = ConnectionState.Disconnected)

        // When
        val newState = initialState.copy(connectionState = ConnectionState.Connecting)

        // Then
        assertEquals(ConnectionState.Disconnected, initialState.connectionState)
        assertEquals(ConnectionState.Connecting, newState.connectionState)
    }

    @Test
    fun `should support connecting to connected transition`() {
        // Given
        val initialState = TrackerUIState(connectionState = ConnectionState.Connecting)

        // When
        val newState = initialState.copy(connectionState = ConnectionState.Connected)

        // Then
        assertEquals(ConnectionState.Connecting, initialState.connectionState)
        assertEquals(ConnectionState.Connected, newState.connectionState)
    }

    @Test
    fun `should support connected to error transition`() {
        // Given
        val initialState = TrackerUIState(connectionState = ConnectionState.Connected)
        val errorMessage = "Connection lost"

        // When
        val newState = initialState.copy(connectionState = ConnectionState.Error(errorMessage))

        // Then
        assertEquals(ConnectionState.Connected, initialState.connectionState)
        assertTrue(newState.connectionState is ConnectionState.Error)
        assertEquals(errorMessage, (newState.connectionState as ConnectionState.Error).message)
    }

    @Test
    fun `should support adding stocks while maintaining connection state`() {
        // Given
        val initialState = TrackerUIState(connectionState = ConnectionState.Connected)
        val newStocks = listOf(
            StockUIModel("AAPL", "logo", 150.0, 145.0, 5.0, 3.45, true, 1000L)
        )

        // When
        val newState = initialState.copy(stocks = newStocks)

        // Then
        assertTrue(initialState.stocks.isEmpty())
        assertEquals(1, newState.stocks.size)
        assertEquals(ConnectionState.Connected, newState.connectionState)
    }

    // ========== Multiple State Changes Tests ==========

    @Test
    fun `should handle multiple sequential state updates`() {
        // Given
        val state1 = TrackerUIState()

        // When
        val state2 = state1.copy(connectionState = ConnectionState.Connecting)
        val state3 = state2.copy(connectionState = ConnectionState.Connected)
        val stocks = listOf(
            StockUIModel("AAPL", "logo", 150.0, 145.0, 5.0, 3.45, true, 1000L)
        )
        val state4 = state3.copy(stocks = stocks)

        // Then
        assertEquals(ConnectionState.Disconnected, state1.connectionState)
        assertEquals(ConnectionState.Connecting, state2.connectionState)
        assertEquals(ConnectionState.Connected, state3.connectionState)
        assertEquals(ConnectionState.Connected, state4.connectionState)
        assertEquals(1, state4.stocks.size)
    }

    // ========== Data Class Features Tests ==========

    @Test
    fun `should destructure correctly`() {
        // Given
        val stocks = listOf(
            StockUIModel("AAPL", "logo", 150.0, 145.0, 5.0, 3.45, true, 1000L)
        )
        val connectionState = ConnectionState.Connected
        val state = TrackerUIState(stocks = stocks, connectionState = connectionState)

        // When
        val (extractedStocks, extractedConnectionState) = state

        // Then
        assertEquals(stocks, extractedStocks)
        assertEquals(connectionState, extractedConnectionState)
    }

    @Test
    fun `toString should include all properties`() {
        // Given
        val stocks = listOf(
            StockUIModel("AAPL", "logo", 150.0, 145.0, 5.0, 3.45, true, 1000L)
        )
        val state = TrackerUIState(stocks = stocks, connectionState = ConnectionState.Connected)

        // When
        val stringRepresentation = state.toString()

        // Then
        assertTrue(stringRepresentation.contains("stocks"))
        assertTrue(stringRepresentation.contains("connectionState"))
        assertTrue(stringRepresentation.contains("AAPL"))
        assertTrue(stringRepresentation.contains("Connected"))
    }
}


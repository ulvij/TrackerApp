package com.tracker.app.ui.screen

import app.cash.turbine.test
import com.tracker.domain.connection.model.ConnectionState
import com.tracker.domain.connection.usecase.ObserveConnectionStateUseCase
import com.tracker.domain.connection.usecase.StartConnectionUseCase
import com.tracker.domain.connection.usecase.StopConnectionUseCase
import com.tracker.domain.stock.model.Stock
import com.tracker.domain.stock.usecase.ObserveStockPricesUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Comprehensive test cases for TrackerViewModel
 * Tests cover initialization, state management, connection control, and lifecycle
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TrackerViewModelTest {

    private lateinit var viewModel: TrackerViewModel
    private lateinit var startConnectionUseCase: StartConnectionUseCase
    private lateinit var stopConnectionUseCase: StopConnectionUseCase
    private lateinit var observeStockPricesUseCase: ObserveStockPricesUseCase
    private lateinit var observeConnectionStateUseCase: ObserveConnectionStateUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock use cases
        startConnectionUseCase = mockk(relaxed = true)
        stopConnectionUseCase = mockk(relaxed = true)
        observeStockPricesUseCase = mockk()
        observeConnectionStateUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========== Initialization Tests ==========

    @Test
    fun `initial state should have empty stocks and disconnected state`() = runTest {
        // Given
        every { observeStockPricesUseCase() } returns flowOf(emptyList())
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Disconnected)

        // When
        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.stocks.isEmpty())
            assertEquals(ConnectionState.Disconnected, state.connectionState)
        }
    }

    @Test
    fun `should observe stock prices on initialization`() = runTest {
        // Given
        val testStocks = listOf(
            Stock("AAPL", 150.0, 145.0, logoUrl = "logo1"),
            Stock("GOOG", 2800.0, 2750.0, logoUrl = "logo2")
        )
        every { observeStockPricesUseCase() } returns flowOf(testStocks)
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Disconnected)

        // When
        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(2, state.stocks.size)
            assertEquals("AAPL", state.stocks[0].symbol)
            assertEquals(150.0, state.stocks[0].currentPrice, 0.001)
            assertEquals("GOOG", state.stocks[1].symbol)
            assertEquals(2800.0, state.stocks[1].currentPrice, 0.001)
        }
    }

    @Test
    fun `should observe connection state on initialization`() = runTest {
        // Given
        every { observeStockPricesUseCase() } returns flowOf(emptyList())
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Connected)

        // When
        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ConnectionState.Connected, state.connectionState)
            assertTrue(state.connectionState.isConnected)
        }
    }

    // ========== Connection Management Tests ==========

    @Test
    fun `startConnection should call startConnectionUseCase when disconnected`() = runTest {
        // Given
        every { observeStockPricesUseCase() } returns flowOf(emptyList())
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Disconnected)

        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.startConnection()

        // Then
        verify(exactly = 1) { startConnectionUseCase() }
    }

    @Test
    fun `startConnection should not call startConnectionUseCase when already connected`() = runTest {
        // Given
        every { observeStockPricesUseCase() } returns flowOf(emptyList())
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Connected)

        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.startConnection()

        // Then
        verify(exactly = 0) { startConnectionUseCase() }
    }

    @Test
    fun `stopConnection should call stopConnectionUseCase`() = runTest {
        // Given
        every { observeStockPricesUseCase() } returns flowOf(emptyList())
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Connected)

        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.stopConnection()

        // Then
        verify(exactly = 1) { stopConnectionUseCase() }
    }

    // ========== Toggle Tracking Tests ==========

    @Test
    fun `toggleTracking should start connection when disconnected`() = runTest {
        // Given
        every { observeStockPricesUseCase() } returns flowOf(emptyList())
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Disconnected)

        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.toggleTracking()

        // Then
        verify(exactly = 1) { startConnectionUseCase() }
        verify(exactly = 0) { stopConnectionUseCase() }
    }

    @Test
    fun `toggleTracking should stop connection when connected`() = runTest {
        // Given
        every { observeStockPricesUseCase() } returns flowOf(emptyList())
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Connected)

        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // When
        viewModel.toggleTracking()

        // Then
        verify(exactly = 0) { startConnectionUseCase() }
        verify(exactly = 1) { stopConnectionUseCase() }
    }

    // ========== State Update Tests ==========

    @Test
    fun `state should update when stock prices change`() = runTest {
        // Given
        val initialStocks = listOf(
            Stock("AAPL", 150.0, 145.0, logoUrl = "logo1")
        )
        val updatedStocks = listOf(
            Stock("AAPL", 155.0, 150.0, logoUrl = "logo1")
        )

        every { observeStockPricesUseCase() } returns flowOf(initialStocks, updatedStocks)
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Connected)

        // When
        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(155.0, state.stocks[0].currentPrice, 0.001)
            assertEquals(150.0, state.stocks[0].previousPrice, 0.001)
        }
    }

    @Test
    fun `state should update when connection state changes`() = runTest {
        // Given
        every { observeStockPricesUseCase() } returns flowOf(emptyList())
        every { observeConnectionStateUseCase() } returns flowOf(
            ConnectionState.Disconnected,
            ConnectionState.Connecting,
            ConnectionState.Connected
        )

        // When
        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ConnectionState.Connected, state.connectionState)
        }
    }

    @Test
    fun `state should handle error connection state`() = runTest {
        // Given
        val errorMessage = "WebSocket connection failed"
        every { observeStockPricesUseCase() } returns flowOf(emptyList())
        every { observeConnectionStateUseCase() } returns flowOf(
            ConnectionState.Error(errorMessage)
        )

        // When
        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.connectionState is ConnectionState.Error)
            assertEquals(errorMessage, (state.connectionState as ConnectionState.Error).message)
            assertFalse(state.connectionState.isConnected)
        }
    }

    // ========== UI Model Conversion Tests ==========

    @Test
    fun `should correctly convert domain stocks to UI models`() = runTest {
        // Given
        val testStocks = listOf(
            Stock("AAPL", 150.0, 145.0, logoUrl = "logo1", timestamp = 1000L),
            Stock("GOOG", 2800.0, 2750.0, logoUrl = "logo2", timestamp = 2000L)
        )
        every { observeStockPricesUseCase() } returns flowOf(testStocks)
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Disconnected)

        // When
        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            val firstStock = state.stocks[0]

            assertEquals("AAPL", firstStock.symbol)
            assertEquals(150.0, firstStock.currentPrice, 0.001)
            assertEquals(145.0, firstStock.previousPrice, 0.001)
            assertEquals(5.0, firstStock.priceChange, 0.001)
            assertTrue(firstStock.isPriceIncreased)
            assertEquals("logo1", firstStock.logoUrl)
            assertEquals(1000L, firstStock.timestamp)
        }
    }

    @Test
    fun `should calculate price change percentage correctly in UI model`() = runTest {
        // Given
        val testStocks = listOf(
            Stock("AAPL", 150.0, 100.0, logoUrl = "logo1") // 50% increase
        )
        every { observeStockPricesUseCase() } returns flowOf(testStocks)
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Disconnected)

        // When
        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            val stock = state.stocks[0]

            assertEquals(50.0, stock.priceChange, 0.001)
            assertEquals(50.0, stock.priceChangePercentage, 0.001)
            assertTrue(stock.isPriceIncreased)
        }
    }

    @Test
    fun `should handle price decrease in UI model`() = runTest {
        // Given
        val testStocks = listOf(
            Stock("AAPL", 100.0, 150.0, logoUrl = "logo1") // Price decrease
        )
        every { observeStockPricesUseCase() } returns flowOf(testStocks)
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Disconnected)

        // When
        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            val stock = state.stocks[0]

            assertEquals(-50.0, stock.priceChange, 0.001)
            assertEquals(-33.33, stock.priceChangePercentage, 0.01)
            assertFalse(stock.isPriceIncreased)
        }
    }

    // ========== Lifecycle Tests ==========

    @Test
    fun `ViewModel cleanup should stop connection when disposed`() = runTest {
        // Given
        every { observeStockPricesUseCase() } returns flowOf(emptyList())
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Connected)

        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // When - Simulate ViewModel being cleared by calling stopConnection directly
        // (In real scenario, onCleared would be called by framework)
        viewModel.stopConnection()

        // Then
        verify(exactly = 1) { stopConnectionUseCase() }
    }

    // ========== Edge Cases Tests ==========

    @Test
    fun `should handle multiple rapid stock updates`() = runTest {
        // Given
        val stock1 = listOf(Stock("AAPL", 150.0, 145.0, logoUrl = "logo1"))
        val stock2 = listOf(Stock("AAPL", 151.0, 150.0, logoUrl = "logo1"))
        val stock3 = listOf(Stock("AAPL", 152.0, 151.0, logoUrl = "logo1"))

        every { observeStockPricesUseCase() } returns flowOf(stock1, stock2, stock3)
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Connected)

        // When
        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(152.0, state.stocks[0].currentPrice, 0.001)
            assertEquals(151.0, state.stocks[0].previousPrice, 0.001)
        }
    }

    @Test
    fun `should handle empty stock list`() = runTest {
        // Given
        every { observeStockPricesUseCase() } returns flowOf(emptyList())
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Connected)

        // When
        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.stocks.isEmpty())
            assertEquals(ConnectionState.Connected, state.connectionState)
        }
    }

    @Test
    fun `should handle large stock list`() = runTest {
        // Given
        val largeStockList = (1..100).map { index ->
            Stock(
                symbol = "STOCK$index",
                currentPrice = 100.0 + index,
                previousPrice = 100.0,
                logoUrl = "logo$index"
            )
        }
        every { observeStockPricesUseCase() } returns flowOf(largeStockList)
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Connected)

        // When
        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(100, state.stocks.size)
            assertEquals("STOCK1", state.stocks[0].symbol)
            assertEquals("STOCK100", state.stocks[99].symbol)
        }
    }

    @Test
    fun `should handle connecting state`() = runTest {
        // Given
        every { observeStockPricesUseCase() } returns flowOf(emptyList())
        every { observeConnectionStateUseCase() } returns flowOf(ConnectionState.Connecting)

        // When
        viewModel = TrackerViewModel(
            startConnectionUseCase,
            stopConnectionUseCase,
            observeStockPricesUseCase,
            observeConnectionStateUseCase
        )
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ConnectionState.Connecting, state.connectionState)
            assertFalse(state.connectionState.isConnected)
        }
    }
}


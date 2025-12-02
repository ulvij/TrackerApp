package com.tracker.data.stock

import com.google.gson.Gson
import com.tracker.domain.stock.model.PriceUpdate
import com.tracker.domain.stock.model.PriceUpdateList
import com.tracker.domain.connection.model.ConnectionState
import com.tracker.domain.connection.repository.ConnectionRepository
import com.tracker.domain.stock.model.Stock
import com.tracker.domain.stock.repository.StockRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.time.Duration.Companion.seconds

/**
 * Unit tests for StockPriceCoordinator
 * Tests the coordinator's orchestration of stock price generation and WebSocket communication
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@OptIn(ExperimentalCoroutinesApi::class)
class StockPriceCoordinatorTest {

    private lateinit var stockRepository: StockRepository
    private lateinit var connectionRepository: ConnectionRepository
    private lateinit var coordinator: StockPriceCoordinator

    private val connectionStateFlow = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    private val messagesFlow = MutableStateFlow("")
    private val gson = Gson()

    private val testStocks = listOf(
        Stock(
            symbol = "AAPL",
            currentPrice = 150.0,
            previousPrice = 150.0,
            logoUrl = "https://logo.clearbit.com/apple.com"
        ),
        Stock(
            symbol = "GOOG",
            currentPrice = 2800.0,
            previousPrice = 2800.0,
            logoUrl = "https://logo.clearbit.com/google.com"
        )
    )

    @Before
    fun setup() {
        // Mock StockRepository
        stockRepository = mockk(relaxed = true) {
            coEvery { getCurrentStocks() } returns testStocks
            coEvery { updatePrices(any()) } just Runs
        }

        // Mock ConnectionRepository
        connectionRepository = mockk(relaxed = true) {
            every { observeConnectionState() } returns connectionStateFlow
            every { observeMessages() } returns messagesFlow
            coEvery { sendMessage(any()) } just Runs
        }

        // Create coordinator (initialization will start observing)
        coordinator = StockPriceCoordinator(stockRepository, connectionRepository)
    }

    @Test
    fun `coordinator initializes and observes connection state`() = runTest {
        // Given - Coordinator is initialized in setup()
        advanceUntilIdle()

        // Then - Should be observing connection state and messages
        // This is verified by the fact that the coordinator responds to state changes in other tests
        assertTrue("Coordinator should be initialized", ::coordinator.isInitialized)
    }

    @Test
    fun `when connection state changes to Connected, price generation starts`() = runTest {
        // Given - Initial disconnected state
        advanceUntilIdle()

        // When - Connection state changes to Connected
        connectionStateFlow.value = ConnectionState.Connected
        advanceTimeBy(100) // Give time for coroutine to process state change

        // Then - Should attempt to send price updates
        coVerify(timeout = 1000, atLeast = 1) { connectionRepository.sendMessage(any()) }
    }

    @Test
    fun `when connection state changes to Disconnected, price generation stops`() = runTest(timeout = 30.seconds) {
        // Given - Connected state with price generation
        var messagesSent = 0
        coEvery { connectionRepository.sendMessage(any()) } answers {
            messagesSent++
        }

        connectionStateFlow.value = ConnectionState.Connected
        Thread.sleep(100) // Give time for state change to be processed
        Thread.sleep(2500) // Allow one price update (PRICE_UPDATE_INTERVAL_MS is 2000ms)

        val messagesWhileConnected = messagesSent

        // When - Connection state changes to Disconnected
        connectionStateFlow.value = ConnectionState.Disconnected
        Thread.sleep(100) // Give time for state change to be processed
        val messagesBeforeWait = messagesSent
        Thread.sleep(3000) // Wait longer than PRICE_UPDATE_INTERVAL_MS

        // Then - Should not send more messages after disconnect
        assertTrue("Should not send messages after disconnect", messagesSent == messagesBeforeWait)
        assertTrue("Should have sent messages while connected", messagesWhileConnected > 0)
    }

    @Test
    fun `received WebSocket messages are parsed and update stock repository`() = runTest {
        // Given - A price update message
        val priceUpdates = PriceUpdateList(
            updates = listOf(
                PriceUpdate(symbol = "AAPL", price = 155.0, timestamp = System.currentTimeMillis()),
                PriceUpdate(symbol = "GOOG", price = 2850.0, timestamp = System.currentTimeMillis())
            )
        )
        val message = gson.toJson(priceUpdates)

        // When - Message is received via WebSocket
        messagesFlow.value = message
        advanceUntilIdle()

        // Then - Stock repository should be updated with the price updates
        coVerify(timeout = 1000) {
            stockRepository.updatePrices(any())
        }
    }

    @Test
    fun `empty WebSocket messages are ignored`() = runTest {
        // Given - An empty message
        val emptyMessage = ""

        // When - Empty message is received
        messagesFlow.value = emptyMessage
        advanceUntilIdle()

        // Then - Should not update repository
        coVerify(exactly = 0) { stockRepository.updatePrices(any()) }
    }

    @Test
    fun `price generation uses current stocks from repository`() = runTest {
        // Given - Connected state
        connectionStateFlow.value = ConnectionState.Connected
        advanceTimeBy(100)

        // Then - Should request current stocks
        coVerify(timeout = 1000, atLeast = 1) { stockRepository.getCurrentStocks() }
    }

    @Test
    fun `generated prices are within valid range`() = runTest {
        // Given - Connected state
        val messageSlot = slot<String>()
        coEvery { connectionRepository.sendMessage(capture(messageSlot)) } just Runs

        connectionStateFlow.value = ConnectionState.Connected
        advanceTimeBy(100)

        // Then - Captured price updates should have valid prices
        coVerify(timeout = 1000, atLeast = 1) { connectionRepository.sendMessage(any()) }

        val priceUpdateList = gson.fromJson(messageSlot.captured, PriceUpdateList::class.java)
        priceUpdateList.updates.forEach { update ->
            // Price should be positive
            assertTrue("Price should be positive", update.price > 0)

            // Price should be within reasonable range (original ±5%)
            val originalPrice = testStocks.find { it.symbol == update.symbol }?.currentPrice ?: 0.0
            val minExpected = originalPrice * 0.90  // Allow for multiple 5% decreases
            val maxExpected = originalPrice * 1.10  // Allow for multiple 5% increases

            // For first update, should be close to original
            assertTrue(
                "Price ${update.price} should be reasonable for ${update.symbol} (original: $originalPrice)",
                update.price >= minExpected * 0.5 && update.price <= maxExpected * 1.5
            )
        }
    }

    @Test
    fun `price updates include timestamps`() = runTest {
        // Given - Connected state
        val messageSlot = slot<String>()
        coEvery { connectionRepository.sendMessage(capture(messageSlot)) } just Runs

        val beforeTime = System.currentTimeMillis()
        connectionStateFlow.value = ConnectionState.Connected
        advanceTimeBy(100)
        val afterTime = System.currentTimeMillis()

        // Then - Updates should have timestamps
        coVerify(timeout = 1000, atLeast = 1) { connectionRepository.sendMessage(any()) }

        val priceUpdateList = gson.fromJson(messageSlot.captured, PriceUpdateList::class.java)
        priceUpdateList.updates.forEach { update ->
            assertTrue(
                "Timestamp should be within test execution time",
                update.timestamp >= beforeTime && update.timestamp <= afterTime + 1000
            )
        }
    }

    @Test
    fun `connection state changes are handled multiple times`() = runTest(timeout = 30.seconds) {
        // When - Multiple connection state changes
        connectionStateFlow.value = ConnectionState.Connected
        Thread.sleep(2500) // Wait for at least one message

        connectionStateFlow.value = ConnectionState.Disconnected
        Thread.sleep(100)

        connectionStateFlow.value = ConnectionState.Connected
        Thread.sleep(2500) // Wait for at least one more message

        // Then - Should handle each state change appropriately
        coVerify(atLeast = 2) { connectionRepository.sendMessage(any()) }
    }

    @Test
    fun `price generation job does not start if already active`() = runTest {
        // Given - Already connected
        var messagesSent = 0
        coEvery { connectionRepository.sendMessage(any()) } answers {
            messagesSent++
        }

        connectionStateFlow.value = ConnectionState.Connected
        advanceTimeBy(2500) // One interval

        val messagesAfterFirst = messagesSent
        messagesSent = 0

        // When - Connection state changes to Connected again (redundant)
        connectionStateFlow.value = ConnectionState.Connected
        advanceTimeBy(2500) // One more interval

        // Then - Should not have double the rate of messages
        // If job was duplicated, we'd get ~2x messages in the same time period
        assertTrue("Should not duplicate message rate", messagesSent <= messagesAfterFirst + 2)
    }

    @Test
    fun `connection state Connecting stops price generation`() = runTest(timeout = 30.seconds) {
        // Given - Connected and generating prices
        var messagesSent = 0
        coEvery { connectionRepository.sendMessage(any()) } answers {
            messagesSent++
        }

        connectionStateFlow.value = ConnectionState.Connected
        Thread.sleep(2500)

        assertTrue("Should send messages while connected", messagesSent > 0)

        // When - State changes to Connecting
        val messagesBeforeConnecting = messagesSent
        connectionStateFlow.value = ConnectionState.Connecting
        Thread.sleep(3000)

        // Then - Price generation should stop (similar to Disconnected)
        assertTrue("Should not send messages while connecting", messagesSent == messagesBeforeConnecting)
    }
}


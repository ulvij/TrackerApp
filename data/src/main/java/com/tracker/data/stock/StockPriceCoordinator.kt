package com.tracker.data.stock

import android.util.Log
import com.google.gson.Gson
import com.tracker.data.stock.model.PriceUpdateDto
import com.tracker.data.stock.model.PriceUpdateListDto
import com.tracker.domain.connection.model.ConnectionState
import com.tracker.domain.connection.repository.ConnectionRepository
import com.tracker.domain.stock.repository.StockRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Coordinator that orchestrates stock price generation and WebSocket communication.
 * This coordinator manages the interaction between StockRepository and ConnectionRepository,
 * eliminating the need for repositories to depend on each other.
 *
 * Responsibilities:
 * - Observes connection state and manages price generation lifecycle
 * - Generates random price updates when connected
 * - Sends price updates via WebSocket
 * - Receives WebSocket messages and updates stock repository
 */
@Singleton
class StockPriceCoordinator @Inject constructor(
    private val stockRepository: StockRepository,
    private val connectionRepository: ConnectionRepository
) {
    companion object {
        private const val TAG = "StockPriceCoordinator"
        private const val PRICE_UPDATE_INTERVAL_MS = 2000L
        private const val MIN_PRICE = 1.0
        private const val MAX_PRICE_CHANGE_PERCENT = 5.0
    }

    private val coordinatorScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val gson = Gson()
    private var generationJob: Job? = null

    init {
        Log.d(TAG, "StockPriceCoordinator initialized")

        // Start observing connection state and manage price generation
        observeConnectionStateAndManageGeneration()

        // Start observing WebSocket messages and update stock prices
        observeWebSocketMessages()
    }

    /**
     * Observe connection state and start/stop price generation accordingly
     */
    private fun observeConnectionStateAndManageGeneration() {
        connectionRepository.observeConnectionState()
            .onEach { connectionState ->
                Log.d(TAG, "Connection state changed: $connectionState")
                when (connectionState) {
                    is ConnectionState.Connected -> startPriceGeneration()
                    else -> stopPriceGeneration()
                }
            }
            .launchIn(coordinatorScope)
    }

    /**
     * Observe WebSocket messages and update stock prices
     */
    private fun observeWebSocketMessages() {
        connectionRepository.observeMessages()
            .onEach { message ->
                if (message.isNotEmpty()) {
                    Log.d(TAG, "Received message: ${message.take(100)}...")
                    try {
                        val priceUpdates = gson.fromJson(message, PriceUpdateListDto::class.java)
                        Log.d(TAG, "Parsed ${priceUpdates.updates.size} price updates")
                        stockRepository.updatePrices(priceUpdates.updates)
                        Log.d(TAG, "Successfully updated stock prices")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse message: ${e.message}")
                    }
                }
            }
            .launchIn(coordinatorScope)
    }

    /**
     * Start generating prices and sending them via WebSocket
     */
    private fun startPriceGeneration() {
        if (generationJob?.isActive == true) {
            Log.d(TAG, "Price generation already active, skipping")
            return
        }

        Log.d(TAG, "Starting price generation")
        generationJob = coordinatorScope.launch {
            while (true) {
                // Get current stocks from repository
                val currentStocks = stockRepository.getCurrentStocks()
                Log.d(TAG, "Generating prices for ${currentStocks.size} stocks")

                // Generate new prices for all stocks
                val priceUpdates = currentStocks.map { stock ->
                    PriceUpdateDto(
                        symbol = stock.symbol,
                        price = generateNextPrice(stock.currentPrice),
                        timestamp = System.currentTimeMillis()
                    )
                }

                // Send all price updates as a single list via WebSocket
                val priceUpdateList = PriceUpdateListDto(updates = priceUpdates)
                val message = gson.toJson(priceUpdateList)
                Log.d(TAG, "Sending price update message: ${message.take(100)}...")
                connectionRepository.sendMessage(message)

                delay(PRICE_UPDATE_INTERVAL_MS)
            }
        }
    }

    /**
     * Generate next price with random change between -5% and +5%
     */
    private fun generateNextPrice(currentPrice: Double): Double {
        val changePercentage = Random.nextDouble(-MAX_PRICE_CHANGE_PERCENT, MAX_PRICE_CHANGE_PERCENT)
        val priceChange = currentPrice * (changePercentage / 100)
        return (currentPrice + priceChange).coerceAtLeast(MIN_PRICE)
    }

    /**
     * Stop price generation
     */
    private fun stopPriceGeneration() {
        Log.d(TAG, "Stopping price generation")
        generationJob?.cancel()
        generationJob = null
    }
}


package com.tracker.data.stock

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
 */
@Singleton
class StockPriceCoordinator @Inject constructor(
    private val stockRepository: StockRepository,
    private val connectionRepository: ConnectionRepository
) {
    companion object {
        private const val PRICE_UPDATE_INTERVAL_MS = 2000L
        private const val MIN_PRICE = 1.0
        private const val MAX_PRICE_CHANGE_PERCENT = 5.0
    }

    private val coordinatorScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val gson = Gson()
    private var generationJob: Job? = null

    init {
        observeConnectionStateAndManageGeneration()
        observeWebSocketMessages()
    }

    private fun observeConnectionStateAndManageGeneration() {
        connectionRepository.observeConnectionState()
            .onEach { connectionState ->
                when (connectionState) {
                    is ConnectionState.Connected -> startPriceGeneration()
                    else -> stopPriceGeneration()
                }
            }
            .launchIn(coordinatorScope)
    }

    private fun observeWebSocketMessages() {
        connectionRepository.observeMessages()
            .onEach { message ->
                if (message.isNotEmpty()) {
                    val priceUpdates = gson.fromJson(message, PriceUpdateListDto::class.java)
                    stockRepository.updatePrices(priceUpdates.updates)
                }
            }
            .launchIn(coordinatorScope)
    }

    private fun startPriceGeneration() {
        if (generationJob?.isActive == true) return

        generationJob = coordinatorScope.launch {
            while (true) {
                val currentStocks = stockRepository.getCurrentStocks()

                if (currentStocks.isNotEmpty()) {
                    val shuffledStocks = currentStocks.shuffled()
                    val delayBetweenUpdates = PRICE_UPDATE_INTERVAL_MS / shuffledStocks.size

                    shuffledStocks.forEach { stock ->
                        val priceUpdate = PriceUpdateDto(
                            symbol = stock.symbol,
                            price = generateNextPrice(stock.currentPrice),
                            timestamp = System.currentTimeMillis()
                        )
                        val priceUpdateList = PriceUpdateListDto(updates = listOf(priceUpdate))
                        val message = gson.toJson(priceUpdateList)
                        connectionRepository.sendMessage(message)
                        delay(delayBetweenUpdates)
                    }
                } else {
                    delay(PRICE_UPDATE_INTERVAL_MS)
                }
            }
        }
    }

    private fun generateNextPrice(currentPrice: Double): Double {
        val changePercentage = Random.nextDouble(-MAX_PRICE_CHANGE_PERCENT, MAX_PRICE_CHANGE_PERCENT)
        val priceChange = currentPrice * (changePercentage / 100)
        return (currentPrice + priceChange).coerceAtLeast(MIN_PRICE)
    }

    private fun stopPriceGeneration() {
        generationJob?.cancel()
        generationJob = null
    }
}

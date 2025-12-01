package com.tracker.data.stock

import app.cash.turbine.test
import com.tracker.data.stock.model.PriceUpdateDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Test cases for StockRepositoryImpl
 * Note: This now tests only the repository's data management capabilities.
 * Integration with WebSocket and price generation is tested in StockPriceCoordinatorTest.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StockRepositoryImplTest {

    private lateinit var stockRepository: StockRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        stockRepository = StockRepositoryImpl()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial stock list should contain 25 stocks`() = runTest {
        // When
        stockRepository.observePriceUpdates().test {
            val stocks = awaitItem()

            // Then
            assertEquals(25, stocks.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `all stocks should have valid symbols from StockDataSource`() = runTest {
        // When
        stockRepository.observePriceUpdates().test {
            val stocks = awaitItem()

            // Then
            stocks.forEach { stock ->
                assertTrue(
                    "Stock symbol should be in StockDataSource",
                    StockDataSource.symbols.contains(stock.symbol)
                )
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `all stocks should have logo URLs`() = runTest {
        // When
        stockRepository.observePriceUpdates().test {
            val stocks = awaitItem()

            // Then
            stocks.forEach { stock ->
                assertNotNull("Stock should have logo URL", stock.logoUrl)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stocks should be sorted by price descending`() = runTest {
        // When
        stockRepository.observePriceUpdates().test {
            val stocks = awaitItem()

            // Then
            for (i in 0 until stocks.size - 1) {
                assertTrue(
                    "Stocks should be sorted by price descending",
                    stocks[i].currentPrice >= stocks[i + 1].currentPrice
                )
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update stock prices via updatePrices method`() = runTest {
        // Given
        val updates = listOf(
            PriceUpdateDto("AAPL", 999.99, System.currentTimeMillis()),
            PriceUpdateDto("GOOG", 888.88, System.currentTimeMillis())
        )

        // When
        stockRepository.observePriceUpdates().test {
            awaitItem() // Initial state

            stockRepository.updatePrices(updates)

            val updatedStocks = awaitItem()

            // Then
            val appleStock = updatedStocks.find { it.symbol == "AAPL" }
            val googleStock = updatedStocks.find { it.symbol == "GOOG" }

            assertNotNull(appleStock)
            assertNotNull(googleStock)
            assertEquals(999.99, appleStock!!.currentPrice, 0.001)
            assertEquals(888.88, googleStock!!.currentPrice, 0.001)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should preserve previous price when updating`() = runTest {
        // Given
        val symbol = "AAPL"

        stockRepository.observePriceUpdates().test {
            val initialStocks = awaitItem()
            val initialAppleStock = initialStocks.find { it.symbol == symbol }!!
            val initialPrice = initialAppleStock.currentPrice

            // When - Update with new price
            val newPrice = initialPrice + 10.0
            val updates = listOf(
                PriceUpdateDto(symbol, newPrice, System.currentTimeMillis())
            )

            stockRepository.updatePrices(updates)

            val updatedStocks = awaitItem()
            val updatedAppleStock = updatedStocks.find { it.symbol == symbol }!!

            // Then
            assertEquals(newPrice, updatedAppleStock.currentPrice, 0.001)
            assertEquals(initialPrice, updatedAppleStock.previousPrice, 0.001)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should update multiple stocks at once`() = runTest {
        // Given
        val updates = listOf(
            PriceUpdateDto("AAPL", 100.0, System.currentTimeMillis()),
            PriceUpdateDto("GOOG", 200.0, System.currentTimeMillis()),
            PriceUpdateDto("TSLA", 300.0, System.currentTimeMillis())
        )

        // When
        stockRepository.observePriceUpdates().test {
            awaitItem() // Initial state

            stockRepository.updatePrices(updates)

            val updatedStocks = awaitItem()

            // Then
            val appleStock = updatedStocks.find { it.symbol == "AAPL" }
            val googleStock = updatedStocks.find { it.symbol == "GOOG" }
            val teslaStock = updatedStocks.find { it.symbol == "TSLA" }

            assertEquals(100.0, appleStock!!.currentPrice, 0.001)
            assertEquals(200.0, googleStock!!.currentPrice, 0.001)
            assertEquals(300.0, teslaStock!!.currentPrice, 0.001)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should maintain sorted order after price updates`() = runTest {
        // Given - Make AAPL have the highest price
        val updates = listOf(
            PriceUpdateDto("AAPL", 99999.99, System.currentTimeMillis())
        )

        // When
        stockRepository.observePriceUpdates().test {
            awaitItem() // Initial state

            stockRepository.updatePrices(updates)

            val updatedStocks = awaitItem()

            // Then - AAPL should be first (highest price)
            assertEquals("AAPL", updatedStocks.first().symbol)

            // Verify sorting
            for (i in 0 until updatedStocks.size - 1) {
                assertTrue(
                    "Stocks should remain sorted after update",
                    updatedStocks[i].currentPrice >= updatedStocks[i + 1].currentPrice
                )
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCurrentStocks should return current stock list`() = runTest {
        // When
        val stocks = stockRepository.getCurrentStocks()

        // Then
        assertEquals(25, stocks.size)
        stocks.forEach { stock ->
            assertTrue(StockDataSource.symbols.contains(stock.symbol))
            assertTrue(stock.currentPrice > 0)
        }
    }
}


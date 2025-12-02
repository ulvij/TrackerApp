package com.tracker.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tracker.app.ui.TestTags
import com.tracker.app.ui.tool.createStockWithDecrease
import com.tracker.app.ui.tool.createStockWithIncrease
import com.tracker.app.ui.tool.createStockWithNoChange
import com.tracker.app.ui.tool.createTestStockUIModel
import com.tracker.app.ui.model.StockUIModel
import com.tracker.app.ui.theme.TrackerAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StockListItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setStockContent(stock: StockUIModel, isDark: Boolean = false) {
        composeTestRule.setContent {
            TrackerAppTheme(isDarkTheme = isDark) {
                StockListItem(stock)
            }
        }
    }

    // ───────────────────────────────────────
    // Rendering
    // ───────────────────────────────────────

    @Test
    fun displays_stock_symbol() {
        val stock = createTestStockUIModel(symbol = "AAPL")

        setStockContent(stock)

        composeTestRule.onNodeWithTag("${TestTags.STOCK_ITEM_PREFIX}AAPL")
            .assertIsDisplayed()
    }

    @Test
    fun displays_current_price() {
        val stock = createTestStockUIModel(
            symbol = "AAPL",
            currentPrice = 185.50
        )

        setStockContent(stock)

        composeTestRule.onNodeWithTag("${TestTags.STOCK_PRICE_PREFIX}AAPL")
            .assertIsDisplayed()
            .assertTextContains("$185.50")
    }

    @Test
    fun displays_price_increase_indicator() {
        val stock = createStockWithIncrease("AAPL")

        setStockContent(stock)

        composeTestRule.onNodeWithTag("${TestTags.STOCK_CHANGE_PREFIX}AAPL")
            .assertTextContains("▲", substring = true)
            .assertTextContains("$3.20", substring = true)
            .assertTextContains("1.76%", substring = true)
    }


    @Test
    fun displays_price_decrease_indicator() {
        val stock = createStockWithDecrease("GOOG")

        setStockContent(stock)

        composeTestRule.onNodeWithTag("${TestTags.STOCK_CHANGE_PREFIX}GOOG")
            .assertTextContains("▼", substring = true)
            .assertTextContains("$5.45", substring = true)
            .assertTextContains("3.68%", substring = true)
    }

    @Test
    fun hides_indicator_when_no_change() {
        val stock = createStockWithNoChange("TSLA")

        setStockContent(stock)

        composeTestRule.onNodeWithTag("${TestTags.STOCK_CHANGE_PREFIX}TSLA")
            .assertDoesNotExist()
    }

    // ───────────────────────────────────────
    // Formatting
    // ───────────────────────────────────────

    @Test
    fun formats_price_to_two_decimals() {
        val stock = createTestStockUIModel(
            symbol = "TEST",
            currentPrice = 123.456
        )

        setStockContent(stock)

        composeTestRule.onNodeWithTag("${TestTags.STOCK_PRICE_PREFIX}TEST")
            .assertTextContains("$123.46")
    }

    @Test
    fun formats_price_change() {
        val stock = createTestStockUIModel(
            symbol = "TEST",
            currentPrice = 100.123,
            previousPrice = 95.678,
            priceChange = 4.445,
            priceChangePercentage = 4.645,
            isPriceIncreased = true
        )

        setStockContent(stock)

        composeTestRule.onNodeWithTag("${TestTags.STOCK_CHANGE_PREFIX}TEST")
            .assertTextContains("4.45", substring = true)
            .assertTextContains("4.65%", substring = true)
            .assertTextEquals("▲ $4.45 (4.65%)")
    }


    // ───────────────────────────────────────
    // Multi-item rendering
    // ───────────────────────────────────────

    @Test
    fun renders_multiple_stocks_correctly() {
        val up = createStockWithIncrease("AAPL")
        val down = createStockWithDecrease("GOOG")
        val flat = createStockWithNoChange("TSLA")

        composeTestRule.setContent {
            TrackerAppTheme {
                Column {
                    StockListItem(up)
                    StockListItem(down)
                    StockListItem(flat)
                }
            }
        }

        composeTestRule.onNodeWithTag("${TestTags.STOCK_ITEM_PREFIX}AAPL").assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TestTags.STOCK_ITEM_PREFIX}GOOG").assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TestTags.STOCK_ITEM_PREFIX}TSLA").assertIsDisplayed()

        composeTestRule.onNodeWithTag("${TestTags.STOCK_CHANGE_PREFIX}AAPL").assertTextContains("▲", substring = true)
        composeTestRule.onNodeWithTag("${TestTags.STOCK_CHANGE_PREFIX}GOOG").assertTextContains("▼", substring = true)
    }

    // ───────────────────────────────────────
    // Theming
    // ───────────────────────────────────────

    @Test
    fun renders_in_light_theme() {
        val stock = createStockWithIncrease("AAPL")

        setStockContent(stock, isDark = false)

        composeTestRule.onNodeWithTag("${TestTags.STOCK_ITEM_PREFIX}AAPL")
            .assertIsDisplayed()
    }

    @Test
    fun renders_in_dark_theme() {
        val stock = createStockWithDecrease("GOOG")

        setStockContent(stock, isDark = true)

        composeTestRule.onNodeWithTag("${TestTags.STOCK_ITEM_PREFIX}GOOG")
            .assertIsDisplayed()
    }
}



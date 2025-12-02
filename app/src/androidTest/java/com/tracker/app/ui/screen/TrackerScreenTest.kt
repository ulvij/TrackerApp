package com.tracker.app.ui.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tracker.app.ui.TestTags
import com.tracker.app.ui.tool.createStockWithDecrease
import com.tracker.app.ui.tool.createStockWithIncrease
import com.tracker.app.ui.tool.createStockWithNoChange
import com.tracker.app.ui.tool.createTestTrackerUIState
import com.tracker.app.ui.theme.TrackerAppTheme
import com.tracker.domain.connection.model.ConnectionState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrackerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Helper to set TrackerContent from a concrete state
    private fun ComposeContentTestRule.setTrackerContent(
        state: MutableState<TrackerUIState>,
        isDarkTheme: MutableState<Boolean> = mutableStateOf(false),
        onConnectionToggle: () -> Unit = {},
        onThemeToggle: () -> Unit = {}
    ) {
        setContent {
            TrackerAppTheme(isDarkTheme = isDarkTheme.value) {
                TrackerContent(
                    state = state.value,
                    onConnectionToggle = onConnectionToggle,
                    onThemeToggle = onThemeToggle,
                    isDarkTheme = isDarkTheme.value
                )
            }
        }
    }

    // Helper to set TrackerContent driven by a MutableState<TrackerUIState>
    private fun ComposeContentTestRule.setTrackerContentFromStateHolder(
        stateHolder: MutableState<TrackerUIState>,
        isDarkThemeHolder: MutableState<Boolean> = mutableStateOf(false),
        onConnectionToggle: () -> Unit = {},
        onThemeToggle: () -> Unit = {}
    ) {
        setContent {
            TrackerAppTheme(isDarkTheme = isDarkThemeHolder.value) {
                TrackerContent(
                    state = stateHolder.value,
                    onConnectionToggle = onConnectionToggle,
                    onThemeToggle = onThemeToggle,
                    isDarkTheme = isDarkThemeHolder.value
                )
            }
        }
    }

    // ====================
    // Rendering basics
    // ====================

    @Test
    fun trackerScreen_rendersTrackerScreen_andTopBar() {
        val state = mutableStateOf(createTestTrackerUIState()) // default empty disconnected
        composeTestRule.setTrackerContent(state)

        composeTestRule.onNodeWithTag(TestTags.TRACKER_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.TOP_BAR).assertIsDisplayed()
    }

    // ====================
    // Empty / Loading states
    // ====================

    @Test
    fun trackerScreen_showsEmptyMessage_whenNoStocksAndDisconnected() {
        val state = mutableStateOf(
            createTestTrackerUIState(
                stocks = emptyList(),
                connectionState = ConnectionState.Disconnected
            )
        )
        composeTestRule.setTrackerContent(state)

        // prefer tag-based assertion (more stable)
        composeTestRule.onNodeWithTag(TestTags.EMPTY_MESSAGE).assertIsDisplayed()
    }

    @Test
    fun trackerScreen_showsLoadingIndicator_whenConnectedAndNoStocks() {
        val state = mutableStateOf(
            createTestTrackerUIState(
                stocks = emptyList(),
                connectionState = ConnectionState.Connected
            )
        )
        composeTestRule.setTrackerContent(state)

        composeTestRule.onNodeWithTag(TestTags.LOADING_INDICATOR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.EMPTY_MESSAGE).assertDoesNotExist()
    }

    @Test
    fun trackerScreen_showsEmptyMessage_whenConnecting_andNoStocks() {
        val state = mutableStateOf(
            createTestTrackerUIState(
                stocks = emptyList(),
                connectionState = ConnectionState.Connecting
            )
        )
        composeTestRule.setTrackerContent(state)

        // According to your screen logic Connecting.isConnected is false -> should show empty message
        composeTestRule.onNodeWithTag(TestTags.EMPTY_MESSAGE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.LOADING_INDICATOR).assertDoesNotExist()
    }

    // ====================
    // Stock list rendering & scrolling
    // ====================

    @Test
    fun trackerScreen_displaysStockList_andItems_whenStocksAvailable() {
        val state = mutableStateOf(
            createTestTrackerUIState(
                stocks = listOf(
                    createStockWithIncrease(symbol = "AAPL"),
                    createStockWithDecrease(symbol = "GOOG"),
                    createStockWithNoChange(symbol = "TSLA")
                ),
                connectionState = ConnectionState.Connected
            )
        )
        composeTestRule.setTrackerContent(state)

        composeTestRule.onNodeWithTag(TestTags.STOCK_LIST).assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TestTags.STOCK_ITEM_PREFIX}AAPL").assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TestTags.STOCK_ITEM_PREFIX}GOOG").assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TestTags.STOCK_ITEM_PREFIX}TSLA").assertIsDisplayed()
    }

    @Test
    fun trackerScreen_stockList_canScroll_toLastItem() {
        val stocks = List(30) { index -> createStockWithIncrease(symbol = "STOCK$index") }
        val state = mutableStateOf(createTestTrackerUIState(stocks = stocks, connectionState = ConnectionState.Connected))

        composeTestRule.setTrackerContent(state)

        // First item visible
        composeTestRule.onNodeWithTag("${TestTags.STOCK_ITEM_PREFIX}STOCK0").assertIsDisplayed()

        // Scroll to index 29 and check it exists
        composeTestRule.onNodeWithTag(TestTags.STOCK_LIST)
            .performScrollToIndex(29)

        composeTestRule.onNodeWithTag("${TestTags.STOCK_ITEM_PREFIX}STOCK29")
            .assertIsDisplayed()
    }

    // ====================
    // Connection toggle behavior
    // ====================

    @Test
    fun trackerScreen_connectionToggle_invokesCallback() {
        var clicked = false
        val state = mutableStateOf(createTestTrackerUIState()) // default disconnected

        composeTestRule.setTrackerContent(
            state = state,
            onConnectionToggle = { clicked = true }
        )

        composeTestRule.onNodeWithTag(TestTags.CONNECTION_SWITCH).performClick()
        assert(clicked) { "Connection toggle callback should be invoked" }
    }

    @Test
    fun trackerScreen_connectionSwitch_reflectsState_and_canBeToggled_viaStateHolder() {
        // Start disconnected with no stocks
        val stateHolder = mutableStateOf(
            createTestTrackerUIState(
                stocks = emptyList(),
                connectionState = ConnectionState.Disconnected
            )
        )

        // set content once, reading the stateHolder.value inside composition
        composeTestRule.setTrackerContentFromStateHolder(
            stateHolder = stateHolder,
            onConnectionToggle = {
                // toggle behavior that updates the stateHolder value
                stateHolder.value = stateHolder.value.copy(
                    connectionState = if (stateHolder.value.connectionState.isConnected) {
                        ConnectionState.Disconnected
                    } else {
                        ConnectionState.Connected
                    }
                )
            }
        )

        // initially off
        composeTestRule.onNodeWithTag(TestTags.CONNECTION_SWITCH).assertIsOff()

        // perform click and expect switch to be on (stateHolder toggled)
        composeTestRule.onNodeWithTag(TestTags.CONNECTION_SWITCH).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.CONNECTION_SWITCH).assertIsOn()
    }

    // ====================
    // Theme toggle behavior
    // ====================

    @Test
    fun trackerScreen_themeToggle_invokesCallback_and_iconsReflectTheme() {
        var themeClicked = false
        val state = mutableStateOf(createTestTrackerUIState())
        val isDarkTheme = mutableStateOf(false)
        // Light mode
        composeTestRule.setTrackerContent(state = state, isDarkTheme = isDarkTheme, onThemeToggle = { themeClicked = true })

        composeTestRule.onNodeWithTag(TestTags.THEME_TOGGLE).performClick()
        assert(themeClicked) { "Theme toggle callback should be invoked" }

        // Light icon

        isDarkTheme.value = false
        composeTestRule.onNodeWithText("☀️").assertIsDisplayed()

        // Dark icon
        isDarkTheme.value = true
        composeTestRule.onNodeWithText("🌙").assertIsDisplayed()
    }

    // ====================
    // State transitions in one composition (safe from setContent issues)
    // ====================

    @Test
    fun trackerScreen_transitions_empty_to_loading_to_list_usingStateHolder() {
        val stateHolder = mutableStateOf(
            createTestTrackerUIState(
                stocks = emptyList(),
                connectionState = ConnectionState.Disconnected
            )
        )

        // set content once and change stateHolder.value to trigger recomposition
        composeTestRule.setTrackerContentFromStateHolder(stateHolder = stateHolder)

        // Initially empty message
        composeTestRule.onNodeWithTag(TestTags.EMPTY_MESSAGE).assertIsDisplayed()

        // Move to Connected (empty stocks) => loading indicator
        composeTestRule.runOnUiThread {
            stateHolder.value = createTestTrackerUIState(
                stocks = emptyList(),
                connectionState = ConnectionState.Connected
            )
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.LOADING_INDICATOR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.EMPTY_MESSAGE).assertDoesNotExist()

        // Move to Connected with stocks => list shown
        composeTestRule.runOnUiThread {
            stateHolder.value = createTestTrackerUIState(
                stocks = listOf(createStockWithIncrease(symbol = "AAPL")),
                connectionState = ConnectionState.Connected
            )
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TestTags.STOCK_LIST).assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TestTags.STOCK_ITEM_PREFIX}AAPL").assertIsDisplayed()
    }

    // ====================
    // Error states
    // ====================

    @Test
    fun trackerScreen_showsEmpty_whenError_andNoStocks() {
        val state = mutableStateOf(
            createTestTrackerUIState(
                stocks = emptyList(),
                connectionState = ConnectionState.Error("failed")
            )
        )
        composeTestRule.setTrackerContent(state)

        composeTestRule.onNodeWithTag(TestTags.EMPTY_MESSAGE).assertIsDisplayed()
    }

    @Test
    fun trackerScreen_showsList_whenError_andCachedStocksExist() {
        val state = mutableStateOf(
            createTestTrackerUIState(
                stocks = listOf(createStockWithIncrease(symbol = "AAPL")),
                connectionState = ConnectionState.Error("failed")
            )
        )
        composeTestRule.setTrackerContent(state)

        composeTestRule.onNodeWithTag(TestTags.STOCK_LIST).assertIsDisplayed()
        composeTestRule.onNodeWithTag("${TestTags.STOCK_ITEM_PREFIX}AAPL").assertIsDisplayed()
    }

    // ====================
    // Theming render tests
    // ====================

    @Test
    fun trackerScreen_rendersCorrectly_inLight_andDarkTheme() {
        val state = mutableStateOf(
            createTestTrackerUIState(
                stocks = listOf(createStockWithIncrease(symbol = "AAPL")),
                connectionState = ConnectionState.Connected
            )
        )

        val isDarkTheme = mutableStateOf(false)

        // Light
        composeTestRule.setTrackerContent(state, isDarkTheme = isDarkTheme)
        composeTestRule.onNodeWithTag(TestTags.TRACKER_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.STOCK_LIST).assertIsDisplayed()

        // Dark
        isDarkTheme.value = true
        composeTestRule.onNodeWithTag(TestTags.TRACKER_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.STOCK_LIST).assertIsDisplayed()
    }
}



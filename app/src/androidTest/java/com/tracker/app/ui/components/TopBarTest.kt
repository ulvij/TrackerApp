package com.tracker.app.ui.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tracker.app.ui.MainActivity
import com.tracker.app.ui.TestTags
import com.tracker.app.ui.theme.TrackerAppTheme
import com.tracker.domain.connection.model.ConnectionState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Optimized UI Tests for TopBar component
 *
 * Tests verify:
 * - Component rendering with different connection states
 * - Theme toggle interaction
 * - Connection switch interaction and state
 * - Connection status indicator visibility
 * - Light and dark theme rendering
 */
@RunWith(AndroidJUnit4::class)
class TopBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==================== Helper Function ====================
    private fun setTopBarContent(
        connectionState: MutableState<ConnectionState>,
        isDarkTheme: MutableState<Boolean> = mutableStateOf(false),
        onConnectionToggle: () -> Unit = {},
        onThemeToggle: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            TrackerAppTheme(isDarkTheme = isDarkTheme.value) {
                TopBar(
                    connectionState = connectionState.value,
                    onConnectionToggle = onConnectionToggle,
                    onThemeToggle = onThemeToggle,
                    isDarkTheme = isDarkTheme.value
                )
            }
        }
    }

    // ==================== Rendering Tests ====================
    @Test
    fun topBar_rendersAllComponents() {
        setTopBarContent(mutableStateOf(ConnectionState.Disconnected))

        composeTestRule.onNodeWithTag(TestTags.TOP_BAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.APP_TITLE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CONNECTION_INDICATOR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.THEME_TOGGLE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CONNECTION_SWITCH).assertIsDisplayed()
    }

    @Test
    fun topBar_displaysAppTitle() {
        setTopBarContent(mutableStateOf(ConnectionState.Disconnected))

        composeTestRule.onNodeWithTag(TestTags.APP_TITLE).assertIsDisplayed()
        composeTestRule.onNodeWithText("TrackerApp").assertIsDisplayed()
    }

    @Test
    fun topBar_displaysConnectionStatusIndicator() {
        setTopBarContent(mutableStateOf(ConnectionState.Connected))

        composeTestRule.onNodeWithTag(TestTags.CONNECTION_INDICATOR).assertIsDisplayed()
    }

    // ==================== Theme Toggle Tests ====================
    @Test
    fun topBar_displaysCorrectThemeIcon_whenThemeChanges() {
        // Mutable state for theme
        val isDarkTheme = mutableStateOf(false)

        // Set content observing the mutable state
        setTopBarContent(
            connectionState = mutableStateOf(ConnectionState.Disconnected),
            isDarkTheme = isDarkTheme
        )

        // Initially light theme -> Sun icon should be visible
        composeTestRule.onNodeWithText("☀️").assertIsDisplayed()

        // Switch to dark theme
        composeTestRule.runOnUiThread { isDarkTheme.value = true }
        composeTestRule.waitForIdle()

        // Now dark theme -> Moon icon should be visible
        composeTestRule.onNodeWithText("🌙").assertIsDisplayed()
    }


    @Test
    fun topBar_themeToggle_triggersCallback() {
        var themeClicked = false
        setTopBarContent(mutableStateOf(ConnectionState.Disconnected), onThemeToggle = { themeClicked = true })

        composeTestRule.onNodeWithTag(TestTags.THEME_TOGGLE).performClick()
        assert(themeClicked) { "Theme toggle callback was not invoked" }
    }

    // ==================== Connection Switch Tests ====================
    @Test
    fun topBar_connectionSwitch_reflectsState() {
        // Mutable state for connection
        val connectionState = mutableStateOf<ConnectionState>(ConnectionState.Disconnected)

        // Set content once
        composeTestRule.setContent {
            TopBar(
                connectionState = connectionState.value,
                onConnectionToggle = {},
                onThemeToggle = {},
                isDarkTheme = false
            )
        }

        // Test each state by updating the state
        val states = listOf(
            ConnectionState.Disconnected to false,
            ConnectionState.Connected to true,
            ConnectionState.Connecting to false,
            ConnectionState.Error("Error") to false
        )

        states.forEach { (state, expectedOn) ->
            // Update state
            composeTestRule.runOnUiThread { connectionState.value = state }
            composeTestRule.waitForIdle()

            val node = composeTestRule.onNodeWithTag(TestTags.CONNECTION_SWITCH)
            if (expectedOn) node.assertIsOn() else node.assertIsOff()
        }
    }


    @Test
    fun topBar_connectionSwitch_triggersCallback() {
        var toggleClicked = false
        setTopBarContent(mutableStateOf(ConnectionState.Disconnected), onConnectionToggle = { toggleClicked = true })

        composeTestRule.onNodeWithTag(TestTags.CONNECTION_SWITCH).performClick()
        assert(toggleClicked) { "Connection toggle callback was not invoked" }
    }

    // ==================== State Transition Test ====================
    @Test
    fun topBar_switchState_updatesOnToggle() {
        val connectionState = mutableStateOf<ConnectionState>(ConnectionState.Disconnected)


        setTopBarContent(
            connectionState = connectionState,
            onConnectionToggle = {
                connectionState.value = if (connectionState.value == ConnectionState.Disconnected) ConnectionState.Connected
                else ConnectionState.Disconnected
            }
        )

        val switchNode = composeTestRule.onNodeWithTag(TestTags.CONNECTION_SWITCH)
        switchNode.assertIsOff()

        switchNode.performClick()
        composeTestRule.waitForIdle()
        switchNode.assertIsOn()
    }

    // ==================== Error State Rendering ====================
    @Test
    fun topBar_rendersCorrectly_withErrorState() {
        setTopBarContent(mutableStateOf(ConnectionState.Error("Connection failed")))

        composeTestRule.onNodeWithTag(TestTags.TOP_BAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CONNECTION_INDICATOR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.CONNECTION_SWITCH).assertIsOff()
    }

    // ==================== Theming Tests ====================
    @Test
    fun topBar_rendersCorrectly_inThemes() {
        val isDarkTheme = mutableStateOf(false)

        // Light theme
        setTopBarContent(mutableStateOf(ConnectionState.Connected), isDarkTheme = isDarkTheme)
        composeTestRule.onNodeWithTag(TestTags.TOP_BAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.APP_TITLE).assertIsDisplayed()
        composeTestRule.onNodeWithText("☀️").assertIsDisplayed()

        // Dark theme
        isDarkTheme.value = true
        composeTestRule.onNodeWithTag(TestTags.TOP_BAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.APP_TITLE).assertIsDisplayed()
        composeTestRule.onNodeWithText("🌙").assertIsDisplayed()
    }
}



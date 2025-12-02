package com.tracker.app.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tracker.app.ui.TestTags
import com.tracker.app.ui.theme.TrackerAppTheme
import com.tracker.domain.connection.model.ConnectionState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConnectionStatusIndicatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==================== Rendering & Semantics Test ====================
    @Test
    fun connectionStatusIndicator_rendersAndHasCorrectSemantics() {
        val connectionState = mutableStateOf<ConnectionState>(ConnectionState.Disconnected)

        composeTestRule.setContent {
            TrackerAppTheme {
                ConnectionStatusIndicator(connectionState = connectionState.value)
            }
        }

        val node = composeTestRule.onNodeWithTag(TestTags.CONNECTION_INDICATOR)

        // Test all states dynamically inside single composition
        val states = listOf(
            ConnectionState.Disconnected to "Disconnected",
            ConnectionState.Connecting to "Connecting",
            ConnectionState.Connected to "Connected",
            ConnectionState.Error("Failed") to "Error"
        )

        states.forEach { (state, expectedSemantics) ->
            composeTestRule.runOnUiThread { connectionState.value = state }
            composeTestRule.waitForIdle()
            node.assertIsDisplayed()
                .assert(SemanticsMatcher.expectValue(ConnectionStatusKey, expectedSemantics))
        }
    }

    // ==================== Dynamic State Change Test ====================
    @Test
    fun connectionStatusIndicator_updatesSemantics_whenStateChanges() {
        val connectionState = mutableStateOf<ConnectionState>(ConnectionState.Disconnected)

        composeTestRule.setContent {
            TrackerAppTheme {
                ConnectionStatusIndicator(connectionState = connectionState.value)
            }
        }

        val node = composeTestRule.onNodeWithTag(TestTags.CONNECTION_INDICATOR)

        // Change state dynamically
        val states = listOf(
            ConnectionState.Disconnected to "Disconnected",
            ConnectionState.Connecting to "Connecting",
            ConnectionState.Connected to "Connected",
            ConnectionState.Error("Error") to "Error"
        )

        states.forEach { (state, expectedSemantics) ->
            composeTestRule.runOnUiThread { connectionState.value = state }
            composeTestRule.waitForIdle()
            node.assert(SemanticsMatcher.expectValue(ConnectionStatusKey, expectedSemantics))
        }
    }

    // ==================== Theme Rendering Test ====================
    @Test
    fun connectionStatusIndicator_rendersCorrectly_inLightAndDarkThemes() {
        val connectionState = ConnectionState.Connected
        val isDarkTheme = mutableStateOf(false)

        composeTestRule.setContent {
            TrackerAppTheme(isDarkTheme = isDarkTheme.value) {
                ConnectionStatusIndicator(connectionState = connectionState)
            }
        }

        val node = composeTestRule.onNodeWithTag(TestTags.CONNECTION_INDICATOR)

        // Initially light theme
        node.assertIsDisplayed()

        // Switch to dark theme dynamically
        composeTestRule.runOnUiThread { isDarkTheme.value = true }
        composeTestRule.waitForIdle()
        node.assertIsDisplayed()
    }
}





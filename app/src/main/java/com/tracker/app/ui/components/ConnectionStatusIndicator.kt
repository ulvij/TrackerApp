package com.tracker.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tracker.app.ui.TestTags
import com.tracker.app.ui.theme.DarkRed
import com.tracker.app.ui.theme.Green
import com.tracker.app.ui.theme.Orange
import com.tracker.app.ui.theme.Red
import com.tracker.app.ui.theme.TrackerAppTheme
import com.tracker.domain.connection.model.ConnectionState

// Semantic property for connection status testing
val ConnectionStatusKey = SemanticsPropertyKey<String>("ConnectionStatus")
var SemanticsPropertyReceiver.connectionStatus by ConnectionStatusKey

/**
 * Visual indicator for WebSocket connection status
 */
@Composable
fun ConnectionStatusIndicator(
    connectionState: ConnectionState,
    modifier: Modifier = Modifier
) {
    val color = when (connectionState) {
        is ConnectionState.Connected -> Green
        is ConnectionState.Connecting -> Orange
        is ConnectionState.Disconnected -> Red
        is ConnectionState.Error -> DarkRed
    }

    val statusText = when (connectionState) {
        is ConnectionState.Connected -> "Connected"
        is ConnectionState.Connecting -> "Connecting"
        is ConnectionState.Disconnected -> "Disconnected"
        is ConnectionState.Error -> "Error"
    }

    Box(
        modifier = modifier
            .size(12.dp)
            .background(color = color, shape = CircleShape)
            .semantics { connectionStatus = statusText }
            .testTag(TestTags.CONNECTION_INDICATOR)
    )
}

@Preview(showBackground = true)
@Composable
fun ConnectionStatusIndicatorPreview() {
    TrackerAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ConnectionStatusIndicator(connectionState = ConnectionState.Connected)
                Text(text = "Connected")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ConnectionStatusIndicator(connectionState = ConnectionState.Connecting)
                Text(text = "Connecting")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ConnectionStatusIndicator(connectionState = ConnectionState.Disconnected)
                Text(text = "Disconnected")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ConnectionStatusIndicator(connectionState = ConnectionState.Error("Connection failed"))
                Text(text = "Error")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConnectionStatusIndicatorConnectedPreview() {
    TrackerAppTheme {
        ConnectionStatusIndicator(
            connectionState = ConnectionState.Connected,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConnectionStatusIndicatorDisconnectedPreview() {
    TrackerAppTheme {
        ConnectionStatusIndicator(
            connectionState = ConnectionState.Disconnected,
            modifier = Modifier.padding(16.dp)
        )
    }
}

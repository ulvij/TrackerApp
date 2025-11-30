package com.tracker.app.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tracker.app.R
import com.tracker.app.ui.theme.TrackerAppTheme
import com.tracker.domain.connection.model.ConnectionState

/**
 * Top bar component showing connection status and start/stop button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    connectionState: ConnectionState,
    onConnectionToggle: () -> Unit,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
) {
    TopAppBar(
        title = {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title with connection status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                    ConnectionStatusIndicator(connectionState = connectionState)
                }

                // Actions: Theme toggle and Start/Stop button
                Row(
                    modifier = Modifier.padding(end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Theme toggle button
                    IconButton(onClick = onThemeToggle) {
                        Text(
                            text = if (isDarkTheme) "🌙" else "☀️",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    // Start/Stop switch
                    Switch(
                        checked = connectionState.isConnected,
                        onCheckedChange = { onConnectionToggle() }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true, name = "Connected - Light")
@Composable
fun TopBarConnectedPreview() {
    TrackerAppTheme(isDarkTheme = false) {
        TopBar(
            connectionState = ConnectionState.Connected,
            onConnectionToggle = {},
            isDarkTheme = false,
            onThemeToggle = {}
        )
    }
}

@Preview(showBackground = true, name = "Connected - Dark")
@Composable
fun TopBarConnectedDarkPreview() {
    TrackerAppTheme(isDarkTheme = true) {
        TopBar(
            connectionState = ConnectionState.Connected,
            onConnectionToggle = {},
            isDarkTheme = true,
            onThemeToggle = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarDisconnectedPreview() {
    TrackerAppTheme(isDarkTheme = false) {
        TopBar(
            connectionState = ConnectionState.Disconnected,
            onConnectionToggle = {},
            isDarkTheme = false,
            onThemeToggle = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarConnectingPreview() {
    TrackerAppTheme(isDarkTheme = false) {
        TopBar(
            connectionState = ConnectionState.Connecting,
            onConnectionToggle = {},
            isDarkTheme = false,
            onThemeToggle = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarErrorPreview() {
    TrackerAppTheme(isDarkTheme = false) {
        TopBar(
            connectionState = ConnectionState.Error("Connection failed"),
            onConnectionToggle = {},
            isDarkTheme = false,
            onThemeToggle = {}
        )
    }
}

package com.tracker.data.connection.repository

import app.cash.turbine.test
import com.tracker.data.connection.client.WebSocketManager
import com.tracker.domain.connection.model.ConnectionState
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Test cases for ConnectionRepositoryImpl
 */
class ConnectionRepositoryImplTest {

    private lateinit var webSocketManager: WebSocketManager
    private lateinit var connectionRepository: ConnectionRepositoryImpl

    private val connectionStateFlow = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    private val messagesFlow = MutableStateFlow("")

    @Before
    fun setup() {
        webSocketManager = mockk(relaxed = true) {
            every { connectionState } returns connectionStateFlow
            every { messages } returns messagesFlow
        }

        connectionRepository = ConnectionRepositoryImpl(webSocketManager)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `connect should call webSocketManager connect`() {
        // When
        connectionRepository.connect()

        // Then
        verify(exactly = 1) { webSocketManager.connect() }
    }

    @Test
    fun `disconnect should call webSocketManager disconnect`() {
        // When
        connectionRepository.disconnect()

        // Then
        verify(exactly = 1) { webSocketManager.disconnect() }
    }

    @Test
    fun `sendMessage should call webSocketManager sendMessage`() {
        // Given
        val message = "test message"

        // When
        connectionRepository.sendMessage(message)

        // Then
        verify(exactly = 1) { webSocketManager.sendMessage(message) }
    }

    @Test
    fun `observeMessages should return messages flow from webSocketManager`() = runTest {
        // Given
        val testMessage = "test message"

        // When
        connectionRepository.observeMessages().test {
            awaitItem() // Consume initial empty value
            
            messagesFlow.value = testMessage
            val receivedMessage = awaitItem()

            // Then
            assertEquals(testMessage, receivedMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeConnectionState should return connection state flow from webSocketManager`() = runTest {
        // When
        connectionRepository.observeConnectionState().test {
            // Initial state
            assertEquals(ConnectionState.Disconnected, awaitItem())

            // Change to connecting
            connectionStateFlow.value = ConnectionState.Connecting
            assertEquals(ConnectionState.Connecting, awaitItem())

            // Change to connected
            connectionStateFlow.value = ConnectionState.Connected
            assertEquals(ConnectionState.Connected, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeConnectionState should emit error state`() = runTest {
        // When
        connectionRepository.observeConnectionState().test {
            awaitItem() // Initial state

            // Change to error
            val errorState = ConnectionState.Error("Test error")
            connectionStateFlow.value = errorState
            val receivedState = awaitItem()

            // Then
            assertTrue(receivedState is ConnectionState.Error)
            assertEquals("Test error", (receivedState as ConnectionState.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should handle multiple connect calls`() {
        // When
        connectionRepository.connect()
        connectionRepository.connect()
        connectionRepository.connect()

        // Then
        verify(exactly = 3) { webSocketManager.connect() }
    }

    @Test
    fun `should handle multiple disconnect calls`() {
        // When
        connectionRepository.disconnect()
        connectionRepository.disconnect()
        connectionRepository.disconnect()

        // Then
        verify(exactly = 3) { webSocketManager.disconnect() }
    }

    @Test
    fun `should handle multiple messages`() = runTest {
        // Given
        val messages = listOf("message1", "message2", "message3")

        // When
        connectionRepository.observeMessages().test {
            awaitItem() // Consume initial empty value
            
            messages.forEach { message ->
                messagesFlow.value = message
                assertEquals(message, awaitItem())
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should handle empty messages`() = runTest {
        // When
        connectionRepository.observeMessages().test {
            // Initial value is already empty string
            val receivedMessage = awaitItem()

            // Then
            assertEquals("", receivedMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendMessage should handle empty message`() {
        // When
        connectionRepository.sendMessage("")

        // Then
        verify(exactly = 1) { webSocketManager.sendMessage("") }
    }

    @Test
    fun `sendMessage should handle long message`() {
        // Given
        val longMessage = "x".repeat(10000)

        // When
        connectionRepository.sendMessage(longMessage)

        // Then
        verify(exactly = 1) { webSocketManager.sendMessage(longMessage) }
    }

    @Test
    fun `should handle rapid state changes`() = runTest {
        // When
        connectionRepository.observeConnectionState().test {
            awaitItem() // Initial state

            // Rapid state changes
            connectionStateFlow.value = ConnectionState.Connecting
            assertEquals(ConnectionState.Connecting, awaitItem())

            connectionStateFlow.value = ConnectionState.Connected
            assertEquals(ConnectionState.Connected, awaitItem())

            connectionStateFlow.value = ConnectionState.Disconnected
            assertEquals(ConnectionState.Disconnected, awaitItem())

            connectionStateFlow.value = ConnectionState.Connecting
            assertEquals(ConnectionState.Connecting, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}


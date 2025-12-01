package com.tracker.data.theme

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * Unit tests for ThemeRepositoryImpl
 * Tests the theme repository implementation with real DataStore
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ThemeRepositoryImplTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder()

    private lateinit var testContext: Context
    private lateinit var testDataStore: DataStore<Preferences>
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var resources: Resources
    private lateinit var configuration: Configuration
    private lateinit var themeRepository: ThemeRepositoryImpl
    private lateinit var testFile: File

    @Before
    fun setup() {
        // Create test DataStore file
        testFile = tmpFolder.newFile("test_theme_prefs.preferences_pb")
        testDataStore = PreferenceDataStoreFactory.create(
            produceFile = { testFile }
        )

        // Create real Configuration object
        configuration = Configuration()
        configuration.uiMode = Configuration.UI_MODE_NIGHT_NO

        // Mock Resources and Context
        resources = mockk(relaxed = true)
        every { resources.configuration } returns configuration

        testContext = mockk(relaxed = true)
        every { testContext.resources } returns resources
        every { testContext.applicationContext } returns testContext

        // Create repository with test DataStore
        themeRepository = ThemeRepositoryImpl(testContext, testDataStore)
    }

    @After
    fun tearDown() {
        // Clean up test file
        if (::testFile.isInitialized && testFile.exists()) {
            testFile.delete()
        }
    }

    @Test
    fun `isDarkTheme returns null when no preference is saved`() = runTest(testDispatcher) {
        // When
        val result = themeRepository.isDarkTheme.first()

        // Then
        assertNull("Should return null for unsaved preference", result)
    }

    @Test
    fun `toggleTheme from null with light system mode sets dark theme`() = runTest(testDispatcher) {
        // Given
        configuration.uiMode = Configuration.UI_MODE_NIGHT_NO

        // When
        themeRepository.toggleTheme(null)
        val result = themeRepository.isDarkTheme.first()

        // Then
        assertTrue("Should set dark theme when system is light", result == true)
    }

    @Test
    fun `toggleTheme from null with dark system mode sets light theme`() = runTest(testDispatcher) {
        // Given
        configuration.uiMode = Configuration.UI_MODE_NIGHT_YES

        // When
        themeRepository.toggleTheme(null)
        val result = themeRepository.isDarkTheme.first()

        // Then
        assertFalse("Should set light theme when system is dark", result == true)
    }

    @Test
    fun `toggleTheme from dark to light works correctly`() = runTest(testDispatcher) {
        // Given
        themeRepository.toggleTheme(null) // Sets to dark
        var theme = themeRepository.isDarkTheme.first()
        assertTrue("Should start as dark", theme == true)

        // When
        themeRepository.toggleTheme(true)
        theme = themeRepository.isDarkTheme.first()

        // Then
        assertFalse("Should toggle to light", theme == true)
    }

    @Test
    fun `toggleTheme from light to dark works correctly`() = runTest(testDispatcher) {
        // Given - First set to dark, then to light
        themeRepository.toggleTheme(null)
        themeRepository.toggleTheme(true)
        var theme = themeRepository.isDarkTheme.first()
        assertFalse("Should start as light", theme == true)

        // When
        themeRepository.toggleTheme(false)
        theme = themeRepository.isDarkTheme.first()

        // Then
        assertTrue("Should toggle to dark", theme == true)
    }

    @Test
    fun `theme preference persists in DataStore`() = runTest(testDispatcher) {
        // When
        themeRepository.toggleTheme(null)
        val first = themeRepository.isDarkTheme.first()
        val second = themeRepository.isDarkTheme.first()

        // Then
        assertEquals("Theme should persist", first, second)
    }

    @Test
    fun `UI_MODE_NIGHT_MASK correctly extracts night mode`() {
        // Test night mode extraction
        val nightYes = Configuration.UI_MODE_NIGHT_YES and Configuration.UI_MODE_NIGHT_MASK
        val nightNo = Configuration.UI_MODE_NIGHT_NO and Configuration.UI_MODE_NIGHT_MASK

        assertEquals(Configuration.UI_MODE_NIGHT_YES, nightYes)
        assertEquals(Configuration.UI_MODE_NIGHT_NO, nightNo)
    }
}


package com.tracker.app.ui

import app.cash.turbine.test
import com.tracker.domain.theme.usecase.ObserveIsDarkThemeUseCase
import com.tracker.domain.theme.usecase.ToggleThemeUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var observeIsDarkThemeUseCase: ObserveIsDarkThemeUseCase
    private lateinit var toggleThemeUseCase: ToggleThemeUseCase

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        observeIsDarkThemeUseCase = mockk()
        toggleThemeUseCase = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========== Initialization Tests ==========

    @Test
    fun `initial theme should be null when no preference saved`() = testScope.runTest {
        every { observeIsDarkThemeUseCase.execute(Unit) } returns MutableStateFlow<Boolean?>(null)

        viewModel = MainViewModel(observeIsDarkThemeUseCase, toggleThemeUseCase)

        viewModel.isDarkTheme.test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial theme should reflect saved value`() = testScope.runTest {
        val themeFlow = MutableStateFlow(true)
        every { observeIsDarkThemeUseCase.execute(Unit) } returns themeFlow

        viewModel = MainViewModel(observeIsDarkThemeUseCase, toggleThemeUseCase)

        viewModel.isDarkTheme.test {
            assertEquals(null, awaitItem()) // initial value from stateIn
            assertEquals(true, awaitItem()) // actual value from your flow
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `toggleTheme should not throw when theme is null`() = testScope.runTest {
        val themeFlow = MutableStateFlow<Boolean?>(null)
        every { observeIsDarkThemeUseCase.execute(Unit) } returns themeFlow

        viewModel = MainViewModel(observeIsDarkThemeUseCase, toggleThemeUseCase)

        // When & Then - should not throw (launch is extension function, hard to verify)
        viewModel.toggleTheme()
        testScheduler.advanceUntilIdle()
    }

    // ========== StateFlow Updates Tests ==========

    @Test
    fun `isDarkTheme flow should emit updates when theme changes`() = testScope.runTest {
        val themeFlow = MutableStateFlow<Boolean?>(true)
        every { observeIsDarkThemeUseCase.execute(Unit) } returns themeFlow

        viewModel = MainViewModel(observeIsDarkThemeUseCase, toggleThemeUseCase)

        viewModel.isDarkTheme.test {
            assertEquals(null, awaitItem())
            assertEquals(true, awaitItem())

            themeFlow.value = false
            assertEquals(false, awaitItem())

            themeFlow.value = null
            assertEquals(null, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}








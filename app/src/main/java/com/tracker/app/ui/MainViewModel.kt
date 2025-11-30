package com.tracker.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.domain.theme.usecase.ObserveIsDarkThemeUseCase
import com.tracker.domain.theme.usecase.ToggleThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    observeIsDarkThemeUseCase: ObserveIsDarkThemeUseCase,
    private val toggleThemeUseCase: ToggleThemeUseCase
): ViewModel() {

    // Use case returns null when no preference saved (use system default)
    val isDarkTheme: StateFlow<Boolean?> = observeIsDarkThemeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // null means use system default
        )

    fun toggleTheme() {
        viewModelScope.launch {
            toggleThemeUseCase(isDarkTheme.value)
        }
    }

}
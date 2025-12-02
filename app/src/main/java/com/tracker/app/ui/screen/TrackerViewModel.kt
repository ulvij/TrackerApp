package com.tracker.app.ui.screen

import androidx.lifecycle.viewModelScope
import com.tracker.app.base.BaseViewModel
import com.tracker.app.ui.model.toUIModels
import com.tracker.domain.connection.usecase.ObserveConnectionStateUseCase
import com.tracker.domain.connection.usecase.StartConnectionUseCase
import com.tracker.domain.connection.usecase.StopConnectionUseCase
import com.tracker.domain.stock.usecase.ObserveStockPricesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * ViewModel for the Price Tracker screen with Hilt injection
 */
@HiltViewModel
class TrackerViewModel @Inject constructor(
    private val startConnectionUseCase: StartConnectionUseCase,
    private val stopConnectionUseCase: StopConnectionUseCase,
    private val observeStockPricesUseCase: ObserveStockPricesUseCase,
    private val observeConnectionStateUseCase: ObserveConnectionStateUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow(TrackerUIState())
    val state: StateFlow<TrackerUIState> = _state.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        // Observe price updates
        observeStockPricesUseCase
            .execute(Unit)
            .onEach { stocks -> _state.value = _state.value.copy(stocks = stocks.toUIModels()) }
            .launchIn(viewModelScope)

        // Observe connection state
        observeConnectionStateUseCase.execute(Unit)
            .onEach { connectionState -> _state.value = _state.value.copy(connectionState = connectionState) }
            .launchIn(viewModelScope)
    }

    fun startConnection() {
        if (_state.value.connectionState.isConnected) return
        startConnectionUseCase.launch(Unit)
    }

    fun stopConnection() {
        stopConnectionUseCase.launch(Unit)
    }

    fun toggleTracking() {
        if (_state.value.connectionState.isConnected) {
            stopConnection()
        } else {
            startConnection()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopConnection()
    }
}
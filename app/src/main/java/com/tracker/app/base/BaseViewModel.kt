package com.tracker.app.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.domain.base.BaseUseCase
import com.tracker.domain.base.CompletionBlock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class BaseViewModel() : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    protected fun handleError(t: Throwable) {
        Log.e("BaseViewModel", "Unhandled error", t)
    }

    protected fun <P, R, U : BaseUseCase<P, R>> U.launch(
        param: P,
        loadingHandle: (Boolean) -> Unit = ::setLoading,
        block: CompletionBlock<R> = {}
    ) {
        viewModelScope.launch {
            val actualRequest = BaseUseCase.Request<R>().apply(block)

            val proxy: CompletionBlock<R> = {
                onStart = {
                    loadingHandle(true)
                    actualRequest.onStart?.invoke()
                }
                onSuccess = {
                    loadingHandle(false)
                    actualRequest.onSuccess(it)
                }
                onCancel = {
                    loadingHandle(false)
                    actualRequest.onCancel?.invoke(it)
                }
                onError = {
                    loadingHandle(false)
                    actualRequest.onError?.invoke(it) ?: handleError(it)
                }
            }
            execute(param, proxy)
        }
    }

    protected fun setLoading(state: Boolean) {
        if (_isLoading.value == state)
            return

        viewModelScope.launch {
            _isLoading.emit(state)
        }
    }

    fun isLoading(): Boolean {
        return isLoading.value
    }

}
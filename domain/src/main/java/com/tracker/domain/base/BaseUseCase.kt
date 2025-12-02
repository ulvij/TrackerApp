package com.tracker.domain.base

import com.tracker.domain.error.ErrorConverter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

typealias CompletionBlock<T> = BaseUseCase.Request<T>.() -> Unit

abstract class BaseUseCase<P, R>(
    private val executionContext: CoroutineContext,
    private val errorConverter: ErrorConverter,
) {
    protected abstract suspend fun executeOnBackground(params: P): R

    suspend fun execute(params: P, block: CompletionBlock<R> = {}) {
        val request = Request<R>().apply(block).also { it.onStart?.invoke() }
        try {
            val result = withContext(executionContext) { executeOnBackground(params) }
            request.onSuccess(result)
        } catch (e: CancellationException) {
            request.onCancel?.invoke(e)
        } catch (e: Throwable) {
            request.onError?.invoke(errorConverter.convert(e))
        }
    }

    suspend fun executeSync(params: P): R {
        try {
            return withContext(currentCoroutineContext()) { executeOnBackground(params) }
        } catch (e: Throwable) {
            throw errorConverter.convert(e)
        }
    }

    class Request<T> {
        var onSuccess: (T) -> Unit = {}
        var onStart: (() -> Unit)? = null
        var onError: ((Throwable) -> Unit)? = null
        var onCancel: ((CancellationException) -> Unit)? = null
    }
}
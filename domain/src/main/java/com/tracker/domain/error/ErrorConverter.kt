package com.tracker.domain.error

interface ErrorConverter {
    fun convert(t: Throwable): Throwable
}
package com.tracker.domain.error

import javax.inject.Inject

class ErrorConverterImpl @Inject constructor() : ErrorConverter {

    override fun convert(t: Throwable): Throwable {
        // we can customize error conversion logic here
        return t
    }

}
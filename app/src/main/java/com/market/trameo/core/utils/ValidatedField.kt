package com.market.trameo.core.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ValidatedField<T>(
    private val initialValue: T,
    private val validator: (T) -> String?
) {
    var value by mutableStateOf(initialValue)
        private set

    var isTouched by mutableStateOf(false)
        private set

    val error: String?
        get() = if (isTouched) validator(value) else null

    val isValid: Boolean
        get() = validator(value) == null

    fun onChange(newValue: T) {
        value = newValue
        isTouched = true
    }

    fun forceShowError() {
        isTouched = true
    }

    fun reset() {
        value = initialValue
        isTouched = false
    }
}
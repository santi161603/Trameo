package com.market.trameo.core.session

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SessionDataStore @Inject constructor() {

    private val _userId = MutableStateFlow("seed-user-1")
    val userId: StateFlow<String> = _userId.asStateFlow()

    fun updateUserId(value: String) {
        _userId.value = value
    }
}


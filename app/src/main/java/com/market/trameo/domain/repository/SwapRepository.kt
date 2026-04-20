package com.market.trameo.domain.repository

import com.market.trameo.domain.model.SwapObject
import kotlinx.coroutines.flow.StateFlow

interface SwapRepository {
    val objects: StateFlow<List<SwapObject>>
    suspend fun publish(swapObject: SwapObject): SwapObject
    fun findById(id: String): SwapObject?
}

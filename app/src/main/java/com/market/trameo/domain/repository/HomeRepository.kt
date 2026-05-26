package com.market.trameo.domain.repository

import com.market.trameo.domain.model.HomeObject
import com.market.trameo.domain.model.ModerationStatus
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getPublishedObjects(): Flow<List<HomeObject>>
    fun getObjectsByOwner(ownerId: String): Flow<List<HomeObject>>
    fun getPendingObjects(): Flow<List<HomeObject>>
    suspend fun getObjectById(objectId: String): HomeObject?
    suspend fun updateObjectStatus(objectId: String, status: ModerationStatus): Result<Unit>
}

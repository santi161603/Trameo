package com.market.trameo.data.repository

import android.net.Uri
import com.market.trameo.domain.model.ModerationStatus
import com.market.trameo.domain.model.ObjectCategory
import com.market.trameo.domain.model.ObjectCondition
import com.market.trameo.domain.model.SwapObject
import com.market.trameo.domain.repository.SwapRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

@Singleton
class SwapRepositoryImpl @Inject constructor() : SwapRepository {

    private val _objects = MutableStateFlow(
        listOf(
            SwapObject(
                id = "seed-swap-1",
                ownerId = "seed-user-1",
                photos = listOf(Uri.parse("https://picsum.photos/seed/swap-2/900/700")),
                name = "Patineta urbana",
                description = "Patineta en buen estado, ideal para trayectos cortos.",
                category = ObjectCategory.DEPORTES,
                condition = ObjectCondition.BUENO,
                exchangePreferences = "Busco libro o audifonos",
                moderationStatus = ModerationStatus.PUBLICADO
            )
        )
    )
    override val objects: StateFlow<List<SwapObject>> = _objects.asStateFlow()

    override suspend fun publish(swapObject: SwapObject): SwapObject = withContext(Dispatchers.IO) {
        val objectToSave = swapObject.copy(id = swapObject.id.ifBlank { UUID.randomUUID().toString() })
        _objects.value = _objects.value + objectToSave
        objectToSave
    }

    override fun findById(id: String): SwapObject? = _objects.value.firstOrNull { it.id == id }
}

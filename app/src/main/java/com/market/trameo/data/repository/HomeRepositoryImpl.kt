package com.market.trameo.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.market.trameo.domain.model.HomeObject
import com.market.trameo.domain.model.ModerationStatus
import com.market.trameo.domain.repository.HomeRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HomeRepository {

    private companion object {
        const val OBJETOS_COLLECTION = "objetos"
    }

    override fun getPublishedObjects(): Flow<List<HomeObject>> = callbackFlow {
        val subscription = firestore.collection(OBJETOS_COLLECTION)
            .whereEqualTo("moderationStatus", ModerationStatus.PUBLICADO.name)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val objects = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(HomeObject::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                
                trySend(objects)
            }
        
        awaitClose { subscription.remove() }
    }

    override fun getObjectsByOwner(ownerId: String): Flow<List<HomeObject>> = callbackFlow {
        val subscription = firestore.collection(OBJETOS_COLLECTION)
            .whereEqualTo("ownerId", ownerId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val objects = snapshot?.documents?.mapNotNull(::documentToHomeObject) ?: emptyList()
                trySend(objects)
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun getObjectById(objectId: String): HomeObject? = withContext(Dispatchers.IO) {
        runCatching {
            val snapshot = firestore.collection(OBJETOS_COLLECTION).document(objectId).get().await()
            snapshot.takeIf { it.exists() }?.let(::documentToHomeObject)
        }.getOrNull()
    }

    private fun documentToHomeObject(doc: DocumentSnapshot): HomeObject? {
        val data = doc.data ?: return null
        val moderationStatus = (data["moderationStatus"] as? String)
            ?.let { runCatching { ModerationStatus.valueOf(it) }.getOrNull() }
            ?: ModerationStatus.PENDIENTE_VERIFICACION
        val photos = (data["photos"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        val timestamp = (data["timestamp"] as? Number)?.toLong() ?: 0L

        return HomeObject(
            id = doc.id,
            ownerId = data["ownerId"] as? String ?: "",
            photos = photos,
            name = data["name"] as? String ?: "",
            description = data["description"] as? String ?: "",
            category = data["category"] as? String ?: "",
            condition = data["condition"] as? String ?: "",
            exchangePreferences = data["exchangePreferences"] as? String ?: "",
            moderationStatus = moderationStatus,
            timestamp = timestamp
        )
    }
}

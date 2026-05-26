package com.market.trameo.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.market.trameo.domain.model.Propuesta
import com.market.trameo.domain.repository.PropuestaRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Singleton
class PropuestaRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PropuestaRepository {

    private companion object {
        const val PROPUESTAS_COLLECTION = "propuestas"
    }

    override suspend fun crearPropuesta(propuesta: Propuesta): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val doc = firestore.collection(PROPUESTAS_COLLECTION)
                .document(propuesta.id.ifBlank { firestore.collection(PROPUESTAS_COLLECTION).document().id })
            val payload = propuesta.copy(id = doc.id)
            doc.set(payload.toFirestoreMap()).await()
            Unit
        }
    }

    override fun getMisPropuestasEnviadas(uid: String): Flow<List<Propuesta>> = callbackFlow {
        val subscription = firestore.collection(PROPUESTAS_COLLECTION)
            .whereEqualTo("proponenteUid", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val propuestas = snapshot?.documents?.mapNotNull(::documentToPropuesta) ?: emptyList()
                trySend(propuestas)
            }

        awaitClose { subscription.remove() }
    }

    override fun getPropuestasRecibidas(uid: String): Flow<List<Propuesta>> = callbackFlow {
        val subscription = firestore.collection(PROPUESTAS_COLLECTION)
            .whereEqualTo("receptorUid", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val propuestas = snapshot?.documents?.mapNotNull(::documentToPropuesta) ?: emptyList()
                trySend(propuestas)
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun actualizarEstadoPropuesta(propuestaId: String, nuevoEstado: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            firestore.collection(PROPUESTAS_COLLECTION)
                .document(propuestaId)
                .update("estado", nuevoEstado)
                .await()
            Unit
        }
    }

    override suspend fun getPropuestaById(id: String): Propuesta? = withContext(Dispatchers.IO) {
        runCatching {
            val snapshot = firestore.collection(PROPUESTAS_COLLECTION).document(id).get().await()
            snapshot.takeIf { it.exists() }?.let(::documentToPropuesta)
        }.getOrNull()
    }

    private fun documentToPropuesta(doc: DocumentSnapshot): Propuesta? {
        val data = doc.data ?: return null
        return Propuesta(
            id = doc.id,
            proponenteUid = data["proponenteUid"] as? String ?: "",
            receptorUid = data["receptorUid"] as? String ?: "",
            objetoDeseadoUid = data["objetoDeseadoUid"] as? String ?: "",
            objetoOfrecidoUid = data["objetoOfrecidoUid"] as? String ?: "",
            estado = data["estado"] as? String ?: "PENDIENTE",
            timestamp = (data["timestamp"] as? Number)?.toLong() ?: 0L
        )
    }

    private fun Propuesta.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "proponenteUid" to proponenteUid,
            "receptorUid" to receptorUid,
            "objetoDeseadoUid" to objetoDeseadoUid,
            "objetoOfrecidoUid" to objetoOfrecidoUid,
            "estado" to estado,
            "timestamp" to timestamp
        )
    }
}

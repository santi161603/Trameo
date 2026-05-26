package com.market.trameo.data.service

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.market.trameo.domain.model.CreateHomeObjectRequest
import com.market.trameo.domain.model.HomeObject
import com.market.trameo.domain.model.ModerationStatus
import com.market.trameo.domain.service.HomeObjectCreationService
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class HomeObjectCreationServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : HomeObjectCreationService {

    private companion object {
        const val OBJETOS_COLLECTION = "objetos"
    }

    override suspend fun create(request: CreateHomeObjectRequest): HomeObject = withContext(Dispatchers.IO) {
        val ownerId = auth.currentUser?.uid ?: error("Debes iniciar sesion para crear un objeto")
        val document = firestore.collection(OBJETOS_COLLECTION).document()
        val uploadedPhotos = request.photos.map { uploadObjectPhoto(Uri.parse(it)) }
        val homeObject = HomeObject(
            id = document.id,
            ownerId = ownerId,
            photos = uploadedPhotos,
            name = request.name.trim(),
            description = request.description.trim(),
            category = request.category.trim(),
            condition = request.condition.trim(),
            exchangePreferences = request.exchangePreferences.trim(),
            moderationStatus = ModerationStatus.PENDIENTE_VERIFICACION,
            timestamp = System.currentTimeMillis()
        )

        document.set(homeObject.toFirestoreMap()).await()
        homeObject
    }

    private suspend fun uploadObjectPhoto(uri: Uri): String = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(uri)
                .option("resource_type", "image")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) = Unit

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) = Unit

                    override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                        val secureUrl = resultData["secure_url"] as? String
                        if (secureUrl.isNullOrBlank()) {
                            continuation.resumeWithException(Exception("Cloudinary no devolvió secure_url"))
                        } else {
                            continuation.resume(secureUrl)
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        continuation.resumeWithException(Exception(error?.description ?: "Error subiendo imagen"))
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        continuation.resumeWithException(Exception(error?.description ?: "Reintento de subida"))
                    }
                })
                .dispatch()
        }
    }

    private fun HomeObject.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "ownerId" to ownerId,
            "photos" to photos,
            "name" to name,
            "description" to description,
            "category" to category,
            "condition" to condition,
            "exchangePreferences" to exchangePreferences,
            "moderationStatus" to moderationStatus.name,
            "timestamp" to timestamp
        )
    }
}

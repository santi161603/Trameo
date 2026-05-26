package com.market.trameo.data.repository

import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.market.trameo.domain.model.User
import com.market.trameo.domain.model.UserRole
import com.market.trameo.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserRepository {

    private companion object {
        const val USERS_COLLECTION = "usuarios"
        const val ADMIN_EMAIL = "santiagoaceroospina@gmail.com"
    }

    private val _users = MutableStateFlow<List<User>>(emptyList())
    override val users: StateFlow<List<User>> = _users.asStateFlow()

    override suspend fun save(user: User): User = withContext(Dispatchers.IO) {
        val email = user.email.trim()
        val password = user.password ?: throw IllegalArgumentException("Password is required for registration")

        Log.d("UserRepositoryImpl", "DEBUG [1] >>> Iniciando save para $email")

        suspendCancellableCoroutine { continuation ->
            Log.d("UserRepositoryImpl", "DEBUG [2] >>> Ejecutando createUser (Email: $email)")

            // Timeout de 20 segundos para no quedar colgados
            val handler = android.os.Handler(android.os.Looper.getMainLooper())
            val timeoutRunnable = Runnable {
                if (continuation.isActive) {
                    Log.e("UserRepositoryImpl", "DEBUG [!] >>> TIMEOUT: Firebase Auth no respondió. Revisa SHA-1.")
                    continuation.resumeWithException(Exception("Error de comunicación con Google. Verifica el SHA-1 en la consola de Firebase."))
                }
            }
            handler.postDelayed(timeoutRunnable, 20000)

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    handler.removeCallbacks(timeoutRunnable)
                    val uid = authResult.user?.uid ?: ""
                    Log.d("UserRepositoryImpl", "DEBUG [3] >>> AUTH EXITOSO. UID: $uid")

                    kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                        runCatching {
                            val remotePhotoUrl = user.profilePhotoUri
                                ?.takeIf { it.isNotBlank() }
                                ?.let { uploadProfilePhoto(Uri.parse(it)) }

                            val normalizedUser = user.copy(
                                id = uid,
                                email = email,
                                password = null,
                                role = if (email.equals(ADMIN_EMAIL, ignoreCase = true)) UserRole.ADMIN else UserRole.CLIENTE,
                                profilePhotoUri = remotePhotoUrl
                            )

                            Log.d("UserRepositoryImpl", "DEBUG [4] >>> Guardando en Firestore...")
                            firestore.collection(USERS_COLLECTION).document(uid)
                                .set(normalizedUser.toFirestoreMap())
                                .await()
                            normalizedUser
                        }.onSuccess { normalizedUser ->
                            Log.d("UserRepositoryImpl", "DEBUG [5] >>> FIRESTORE EXITOSO")
                            if (continuation.isActive) continuation.resume(normalizedUser)
                        }.onFailure { error ->
                            Log.e("UserRepositoryImpl", "DEBUG [!] >>> FIRESTORE ERROR: ${error.message}")
                            if (continuation.isActive) continuation.resumeWithException(error)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    handler.removeCallbacks(timeoutRunnable)
                    Log.e("UserRepositoryImpl", "DEBUG [!] >>> AUTH FALLÓ: ${e.message}")
                    if (continuation.isActive) continuation.resumeWithException(e)
                }
        }
    }

    override suspend fun uploadProfilePhoto(uri: Uri): String = withContext(Dispatchers.IO) {
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

    override suspend fun login(email: String, password: String): User? = withContext(Dispatchers.IO) {
        try {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
            val uid = auth.currentUser?.uid ?: return@withContext null
            findById(uid)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun findById(userId: String): User? = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection(USERS_COLLECTION).document(userId).get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Helper to fetch user data (using the suspend findById now)
    suspend fun getById(userId: String): User? = findById(userId)

    override suspend fun sendPasswordResetEmail(email: String): Unit = withContext(Dispatchers.IO) {
        try {
            auth.sendPasswordResetEmail(email.trim()).await()
        } catch (e: Exception) {
            throw e
        }
    }

    private fun User.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "city" to city,
            "address" to address,
            "phoneNumber" to phoneNumber,
            "latitude" to latitude,
            "longitude" to longitude,
            "registrationDate" to registrationDate,
            "score" to score,
            "profilePhotoUri" to profilePhotoUri,
            "role" to role.name
        )
    }
}

package com.market.trameo.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.market.trameo.domain.model.Review
import com.market.trameo.domain.repository.ReviewRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReviewRepository {

    companion object {
        private const val COLLECTION_REVIEWS = "reseñas"
    }

    override fun getReviewsForUser(targetUserId: String): Flow<List<Review>> = callbackFlow {
        val listener = firestore.collection(COLLECTION_REVIEWS)
            .whereEqualTo("targetUserId", targetUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val reviews = snapshot?.documents?.mapNotNull { it.toObject(Review::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(reviews.sortedByDescending { it.timestamp })
            }

        awaitClose { listener.remove() }
    }

    override suspend fun createReview(review: Review): Result<Unit> = try {
        val ref = firestore.collection(COLLECTION_REVIEWS).document()
        val reviewToSave = review.copy(id = ref.id)
        ref.set(reviewToSave).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}


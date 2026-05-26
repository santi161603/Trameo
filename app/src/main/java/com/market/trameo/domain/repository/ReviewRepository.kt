package com.market.trameo.domain.repository

import com.market.trameo.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun getReviewsForUser(targetUserId: String): Flow<List<Review>>
    suspend fun createReview(review: Review): Result<Unit>
}


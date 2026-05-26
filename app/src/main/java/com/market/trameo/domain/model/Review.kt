package com.market.trameo.domain.model

data class Review(
    val id: String = "",
    val reviewerId: String = "",
    val reviewerName: String = "",
    val reviewerPhotoUrl: String? = null,
    val targetUserId: String = "",
    val rating: Int = 0, // 0 to 5
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

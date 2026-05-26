package com.market.trameo.domain.model

data class HomeObject(
    val id: String = "",
    val ownerId: String = "",
    val photos: List<String> = emptyList(),
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val condition: String = "",
    val exchangePreferences: String = "",
    val moderationStatus: ModerationStatus = ModerationStatus.PENDIENTE_VERIFICACION,
    val timestamp: Long = System.currentTimeMillis()
)

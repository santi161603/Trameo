package com.market.trameo.domain.model

import android.net.Uri

data class SwapObject(
    val id: String = "",
    val ownerId: String,
    val photos: List<Uri>,
    val name: String,
    val description: String,
    val category: ObjectCategory,
    val condition: ObjectCondition,
    val exchangePreferences: String,
    val moderationStatus: ModerationStatus = ModerationStatus.PENDIENTE_VERIFICACION
)

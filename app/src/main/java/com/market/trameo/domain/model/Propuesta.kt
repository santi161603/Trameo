package com.market.trameo.domain.model

data class Propuesta(
    val id: String = "",
    val proponenteUid: String = "",
    val receptorUid: String = "",
    val objetoDeseadoUid: String = "",
    val objetoOfrecidoUid: String = "",
    val estado: String = "PENDIENTE",
    val timestamp: Long = System.currentTimeMillis()
)

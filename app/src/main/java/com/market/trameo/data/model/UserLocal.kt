package com.market.trameo.data.model

/**
 * Modelo de datos local temporal para simular autenticacion.
 */
data class UserLocal(
    val id: String,
    val email: String,
    val password: String,
    val displayName: String
)


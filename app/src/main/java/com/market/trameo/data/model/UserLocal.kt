package com.market.trameo.data.model

/**
 * Modelo de datos local temporal para simular autenticacion.
 */
data class UserLocal(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val city: String,
    val address: String,
    val phoneNumber: String,
    val profilePhotoUri: String?
)

package com.market.trameo.domain.model

/**
 * Entidad de dominio usada por los casos de autenticacion.
 */
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val password: String? = null,
    val city: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val registrationDate: Long = System.currentTimeMillis(),
    val score: Int = 0,
    val profilePhotoUri: String? = null,
    val role: UserRole = UserRole.CLIENTE
)

package com.market.trameo.domain.model

/**
 * Entidad de dominio usada por los casos de autenticacion.
 */
data class User(
    val id: String = "",
    val name: String,
    val email: String,
    val password: String,
    val city: String,
    val address: String,
    val phoneNumber: String,
    val profilePhotoUri: String? = null,
    val role: UserRole = UserRole.CLIENTE
)

package com.market.trameo.data.model

import com.market.trameo.domain.model.UserRole

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
    val profilePhotoUri: String?,
    val role: UserRole = UserRole.CLIENTE
)

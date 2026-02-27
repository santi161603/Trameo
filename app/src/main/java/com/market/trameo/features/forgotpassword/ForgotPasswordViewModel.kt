package com.market.trameo.features.forgotpassword

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.market.trameo.core.utils.RequestResult
import com.market.trameo.core.utils.ValidatedField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ForgotPasswordViewModel : ViewModel(){

    // Campo de email con validación usando ValidatedField
    val email = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El correo electrónico es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value)
                .matches() -> "Ingresa un correo electrónico válido"

            else -> null
        }
    }

    // Estado del resultado de la solicitud de recuperación
    private val _recoveryResult = MutableStateFlow<RequestResult?>(null)
    val recoveryResult: StateFlow<RequestResult?> = _recoveryResult.asStateFlow()

    // Indica si el formulario es válido
    val isFormValid: Boolean
        get() = email.isValid

    // Simula el envío del enlace de recuperación
    fun sendRecoveryEmail() {
        if (isFormValid) {
            // Simulación: solo el correo registrado recibe el enlace
            _recoveryResult.value = if (email.value == "carlos@email.com") {
                RequestResult.Success("Se ha enviado un enlace de recuperación a ${email.value}")
            } else {
                RequestResult.Failure("No se encontró una cuenta asociada a este correo")
            }
        }
    }

    // Resetea el resultado después de mostrarlo en el Snackbar
    fun resetRecoveryResult() {
        _recoveryResult.value = null
    }

    // Resetea todo el formulario
    fun resetForm() {
        email.reset()
        resetRecoveryResult()
    }
}
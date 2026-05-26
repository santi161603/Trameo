package com.market.trameo.features.forgotpassword

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.core.utils.RequestResult
import com.market.trameo.core.utils.ValidatedField
import com.market.trameo.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

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

    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog: StateFlow<Boolean> = _showSuccessDialog.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Indica si el formulario es válido
    val isFormValid: Boolean
        get() = email.isValid

    // Envía el enlace de recuperación con Firebase
    fun sendRecoveryEmail() {
        if (isFormValid) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    userRepository.sendPasswordResetEmail(email.value)
                    _showSuccessDialog.value = true
                    _recoveryResult.value = RequestResult.Success("Correo enviado")
                } catch (e: Exception) {
                    _recoveryResult.value = RequestResult.Failure(
                        when {
                            e.message?.contains("user-not-found") == true -> "No hay ningún usuario registrado con este correo"
                            e.message?.contains("invalid-email") == true -> "El formato del correo es inválido"
                            else -> "Error al enviar el correo: ${e.localizedMessage}"
                        }
                    )
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            email.forceShowError()
        }
    }

    fun dismissSuccessDialog() {
        _showSuccessDialog.value = false
    }

    // Resetea el resultado después de mostrarlo en el Snackbar
    fun resetRecoveryResult() {
        _recoveryResult.value = null
    }

    // Resetea todo el formulario
    fun resetForm() {
        email.reset()
        resetRecoveryResult()
        _showSuccessDialog.value = false
    }
}

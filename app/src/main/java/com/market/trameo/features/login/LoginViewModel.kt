package com.market.trameo.features.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.core.auth.MockAuthStore
import com.market.trameo.core.utils.RequestResult
import com.market.trameo.core.utils.ValidatedField
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authStore: MockAuthStore
) : ViewModel() {

    // Campos validados usando ValidatedField
    val email = ValidatedField(authStore.registeredEmail) { value ->
        when {
            value.isEmpty() -> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Ingresa un email válido"
            else -> null
        }
    }

    val password = ValidatedField(authStore.currentPasswordValue()) { value ->
        when {
            value.isEmpty() -> "La contraseña es obligatoria"
            value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    // Estado para el resultado del login (privado mutable, público inmutable)
    private val _loginResult = MutableStateFlow<RequestResult?>(null)
    val loginResult: StateFlow<RequestResult?> = _loginResult.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Validación del formulario
    val isFormValid: Boolean
        get() = email.isValid && password.isValid

    // Función para realizar el login
    fun login() {
        if (!isFormValid) return

        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Simulación de una llamada de red con delay
                delay(1500)

                // Simulación de un proceso de login con datos estáticos
                _loginResult.value = if (authStore.isValidLogin(email.value, password.value)) {
                    RequestResult.Success("Login exitoso")
                } else {
                    RequestResult.Failure("Credenciales inválidas")
                }
            } catch (e: Exception) {
                _loginResult.value = RequestResult.Failure("Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Resetear el resultado del login
    fun resetLoginResult() {
        _loginResult.value = null
    }

    // Resetear el formulario completo
    fun resetForm() {
        email.reset()
        password.reset()
        _loginResult.value = null
    }
}
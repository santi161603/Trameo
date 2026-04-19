package com.market.trameo.features.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class RegisterViewModel @Inject constructor() : ViewModel() {

    // Campos validados usando ValidatedField
    val nombre = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El nombre es obligatorio"
            value.length < 2 -> "El nombre debe tener al menos 2 caracteres"
            else -> null
        }
    }

    val apellidos = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "Los apellidos son obligatorios"
            value.length < 2 -> "Los apellidos deben tener al menos 2 caracteres"
            else -> null
        }
    }

    val cedula = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La cédula es obligatoria"
            value.length < 6 -> "La cédula debe tener al menos 6 dígitos"
            !value.all { it.isDigit() } -> "La cédula solo debe contener números"
            else -> null
        }
    }

    val correo = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Ingresa un correo válido"
            else -> null
        }
    }

    val password = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La contraseña es obligatoria"
            value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            !value.any { it.isDigit() } -> "La contraseña debe contener al menos un número"
            else -> null
        }
    }

    val repetirPassword = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "Debes confirmar la contraseña"
            value != password.value -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    val celular = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El celular es obligatorio"
            value.length < 10 -> "El celular debe tener al menos 10 dígitos"
            !value.all { it.isDigit() || it == '+' || it == ' ' } -> "Formato de celular inválido"
            else -> null
        }
    }

    val fechaNacimiento = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "La fecha de nacimiento es obligatoria"
            else -> null
        }
    }

    // Estado para el resultado del registro
    private val _registerResult = MutableStateFlow<RequestResult?>(null)
    val registerResult: StateFlow<RequestResult?> = _registerResult.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Validación del formulario completo
    val isFormValid: Boolean
        get() = nombre.isValid &&
                apellidos.isValid &&
                cedula.isValid &&
                correo.isValid &&
                password.isValid &&
                repetirPassword.isValid &&
                celular.isValid &&
                fechaNacimiento.isValid

    // Función para realizar el registro
    fun register() {
        if (!isFormValid) {
            // ⭐ USAR forceShowError() en lugar de showError = true
            // Mostrar todos los errores si el formulario no es válido
            nombre.forceShowError()
            apellidos.forceShowError()
            cedula.forceShowError()
            correo.forceShowError()
            password.forceShowError()
            repetirPassword.forceShowError()
            celular.forceShowError()
            fechaNacimiento.forceShowError()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Simulación de una llamada de red con delay
                delay(2000)

                // Simulación de registro exitoso
                _registerResult.value = RequestResult.Success(
                    "Registro exitoso. Bienvenido ${nombre.value}!"
                )
            } catch (e: Exception) {
                _registerResult.value = RequestResult.Failure(
                    "Error al registrar: ${e.message}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Resetear el resultado del registro
    fun resetRegisterResult() {
        _registerResult.value = null
    }

    // Resetear el formulario completo
    fun resetForm() {
        nombre.reset()
        apellidos.reset()
        cedula.reset()
        correo.reset()
        password.reset()
        repetirPassword.reset()
        celular.reset()
        fechaNacimiento.reset()
        _registerResult.value = null
    }

    // Validar que las contraseñas coincidan cuando cambia repetirPassword
    fun validatePasswordMatch() {
        // Solo revalidar si ya se estaba mostrando el error
        if (repetirPassword.error != null) {
            repetirPassword.onChange(repetirPassword.value)
        }
    }
}
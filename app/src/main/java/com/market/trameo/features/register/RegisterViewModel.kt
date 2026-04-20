package com.market.trameo.features.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.core.utils.RequestResult
import com.market.trameo.core.utils.ValidatedField
import com.market.trameo.domain.model.User
import com.market.trameo.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    val name = ValidatedField("") { value ->
        when {
            value.isBlank() -> "El nombre es obligatorio"
            value.trim().length < 3 -> "Ingresa al menos 3 caracteres"
            else -> null
        }
    }

    val city = ValidatedField("") { value ->
        if (value.isBlank()) "La ciudad es obligatoria" else null
    }

    val address = ValidatedField("") { value ->
        when {
            value.isBlank() -> "La direccion es obligatoria"
            value.trim().length < 5 -> "Ingresa una direccion valida"
            else -> null
        }
    }

    val phoneNumber = ValidatedField("") { value ->
        val digits = value.filter { it.isDigit() }
        when {
            value.isBlank() -> "El celular es obligatorio"
            digits.length < 10 -> "Ingresa minimo 10 digitos"
            else -> null
        }
    }

    val email = ValidatedField("") { value ->
        when {
            value.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Correo electronico invalido"
            else -> null
        }
    }

    val password = ValidatedField("") { value ->
        when {
            value.isBlank() -> "La contrasena es obligatoria"
            value.length < 6 -> "Minimo 6 caracteres"
            else -> null
        }
    }

    val confirmPassword = ValidatedField("") { value ->
        when {
            value.isBlank() -> "Confirma tu contrasena"
            value != password.value -> "Las contrasenas no coinciden"
            else -> null
        }
    }

    private val _registerResult = MutableStateFlow<RequestResult?>(null)
    val registerResult: StateFlow<RequestResult?> = _registerResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val isFormValid: Boolean
        get() = name.isValid &&
            city.isValid &&
            address.isValid &&
            phoneNumber.isValid &&
            email.isValid &&
            password.isValid &&
            confirmPassword.isValid

    fun register() {
        val valid = isFormValid
        if (!valid) {
            name.forceShowError()
            city.forceShowError()
            address.forceShowError()
            phoneNumber.forceShowError()
            email.forceShowError()
            password.forceShowError()
            confirmPassword.forceShowError()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _registerResult.value = null
            runCatching {
                repository.save(
                    User(
                        name = name.value.trim(),
                        email = email.value.trim(),
                        password = password.value,
                        city = city.value.trim(),
                        address = address.value.trim(),
                        phoneNumber = phoneNumber.value.trim()
                    )
                )
            }.onSuccess {
                _registerResult.value = RequestResult.Success("Registro exitoso")
            }.onFailure { error ->
                _registerResult.value = RequestResult.Failure(error.message ?: "Error al registrar usuario")
            }
            _isLoading.value = false
        }
    }

    fun resetRegisterResult() {
        _registerResult.value = null
    }

    fun resetForm() {
        name.reset()
        city.reset()
        address.reset()
        phoneNumber.reset()
        email.reset()
        password.reset()
        confirmPassword.reset()
        _registerResult.value = null
    }
}
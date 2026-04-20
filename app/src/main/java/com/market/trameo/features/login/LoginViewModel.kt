package com.market.trameo.features.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.core.session.SessionDataStore
import com.market.trameo.core.utils.RequestResult
import com.market.trameo.core.utils.ValidatedField
import com.market.trameo.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    val email = ValidatedField("") { value ->
        when {
            value.isBlank() -> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Ingresa un email valido"
            else -> null
        }
    }

    val password = ValidatedField("") { value ->
        when {
            value.isBlank() -> "La contrasena es obligatoria"
            value.length < 6 -> "La contrasena debe tener al menos 6 caracteres"
            else -> null
        }
    }

    private val _loginResult = MutableStateFlow<RequestResult?>(null)
    val loginResult: StateFlow<RequestResult?> = _loginResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val isFormValid: Boolean
        get() = email.isValid && password.isValid

    fun login() {
        val formIsValid = email.isValid && password.isValid
        if (!formIsValid) {
            email.forceShowError()
            password.forceShowError()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _loginResult.value = null
            runCatching {
                userRepository.login(
                    email = email.value.trim(),
                    password = password.value
                )
            }.onSuccess { user ->
                _loginResult.value = if (user != null) {
                    sessionDataStore.updateUserId(user.id)
                    RequestResult.Success("Bienvenido, ${user.name}")
                } else {
                    RequestResult.Failure("Credenciales invalidas")
                }
            }.onFailure { error ->
                _loginResult.value = RequestResult.Failure(error.message ?: "Error inesperado")
            }
            _isLoading.value = false
        }
    }

    fun resetLoginResult() {
        _loginResult.value = null
    }

    fun resetForm() {
        email.reset()
        password.reset()
        _loginResult.value = null
    }
}
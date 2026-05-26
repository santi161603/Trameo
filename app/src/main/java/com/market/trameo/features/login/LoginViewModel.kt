package com.market.trameo.features.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
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
            try {
                val user = userRepository.login(
                    email = email.value.trim(),
                    password = password.value
                )
                if (user != null) {
                    sessionDataStore.updateUserId(user.id)
                    _loginResult.value = RequestResult.Success("Bienvenido, ${user.name}")
                } else {
                    _loginResult.value = RequestResult.Failure("No se pudo obtener la información del usuario.")
                }
            } catch (e: FirebaseAuthInvalidUserException) {
                _loginResult.value = RequestResult.Failure("El correo electrónico no está registrado.")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _loginResult.value = RequestResult.Failure("La contraseña es incorrecta o el correo tiene un formato inválido.")
            } catch (e: FirebaseNetworkException) {
                _loginResult.value = RequestResult.Failure("Error de conexión. Revisa tu internet.")
            } catch (error: Exception) {
                _loginResult.value = RequestResult.Failure(error.message ?: "Error inesperado al iniciar sesión")
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
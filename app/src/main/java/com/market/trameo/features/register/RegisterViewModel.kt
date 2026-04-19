package com.market.trameo.features.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<RegisterUiEffect>()
    val effects = _effects.asSharedFlow()

    // Simulacion local en memoria para evitar registros duplicados por correo.
    private val registeredEmails = mutableSetOf<String>()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnFullNameChange -> updateState {
                it.copy(fullName = event.value, fullNameError = null)
            }

            is RegisterEvent.OnEmailChange -> updateState {
                it.copy(email = event.value.trim(), emailError = null)
            }

            is RegisterEvent.OnPhoneChange -> {
                val filtered = event.value.filter { char -> char.isDigit() || char == '+' || char == ' ' }
                updateState { it.copy(phone = filtered, phoneError = null) }
            }

            is RegisterEvent.OnAddressChange -> updateState {
                it.copy(address = event.value, addressError = null)
            }

            is RegisterEvent.OnDepartmentChange -> updateState {
                it.copy(
                    department = event.department,
                    departmentError = null,
                    city = "",
                    cityError = null,
                    latitude = null,
                    longitude = null,
                    locationError = null
                )
            }

            is RegisterEvent.OnCityChange -> updateState {
                it.copy(
                    city = event.city,
                    cityError = null,
                    latitude = null,
                    longitude = null,
                    locationError = null
                )
            }

            is RegisterEvent.OnPasswordChange -> updateState {
                it.copy(password = event.value, passwordError = null, confirmPasswordError = null)
            }

            is RegisterEvent.OnConfirmPasswordChange -> updateState {
                it.copy(confirmPassword = event.value, confirmPasswordError = null)
            }

            is RegisterEvent.OnLocationSelected -> updateState {
                it.copy(
                    latitude = event.latitude,
                    longitude = event.longitude,
                    locationError = null
                )
            }

            RegisterEvent.Submit -> submit()
        }
    }

    private fun updateState(transform: (RegisterUiState) -> RegisterUiState) {
        _uiState.update { current ->
            val updated = transform(current)
            updated.copy(canSubmit = canSubmit(updated))
        }
    }

    private fun submit() {
        val current = _uiState.value
        val fullNameError = validateFullName(current.fullName)
        val emailError = validateEmail(current.email)
        val phoneError = validatePhone(current.phone)
        val addressError = validateAddress(current.address)
        val departmentError = validateDepartment(current.department)
        val cityError = validateCity(current.city)
        val passwordError = validatePassword(current.password)
        val confirmPasswordError = validateConfirmPassword(current.password, current.confirmPassword)
        val locationError = validateLocation(current.latitude, current.longitude)

        val hasErrors = listOf(
            fullNameError,
            emailError,
            phoneError,
            addressError,
            departmentError,
            cityError,
            passwordError,
            confirmPasswordError,
            locationError
        ).any { it != null }

        if (hasErrors) {
            _uiState.update {
                it.copy(
                    fullNameError = fullNameError,
                    emailError = emailError,
                    phoneError = phoneError,
                    addressError = addressError,
                    departmentError = departmentError,
                    cityError = cityError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                    locationError = locationError,
                    canSubmit = false
                )
            }
            viewModelScope.launch {
                _effects.emit(RegisterUiEffect.ShowMessage("Revisa los campos del formulario"))
            }
            return
        }

        if (registeredEmails.contains(current.email.lowercase())) {
            _uiState.update {
                it.copy(
                    emailError = "Este correo ya fue registrado localmente",
                    canSubmit = false
                )
            }
            viewModelScope.launch {
                _effects.emit(RegisterUiEffect.ShowMessage("Correo ya registrado"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            delay(1200)
            registeredEmails.add(current.email.lowercase())
            _uiState.value = RegisterUiState()
            _effects.emit(RegisterUiEffect.ShowMessage("Registro local exitoso"))
            _effects.emit(RegisterUiEffect.NavigateSuccess)
        }
    }

    private fun canSubmit(state: RegisterUiState): Boolean {
        return validateFullName(state.fullName) == null &&
            validateEmail(state.email) == null &&
            validatePhone(state.phone) == null &&
            validateAddress(state.address) == null &&
            validateDepartment(state.department) == null &&
            validateCity(state.city) == null &&
            validatePassword(state.password) == null &&
            validateConfirmPassword(state.password, state.confirmPassword) == null &&
            validateLocation(state.latitude, state.longitude) == null &&
            !state.isSubmitting
    }

    private fun validateFullName(value: String): String? = when {
        value.isBlank() -> "El nombre es obligatorio"
        value.trim().length < 3 -> "Ingresa al menos 3 caracteres"
        else -> null
    }

    private fun validateEmail(value: String): String? = when {
        value.isBlank() -> "El correo es obligatorio"
        !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Correo electronico invalido"
        else -> null
    }

    private fun validatePhone(value: String): String? {
        val digits = value.filter { it.isDigit() }
        return when {
            value.isBlank() -> "El celular es obligatorio"
            digits.length < 10 -> "Ingresa minimo 10 digitos"
            else -> null
        }
    }

    private fun validateAddress(value: String): String? = when {
        value.isBlank() -> "La direccion es obligatoria"
        value.trim().length < 5 -> "Ingresa una direccion valida"
        else -> null
    }

    private fun validateDepartment(value: Department?): String? = when {
        value == null -> "El departamento es obligatorio"
        else -> null
    }

    private fun validateCity(value: String): String? = when {
        value.isBlank() -> "La ciudad es obligatoria"
        else -> null
    }

    private fun validatePassword(value: String): String? = when {
        value.isBlank() -> "La contrasena es obligatoria"
        value.length < 6 -> "Minimo 6 caracteres"
        !value.any { it.isDigit() } -> "Incluye al menos un numero"
        else -> null
    }

    private fun validateConfirmPassword(password: String, confirm: String): String? = when {
        confirm.isBlank() -> "Confirma tu contrasena"
        password != confirm -> "Las contrasenas no coinciden"
        else -> null
    }

    private fun validateLocation(latitude: Double?, longitude: Double?): String? = when {
        latitude == null || longitude == null -> "La ubicacion es obligatoria"
        else -> null
    }
}

enum class Department(val label: String, val cities: List<String>) {
    ANTIOQUIA("Antioquia", listOf("Medellin", "Bello", "Itagui", "Envigado")),
    BOGOTA_DC("Bogota D.C.", listOf("Bogota")),
    VALLE_DEL_CAUCA("Valle del Cauca", listOf("Cali", "Palmira", "Buenaventura")),
    ATLANTICO("Atlantico", listOf("Barranquilla", "Soledad", "Malambo")),
    CUNDINAMARCA("Cundinamarca", listOf("Soacha", "Facatativa", "Chia"))
}

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val department: Department? = null,
    val city: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val password: String = "",
    val confirmPassword: String = "",
    val fullNameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val addressError: String? = null,
    val departmentError: String? = null,
    val cityError: String? = null,
    val locationError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false
)

sealed interface RegisterEvent {
    data class OnFullNameChange(val value: String) : RegisterEvent
    data class OnEmailChange(val value: String) : RegisterEvent
    data class OnPhoneChange(val value: String) : RegisterEvent
    data class OnAddressChange(val value: String) : RegisterEvent
    data class OnDepartmentChange(val department: Department) : RegisterEvent
    data class OnCityChange(val city: String) : RegisterEvent
    data class OnPasswordChange(val value: String) : RegisterEvent
    data class OnConfirmPasswordChange(val value: String) : RegisterEvent
    data class OnLocationSelected(val latitude: Double, val longitude: Double) : RegisterEvent
    data object Submit : RegisterEvent
}

sealed interface RegisterUiEffect {
    data class ShowMessage(val message: String) : RegisterUiEffect
    data object NavigateSuccess : RegisterUiEffect
}
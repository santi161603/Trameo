package com.market.trameo.features.resetpassword

import androidx.lifecycle.ViewModel
import com.market.trameo.core.auth.MockAuthStore
import com.market.trameo.core.utils.RequestResult
import com.market.trameo.core.utils.ValidatedField
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authStore: MockAuthStore
) : ViewModel() {

    val newPassword = ValidatedField(authStore.currentPasswordValue()) { value ->
        when {
            value.isBlank() -> "La contraseña es obligatoria"
            value.length < 6 -> "Mínimo 6 caracteres"
            else -> null
        }
    }

    val confirmPassword = ValidatedField("") { value ->
        when {
            value.isBlank() -> "Confirma la contraseña"
            value != newPassword.value -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    private val _resetResult = MutableStateFlow<RequestResult?>(null)
    val resetResult: StateFlow<RequestResult?> = _resetResult.asStateFlow()

    val isFormValid: Boolean
        get() = newPassword.isValid && confirmPassword.isValid

    fun resetPassword() {
        if (!isFormValid) return
        authStore.updatePassword(newPassword.value)
        _resetResult.value = RequestResult.Success("Contraseña actualizada. Ahora puedes iniciar sesión con la nueva contraseña")
    }

    fun currentBurnedPassword(): String = authStore.currentPasswordValue()

    fun resetResetResult() {
        _resetResult.value = null
    }
}


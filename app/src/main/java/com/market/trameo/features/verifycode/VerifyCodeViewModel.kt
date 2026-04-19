package com.market.trameo.features.verifycode

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
class VerifyCodeViewModel @Inject constructor(
    private val authStore: MockAuthStore
) : ViewModel() {

    val code = ValidatedField("") { value ->
        when {
            value.isBlank() -> "El código es obligatorio"
            value.length != 6 -> "El código debe tener 6 dígitos"
            !value.all { it.isDigit() } -> "Solo se permiten números"
            else -> null
        }
    }

    private val _verifyResult = MutableStateFlow<RequestResult?>(null)
    val verifyResult: StateFlow<RequestResult?> = _verifyResult.asStateFlow()

    val isFormValid: Boolean
        get() = code.isValid

    fun verifyCode() {
        if (!isFormValid) return
        _verifyResult.value = if (authStore.isValidCode(code.value)) {
            RequestResult.Success("Código validado correctamente")
        } else {
            RequestResult.Failure("Código inválido. Usa el código quemado: ${authStore.verificationCode}")
        }
    }

    fun resetVerifyResult() {
        _verifyResult.value = null
    }
}


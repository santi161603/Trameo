package com.market.trameo.core.auth

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class MockAuthStore @Inject constructor() {
    val registeredEmail: String = "santi@mail.com"
    val verificationCode: String = "123456"

    private val _currentPassword = MutableStateFlow("123456")
    val currentPassword: StateFlow<String> = _currentPassword.asStateFlow()

    fun canRecover(email: String): Boolean {
        return email.equals(registeredEmail, ignoreCase = true)
    }

    fun isValidLogin(email: String, password: String): Boolean {
        return email.equals(registeredEmail, ignoreCase = true) && password == _currentPassword.value
    }

    fun isValidCode(code: String): Boolean {
        return code == verificationCode
    }

    fun updatePassword(newPassword: String) {
        _currentPassword.value = newPassword
    }

    fun currentPasswordValue(): String = _currentPassword.value
}


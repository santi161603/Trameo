package com.market.trameo.features.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.core.session.SessionDataStore
import com.market.trameo.domain.model.User
import com.market.trameo.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PerfilViewModel @Inject constructor(
    userRepository: UserRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    val currentUser: StateFlow<User?> = combine(
        userRepository.users,
        sessionDataStore.userId
    ) { users, userId ->
        users.firstOrNull { it.id == userId }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    private val _logoutCompleted = MutableStateFlow(false)
    val logoutCompleted: StateFlow<Boolean> = _logoutCompleted

    fun logout() {
        viewModelScope.launch {
            sessionDataStore.clearSession()
            _logoutCompleted.value = true
        }
    }

    fun resetLogoutState() {
        _logoutCompleted.value = false
    }
}


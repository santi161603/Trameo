package com.market.trameo.features.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.domain.model.HomeObject
import com.market.trameo.domain.model.ModerationStatus
import com.market.trameo.domain.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val pendingObjects: List<HomeObject> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedObject: HomeObject? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadPendingObjects()
    }

    private fun loadPendingObjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            homeRepository.getPendingObjects()
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { objects ->
                    _uiState.update { it.copy(isLoading = false, pendingObjects = objects) }
                }
        }
    }

    fun selectObject(homeObject: HomeObject?) {
        _uiState.update { it.copy(selectedObject = homeObject) }
    }

    fun updateStatus(objectId: String, status: ModerationStatus) {
        viewModelScope.launch {
            val result = homeRepository.updateObjectStatus(objectId, status)
            if (result.isSuccess) {
                _uiState.update { it.copy(selectedObject = null) }
                // No hace falta recargar manualmente si getPendingObjects es un Flow reactivo
            } else {
                _uiState.update { it.copy(error = "Error al actualizar estado") }
            }
        }
    }
}

package com.market.trameo.features.intercambios.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.core.session.SessionDataStore
import com.market.trameo.domain.model.HomeObject
import com.market.trameo.domain.model.ModerationStatus
import com.market.trameo.domain.model.Propuesta
import com.market.trameo.domain.repository.HomeRepository
import com.market.trameo.domain.repository.PropuestaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IntercambioDetalleUIState(
    val loading: Boolean = true,
    val propuesta: Propuesta? = null,
    val objetoDeseado: HomeObject? = null,
    val objetoOfrecido: HomeObject? = null,
    val isSent: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class IntercambioDetalleViewModel @Inject constructor(
    private val propuestaRepository: PropuestaRepository,
    private val homeRepository: HomeRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(IntercambioDetalleUIState())
    val uiState: StateFlow<IntercambioDetalleUIState> = _uiState.asStateFlow()

    fun loadDetalle(intercambioId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            val userId = sessionDataStore.userId.first()
            val propuesta = propuestaRepository.getPropuestaById(intercambioId)
            
            if (propuesta != null && userId != null) {
                val objetoDeseado = homeRepository.getObjectById(propuesta.objetoDeseadoUid)
                val objetoOfrecido = homeRepository.getObjectById(propuesta.objetoOfrecidoUid)
                val isSent = propuesta.proponenteUid == userId

                _uiState.update {
                    it.copy(
                        loading = false,
                        propuesta = propuesta,
                        objetoDeseado = objetoDeseado,
                        objetoOfrecido = objetoOfrecido,
                        isSent = isSent
                    )
                }
            } else {
                _uiState.update { it.copy(loading = false, error = "No se pudo cargar el intercambio") }
            }
        }
    }

    fun responderPropuesta(aceptar: Boolean) {
        val propuesta = _uiState.value.propuesta ?: return
        val nuevoEstado = if (aceptar) "ACEPTADO" else "RECHAZADO"
        
        viewModelScope.launch {
            val result = propuestaRepository.actualizarEstadoPropuesta(propuesta.id, nuevoEstado)
            if (result.isSuccess) {
                if (aceptar) {
                    // Si se acepta el intercambio, ambos objetos pasan a FINALIZADO
                    homeRepository.updateObjectStatus(propuesta.objetoDeseadoUid, ModerationStatus.FINALIZADO)
                    homeRepository.updateObjectStatus(propuesta.objetoOfrecidoUid, ModerationStatus.FINALIZADO)
                } else {
                    // Si se rechaza, vuelven a estar disponibles (PUBLICADO)
                    homeRepository.updateObjectStatus(propuesta.objetoDeseadoUid, ModerationStatus.PUBLICADO)
                    homeRepository.updateObjectStatus(propuesta.objetoOfrecidoUid, ModerationStatus.PUBLICADO)
                }
                _uiState.update { it.copy(propuesta = propuesta.copy(estado = nuevoEstado)) }
            }
        }
    }
}

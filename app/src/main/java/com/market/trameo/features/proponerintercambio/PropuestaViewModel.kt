package com.market.trameo.features.proponerintercambio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.market.trameo.domain.model.HomeObject
import com.market.trameo.domain.model.ModerationStatus
import com.market.trameo.domain.model.Propuesta
import com.market.trameo.domain.repository.HomeRepository
import com.market.trameo.domain.repository.PropuestaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PropuestaViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val homeRepository: HomeRepository,
    private val propuestaRepository: PropuestaRepository
) : ViewModel() {

    private val uid: String? = auth.currentUser?.uid

    val misObjetos: StateFlow<List<HomeObject>> = if (uid.isNullOrBlank()) {
        flowOf(emptyList<HomeObject>())
    } else {
        homeRepository.getObjectsByOwner(uid).map { list -> 
            list.filter { it.moderationStatus == ModerationStatus.PUBLICADO }
                .distinctBy { it.id } 
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _objetoSeleccionado = MutableStateFlow<HomeObject?>(null)
    val objetoSeleccionado: StateFlow<HomeObject?> = _objetoSeleccionado.asStateFlow()

    private val _uiState = MutableStateFlow(PropuestaUiState())
    val uiState: StateFlow<PropuestaUiState> = _uiState.asStateFlow()

    fun seleccionarObjeto(objeto: HomeObject) {
        _objetoSeleccionado.value = objeto
    }

    fun enviarPropuesta(objetoDeseadoUid: String, receptorUid: String) {
        val currentUid = uid
        val objetoOfrecido = _objetoSeleccionado.value
        if (currentUid.isNullOrBlank()) {
            _uiState.value = PropuestaUiState(errorMessage = "Debes iniciar sesion")
            return
        }
        if (objetoOfrecido == null) {
            _uiState.value = PropuestaUiState(errorMessage = "Selecciona un objeto")
            return
        }

        viewModelScope.launch {
            _uiState.value = PropuestaUiState(isLoading = true)
            val propuesta = Propuesta(
                id = "",
                proponenteUid = currentUid,
                receptorUid = receptorUid,
                objetoDeseadoUid = objetoDeseadoUid,
                objetoOfrecidoUid = objetoOfrecido.id,
                estado = "PENDIENTE",
                timestamp = System.currentTimeMillis()
            )
            val result = propuestaRepository.crearPropuesta(propuesta)
            if (result.isSuccess) {
                // Al proponer, ambos objetos pasan a estar "EN PROCESO"
                homeRepository.updateObjectStatus(objetoOfrecido.id, ModerationStatus.EN_PROCESO_DE_INTERCAMBIO)
                homeRepository.updateObjectStatus(objetoDeseadoUid, ModerationStatus.EN_PROCESO_DE_INTERCAMBIO)
                
                _uiState.value = PropuestaUiState(successMessage = "Propuesta enviada")
            } else {
                _uiState.value = PropuestaUiState(errorMessage = result.exceptionOrNull()?.message ?: "No se pudo enviar la propuesta")
            }
        }
    }
}

data class PropuestaUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)


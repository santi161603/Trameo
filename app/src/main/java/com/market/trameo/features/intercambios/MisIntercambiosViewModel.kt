package com.market.trameo.features.intercambios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.core.session.SessionDataStore
import com.market.trameo.domain.model.HomeObject
import com.market.trameo.domain.model.Propuesta
import com.market.trameo.domain.repository.HomeRepository
import com.market.trameo.domain.repository.PropuestaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IntercambioUI(
    val id: String,
    val objetoDeseado: HomeObject?,
    val objetoOfrecido: HomeObject?,
    val statusText: String,
    val isSent: Boolean,
    val timestamp: Long
)

@HiltViewModel
class MisIntercambiosViewModel @Inject constructor(
    private val propuestaRepository: PropuestaRepository,
    private val homeRepository: HomeRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val intercambios: StateFlow<List<IntercambioUI>> = sessionDataStore.userId
        .flatMapLatest { userId ->
            if (userId == null) return@flatMapLatest flowOf(emptyList())

            val enviadasFlow = propuestaRepository.getMisPropuestasEnviadas(userId)
            val recibidasFlow = propuestaRepository.getPropuestasRecibidas(userId)

            combine(enviadasFlow, recibidasFlow) { enviadas, recibidas ->
                val all = mutableListOf<IntercambioUI>()
                
                enviadas.forEach { prop ->
                    all.add(createUIItem(prop, isSent = true))
                }
                recibidas.forEach { prop ->
                    all.add(createUIItem(prop, isSent = false))
                }
                
                all.distinctBy { it.id }.sortedByDescending { it.timestamp }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private suspend fun createUIItem(prop: Propuesta, isSent: Boolean): IntercambioUI {
        val objetoDeseado = homeRepository.getObjectById(prop.objetoDeseadoUid)
        val objetoOfrecido = homeRepository.getObjectById(prop.objetoOfrecidoUid)
        
        val statusText = when {
            prop.estado == "ACEPTADO" -> "Aceptado"
            prop.estado == "RECHAZADO" -> "Rechazado"
            isSent -> "En espera de ser aceptado"
            else -> "Pendiente de aceptar o rechazar"
        }

        return IntercambioUI(
            id = prop.id,
            objetoDeseado = objetoDeseado,
            objetoOfrecido = objetoOfrecido,
            statusText = statusText,
            isSent = isSent,
            timestamp = prop.timestamp
        )
    }
}

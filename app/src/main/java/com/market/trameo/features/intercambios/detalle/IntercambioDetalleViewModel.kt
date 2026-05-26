package com.market.trameo.features.intercambios.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.core.session.SessionDataStore
import com.market.trameo.domain.model.HomeObject
import com.market.trameo.domain.model.ModerationStatus
import com.market.trameo.domain.model.Propuesta
import com.market.trameo.domain.repository.HomeRepository
import com.market.trameo.domain.repository.PropuestaRepository
import com.market.trameo.domain.repository.ReviewRepository
import com.market.trameo.domain.repository.UserRepository
import com.market.trameo.domain.model.Review
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
    val error: String? = null,
    val showReviewDialog: Boolean = false,
    val reviewRating: Int = 0,
    val reviewComment: String = "",
    val reviewSubmitted: Boolean = false
)

@HiltViewModel
class IntercambioDetalleViewModel @Inject constructor(
    private val propuestaRepository: PropuestaRepository,
    private val homeRepository: HomeRepository,
    private val sessionDataStore: SessionDataStore,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
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

                _uiState.update {
                    it.copy(
                        propuesta = propuesta.copy(estado = nuevoEstado),
                        showReviewDialog = aceptar
                    )
                }
            }
        }
    }

    fun updateReviewRating(rating: Int) {
        _uiState.update { it.copy(reviewRating = rating) }
    }

    fun updateReviewComment(comment: String) {
        _uiState.update { it.copy(reviewComment = comment) }
    }

    fun dismissReviewDialog() {
        _uiState.update { it.copy(showReviewDialog = false) }
    }

    fun submitReview() {
        val state = _uiState.value
        val propuesta = state.propuesta ?: return

        viewModelScope.launch {
            val userId = sessionDataStore.userId.first() ?: return@launch

            // Quién hizo la reseña (el usuario logueado)
            val reviewerId = userId
            // A quién va dirigida la reseña (la otra parte)
            val targetUserId = if (state.isSent) propuesta.receptorUid else propuesta.proponenteUid

            val reviewer = userRepository.findById(reviewerId)

            val review = Review(
                reviewerId = reviewerId,
                reviewerName = reviewer?.name ?: "",
                reviewerPhotoUrl = reviewer?.profilePhotoUri,
                targetUserId = targetUserId,
                rating = state.reviewRating,
                comment = state.reviewComment,
                timestamp = System.currentTimeMillis()
            )

            val result = reviewRepository.createReview(review)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        showReviewDialog = false,
                        reviewSubmitted = true
                    )
                }
            } else {
                // Manejar error de forma básica
                _uiState.update { it.copy(error = "No se pudo guardar la reseña") }
            }
        }
    }
}

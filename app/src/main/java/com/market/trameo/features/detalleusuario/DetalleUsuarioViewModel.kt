package com.market.trameo.features.detalleusuario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.domain.model.Review
import com.market.trameo.domain.model.User
import com.market.trameo.domain.repository.ReviewRepository
import com.market.trameo.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetalleUsuarioUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val reviews: List<Review> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DetalleUsuarioViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleUsuarioUiState())
    val uiState: StateFlow<DetalleUsuarioUiState> = _uiState.asStateFlow()

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val user = userRepository.findById(userId)
                if (user != null) {
                    _uiState.value = _uiState.value.copy(user = user)

                    // Fetch reviews
                    reviewRepository.getReviewsForUser(userId)
                        .catch { e ->
                            _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
                        }
                        .collect { reviews ->
                            _uiState.value = _uiState.value.copy(reviews = reviews, isLoading = false)
                        }
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Usuario no encontrado",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.localizedMessage ?: "Error al cargar usuario",
                    isLoading = false
                )
            }
        }
    }
}

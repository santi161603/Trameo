package com.market.trameo.features.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.domain.model.HomeObject
import com.market.trameo.domain.model.UserSummary
import com.market.trameo.domain.repository.HomeRepository
import com.market.trameo.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DetalleObjetoViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _item = MutableStateFlow<HomeObject?>(null)
    val item: StateFlow<HomeObject?> = _item.asStateFlow()

    private val _owner = MutableStateFlow<UserSummary?>(null)
    val owner: StateFlow<UserSummary?> = _owner.asStateFlow()

    fun load(objectId: String) {
        viewModelScope.launch {
            val homeObject = homeRepository.getObjectById(objectId)
            _item.value = homeObject
            _owner.value = homeObject?.ownerId?.takeIf { it.isNotBlank() }
                ?.let { userRepository.getUserSummary(it) }
        }
    }
}

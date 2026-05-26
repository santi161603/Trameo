package com.market.trameo.features.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.domain.model.HomeObject
import com.market.trameo.domain.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DetalleObjetoViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _item = MutableStateFlow<HomeObject?>(null)
    val item: StateFlow<HomeObject?> = _item.asStateFlow()

    fun load(objectId: String) {
        viewModelScope.launch {
            _item.value = homeRepository.getObjectById(objectId)
        }
    }
}

package com.market.trameo.features.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.domain.model.SwapObject
import com.market.trameo.domain.repository.SwapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class DetalleObjetoViewModel @Inject constructor(
    private val swapRepository: SwapRepository
) : ViewModel() {

    private val _item = MutableStateFlow<SwapObject?>(null)
    val item: StateFlow<SwapObject?> = _item.asStateFlow()

    fun load(objectId: String) {
        swapRepository.objects
            .map { objects -> objects.firstOrNull { it.id == objectId } }
            .onEach { _item.value = it }
            .launchIn(viewModelScope)
    }
}


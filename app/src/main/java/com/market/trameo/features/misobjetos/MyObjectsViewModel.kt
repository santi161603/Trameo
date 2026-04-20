package com.market.trameo.features.misobjetos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.trameo.core.session.SessionDataStore
import com.market.trameo.domain.model.SwapObject
import com.market.trameo.domain.repository.SwapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MyObjectsViewModel @Inject constructor(
    swapRepository: SwapRepository,
    sessionDataStore: SessionDataStore
) : ViewModel() {

    val items: StateFlow<List<SwapObject>> = combine(
        swapRepository.objects,
        sessionDataStore.userId
    ) { objects, userId ->
        objects.filter { it.ownerId == userId }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}


package com.market.trameo.features.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _items = MutableStateFlow(HomeObjectsData.objects)
    val items: StateFlow<List<HomeObjectItem>> = _items.asStateFlow()
}


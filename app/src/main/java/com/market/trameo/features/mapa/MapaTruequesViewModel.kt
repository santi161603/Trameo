package com.market.trameo.features.mapa

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapaTruequesViewModel @Inject constructor() : ViewModel() {
    val defaultLocation = LatLng(4.7110, -74.0721)
    val defaultZoom = 13f
}


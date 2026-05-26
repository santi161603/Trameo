package com.market.trameo.features.mapa

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.market.trameo.R
import com.market.trameo.core.theme.Marfil

@SuppressLint("MissingPermission")
@Composable
fun MapaTruequesMockScreen(
    onBackClick: () -> Unit,
    viewModel: MapaTruequesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    var googleMapInstance by remember { mutableStateOf<GoogleMap?>(null) }
    var hasCenteredOnUser by remember { mutableStateOf(false) }
    val userLocation by viewModel.userLocation.collectAsState()
    val mapItems by viewModel.mapItems.collectAsState()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            googleMapInstance?.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.updateUserLocation(it.latitude, it.longitude)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    LaunchedEffect(mapView) {
        mapView.getMapAsync { googleMap ->
            googleMapInstance = googleMap
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true

            // Inicialmente centrar en la ubicación por defecto si no hay ubicación de usuario
            if (userLocation == null) {
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        viewModel.defaultLocation,
                        viewModel.defaultZoom
                    )
                )
            }
        }
    }

    LaunchedEffect(userLocation, mapItems) {
        val latLng = userLocation ?: return@LaunchedEffect
        googleMapInstance?.let { map ->
            if (!hasCenteredOnUser) {
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(latLng, viewModel.defaultZoom)
                )
                hasCenteredOnUser = true
            }

            // Limpiar markers anteriores para evitar duplicados si mapItems cambia
            map.clear()

            // Agregar markers reales basados en los items publicados
            mapItems.forEach { item ->
                // Como HomeObject no tiene lat/lng real aún, generamos posiciones determinísticas
                // basadas en su ID para que no se muevan pero estén cerca del usuario en el mock.
                // En una app real, HomeObject debería tener campos de ubicación.
                val hash = item.id.hashCode().toDouble()
                val offsetLat = (hash % 100) / 10000.0 - 0.005
                val offsetLng = ((hash / 100).toInt() % 100) / 10000.0 - 0.005
                
                val itemPos = LatLng(latLng.latitude + offsetLat, latLng.longitude + offsetLng)
                
                map.addMarker(
                    MarkerOptions()
                        .position(itemPos)
                        .title(item.name)
                        .snippet("Intercambio por: ${item.exchangePreferences}")
                )
            }
        }
    }

    Scaffold(
        containerColor = Marfil,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back))
                }
                Text(
                    text = stringResource(id = R.string.mapa_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Marfil)
        ) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context).apply { onCreate(Bundle()) } }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }

        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    return mapView
}

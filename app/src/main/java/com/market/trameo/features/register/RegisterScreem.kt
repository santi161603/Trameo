package com.market.trameo.features.register

import android.annotation.SuppressLint
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.Settings
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.core.content.ContextCompat
import com.market.trameo.ui.theme.GrisPizarra
import com.market.trameo.ui.theme.Marfil
import com.market.trameo.ui.theme.Terracota
import com.market.trameo.ui.theme.TrameoTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit = {},
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var profileImage by remember { mutableStateOf<Bitmap?>(null) }
    var showLocationPicker by remember { mutableStateOf(false) }
    var permissionDialogState by remember { mutableStateOf<PermissionDialogState?>(null) }

    val locationLabel = remember(uiState.latitude, uiState.longitude) {
        if (uiState.latitude != null && uiState.longitude != null) {
            "Lat: %.6f, Lng: %.6f".format(uiState.latitude, uiState.longitude)
        } else {
            ""
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val loadedBitmap = runCatching { decodeBitmapFromUri(context, uri) }.getOrNull()
            if (loadedBitmap != null) {
                profileImage = loadedBitmap
            } else {
                scope.launch { snackbarHostState.showSnackbar("No se pudo cargar la imagen") }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            profileImage = bitmap
        } else {
            scope.launch { snackbarHostState.showSnackbar("No se pudo tomar la foto") }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            val permanentlyDenied = activity?.let {
                isPermissionPermanentlyDenied(it, Manifest.permission.CAMERA)
            } == true

            if (permanentlyDenied) {
                permissionDialogState = PermissionDialogState.CameraPermanentlyDenied
            } else {
                scope.launch { snackbarHostState.showSnackbar("Permiso de camara denegado") }
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            showLocationPicker = true
        } else {
            val permanentlyDenied = activity?.let {
                isLocationPermanentlyDenied(it)
            } == true

            if (permanentlyDenied) {
                permissionDialogState = PermissionDialogState.LocationPermanentlyDenied
            } else {
                scope.launch { snackbarHostState.showSnackbar("Permiso de ubicacion denegado") }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is RegisterUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                RegisterUiEffect.NavigateSuccess -> onRegisterSuccess()
            }
        }
    }

    Scaffold(
        containerColor = Marfil,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = GrisPizarra
                    )
                }
            }

            Text(
                text = "Crear cuenta",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = GrisPizarra,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Text(
                text = "Completa los datos para registrarte",
                fontSize = 15.sp,
                color = GrisPizarra.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 18.dp)
            )

            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .border(width = 1.5.dp, color = Terracota.copy(alpha = 0.6f), shape = CircleShape)
                    .clickable { showImageSourceDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (profileImage != null) {
                    Image(
                        bitmap = profileImage!!.asImageBitmap(),
                        contentDescription = "Foto de perfil seleccionada",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Agregar foto",
                        tint = Terracota,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Text(
                text = "Agregar foto de perfil",
                fontSize = 14.sp,
                color = Terracota,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 18.dp)
                    .clickable { showImageSourceDialog = true }
            )

            RegisterOutlinedField(
                value = uiState.fullName,
                onValueChange = { viewModel.onEvent(RegisterEvent.OnFullNameChange(it)) },
                label = "Nombre completo",
                leadingIcon = Icons.Default.Person,
                error = uiState.fullNameError,
                enabled = !uiState.isSubmitting
            )

            RegisterOutlinedField(
                value = uiState.email,
                onValueChange = { viewModel.onEvent(RegisterEvent.OnEmailChange(it)) },
                label = "Correo electronico",
                leadingIcon = Icons.Default.Email,
                error = uiState.emailError,
                enabled = !uiState.isSubmitting,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )

            RegisterOutlinedField(
                value = uiState.phone,
                onValueChange = { viewModel.onEvent(RegisterEvent.OnPhoneChange(it)) },
                label = "Celular",
                leadingIcon = Icons.Default.Phone,
                error = uiState.phoneError,
                enabled = !uiState.isSubmitting,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
            )

            RegisterOutlinedField(
                value = uiState.address,
                onValueChange = { viewModel.onEvent(RegisterEvent.OnAddressChange(it)) },
                label = "Direccion",
                leadingIcon = Icons.Default.LocationOn,
                error = uiState.addressError,
                enabled = !uiState.isSubmitting
            )

            RegisterDropdownField(
                label = "Departamento",
                selectedText = uiState.department?.label ?: "",
                options = Department.entries,
                optionLabel = { it.label },
                enabled = !uiState.isSubmitting,
                error = uiState.departmentError,
                onOptionSelected = { selected ->
                    viewModel.onEvent(RegisterEvent.OnDepartmentChange(selected))
                }
            )

            RegisterDropdownField(
                label = "Ciudad",
                selectedText = uiState.city,
                options = uiState.department?.cities ?: emptyList(),
                optionLabel = { it },
                enabled = !uiState.isSubmitting && uiState.department != null,
                error = uiState.cityError,
                onOptionSelected = { selected ->
                    viewModel.onEvent(RegisterEvent.OnCityChange(selected))
                }
            )

            LocationSelectorField(
                value = locationLabel,
                error = uiState.locationError,
                enabled = !uiState.isSubmitting,
                onClick = {
                    if (uiState.department == null || uiState.city.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Primero selecciona departamento y ciudad")
                        }
                    } else {
                        if (hasLocationPermission(context)) {
                            showLocationPicker = true
                        } else {
                            val permanentlyDenied = activity?.let { isLocationPermanentlyDenied(it) } == true
                            if (permanentlyDenied) {
                                permissionDialogState = PermissionDialogState.LocationPermanentlyDenied
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        }
                    }
                }
            )

            RegisterOutlinedField(
                value = uiState.password,
                onValueChange = { viewModel.onEvent(RegisterEvent.OnPasswordChange(it)) },
                label = "Contrasena",
                leadingIcon = Icons.Default.Lock,
                error = uiState.passwordError,
                enabled = !uiState.isSubmitting,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar u ocultar contrasena",
                            tint = GrisPizarra.copy(alpha = 0.7f)
                        )
                    }
                }
            )

            RegisterOutlinedField(
                value = uiState.confirmPassword,
                onValueChange = { viewModel.onEvent(RegisterEvent.OnConfirmPasswordChange(it)) },
                label = "Confirmar contrasena",
                leadingIcon = Icons.Default.Lock,
                error = uiState.confirmPasswordError,
                enabled = !uiState.isSubmitting,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar u ocultar contrasena",
                            tint = GrisPizarra.copy(alpha = 0.7f)
                        )
                    }
                }
            )

            Button(
                onClick = { viewModel.onEvent(RegisterEvent.Submit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(top = 14.dp),
                enabled = uiState.canSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = Terracota),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Crear cuenta", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Foto de perfil") },
            text = { Text("Selecciona como quieres agregar tu imagen") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Galeria")
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        showImageSourceDialog = false
                        if (hasCameraPermission(context)) {
                            cameraLauncher.launch(null)
                        } else {
                            val permanentlyDenied = activity?.let {
                                isPermissionPermanentlyDenied(it, Manifest.permission.CAMERA)
                            } == true
                            if (permanentlyDenied) {
                                permissionDialogState = PermissionDialogState.CameraPermanentlyDenied
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    }) {
                        Text("Camara")
                    }
                    TextButton(onClick = { showImageSourceDialog = false }) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }

    if (showLocationPicker) {
        LocationPickerDialog(
            onDismiss = { showLocationPicker = false },
            onConfirm = { latitude, longitude ->
                viewModel.onEvent(RegisterEvent.OnLocationSelected(latitude, longitude))
                showLocationPicker = false
            }
        )
    }

    permissionDialogState?.let { state ->
        PermissionDeniedDialog(
            title = if (state == PermissionDialogState.CameraPermanentlyDenied) {
                "Permiso de camara requerido"
            } else {
                "Permiso de ubicacion requerido"
            },
            message = if (state == PermissionDialogState.CameraPermanentlyDenied) {
                "Activa el permiso de camara en Ajustes para tomar foto de perfil."
            } else {
                "Activa el permiso de ubicacion en Ajustes para seleccionar punto en el mapa."
            },
            onDismiss = { permissionDialogState = null },
            onOpenSettings = {
                openAppSettings(context)
                permissionDialogState = null
            }
        )
    }
}

@Composable
private fun RegisterOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    error: String?,
    enabled: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(14.dp)),
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = GrisPizarra.copy(alpha = 0.7f)
                )
            },
            trailingIcon = trailingIcon,
            singleLine = true,
            enabled = enabled,
            isError = error != null,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Terracota,
                focusedLabelColor = Terracota,
                unfocusedBorderColor = Color(0xFFE0DDD5),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> RegisterDropdownField(
    label: String,
    selectedText: String,
    options: List<T>,
    optionLabel: (T) -> String,
    enabled: Boolean,
    error: String?,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled) expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                enabled = enabled,
                isError = error != null,
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Terracota,
                    focusedLabelColor = Terracota,
                    unfocusedBorderColor = Color(0xFFE0DDD5),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionLabel(option)) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun LocationSelectorField(
    value: String,
    error: String?,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text("Ubicacion") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = GrisPizarra.copy(alpha = 0.7f)
                )
            },
            trailingIcon = {
                Text(
                    text = "Mapa",
                    color = Terracota,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(end = 12.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled, onClick = onClick),
            enabled = enabled,
            isError = error != null,
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Terracota,
                focusedLabelColor = Terracota,
                unfocusedBorderColor = Color(0xFFE0DDD5),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        TextButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Seleccionar en mapa")
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun LocationPickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, Double) -> Unit
) {
    var selectedLatitude by remember { mutableStateOf<Double?>(null) }
    var selectedLongitude by remember { mutableStateOf<Double?>(null) }
    var isMapLoading by remember { mutableStateOf(true) }
    var mapError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isMapLoading) {
        if (isMapLoading) {
            kotlinx.coroutines.delay(8000)
            if (isMapLoading) {
                mapError = "No se pudo inicializar el mapa"
                isMapLoading = false
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecciona una ubicacion") },
        text = {
            Column {
                Text(
                    text = if (selectedLatitude != null && selectedLongitude != null) {
                        "Lat: %.6f, Lng: %.6f".format(selectedLatitude, selectedLongitude)
                    } else {
                        "Toca el mapa para fijar un punto"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = GrisPizarra.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                ) {
                    LocationWebMap(
                        modifier = Modifier.fillMaxSize(),
                        onLocationTapped = { lat, lng ->
                            selectedLatitude = lat
                            selectedLongitude = lng
                        },
                        onMapReady = {
                            isMapLoading = false
                            mapError = null
                        },
                        onPageError = { error ->
                            isMapLoading = false
                            mapError = error
                        }
                    )

                    if (isMapLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    if (mapError != null) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = mapError ?: "Error cargando mapa", color = MaterialTheme.colorScheme.error)
                            Text(
                                text = "Verifica internet y permisos de ubicacion",
                                style = MaterialTheme.typography.bodySmall,
                                color = GrisPizarra.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = selectedLatitude != null && selectedLongitude != null,
                onClick = {
                    onConfirm(selectedLatitude ?: 0.0, selectedLongitude ?: 0.0)
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun LocationWebMap(
    modifier: Modifier = Modifier,
    onLocationTapped: (Double, Double) -> Unit,
    onMapReady: () -> Unit,
    onPageError: (String) -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadsImagesAutomatically = true
                webViewClient = object : WebViewClient() {
                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        onPageError(error?.description?.toString() ?: "No se pudo cargar el mapa")
                    }
                }
                webChromeClient = WebChromeClient()
                addJavascriptInterface(LocationJsBridge(onLocationTapped, onMapReady), "AndroidBridge")
                loadDataWithBaseURL(
                    "https://app.local/",
                    locationPickerHtml,
                    "text/html",
                    "utf-8",
                    null
                )
            }
        }
    )
}

private class LocationJsBridge(
    private val onLocationTapped: (Double, Double) -> Unit,
    private val onMapReadyCallback: () -> Unit
) {
    @JavascriptInterface
    fun onLocationSelected(latitude: Double, longitude: Double) {
        onLocationTapped(latitude, longitude)
    }

    @JavascriptInterface
    fun onMapReady() {
        onMapReadyCallback()
    }
}

private val locationPickerHtml = """
<!DOCTYPE html>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/leaflet@1.9.4/dist/leaflet.css" />
  <script src="https://cdn.jsdelivr.net/npm/leaflet@1.9.4/dist/leaflet.js"></script>
  <style>
    html, body { margin:0; padding:0; width:100%; height:100%; }
    #map { width:100%; height:100%; }
  </style>
</head>
<body>
  <div id="map"></div>
  <script>
    const initial = [4.7110, -74.0721];
    const map = L.map('map').setView(initial, 6);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    map.whenReady(function() {
      if (window.AndroidBridge && window.AndroidBridge.onMapReady) {
        window.AndroidBridge.onMapReady();
      }
    });

    let marker = null;

    map.on('click', function(e) {
      if (marker) {
        marker.setLatLng(e.latlng);
      } else {
        marker = L.marker(e.latlng).addTo(map);
      }

      if (window.AndroidBridge && window.AndroidBridge.onLocationSelected) {
        window.AndroidBridge.onLocationSelected(e.latlng.lat, e.latlng.lng);
      }
    });
  </script>
</body>
</html>
""".trimIndent()

private fun hasCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED
}

private fun hasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED
    return fine || coarse
}

private enum class PermissionDialogState {
    CameraPermanentlyDenied,
    LocationPermanentlyDenied
}

@Composable
private fun PermissionDeniedDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text("Abrir ajustes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun isPermissionPermanentlyDenied(activity: Activity, permission: String): Boolean {
    val granted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    if (granted) return false
    return !activity.shouldShowRequestPermissionRationale(permission)
}

private fun isLocationPermanentlyDenied(activity: Activity): Boolean {
    val fineDeniedPermanently = isPermissionPermanentlyDenied(activity, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarseDeniedPermanently = isPermissionPermanentlyDenied(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fineDeniedPermanently && coarseDeniedPermanently
}

private fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

private fun Context.findActivity(): Activity? {
    var current: Context? = this
    while (current is android.content.ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

private fun decodeBitmapFromUri(context: Context, uri: Uri): Bitmap {
    val source = ImageDecoder.createSource(context.contentResolver, uri)
    return ImageDecoder.decodeBitmap(source)
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    TrameoTheme {
        RegisterScreen(onRegisterSuccess = {})
    }
}
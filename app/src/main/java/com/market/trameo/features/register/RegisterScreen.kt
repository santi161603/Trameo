package com.market.trameo.features.register

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.market.trameo.R
import com.market.trameo.core.utils.RequestResult
import com.market.trameo.core.theme.Marfil
import com.market.trameo.core.theme.Terracota

@SuppressLint("MissingPermission")
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit = {},
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val registerResult by viewModel.registerResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val profilePhotoUri by viewModel.profilePhotoUri.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.updateLocation(it.latitude, it.longitude)
                }
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.onProfilePhotoSelected(uri)
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    LaunchedEffect(registerResult) {
        registerResult?.let { result ->
            when (result) {
                is RequestResult.Success -> {
                    onNavigateToLogin()
                    viewModel.resetForm()
                    viewModel.resetRegisterResult()
                }
                is RequestResult.Failure -> {
                    snackbarHostState.showSnackbar(result.errorMessage)
                    viewModel.resetRegisterResult()
                }
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
                .padding(horizontal = 20.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back))
            }

            Text(
                text = stringResource(id = R.string.register_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            AsyncImage(
                model = profilePhotoUri,
                contentDescription = stringResource(id = R.string.register_profile_photo_content_description),
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .border(1.dp, Terracota, CircleShape)
            )

            Button(
                onClick = { photoPickerLauncher.launch("image/*") },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Terracota),
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            ) {
                Text(stringResource(id = R.string.register_add_profile_photo))
            }

            RegisterField(
                value = viewModel.name.value,
                onValueChange = viewModel.name::onChange,
                label = stringResource(id = R.string.register_label_name),
                leadingIcon = Icons.Default.Person,
                error = viewModel.name.error,
                enabled = !isLoading
            )

            RegisterField(
                value = viewModel.city.value,
                onValueChange = viewModel.city::onChange,
                label = stringResource(id = R.string.register_label_city),
                leadingIcon = Icons.Default.Home,
                error = viewModel.city.error,
                enabled = !isLoading
            )

            RegisterField(
                value = viewModel.address.value,
                onValueChange = viewModel.address::onChange,
                label = stringResource(id = R.string.register_label_address),
                leadingIcon = Icons.Default.LocationOn,
                error = viewModel.address.error,
                enabled = !isLoading
            )

            RegisterField(
                value = viewModel.phoneNumber.value,
                onValueChange = viewModel.phoneNumber::onChange,
                label = stringResource(id = R.string.register_label_phone),
                leadingIcon = Icons.Default.Phone,
                error = viewModel.phoneNumber.error,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            RegisterField(
                value = viewModel.email.value,
                onValueChange = viewModel.email::onChange,
                label = stringResource(id = R.string.register_label_email),
                leadingIcon = Icons.Default.Email,
                error = viewModel.email.error,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            RegisterField(
                value = viewModel.password.value,
                onValueChange = {
                    viewModel.password.onChange(it)
                    viewModel.confirmPassword.onChange(viewModel.confirmPassword.value)
                },
                label = stringResource(id = R.string.register_label_password),
                leadingIcon = Icons.Default.Lock,
                error = viewModel.password.error,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = stringResource(id = R.string.register_toggle_password)
                        )
                    }
                }
            )

            RegisterField(
                value = viewModel.confirmPassword.value,
                onValueChange = viewModel.confirmPassword::onChange,
                label = stringResource(id = R.string.register_label_confirm_password),
                leadingIcon = Icons.Default.Lock,
                error = viewModel.confirmPassword.error,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = stringResource(id = R.string.register_toggle_confirm_password)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = viewModel::register,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Terracota)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(id = R.string.register_button))
                }
            }
        }
    }
}

@Composable
private fun RegisterField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    error: String?,
    enabled: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        trailingIcon = trailingIcon,
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        enabled = enabled,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation
    )
}



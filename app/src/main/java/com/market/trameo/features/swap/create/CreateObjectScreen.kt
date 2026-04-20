package com.market.trameo.features.swap.create

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.market.trameo.core.theme.Marfil
import com.market.trameo.core.theme.Terracota
import com.market.trameo.core.utils.RequestResult
import com.market.trameo.domain.model.ObjectCategory
import com.market.trameo.domain.model.ObjectCondition

@Composable
fun CreateObjectScreen(
    onBackClick: () -> Unit,
    onPublished: () -> Unit,
    viewModel: CreateObjectViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val context = LocalContext.current
    val photos by viewModel.photos.collectAsState()
    val result by viewModel.publishResult.collectAsState()
    val isPublishing by viewModel.isPublishing.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.addPhoto(uri)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        val uri = bitmap?.let { saveBitmapToMediaStore(context, it) }
        if (uri != null) {
            viewModel.addPhoto(uri)
        }
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            galleryLauncher.launch("image/*")
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        }
    }

    LaunchedEffect(result) {
        result?.let {
            val message = when (it) {
                is RequestResult.Success -> it.message
                is RequestResult.Failure -> it.errorMessage
            }
            snackbarHostState.showSnackbar(message)
            if (it is RequestResult.Success) {
                onPublished()
            }
            viewModel.resetPublishResult()
        }
    }

    Scaffold(
        containerColor = Marfil,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
                Text(
                    text = "Crear objeto de trueque",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .background(Marfil)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Sube hasta 5 fotos y completa la informacion del objeto.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                items(photos, key = { it.toString() }) { uri ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Foto del objeto",
                            modifier = Modifier.size(86.dp),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(onClick = { viewModel.removePhoto(uri) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar foto")
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val permission = galleryPermissionForCurrentSdk()
                        if (permission == null || hasPermission(context, permission)) {
                            galleryLauncher.launch("image/*")
                        } else {
                            galleryPermissionLauncher.launch(permission)
                        }
                    },
                    enabled = photos.size < CreateObjectViewModel.MAX_PHOTOS,
                    colors = ButtonDefaults.buttonColors(containerColor = Terracota)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Galeria")
                }

                Button(
                    onClick = {
                        if (hasPermission(context, Manifest.permission.CAMERA)) {
                            cameraLauncher.launch(null)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    enabled = photos.size < CreateObjectViewModel.MAX_PHOTOS,
                    colors = ButtonDefaults.buttonColors(containerColor = Terracota)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Camara")
                }
            }

            OutlinedTextField(
                value = viewModel.name.value,
                onValueChange = viewModel.name::onChange,
                label = { Text("Nombre") },
                supportingText = viewModel.name.error?.let { { Text(it) } },
                isError = viewModel.name.error != null,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.description.value,
                onValueChange = viewModel.description::onChange,
                label = { Text("Descripcion") },
                supportingText = viewModel.description.error?.let { { Text(it) } },
                isError = viewModel.description.error != null,
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            CategoryDropdown(
                value = viewModel.category.value,
                onSelect = viewModel.category::onChange,
                error = viewModel.category.error
            )

            Text(text = "Estado del objeto", style = MaterialTheme.typography.titleMedium)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(ObjectCondition.entries, key = { it.name }) { condition ->
                    FilterChip(
                        selected = viewModel.condition.value == condition,
                        onClick = { viewModel.condition.onChange(condition) },
                        label = { Text(condition.prettyName()) }
                    )
                }
            }
            if (viewModel.condition.error != null) {
                Text(text = viewModel.condition.error!!, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = viewModel.exchangePreferences.value,
                onValueChange = viewModel.exchangePreferences::onChange,
                label = { Text("Que te gustaria a cambio") },
                supportingText = viewModel.exchangePreferences.error?.let { { Text(it) } },
                isError = viewModel.exchangePreferences.error != null,
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = viewModel::publishObject,
                enabled = !isPublishing,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Terracota)
            ) {
                Text("Publicar objeto")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    value: ObjectCategory?,
    onSelect: (ObjectCategory) -> Unit,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value?.prettyName() ?: "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            label = { Text("Categoria") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isError = error != null,
            supportingText = error?.let { { Text(it) } }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ObjectCategory.entries.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.prettyName()) },
                    onClick = {
                        onSelect(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun ObjectCategory.prettyName(): String = when (this) {
    ObjectCategory.TECNOLOGIA -> "Tecnologia"
    ObjectCategory.LIBROS -> "Libros"
    ObjectCategory.ROPA -> "Ropa"
    ObjectCategory.HOGAR -> "Hogar"
    ObjectCategory.DEPORTES -> "Deportes"
}

private fun ObjectCondition.prettyName(): String = when (this) {
    ObjectCondition.NUEVO -> "Nuevo"
    ObjectCondition.COMO_NUEVO -> "Como nuevo"
    ObjectCondition.BUENO -> "Bueno"
    ObjectCondition.REGULAR -> "Regular"
}

private fun galleryPermissionForCurrentSdk(): String? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
}

private fun hasPermission(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
}

private fun saveBitmapToMediaStore(context: Context, bitmap: Bitmap): Uri? {
    val fileName = "trameo_${System.currentTimeMillis()}.jpg"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    if (uri != null) {
        context.contentResolver.openOutputStream(uri)?.use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
        }
    }
    return uri
}



package com.market.trameo.features.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.market.trameo.R
import com.market.trameo.domain.model.HomeObject
import com.market.trameo.domain.model.ModerationStatus

private data class AdminBottomItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AdminScreen(
    onBackClick: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val items = listOf(
        AdminBottomItem(title = stringResource(id = R.string.admin_tab_inicio), icon = Icons.Default.Home),
        AdminBottomItem(title = stringResource(id = R.string.admin_tab_usuarios), icon = Icons.Default.People),
        AdminBottomItem(title = stringResource(id = R.string.admin_tab_ajustes), icon = Icons.Default.Settings)
    )
    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.admin_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.admin_back)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedIndex) {
                0 -> PendingObjectsContent(
                    uiState = uiState,
                    onObjectClick = { viewModel.selectObject(it) }
                )

                1 -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(id = R.string.admin_gestion_usuarios),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(id = R.string.admin_configuracion),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            if (uiState.selectedObject != null) {
                AdminObjectDetailDialog(
                    homeObject = uiState.selectedObject!!,
                    onDismiss = { viewModel.selectObject(null) },
                    onAccept = { viewModel.updateStatus(it.id, ModerationStatus.PUBLICADO) },
                    onReject = { viewModel.updateStatus(it.id, ModerationStatus.RECHAZADO) }
                )
            }
        }
    }
}

@Composable
fun PendingObjectsContent(
    uiState: AdminUiState,
    onObjectClick: (HomeObject) -> Unit
) {
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.pendingObjects.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay publicaciones pendientes")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "Publicaciones Pendientes",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(uiState.pendingObjects) { obj ->
                PendingObjectCard(obj, onClick = { onObjectClick(obj) })
            }
        }
    }
}

@Composable
fun PendingObjectCard(
    obj: HomeObject,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = obj.photos.firstOrNull(),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = obj.name, style = MaterialTheme.typography.titleMedium)
                Text(text = obj.category, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun AdminObjectDetailDialog(
    homeObject: HomeObject,
    onDismiss: () -> Unit,
    onAccept: (HomeObject) -> Unit,
    onReject: (HomeObject) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Revisar Publicación") },
        text = {
            Column {
                AsyncImage(
                    model = homeObject.photos.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = homeObject.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = "Categoría: ${homeObject.category}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Condición: ${homeObject.condition}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Descripción:", style = MaterialTheme.typography.titleSmall)
                Text(text = homeObject.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Preferencias:", style = MaterialTheme.typography.titleSmall)
                Text(text = homeObject.exchangePreferences, style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            Button(
                onClick = { onAccept(homeObject) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Aceptar")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onReject(homeObject) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Rechazar")
            }
        }
    )
}

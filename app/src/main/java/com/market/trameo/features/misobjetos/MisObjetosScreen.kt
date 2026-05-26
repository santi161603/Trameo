package com.market.trameo.features.misobjetos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.market.trameo.R
import com.market.trameo.core.component.BottomNavItem
import com.market.trameo.core.component.TrameoBottomNavigation
import com.market.trameo.core.navigation.Routes
import com.market.trameo.core.theme.Marfil
import com.market.trameo.domain.model.ModerationStatus
import com.market.trameo.domain.model.SwapObject

@Composable
fun MisObjetosScreen(
    onHomeClick: () -> Unit,
    onTruequesClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onObjectClick: (String) -> Unit,
    onPublicarClick: () -> Unit = {},
    viewModel: MyObjectsViewModel = hiltViewModel()
) {
    val items by viewModel.filteredItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatuses by viewModel.selectedStatuses.collectAsState()

    Scaffold(
        containerColor = Marfil,
        bottomBar = {
            TrameoBottomNavigation(
                items = listOf(
                    BottomNavItem(Routes.HOME, stringResource(id = R.string.home_bottom_home), Icons.Default.Home, stringResource(id = R.string.home_bottom_home)),
                    BottomNavItem(Routes.MIS_OBJETOS, stringResource(id = R.string.home_bottom_mis_objetos), Icons.AutoMirrored.Filled.List, stringResource(id = R.string.home_bottom_mis_objetos)),
                    BottomNavItem(Routes.TRUEQUES, stringResource(id = R.string.home_bottom_trueques), Icons.Default.Autorenew, stringResource(id = R.string.home_bottom_trueques)),
                    BottomNavItem(Routes.PERFIL, stringResource(id = R.string.home_bottom_perfil), Icons.Default.Person, stringResource(id = R.string.home_bottom_perfil))
                ),
                currentRoute = Routes.MIS_OBJETOS,
                onItemClick = { item ->
                    when (item.route) {
                        Routes.HOME -> onHomeClick()
                        Routes.TRUEQUES -> onTruequesClick()
                        Routes.PERFIL -> onPerfilClick()
                    }
                },
                onCenterClick = onPublicarClick,
                centerIcon = Icons.Default.Add,
                centerContentDescription = stringResource(id = R.string.home_bottom_publicar)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Marfil),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.mis_objetos_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.mis_objetos_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                SearchField(
                    value = searchQuery,
                    onValueChange = viewModel::updateSearchQuery
                )
                Spacer(modifier = Modifier.height(12.dp))
                StatusFilters(
                    selectedStatuses = selectedStatuses,
                    onToggleStatus = viewModel::toggleStatus
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.mis_objetos_clear_filters),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { viewModel.clearFilters() }
                )
            }

            items(items, key = { it.id }) { item ->
                MiObjetoCard(item = item, onClick = { onObjectClick(item.id) })
            }
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = stringResource(id = R.string.home_search_content_description)) },
        placeholder = { Text(text = stringResource(id = R.string.mis_objetos_search_placeholder)) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun StatusFilters(
    selectedStatuses: Set<ModerationStatus>,
    onToggleStatus: (ModerationStatus) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(ModerationStatus.entries, key = { it.name }) { status ->
            FilterChip(
                selected = selectedStatuses.contains(status),
                onClick = { onToggleStatus(status) },
                label = { Text(text = stringResource(id = status.labelRes())) }
            )
        }
    }
}

@Composable
private fun MiObjetoCard(item: SwapObject, onClick: () -> Unit) {
    val statusLabel = when (item.moderationStatus) {
        ModerationStatus.PUBLICADO -> stringResource(id = R.string.common_status_publicado)
        ModerationStatus.PENDIENTE_VERIFICACION -> stringResource(id = R.string.common_status_pendiente_verificacion)
        ModerationStatus.FINALIZADO -> stringResource(id = R.string.common_status_finalizado)
        ModerationStatus.ELIMINADO -> stringResource(id = R.string.common_status_eliminado)
        ModerationStatus.RECHAZADO -> stringResource(id = R.string.common_status_rechazado)
    }

    val statusColor = when (item.moderationStatus) {
        ModerationStatus.PUBLICADO -> Color(0xFF3F8E4E)
        ModerationStatus.PENDIENTE_VERIFICACION -> Color(0xFFD38A1F)
        ModerationStatus.FINALIZADO -> Color(0xFF4A4A4A)
        ModerationStatus.ELIMINADO -> Color(0xFFB00020)
        ModerationStatus.RECHAZADO -> Color(0xFFB00020)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        val context = LocalContext.current
        Column {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(item.photos.firstOrNull())
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                error = painterResource(id = R.drawable.ic_launcher_foreground),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(id = item.category.labelRes()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = statusLabel,
                    color = statusColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun com.market.trameo.domain.model.ObjectCategory.labelRes(): Int = when (this) {
    com.market.trameo.domain.model.ObjectCategory.TECNOLOGIA -> R.string.object_category_tecnologia
    com.market.trameo.domain.model.ObjectCategory.LIBROS -> R.string.object_category_libros
    com.market.trameo.domain.model.ObjectCategory.ROPA -> R.string.object_category_ropa
    com.market.trameo.domain.model.ObjectCategory.HOGAR -> R.string.object_category_hogar
    com.market.trameo.domain.model.ObjectCategory.DEPORTES -> R.string.object_category_deportes
}

private fun ModerationStatus.labelRes(): Int = when (this) {
    ModerationStatus.PENDIENTE_VERIFICACION -> R.string.common_status_pendiente_verificacion
    ModerationStatus.PUBLICADO -> R.string.common_status_publicado
    ModerationStatus.FINALIZADO -> R.string.common_status_finalizado
    ModerationStatus.ELIMINADO -> R.string.common_status_eliminado
    ModerationStatus.RECHAZADO -> R.string.common_status_rechazado
}

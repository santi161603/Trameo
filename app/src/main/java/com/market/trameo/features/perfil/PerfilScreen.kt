package com.market.trameo.features.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.market.trameo.core.theme.Terracota
import com.market.trameo.domain.model.User
import com.market.trameo.domain.model.UserRole

@Composable
fun PerfilScreen(
    onHomeClick: () -> Unit,
    onTruequesClick: () -> Unit,
    onMisObjetosClick: () -> Unit,
    onPublicarClick: () -> Unit = {},
    onChatsClick: () -> Unit = {},
    onAdminOptionsClick: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val logoutCompleted by viewModel.logoutCompleted.collectAsState()
    val isAdmin = currentUser?.role == UserRole.ADMIN

    LaunchedEffect(logoutCompleted) {
        if (logoutCompleted) {
            onLogoutSuccess()
            viewModel.resetLogoutState()
        }
    }

    Scaffold(
        containerColor = Marfil,
        bottomBar = {
            TrameoBottomNavigation(
                items = listOf(
                    BottomNavItem(
                        Routes.HOME,
                        stringResource(id = R.string.perfil_bottom_home),
                        Icons.Default.Home,
                        stringResource(id = R.string.perfil_bottom_home)
                    ),
                    BottomNavItem(
                        Routes.MIS_OBJETOS,
                        stringResource(id = R.string.perfil_bottom_mis_objetos),
                        Icons.AutoMirrored.Filled.List,
                        stringResource(id = R.string.perfil_bottom_mis_objetos)
                    ),
                    BottomNavItem(
                        Routes.TRUEQUES,
                        stringResource(id = R.string.perfil_bottom_trueques),
                        Icons.Default.Autorenew,
                        stringResource(id = R.string.perfil_bottom_trueques)
                    ),
                    BottomNavItem(
                        Routes.PERFIL,
                        stringResource(id = R.string.perfil_bottom_perfil),
                        Icons.Default.Person,
                        stringResource(id = R.string.perfil_bottom_perfil)
                    )
                ),
                currentRoute = Routes.PERFIL,
                onItemClick = { item ->
                    when (item.route) {
                        Routes.HOME -> onHomeClick()
                        Routes.TRUEQUES -> onTruequesClick()
                        Routes.MIS_OBJETOS -> onMisObjetosClick()
                    }
                },
                onCenterClick = onPublicarClick,
                centerIcon = Icons.Default.Add,
                centerContentDescription = stringResource(id = R.string.perfil_bottom_publicar)
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
                ProfileHeader(currentUser = currentUser)
                Spacer(modifier = Modifier.height(14.dp))
                StatsRow()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.perfil_section_cuenta),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                MenuItem(stringResource(id = R.string.perfil_item_editar_perfil))
                MenuItem(stringResource(id = R.string.perfil_item_metodos_pago))
                MenuItem(stringResource(id = R.string.perfil_item_seguridad))
                MenuItem(stringResource(id = R.string.perfil_item_notificaciones))
                if (isAdmin) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.perfil_section_admin),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    MenuItem(
                        text = stringResource(id = R.string.perfil_item_admin_options),
                        highlighted = true,
                        onClick = onAdminOptionsClick
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.perfil_section_actividad),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                MenuItem(stringResource(id = R.string.perfil_item_historial))
                MenuItem(stringResource(id = R.string.perfil_item_chats), onClick = onChatsClick)
                MenuItem(stringResource(id = R.string.perfil_item_puntos))
                MenuItem(stringResource(id = R.string.perfil_item_ayuda))

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        text = stringResource(id = R.string.perfil_logout_button),
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(currentUser: User?) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(currentUser?.profilePhotoUri ?: "https://picsum.photos/seed/trameo-perfil/300/300")
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(id = R.string.perfil_avatar_content_description),
                    placeholder = painterResource(id = R.drawable.ic_launcher_background),
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    modifier = Modifier
                        .size(74.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = currentUser?.name ?: stringResource(id = R.string.perfil_fallback_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentUser?.city?.let { "$it, Colombia" }
                            ?: stringResource(id = R.string.perfil_fallback_city),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Terracota.copy(alpha = 0.1f)
            ) {
                Text(
                    text = stringResource(id = R.string.perfil_level_text),
                    color = Terracota,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun StatsRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatCard(
            title = stringResource(id = R.string.perfil_stat_swaps_value),
            subtitle = stringResource(id = R.string.perfil_stat_swaps_label),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = stringResource(id = R.string.perfil_stat_active_value),
            subtitle = stringResource(id = R.string.perfil_stat_active_label),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = stringResource(id = R.string.perfil_stat_reputation_value),
            subtitle = stringResource(id = R.string.perfil_stat_reputation_label),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MenuItem(text: String, onClick: (() -> Unit)? = null) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        tonalElevation = 1.dp,
        border = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .let { base -> if (onClick != null) base.clickable(onClick = onClick) else base }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
private fun MenuItem(
    text: String,
    highlighted: Boolean,
    onClick: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (highlighted) Terracota.copy(alpha = 0.12f) else Color.White,
        tonalElevation = if (highlighted) 2.dp else 1.dp,
        border = if (highlighted) BorderStroke(1.dp, Terracota.copy(alpha = 0.65f)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .let { base -> if (onClick != null) base.clickable(onClick = onClick) else base }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (highlighted) Terracota else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (highlighted) FontWeight.SemiBold else FontWeight.Normal
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (highlighted) Terracota else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



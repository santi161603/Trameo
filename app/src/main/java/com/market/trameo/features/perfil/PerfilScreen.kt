package com.market.trameo.features.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
                    BottomNavItem(Routes.HOME, stringResource(id = R.string.perfil_bottom_home), Icons.Default.Home, stringResource(id = R.string.perfil_bottom_home)),
                    BottomNavItem(Routes.MIS_OBJETOS, stringResource(id = R.string.perfil_bottom_mis_objetos), Icons.AutoMirrored.Filled.List, stringResource(id = R.string.perfil_bottom_mis_objetos)),
                    BottomNavItem(Routes.TRUEQUES, stringResource(id = R.string.perfil_bottom_trueques), Icons.Default.Autorenew, stringResource(id = R.string.perfil_bottom_trueques)),
                    BottomNavItem(Routes.PERFIL, stringResource(id = R.string.perfil_bottom_perfil), Icons.Default.Person, stringResource(id = R.string.perfil_bottom_perfil))
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ProfileHeader(currentUser = currentUser)
                Spacer(modifier = Modifier.height(8.dp))
                StatsRow(user = currentUser)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Información Personal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                UserDetailItem(label = "Correo Electrónico", value = currentUser?.email ?: "...")
                UserDetailItem(label = "Teléfono", value = currentUser?.phoneNumber ?: "...")
                UserDetailItem(label = "Ciudad", value = currentUser?.city ?: "...")
                UserDetailItem(label = "Dirección", value = currentUser?.address ?: "...")

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Configuración",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                MenuItem(stringResource(id = R.string.perfil_item_editar_perfil))
                MenuItem(stringResource(id = R.string.perfil_item_notificaciones))
                
                if (isAdmin) {
                    MenuItem(
                        text = "Panel de Administrador",
                        highlighted = true,
                        onClick = onAdminOptionsClick
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(text = "Cerrar Sesión")
                }
                Spacer(modifier = Modifier.height(24.dp))
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
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(currentUser?.profilePhotoUri ?: "https://picsum.photos/seed/trameo/300/300")
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                error = painterResource(id = R.drawable.ic_launcher_foreground),
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, Terracota, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = currentUser?.name ?: "Usuario",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentUser?.role?.name ?: "CLIENTE",
                    style = MaterialTheme.typography.bodySmall,
                    color = Terracota
                )
            }
        }
    }
}

@Composable
private fun UserDetailItem(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun StatsRow(user: User?) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatCard(title = "12", subtitle = "Trueques", modifier = Modifier.weight(1f))
        StatCard(title = "5", subtitle = "Activos", modifier = Modifier.weight(1f))
        StatCard(title = user?.score?.toString() ?: "0", subtitle = "Puntos", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
private fun MenuItem(text: String, highlighted: Boolean = false, onClick: (() -> Unit)? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) Terracota.copy(alpha = 0.1f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal,
                color = if (highlighted) Terracota else Color.Black
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = if (highlighted) Terracota else Color.Gray)
        }
    }
}

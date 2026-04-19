package com.market.trameo.features.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.market.trameo.R
import com.market.trameo.core.component.BottomNavItem
import com.market.trameo.core.component.TrameoBottomNavigation
import com.market.trameo.core.navigation.Routes
import com.market.trameo.ui.theme.Marfil
import com.market.trameo.ui.theme.Terracota

@Composable
fun PerfilScreen(
    onHomeClick: () -> Unit,
    onTruequesClick: () -> Unit,
    onMisObjetosClick: () -> Unit,
    onPublicarClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = Marfil,
        bottomBar = {
            TrameoBottomNavigation(
                items = listOf(
                    BottomNavItem(Routes.HOME, "Home", Icons.Default.Home, "Home"),
                    BottomNavItem(Routes.MIS_OBJETOS, "Mis objetos", Icons.AutoMirrored.Filled.List, "Mis objetos"),
                    BottomNavItem(Routes.TRUEQUES, "Trueques", Icons.Default.Autorenew, "Trueques"),
                    BottomNavItem(Routes.PERFIL, "Perfil", Icons.Default.Person, "Perfil")
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
                centerContentDescription = "Publicar"
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
                ProfileHeader()
                Spacer(modifier = Modifier.height(14.dp))
                StatsRow()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cuenta",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                MenuItem("Editar perfil")
                MenuItem("Metodos de pago")
                MenuItem("Seguridad")
                MenuItem("Notificaciones")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Actividad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                MenuItem("Historial de intercambios")
                MenuItem("Puntos acumulados")
                MenuItem("Ayuda y soporte")
            }
        }
    }
}

@Composable
private fun ProfileHeader() {
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
                        .data("https://picsum.photos/seed/trameo-perfil/300/300")
                        .crossfade(true)
                        .build(),
                    contentDescription = "Avatar perfil",
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
                        text = "Santiago P.",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Medellin, Colombia",
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
                    text = "Nivel Explorador · 840 pts",
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
        StatCard(title = "12", subtitle = "Intercambios", modifier = Modifier.weight(1f))
        StatCard(title = "4", subtitle = "Activos", modifier = Modifier.weight(1f))
        StatCard(title = "4.8", subtitle = "Reputacion", modifier = Modifier.weight(1f))
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
private fun MenuItem(text: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
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



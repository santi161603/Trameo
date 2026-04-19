package com.market.trameo.features.misobjetos

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.market.trameo.ui.theme.MarfilVariant
import com.market.trameo.ui.theme.Terracota

private data class MiObjetoItem(
    val id: Int,
    val title: String,
    val estado: String,
    val puntos: Int,
    val imageUrl: String
)

@Composable
fun MisObjetosScreen(
    onHomeClick: () -> Unit,
    onTruequesClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onPublicarClick: () -> Unit = {}
) {
    val items = listOf(
        MiObjetoItem(1, "Bicicleta urbana", "Publicado", 120, "https://picsum.photos/seed/objeto-1/700/420"),
        MiObjetoItem(2, "Licuadora 2L", "En trueque", 80, "https://picsum.photos/seed/objeto-2/700/420"),
        MiObjetoItem(3, "Guitarra acustica", "Pausado", 150, "https://picsum.photos/seed/objeto-3/700/420")
    )

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
                Text(
                    text = "Mis objetos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Administra tus publicaciones",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                SearchFakeField()
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EstadoChip("Todos", true)
                    EstadoChip("Publicados", false)
                    EstadoChip("En trueque", false)
                }
            }

            items(items, key = { it.id }) { item ->
                MiObjetoCard(item)
            }
        }
    }
}

@Composable
private fun SearchFakeField() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Search, contentDescription = "Buscar")
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Buscar mis objetos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EstadoChip(text: String, selected: Boolean) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (selected) Terracota else MarfilVariant
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun MiObjetoCard(item: MiObjetoItem) {
    val statusColor = when (item.estado) {
        "Publicado" -> Color(0xFF3F8E4E)
        "En trueque" -> Color(0xFFD38A1F)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        val context = LocalContext.current
        Column {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.title,
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
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${item.puntos} pts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = item.estado,
                    color = statusColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

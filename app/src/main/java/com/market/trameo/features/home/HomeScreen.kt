package com.market.trameo.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.market.trameo.R
import com.market.trameo.core.component.BottomNavItem
import com.market.trameo.core.component.TrameoBottomNavigation
import com.market.trameo.core.navigation.Routes
import com.market.trameo.ui.theme.Marfil
import com.market.trameo.ui.theme.MarfilVariant
import com.market.trameo.ui.theme.Terracota

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onTruequesClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onMisObjetosClick: () -> Unit = {},
    onObjectClick: (Int) -> Unit = {},
    onPublicarClick: () -> Unit = {}
) {
    val objects by viewModel.items.collectAsState()

    Scaffold(
        containerColor = Marfil,
        bottomBar = {
            TrameoBottomNavigation(
                items = listOf(
                    BottomNavItem(
                        route = Routes.HOME,
                        title = "Home",
                        icon = Icons.Default.Home,
                        contentDescription = "Home"
                    ),
                    BottomNavItem(
                        route = Routes.MIS_OBJETOS,
                        title = "Mis objetos",
                        icon = Icons.AutoMirrored.Filled.List,
                        contentDescription = "Mis objetos"
                    ),
                    BottomNavItem(
                        route = Routes.TRUEQUES,
                        title = "Trueques",
                        icon = Icons.Default.Autorenew,
                        contentDescription = "Trueques"
                    ),
                    BottomNavItem(
                        route = Routes.PERFIL,
                        title = "Perfil",
                        icon = Icons.Default.Person,
                        contentDescription = "Perfil"
                    )
                ),
                currentRoute = Routes.HOME,
                onItemClick = { item ->
                    if (item.route == Routes.TRUEQUES) {
                        onTruequesClick()
                    }
                    if (item.route == Routes.PERFIL) {
                        onPerfilClick()
                    }
                    if (item.route == Routes.MIS_OBJETOS) {
                        onMisObjetosClick()
                    }
                },
                onCenterClick = onPublicarClick,
                centerIcon = Icons.Default.Add,
                centerContentDescription = "Publicar"
            )
        }
    ) { padding ->
        HomeContent(
            padding = padding,
            items = objects,
            onObjectClick = onObjectClick
        )
    }
}

@Composable
private fun HomeContent(
    padding: PaddingValues,
    items: List<HomeObjectItem>,
    onObjectClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Marfil),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            HomeHeader()
            Spacer(modifier = Modifier.height(12.dp))
            HomeSearchBox()
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HomeChip(text = "Movilidad")
                HomeChip(text = "Hogar")
                HomeChip(text = "Tecnologia")
            }
            Spacer(modifier = Modifier.height(14.dp))
            HomeBanner()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Objetos cerca de ti",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(items, key = { it.id }) { item ->
            HomeObjectCard(item) { onObjectClick(item.id) }
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val context = LocalContext.current
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("https://picsum.photos/seed/trameo-profile/200/200")
                .crossfade(true)
                .build(),
            contentDescription = "Perfil",
            placeholder = painterResource(id = R.drawable.ic_launcher_background),
            error = painterResource(id = R.drawable.ic_launcher_foreground),
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .border(1.dp, Color.White, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hola, Santiago",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Encuentra objetos para intercambiar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = "Notificaciones"
            )
        }
    }
}

@Composable
private fun HomeSearchBox() {
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
                text = "Buscar por nombre o categoria",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HomeBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(185.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        val context = LocalContext.current
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("https://picsum.photos/seed/trameo-home-banner/1000/600")
                    .crossfade(true)
                    .build(),
                contentDescription = "Banner",
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                error = painterResource(id = R.drawable.ic_launcher_foreground),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.68f)
                    .background(Color.Black.copy(alpha = 0.28f))
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = "Encuentra trueques cerca de ti",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Nuevos objetos cada dia",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun HomeObjectCard(item: HomeObjectItem, onClick: () -> Unit) {
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
                    .data(item.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.title,
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                error = painterResource(id = R.drawable.ic_launcher_foreground),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${item.category} • ${item.location}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Terracota.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = "${item.points} pts",
                            color = Terracota,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeChip(text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MarfilVariant,
        modifier = Modifier.border(1.dp, Color.White, RoundedCornerShape(999.dp))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

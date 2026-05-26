package com.market.trameo.features.intercambios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.market.trameo.R
import com.market.trameo.core.component.BottomNavItem
import com.market.trameo.core.component.TrameoBottomNavigation
import com.market.trameo.core.navigation.Routes
import com.market.trameo.core.theme.Marfil
import com.market.trameo.core.theme.Terracota

@Composable
fun MisIntercambiosScreen(
    onHomeClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onMisObjetosClick: () -> Unit,
    onPublicarClick: () -> Unit = {},
    onIntercambioClick: (String) -> Unit = {},
    viewModel: MisIntercambiosViewModel = hiltViewModel()
) {
    val intercambios by viewModel.intercambios.collectAsState()

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
                currentRoute = Routes.TRUEQUES,
                onItemClick = { item ->
                    when (item.route) {
                        Routes.HOME -> onHomeClick()
                        Routes.PERFIL -> onPerfilClick()
                        Routes.MIS_OBJETOS -> onMisObjetosClick()
                    }
                },
                onCenterClick = onPublicarClick,
                centerIcon = Icons.Default.Add,
                centerContentDescription = stringResource(id = R.string.home_bottom_publicar)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Marfil)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.intercambios_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.intercambios_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (intercambios.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No tienes propuestas de intercambio aún.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(intercambios, key = { it.id }) { item ->
                        IntercambioCard(item = item, onClick = { onIntercambioClick(item.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun IntercambioCard(item: IntercambioUI, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Objeto Ofrecido
                TradeObjectImage(
                    imageUrl = item.objetoOfrecido?.photos?.firstOrNull(),
                    label = if (item.isSent) "Ofreces" else "Te ofrecen"
                )

                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = Terracota,
                    modifier = Modifier.size(32.dp)
                )

                // Objeto Deseado
                TradeObjectImage(
                    imageUrl = item.objetoDeseado?.photos?.firstOrNull(),
                    label = if (item.isSent) "Deseas" else "Tu objeto"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${item.objetoOfrecido?.name ?: "Objeto"} por ${item.objetoDeseado?.name ?: "Objeto"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.statusText,
                style = MaterialTheme.typography.bodySmall,
                color = if (item.isSent) Color(0xFFD38A1F) else Terracota,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TradeObjectImage(imageUrl: String?, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

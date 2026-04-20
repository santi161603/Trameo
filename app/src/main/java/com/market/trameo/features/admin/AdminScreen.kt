package com.market.trameo.features.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.market.trameo.R

private data class AdminBottomItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AdminScreen(
    onBackClick: () -> Unit
) {
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
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (selectedIndex) {
                0 -> Text(
                    text = stringResource(id = R.string.admin_hola),
                    style = MaterialTheme.typography.headlineSmall
                )

                1 -> Text(
                    text = stringResource(id = R.string.admin_gestion_usuarios),
                    style = MaterialTheme.typography.titleMedium
                )

                else -> Text(
                    text = stringResource(id = R.string.admin_configuracion),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}



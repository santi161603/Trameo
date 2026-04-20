package com.market.trameo.features.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.market.trameo.R
import com.market.trameo.core.theme.Marfil
import com.market.trameo.core.theme.Terracota

@Composable
fun DetalleObjetoScreen(
    objectId: String,
    onBackClick: () -> Unit,
    onProponerIntercambioClick: (String) -> Unit,
    viewModel: DetalleObjetoViewModel = hiltViewModel()
) {
    LaunchedEffect(objectId) {
        viewModel.load(objectId)
    }
    val item by viewModel.item.collectAsState()

    if (item == null) {
        Scaffold(containerColor = Marfil) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Objeto no encontrado", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBackClick) { Text("Volver") }
            }
        }
        return
    }

    val swapObject = item!!

    Scaffold(
        containerColor = Marfil,
        topBar = {
            IconButton(onClick = onBackClick, modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(Marfil)
                .padding(16.dp)
        ) {
            Text(
                text = swapObject.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${swapObject.category.prettyName()} • ${swapObject.condition.prettyName()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(swapObject.photos.take(5), key = { it.toString() }) { photo ->
                    AsyncImage(
                        model = photo,
                        contentDescription = "Foto del objeto",
                        placeholder = painterResource(id = R.drawable.ic_launcher_background),
                        error = painterResource(id = R.drawable.ic_launcher_foreground),
                        modifier = Modifier
                            .width(260.dp)
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Descripcion",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = swapObject.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Interesado en: ${swapObject.exchangePreferences}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = { onProponerIntercambioClick(swapObject.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Terracota),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text("Proponer intercambio")
            }
        }
    }
}

private fun com.market.trameo.domain.model.ObjectCategory.prettyName(): String = when (this) {
    com.market.trameo.domain.model.ObjectCategory.TECNOLOGIA -> "Tecnologia"
    com.market.trameo.domain.model.ObjectCategory.LIBROS -> "Libros"
    com.market.trameo.domain.model.ObjectCategory.ROPA -> "Ropa"
    com.market.trameo.domain.model.ObjectCategory.HOGAR -> "Hogar"
    com.market.trameo.domain.model.ObjectCategory.DEPORTES -> "Deportes"
}

private fun com.market.trameo.domain.model.ObjectCondition.prettyName(): String = when (this) {
    com.market.trameo.domain.model.ObjectCondition.NUEVO -> "Nuevo"
    com.market.trameo.domain.model.ObjectCondition.COMO_NUEVO -> "Como nuevo"
    com.market.trameo.domain.model.ObjectCondition.BUENO -> "Bueno"
    com.market.trameo.domain.model.ObjectCondition.REGULAR -> "Regular"
}

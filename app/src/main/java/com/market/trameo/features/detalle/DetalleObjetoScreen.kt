package com.market.trameo.features.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.market.trameo.R
import com.market.trameo.domain.model.HomeObject
import com.market.trameo.domain.model.ObjectCategory
import com.market.trameo.domain.model.ObjectCondition

private object DetalleColors {
    val Background = Color(0xFFF6F4EE)
    val MainText = Color(0xFF2F3A3A)
    val Accent = Color(0xFFD9562A)
    val White = Color(0xFFFFFFFF)
    val ImageStroke = Color(0xFF1E1E1E)
    val CategoryChip = Color(0x266B8A4A)
    val ConditionChip = Color(0x26E8A15A)
}

data class ObjetoDetalle(
    val id: String,
    val nombre: String,
    val ubicacion: String,
    val descripcion: String,
    val precio: String,
    val imagenUrl: String?,
    val imagenesAdicionales: List<String> = emptyList(),
    val categoria: String,
    val estado: String,
    val buscaIntercambio: String,
    val publicadoPorNombre: String,
    val publicadoPorFotoUrl: String?
)

@Composable
fun DetalleObjetoScreen(
    objeto: ObjetoDetalle,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    DetalleObjetoContent(
        objeto = objeto,
        onBack = onBack,
        onPrimaryAction = {},
        modifier = modifier
    )
}

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
        Scaffold(
            containerColor = DetalleColors.Background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Objeto no encontrado",
                    style = MaterialTheme.typography.titleMedium,
                    color = DetalleColors.MainText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBackClick) { Text(stringResource(id = R.string.common_back)) }
            }
        }
        return
    }

    val detalle = item!!.toObjetoDetalle()
    DetalleObjetoContent(
        objeto = detalle,
        onBack = onBackClick,
        onPrimaryAction = { onProponerIntercambioClick(detalle.id) }
    )
}

@Composable
private fun DetalleObjetoContent(
    objeto: ObjetoDetalle,
    onBack: () -> Unit,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageUrls = remember(objeto.imagenUrl, objeto.imagenesAdicionales) {
        buildList {
            objeto.imagenUrl?.takeIf { it.isNotBlank() }?.let(::add)
            addAll(objeto.imagenesAdicionales.filter { it.isNotBlank() })
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = DetalleColors.Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(id = R.string.common_back),
                        tint = DetalleColors.MainText
                    )
                }
                Text(
                    text = "Detalle del objeto",
                    style = MaterialTheme.typography.titleLarge,
                    color = DetalleColors.MainText,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DetalleColors.Background),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ImageCarousel(imageUrls = imageUrls)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OvalTag(
                        text = objeto.categoria,
                        background = DetalleColors.CategoryChip,
                        textColor = Color(0xFF6B8A4A)
                    )
                    OvalTag(
                        text = objeto.estado,
                        background = DetalleColors.ConditionChip,
                        textColor = Color(0xFFE8A15A)
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = objeto.nombre,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DetalleColors.MainText
                    )
                    Text(
                        text = objeto.ubicacion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DetalleColors.MainText.copy(alpha = 0.7f)
                    )
                }
            }

            item {
                SectionCard(
                    title = stringResource(id = R.string.detalle_descripcion),
                    body = objeto.descripcion
                )
            }

            item {
                SectionCard(
                    title = stringResource(id = R.string.detalle_busco_cambio),
                    body = objeto.buscaIntercambio
                )
            }

            item {
                PublisherCard(
                    name = objeto.publicadoPorNombre,
                    avatarUrl = objeto.publicadoPorFotoUrl
                )
            }

            item {
                Button(
                    onClick = onPrimaryAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DetalleColors.Accent)
                ) {
                    Text(
                        text = stringResource(id = R.string.proponer_title),
                        color = DetalleColors.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageCarousel(imageUrls: List<String>) {
    if (imageUrls.isEmpty()) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .border(2.dp, DetalleColors.ImageStroke, RoundedCornerShape(20.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DetalleColors.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = stringResource(id = R.string.detalle_sin_imagen),
                    tint = DetalleColors.MainText.copy(alpha = 0.45f),
                    modifier = Modifier.size(60.dp)
                )
            }
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .border(2.dp, DetalleColors.ImageStroke, RoundedCornerShape(20.dp))
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = imageUrls[page],
                    contentDescription = stringResource(id = R.string.detalle_imagen_index, page + 1),
                    placeholder = painterResource(id = R.drawable.ic_launcher_background),
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        if (imageUrls.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(imageUrls.size) { index ->
                    val active = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (active) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (active) DetalleColors.Accent else DetalleColors.MainText.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun OvalTag(
    text: String,
    background: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = background
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    body: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DetalleColors.White,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = DetalleColors.MainText
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = DetalleColors.MainText.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun PublisherCard(
    name: String,
    avatarUrl: String?
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DetalleColors.White,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = stringResource(id = R.string.detalle_foto_publicador),
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                error = painterResource(id = R.drawable.ic_launcher_foreground),
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.detalle_publicado_por),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = DetalleColors.MainText
                )
            }
        }
    }
}

private fun com.market.trameo.domain.model.SwapObject.toObjetoDetalle(): ObjetoDetalle {
    val firstImage = photos.firstOrNull()?.toString()
    val extraImages = photos.drop(1).map { it.toString() }
    val ownerName = if (ownerId == "seed-user-1") "Santiago Acero" else "Usuario Trameo"
    val ownerPhoto = if (ownerId == "seed-user-1") {
        "https://picsum.photos/seed/trameo-profile-seed/200/200"
    } else {
        ""
    }

    return ObjetoDetalle(
        id = id,
        nombre = name,
        ubicacion = "Armenia, Quindio",
        descripcion = description,
        precio = "Intercambio",
        imagenUrl = firstImage,
        imagenesAdicionales = extraImages,
        categoria = category.prettyName(),
        estado = condition.prettyName(),
        buscaIntercambio = exchangePreferences,
        publicadoPorNombre = ownerName,
        publicadoPorFotoUrl = ownerPhoto
    )
}

private fun HomeObject.toObjetoDetalle(): ObjetoDetalle {
    val firstImage = photos.firstOrNull()
    val extraImages = photos.drop(1)
    val ownerName = if (ownerId == "seed-user-1") "Santiago Acero" else "Usuario Trameo"
    val ownerPhoto = if (ownerId == "seed-user-1") {
        "https://picsum.photos/seed/trameo-profile-seed/200/200"
    } else {
        ""
    }

    return ObjetoDetalle(
        id = id,
        nombre = name,
        ubicacion = "Armenia, Quindio",
        descripcion = description,
        precio = "Intercambio",
        imagenUrl = firstImage,
        imagenesAdicionales = extraImages,
        categoria = category,
        estado = condition,
        buscaIntercambio = exchangePreferences,
        publicadoPorNombre = ownerName,
        publicadoPorFotoUrl = ownerPhoto
    )
}

private fun ObjectCategory.prettyName(): String = when (this) {
    ObjectCategory.TECNOLOGIA -> "Tecnologia"
    ObjectCategory.LIBROS -> "Libros"
    ObjectCategory.ROPA -> "Ropa"
    ObjectCategory.HOGAR -> "Hogar"
    ObjectCategory.DEPORTES -> "Deportes"
}

private fun ObjectCondition.prettyName(): String = when (this) {
    ObjectCondition.NUEVO -> "Nuevo"
    ObjectCondition.COMO_NUEVO -> "Como nuevo"
    ObjectCondition.BUENO -> "Bueno"
    ObjectCondition.REGULAR -> "Regular"
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F4EE)
@Composable
private fun PreviewDetalleObjeto() {
    DetalleObjetoScreen(
        objeto = ObjetoDetalle(
            id = "obj-001",
            nombre = "Guitarra",
            ubicacion = "Armenia, Quindio",
            descripcion = "Guitarra acustica en muy buen estado. Incluye estuche blando y cuerdas nuevas.",
            precio = "Intercambio",
            imagenUrl = "https://picsum.photos/seed/trameo-detalle-1/900/700",
            imagenesAdicionales = listOf(
                "https://picsum.photos/seed/trameo-detalle-2/900/700",
                "https://picsum.photos/seed/trameo-detalle-3/900/700"
            ),
            categoria = "Musica",
            estado = "Bueno",
            buscaIntercambio = "Busco consola retro o audifonos bluetooth.",
            publicadoPorNombre = "Santiago Acero",
            publicadoPorFotoUrl = "https://picsum.photos/seed/trameo-profile-seed/200/200"
        ),
        onBack = {}
    )
}

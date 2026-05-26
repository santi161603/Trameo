package com.market.trameo.features.detalleusuario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.market.trameo.R
import com.market.trameo.domain.model.Review
import java.text.SimpleDateFormat
import java.util.*

private object DetalleUsuarioColors {
    val Background = Color(0xFFF6F4EE)
    val MainText = Color(0xFF2F3A3A)
    val White = Color(0xFFFFFFFF)
    val Accent = Color(0xFFD9562A)
    val StarColor = Color(0xFFFFC107)
}

@Composable
fun DetalleUsuarioScreen(
    userId: String,
    onBackClick: () -> Unit,
    viewModel: DetalleUsuarioViewModel = hiltViewModel()
) {
    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = DetalleUsuarioColors.Background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Atrás",
                        tint = DetalleUsuarioColors.MainText
                    )
                }
                Text(
                    text = "Detalle del usuario",
                    style = MaterialTheme.typography.titleLarge,
                    color = DetalleUsuarioColors.MainText,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DetalleUsuarioColors.Accent)
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
            }
        } else if (state.user != null) {
            val user = state.user!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(DetalleUsuarioColors.Background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    UserHeader(
                        name = user.name.ifBlank { "Usuario Trameo" },
                        photoUrl = user.profilePhotoUri,
                        city = user.city.ifBlank { "Desconocida" }
                    )
                }

                item {
                    Text(
                        text = "Reseñas (${state.reviews.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DetalleUsuarioColors.MainText
                    )
                }

                if (state.reviews.isEmpty()) {
                    item {
                        Text(
                            text = "Este usuario aún no tiene reseñas.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DetalleUsuarioColors.MainText.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                } else {
                    items(state.reviews) { review ->
                        ReviewCard(review = review)
                    }
                }
            }
        }
    }
}

@Composable
private fun UserHeader(
    name: String,
    photoUrl: String?,
    city: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DetalleUsuarioColors.White,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Foto de perfil",
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                error = painterResource(id = R.drawable.ic_launcher_foreground),
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = DetalleUsuarioColors.MainText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "De: $city",
                style = MaterialTheme.typography.bodyLarge,
                color = DetalleUsuarioColors.MainText.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val dateString = dateFormat.format(Date(review.timestamp))

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DetalleUsuarioColors.White,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = review.reviewerPhotoUrl,
                    contentDescription = "Foto del autor de la reseña",
                    placeholder = painterResource(id = R.drawable.ic_launcher_background),
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.reviewerName.ifBlank { "Usuario" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DetalleUsuarioColors.MainText
                    )
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodySmall,
                        color = DetalleUsuarioColors.MainText.copy(alpha = 0.5f)
                    )
                }
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = DetalleUsuarioColors.StarColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            if (review.comment.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = review.comment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DetalleUsuarioColors.MainText.copy(alpha = 0.8f)
                )
            }
        }
    }
}


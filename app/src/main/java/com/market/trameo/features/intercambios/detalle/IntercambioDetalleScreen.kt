package com.market.trameo.features.intercambios.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.market.trameo.core.theme.Marfil
import com.market.trameo.core.theme.Terracota
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntercambioDetalleScreen(
    intercambioId: String,
    onBackClick: () -> Unit,
    viewModel: IntercambioDetalleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(intercambioId) {
        viewModel.loadDetalle(intercambioId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Intercambio") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Marfil)
            )
        },
        containerColor = Marfil
    ) { padding ->
        if (uiState.loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Terracota)
            }
        } else if (uiState.propuesta != null) {
            val propuesta = uiState.propuesta!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Card de comparación
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ObjectInfo(
                                name = uiState.objetoOfrecido?.name ?: "Objeto",
                                imageUrl = uiState.objetoOfrecido?.photos?.firstOrNull(),
                                label = if (uiState.isSent) "Tu ofreces" else "Te ofrecen"
                            )

                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = Terracota,
                                modifier = Modifier.size(40.dp)
                            )

                            ObjectInfo(
                                name = uiState.objetoDeseado?.name ?: "Objeto",
                                imageUrl = uiState.objetoDeseado?.photos?.firstOrNull(),
                                label = if (uiState.isSent) "Deseas recibir" else "Tu objeto"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Estado
                Text(
                    text = "Estado: ${propuesta.estado}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = when (propuesta.estado) {
                        "ACEPTADO" -> Color(0xFF4CAF50)
                        "RECHAZADO" -> Color.Red
                        else -> Color(0xFFD38A1F)
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botones de acción
                if (!uiState.isSent && propuesta.estado == "PENDIENTE") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { viewModel.responderPropuesta(true) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Aceptar")
                        }
                        Button(
                            onClick = { viewModel.responderPropuesta(false) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Rechazar")
                        }
                    }
                } else if (uiState.isSent && propuesta.estado == "PENDIENTE") {
                    Text(
                        text = "Pendiente de que se acepte",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (uiState.showReviewDialog) {
                    ReviewDialog(
                        rating = uiState.reviewRating,
                        comment = uiState.reviewComment,
                        onRatingChange = { viewModel.updateReviewRating(it) },
                        onCommentChange = { viewModel.updateReviewComment(it) },
                        onDismiss = { viewModel.dismissReviewDialog() },
                        onSubmit = { viewModel.submitReview() }
                    )
                }

                if (uiState.reviewSubmitted) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "¡Reseña enviada con éxito!",
                        color = Color(0xFF4CAF50),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewDialog(
    rating: Int,
    comment: String,
    onRatingChange: (Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Califica a este usuario",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¿Qué tal fue intercambiar con esta persona?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Estrellas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Calificar con $i estrellas",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { onRatingChange(i) }
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    label = { Text("Escribe un comentario (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Terracota,
                        focusedLabelColor = Terracota
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onSubmit,
                enabled = rating > 0,
                colors = ButtonDefaults.textButtonColors(contentColor = Terracota)
            ) {
                Text("Enviar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
            ) {
                Text("Cancelar")
            }
        },
        containerColor = Marfil
    )
}

@Composable
private fun ObjectInfo(name: String, imageUrl: String?, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(120.dp)) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Terracota)
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

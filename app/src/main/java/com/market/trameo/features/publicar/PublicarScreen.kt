package com.market.trameo.features.publicar

import androidx.compose.runtime.Composable
import com.market.trameo.features.swap.create.CreateObjectScreen

@Composable
fun PublicarScreen(
    onBackClick: () -> Unit
) {
    CreateObjectScreen(
        onBackClick = onBackClick,
        onPublished = onBackClick
    )
}

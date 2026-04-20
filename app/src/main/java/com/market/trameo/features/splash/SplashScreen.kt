package com.market.trameo.features.splash

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.market.trameo.R
import com.market.trameo.core.theme.Marfil
import com.market.trameo.core.theme.Terracota
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinish: () -> Unit,
    durationMillis: Long = 2000L
) {
    LaunchedEffect(Unit) {
        delay(durationMillis)
        onFinish()
    }

    val transition = rememberInfiniteTransition(label = "splash_loading")
    val progress = transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading_progress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Terracota)
    ) {
        // Capa de círculos sutiles para replicar el fondo del diseño.
        Box(
            modifier = Modifier
                .size(360.dp)
                .offset(x = (-130).dp, y = (-80).dp)
                .background(Color(0x4CC44D25), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 140.dp, y = 120.dp)
                .background(Color(0x4CC44D25), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 100.dp, y = (-40).dp)
                .background(Color(0x1AE8A15A), CircleShape)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(Marfil, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = stringResource(id = R.string.splash_logo_content_description),
                    modifier = Modifier.size(88.dp)
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = stringResource(id = R.string.splash_title),
                color = Marfil,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(id = R.string.splash_subtitle),
                color = Marfil.copy(alpha = 0.85f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .width(96.dp)
                    .height(5.dp)
                    .background(Marfil.copy(alpha = 0.30f), RoundedCornerShape(99.dp))
            ) {
                Box(
                    modifier = Modifier
                        .width((96f * progress.value).dp)
                        .height(5.dp)
                        .background(Marfil.copy(alpha = 0.85f), RoundedCornerShape(99.dp))
                )
            }
        }

        Text(
            text = stringResource(id = R.string.splash_footer),
            color = Marfil.copy(alpha = 0.55f),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-36).dp)
        )
    }
}



package com.market.trameo.core.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TerracotaLight,
    onPrimary = Color.White,
    primaryContainer = TerracotaDark,
    onPrimaryContainer = Color.White,
    secondary = OcreSuave,
    onSecondary = GrisPizarraDark,
    secondaryContainer = OcreSuaveDark,
    onSecondaryContainer = Color.White,
    tertiary = VerdeOlivaLight,
    onTertiary = Color.White,
    tertiaryContainer = VerdeOlivaDark,
    onTertiaryContainer = Color.White,
    background = GrisPizarraDark,
    onBackground = Marfil,
    surface = GrisPizarra,
    onSurface = Marfil,
    surfaceVariant = GrisPizarraLight,
    onSurfaceVariant = MarfilVariant,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    outline = GrisPizarraLight
)

private val LightColorScheme = lightColorScheme(
    primary = Terracota,
    onPrimary = Color.White,
    primaryContainer = TerracotaLight,
    onPrimaryContainer = Color.White,
    secondary = OcreSuave,
    onSecondary = GrisPizarraDark,
    secondaryContainer = OcreSuaveLight,
    onSecondaryContainer = GrisPizarraDark,
    tertiary = VerdeOliva,
    onTertiary = Color.White,
    tertiaryContainer = VerdeOlivaLight,
    onTertiaryContainer = Color.White,
    background = Marfil,
    onBackground = GrisPizarra,
    surface = Marfil,
    onSurface = GrisPizarra,
    surfaceVariant = MarfilVariant,
    onSurfaceVariant = GrisPizarraLight,
    error = Color(0xFFB00020),
    onError = Color.White,
    outline = OcreSuaveDark
)

@Composable
fun TrameoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

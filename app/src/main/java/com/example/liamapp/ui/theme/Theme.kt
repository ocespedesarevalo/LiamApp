package com.example.liamapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MedicalPrimary,
    secondary = HealthBlue,
    tertiary = MedicalTertiary,
    background = Color(0xFF1A1C1C),
    surface = Color(0xFF1A1C1C),
    error = MedicalError
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalPrimary,
    secondary = HealthBlue,
    tertiary = MedicalTertiary,
    background = MedicalBackground,
    surface = MedicalSurface,
    error = MedicalError
)

@Composable
fun LiamAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

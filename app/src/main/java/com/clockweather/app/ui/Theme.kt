package com.clockweather.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFF0B5FA5), onPrimary = Color.White, secondary = Color(0xFFB36B00),
    background = Color(0xFFF6F7F9), surface = Color.White, surfaceVariant = Color(0xFFE9EDF2),
    onBackground = Color(0xFF14181D), onSurface = Color(0xFF14181D),
)
private val Dark = darkColorScheme(
    primary = Color(0xFF7DB8F0), onPrimary = Color(0xFF00243F), secondary = Color(0xFFFFB84D),
    background = Color(0xFF0F1318), surface = Color(0xFF181D24), surfaceVariant = Color(0xFF232A33),
    onBackground = Color(0xFFEDF1F5), onSurface = Color(0xFFEDF1F5),
)

@Composable
fun ClockWeatherTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}

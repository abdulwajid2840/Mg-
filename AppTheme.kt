package com.mgplus.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ParchmentLight = lightColorScheme(
    primary = Color(0xFF53664A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E8CC),
    onPrimaryContainer = Color(0xFF172312),
    secondary = Color(0xFF59634F),
    secondaryContainer = Color(0xFFE0E7D5),
    tertiary = Color(0xFF87664A),
    background = Color(0xFFFAF8F0),
    surface = Color(0xFFFAF8F0),
    surfaceContainer = Color(0xFFF0EEE5),
    surfaceVariant = Color(0xFFE7E5DC),
    onSurface = Color(0xFF1B1C17),
    onSurfaceVariant = Color(0xFF45483F),
    outline = Color(0xFF77796F)
)

private val ParchmentDark = darkColorScheme(
    primary = Color(0xFFBCD0AE),
    onPrimary = Color(0xFF26351F),
    primaryContainer = Color(0xFF3C4D33),
    onPrimaryContainer = Color(0xFFD8E8CC),
    secondary = Color(0xFFC4CEB8),
    secondaryContainer = Color(0xFF414A39),
    tertiary = Color(0xFFE4C1A0),
    background = Color(0xFF141610),
    surface = Color(0xFF141610),
    surfaceContainer = Color(0xFF20221B),
    surfaceVariant = Color(0xFF41443A),
    onSurface = Color(0xFFE4E4D9),
    onSurfaceVariant = Color(0xFFC4C7B9),
    outline = Color(0xFF8E9184)
)

@Composable
fun MgPlusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) ParchmentDark else ParchmentLight,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}

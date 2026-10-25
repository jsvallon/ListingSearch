package com.johnvallon.listingsearch

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1E5B47),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCEBE3),
    onPrimaryContainer = Color(0xFF0B3B2C),
    secondaryContainer = Color(0xFFDCEBE3),
    onSecondaryContainer = Color(0xFF1E5B47),
    background = Color(0xFFF4F6F3),
    onBackground = Color(0xFF1A1C1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1C1A),
    surfaceVariant = Color(0xFFE7EAE6),
    onSurfaceVariant = Color(0xFF5B625E),
    surfaceContainer = Color(0xFFF4F6F3),
    surfaceContainerHigh = Color(0xFFE7EAE6),
    outline = Color(0xFF727973),
    outlineVariant = Color(0xFFC3C8C4),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF8C1D18),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8CD5B5),
    onPrimary = Color(0xFF003827),
    primaryContainer = Color(0xFF1E5B47),
    onPrimaryContainer = Color(0xFFCDE9DB),
    secondaryContainer = Color(0xFF2A4A3D),
    onSecondaryContainer = Color(0xFFCDE9DB),
    background = Color(0xFF101411),
    onBackground = Color(0xFFE1E3DF),
    surface = Color(0xFF1A1E1B),
    onSurface = Color(0xFFE1E3DF),
    surfaceVariant = Color(0xFF3F4642),
    onSurfaceVariant = Color(0xFFC0C8C2),
    surfaceContainer = Color(0xFF101411),
    surfaceContainerHigh = Color(0xFF2A2F2B),
    outline = Color(0xFF8A938D),
    outlineVariant = Color(0xFF3F4642),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
)

/**
 * Provides the app-wide Material 3 color palette used across screens.
 *
 * @param darkTheme Whether to use the dark color scheme based on the system preference.
 * @param content The screen content to render with the selected theme.
 */
@Composable
fun ListingSearchTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
}

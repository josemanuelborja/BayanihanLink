package com.example.bayanihanlink.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// The Material color scheme follows the same palette as our own AppColors,
// so Material components (buttons, text fields, dialogs...) match the design.
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2B49CC),
    onPrimary = Color.White,
    secondary = Color(0xFF2B49CC),
    onSecondary = Color.White,
    background = LightAppColors.background,
    onBackground = LightAppColors.textPrimary,
    surface = LightAppColors.surface,
    onSurface = LightAppColors.textPrimary,
    surfaceVariant = LightAppColors.cardGray,
    onSurfaceVariant = LightAppColors.textSecondary,
    outline = LightAppColors.borderGray
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8EA2FF),
    onPrimary = Color(0xFF101018),
    secondary = Color(0xFF8EA2FF),
    onSecondary = Color(0xFF101018),
    background = DarkAppColors.background,
    onBackground = DarkAppColors.textPrimary,
    surface = DarkAppColors.surface,
    onSurface = DarkAppColors.textPrimary,
    surfaceVariant = DarkAppColors.cardGray,
    onSurfaceVariant = DarkAppColors.textSecondary,
    outline = DarkAppColors.borderGray
)

@Composable
fun BayanihanLinkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val appColors = if (darkTheme) DarkAppColors else LightAppColors

    // Provide our own colors to every screen below this point.
    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

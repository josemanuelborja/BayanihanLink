package com.example.bayanihanlink.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The colors used all over the app.
 *
 * Keeping them in one place is what makes Dark Mode possible: instead of
 * writing Color.White / Color(0xFF1A1A2E) on every screen, screens ask for
 * "the page background" or "the normal text color" and this file decides
 * which exact color to use based on the current theme.
 */
data class AppColorPalette(
    val accentBlue: Color,      // buttons, links, highlights
    val headerBlue: Color,      // top header banners (white text sits on it)
    val background: Color,      // page background
    val surface: Color,         // cards, bottom navigation bars
    val cardGray: Color,        // light gray panels inside cards
    val fieldBackground: Color, // text input backgrounds
    val borderGray: Color,      // card / field borders and dividers
    val labelGray: Color,       // small labels and secondary text
    val textPrimary: Color,     // main text
    val textSecondary: Color,   // description text
    val accentSoft: Color,      // light blue panel background (e.g. selected chips)
    val isDark: Boolean
)

// The exact colors the design already uses in light mode.
val LightAppColors = AppColorPalette(
    accentBlue = Color(0xFF2B49CC),
    headerBlue = Color(0xFF2B49CC),
    background = Color.White,
    surface = Color.White,
    cardGray = Color(0xFFF7F8FA),
    fieldBackground = Color(0xFFF3F4F8),
    borderGray = Color(0xFFE3E5EC),
    labelGray = Color(0xFF8A8FA3),
    textPrimary = Color(0xFF1A1A2E),
    textSecondary = Color(0xFF5A5F73),
    accentSoft = Color(0xFFEEF0FC),
    isDark = false
)

// Dark equivalents that keep the same look and feel.
val DarkAppColors = AppColorPalette(
    accentBlue = Color(0xFF8EA2FF),
    headerBlue = Color(0xFF24408F),
    background = Color(0xFF101018),
    surface = Color(0xFF1B1B25),
    cardGray = Color(0xFF24242F),
    fieldBackground = Color(0xFF24242F),
    borderGray = Color(0xFF34344A),
    labelGray = Color(0xFF9AA0B5),
    textPrimary = Color(0xFFF1F2F6),
    textSecondary = Color(0xFFC3C7D4),
    accentSoft = Color(0xFF242A4A),
    isDark = true
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }

/**
 * Read the current colors from inside any @Composable function:
 *
 *     val colors = AppColors
 *     Text("Hello", color = colors.textPrimary)
 */
val AppColors: AppColorPalette
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current

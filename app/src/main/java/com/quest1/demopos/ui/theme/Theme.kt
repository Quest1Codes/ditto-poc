package com.quest1.demopos.ui.theme

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// --- Color Palette ---

// Semantic Palette
val PrimaryAction = Color(0xFFF36921)
val SecondaryAction = Color(0xFF4944F6)
val BrandAccent = Color(0xFF9301DE)

// Feedback Colors
val Success = Color(0xFF28a745)
val Error = Color(0xFFdc3545)
val Warning = Color(0xFFFBB609)

// Neutral Palette (Light)
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFF5F5F5)
val LightTextPrimary = Color(0xFF000038)
val LightTextSecondary = Color(0xFF555555)
val LightBorders = Color(0xFFE0E0E0)

// Neutral Palette (Dark)
val DarkBackground = Color(0xFF000038)
val DarkSurface = Color(0xFF1F1F4B)
val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkTextSecondary = Color(0xFFB0B0B0)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryAction,
    secondary = SecondaryAction,
    tertiary = BrandAccent,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    error = Error,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryAction,
    secondary = SecondaryAction,
    tertiary = BrandAccent,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    error = Error,
    onError = Color.White
)

// --- Typography ---
// Note: You need to add the "Inter" font family to your `res/font` folder.
val InterFontFamily = FontFamily.Default // Replace with actual font if available

val AppTypography = androidx.compose.material3.Typography(
    // H1 (Screen Titles)
    headlineLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = LightTextPrimary
    ),
    // H2 (Section Headers)
    headlineMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = LightTextPrimary
    ),
    // H3 (Card Titles, Item Names)
    headlineSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = LightTextPrimary
    ),
    // Body Text
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = LightTextPrimary
    ),
    // Button Text
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = Color.White
    ),
    // Captions/Labels
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = LightTextSecondary
    )
)


@Composable
fun Quest1POSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

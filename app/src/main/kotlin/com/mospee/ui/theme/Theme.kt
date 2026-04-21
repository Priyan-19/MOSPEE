package com.mospee.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val MospeeBlue = Color(0xFF1A73E8)
val MospeeBlueBright = Color(0xFF4D9BFF)
val MospeeGreen = Color(0xFF00C853)
val MospeeRed = Color(0xFFE53935)
val MospeeAmber = Color(0xFFFFC107)
val MospeeBackground = Color(0xFF0F1117)
val MospeeSurface = Color(0xFF1E1E2A)
val MospeeSurfaceAlt = Color(0xFF262A36)
val MospeeOutline = Color(0xFF454B5C)
val MospeeTextPrimary = Color(0xFFEAEFF7)
val MospeeTextSecondary = Color(0xFF9EA7B8)
val MospeeWhite = Color(0xFFF8FAFF)
val MospeeCream = Color(0xFFFFF9F2)
val MospeeTerracotta = Color(0xFFB0644D)
val MospeeTerracottaLight = Color(0xFFFDEEE9)
val MospeeBadgeOrange = Color(0xFFFEF0E0)

val MospeeNeonGreen = MospeeGreen
val MospeeNeonBlue = MospeeBlueBright
val MospeeSurfaceElev = MospeeSurfaceAlt
val SpeedGreen = MospeeGreen
val SpeedAmber = MospeeAmber
val SpeedRed = MospeeRed

private val DarkColors = darkColorScheme(
    primary = MospeeBlue,
    onPrimary = MospeeWhite,
    primaryContainer = MospeeBlue.copy(alpha = 0.24f),
    onPrimaryContainer = MospeeWhite,
    secondary = MospeeGreen,
    onSecondary = Color.Black,
    secondaryContainer = MospeeGreen.copy(alpha = 0.22f),
    onSecondaryContainer = MospeeWhite,
    tertiary = MospeeAmber,
    onTertiary = Color.Black,
    background = MospeeBackground,
    onBackground = MospeeTextPrimary,
    surface = MospeeSurface,
    onSurface = MospeeTextPrimary,
    surfaceVariant = MospeeSurfaceAlt,
    onSurfaceVariant = MospeeTextSecondary,
    outline = MospeeOutline,
    error = MospeeRed,
    onError = MospeeWhite
)

private val LightColors = lightColorScheme(
    primary = MospeeBlue,
    onPrimary = MospeeWhite,
    secondary = MospeeGreen,
    onSecondary = Color.Black,
    tertiary = MospeeAmber,
    onTertiary = Color.Black
)

private val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 88.sp,
        lineHeight = 92.sp,
        letterSpacing = (-2).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 72.sp,
        lineHeight = 76.sp,
        letterSpacing = (-1).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.1.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.3.sp
    )
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

val MapBackdropGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xAA0C101A),
        Color(0x990F1117),
        MospeeBackground
    )
)

val SheetGlowGradient = Brush.verticalGradient(
    colors = listOf(
        MospeeBlue.copy(alpha = 0.22f),
        MospeeSurface.copy(alpha = 0.95f),
        MospeeSurface
    )
)

@Composable
fun MOSPEETheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

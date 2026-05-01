package com.mospee.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LocalDarkMode = staticCompositionLocalOf { true }

// ── MOSPEE Brand Colors (SpeedoTrack Edition) ───────────────────────────
// Dark Theme (primary)
val StBackground    = Color(0xFF0B0E14)   // Deep navy black
val StSurface       = Color(0xFF131823)   // Card surface
val StSurfaceAlt    = Color(0xFF1C2433)   // Elevated surface
val StOutline       = Color(0xFF2D3748).copy(alpha = 0.5f)   // Subtle border

val StPrimary       = Color(0xFF00E676)   // Vibrant neon green
val StSecondary     = Color(0xFF448AFF)   // Vibrant blue
val StAccent        = Color(0xFF00E5FF)   // Cyan accent
val StError         = Color(0xFFFF5252)   // Vibrant red
val StWarning       = Color(0xFFFFD740)   // Vibrant amber
val StPurple        = Color(0xFF7C4DFF)   // Deep purple

val StTextPrimary   = Color(0xFFF1F5F9)   // Off-white
val StTextSecondary = Color(0xFF94A3B8)   // Slate grey
val StTextMuted     = Color(0xFF64748B)   // Muted slate

// Light Theme equivalents (SpeedoTrack Light)
val LtBackground    = Color(0xFFF8FAFC)
val LtSurface       = Color(0xFFFFFFFF)
val LtSurfaceAlt    = Color(0xFFF1F5F9)
val LtOutline       = Color(0xFFE2E8F0)
val LtTextPrimary   = Color(0xFF0F172A)
val LtTextSecondary = Color(0xFF475569)
val LtTextMuted     = Color(0xFF94A3B8)

// Speedometer arc colors
val SpeedGreen      = Color(0xFF00E676)
val SpeedYellow     = Color(0xFFFFD740)
val SpeedOrange     = Color(0xFFFFAB40)
val SpeedRed        = Color(0xFFFF5252)

// Legacy aliases
val StCoral             = StError
val StCoralLight        = Color(0xFF2D1408)
val StGreen             = StPrimary
val StGreenLight        = Color(0xFF082D1B)
val StBlue              = StSecondary
val StRed               = StError
val StAmber             = StWarning
val MospeeBlue          = StSecondary
val MospeeGreen         = StPrimary
val MospeeRed           = StError
val MospeeAmber         = StWarning
val MospeeBackground    = StBackground
val MospeeSurface       = StSurface
val MospeeSurfaceAlt    = StSurfaceAlt
val MospeeOutline       = StOutline
val MospeeTextPrimary   = StTextPrimary
val MospeeTextSecondary = StTextSecondary
val MospeeWhite         = Color(0xFFFFFFFF)
val MospeeCream         = Color(0xFFF5F5F7)
val MospeeTerracotta    = StError
val MospeeTerracottaLight = StCoralLight
val MospeeBadgeOrange   = Color(0xFF2D1408)
val MospeeNeonGreen     = StPrimary
val MospeeNeonBlue      = StSecondary
val MospeeSurfaceElev   = StSurfaceAlt
val SpeedAmber          = StWarning

private val DarkColors = darkColorScheme(
    primary             = StPrimary,
    onPrimary           = Color.Black,
    primaryContainer    = StPrimary.copy(alpha = 0.15f),
    onPrimaryContainer  = StPrimary,
    secondary           = StSecondary,
    onSecondary         = Color.White,
    secondaryContainer  = StSecondary.copy(alpha = 0.15f),
    onSecondaryContainer = StSecondary,
    tertiary            = StAccent,
    onTertiary          = Color.Black,
    background          = StBackground,
    onBackground        = StTextPrimary,
    surface             = StSurface,
    onSurface           = StTextPrimary,
    surfaceVariant      = StSurfaceAlt,
    onSurfaceVariant    = StTextSecondary,
    outline             = StOutline,
    error               = StError,
    onError             = Color.White
)

private val LightColors = lightColorScheme(
    primary             = StPrimary,
    onPrimary           = Color.White,
    primaryContainer    = StPrimary.copy(alpha = 0.1f),
    onPrimaryContainer  = StPrimary,
    secondary           = StSecondary,
    onSecondary         = Color.White,
    secondaryContainer  = StSecondary.copy(alpha = 0.1f),
    onSecondaryContainer = StSecondary,
    tertiary            = StAccent,
    onTertiary          = Color.Black,
    background          = LtBackground,
    onBackground        = LtTextPrimary,
    surface             = LtSurface,
    onSurface           = LtTextPrimary,
    surfaceVariant      = LtSurfaceAlt,
    onSurfaceVariant    = LtTextSecondary,
    outline             = LtOutline,
    error               = StError,
    onError             = Color.White
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
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp,
        lineHeight = 60.sp,
        letterSpacing = (-1).sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 15.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.3.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.5.sp
    )
)

private val AppShapes = Shapes(
    extraSmall  = RoundedCornerShape(6.dp),
    small       = RoundedCornerShape(10.dp),
    medium      = RoundedCornerShape(14.dp),
    large       = RoundedCornerShape(18.dp),
    extraLarge  = RoundedCornerShape(24.dp)
)

val MapBackdropColor = StBackground
val SheetBackgroundColor = StSurface

@Composable
fun MOSPEETheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = if (darkTheme) DarkColors else LightColors
    CompositionLocalProvider(LocalDarkMode provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

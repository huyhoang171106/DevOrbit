package vn.edu.uit.devorbit.mobile.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Cosmic Design System Tokens for DevOrbit 2026.
 * Implements a high-performance, futuristic aesthetic.
 */

@Immutable
data class CosmicColors(
    val void: Color,          // True base background
    val nebula: Color,        // Primary surface
    val plasma: Color,        // Primary accent
    val supernova: Color,     // Risk/Danger accent
    val aurora: Color,        // Success/On-track accent
    val glass: Color,         // Translucent surface
    val glassBorder: Color,   // Translucent border
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color
)

@Immutable
data class CosmicTypography(
    val display: TextStyle,   // Hero headers
    val command: TextStyle,   // Technical/System labels
    val body: TextStyle,      // Content
    val metric: TextStyle,    // Large numeric values (GPA, Health)
    val label: TextStyle      // Small metadata
)

@Immutable
data class CosmicGradients(
    val primary: Brush,
    val secondary: Brush,
    val danger: Brush,
    val success: Brush,
    val voidDepth: Brush
)

@Immutable
data class CosmicSpacing(
    val atomic: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val cosmic: Dp = 48.dp
)

val LocalCosmicColors = staticCompositionLocalOf {
    CosmicColors(
        void = Color(0xFF05050A),
        nebula = Color(0xFF10101A),
        plasma = Color(0xFF7B61FF),
        supernova = Color(0xFFFF4D4D),
        aurora = Color(0xFF00F5A0),
        glass = Color(0x1AFFFFFF),
        glassBorder = Color(0x33FFFFFF),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xB3FFFFFF),
        textTertiary = Color(0x66FFFFFF)
    )
}

val LocalCosmicTypography = staticCompositionLocalOf {
    CosmicTypography(
        display = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            letterSpacing = (-0.5).sp
        ),
        command = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            letterSpacing = 1.sp
        ),
        body = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        metric = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 48.sp,
            letterSpacing = (-1).sp
        ),
        label = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 0.5.sp
        )
    )
}

val LocalCosmicGradients = staticCompositionLocalOf {
    CosmicGradients(
        primary = Brush.verticalGradient(listOf(Color(0xFF7B61FF), Color(0xFF917AFF))),
        secondary = Brush.horizontalGradient(listOf(Color(0xFF00D2FF), Color(0xFF3A7BD5))),
        danger = Brush.linearGradient(listOf(Color(0xFFFF4D4D), Color(0xFFF9D423))),
        success = Brush.linearGradient(listOf(Color(0xFF00F5A0), Color(0xFF00D9F5))),
        voidDepth = Brush.radialGradient(listOf(Color(0xFF10101A), Color(0xFF05050A)))
    )
}

val LocalCosmicSpacing = staticCompositionLocalOf { CosmicSpacing() }

object CosmicTheme {
    val colors: CosmicColors
        @Composable
        get() = LocalCosmicColors.current

    val typography: CosmicTypography
        @Composable
        get() = LocalCosmicTypography.current

    val gradients: CosmicGradients
        @Composable
        get() = LocalCosmicGradients.current

    val spacing: CosmicSpacing
        @Composable
        get() = LocalCosmicSpacing.current
}

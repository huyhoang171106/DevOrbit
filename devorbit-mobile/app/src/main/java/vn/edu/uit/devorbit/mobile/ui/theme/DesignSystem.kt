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
 * Grounded Academic Design System for DevOrbit.
 * Warm, structured, tactile. A campus planner, not a spaceship.
 */

@Immutable
data class CosmicColors(
    val void: Color,
    val nebula: Color,
    val plasma: Color,
    val supernova: Color,
    val aurora: Color,
    val glass: Color,
    val glassBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color
)

@Immutable
data class CosmicTypography(
    val display: TextStyle,
    val command: TextStyle,
    val body: TextStyle,
    val metric: TextStyle,
    val label: TextStyle,
    val titleMedium: TextStyle
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
        void = Slate900,
        nebula = Slate850,
        plasma = Amber,
        supernova = Crimson,
        aurora = Sage,
        glass = Color(0x0AFFFFFF),
        glassBorder = BorderSubtle,
        textPrimary = TextHigh,
        textSecondary = TextMedium,
        textTertiary = TextLow
    )
}

val LocalCosmicTypography = staticCompositionLocalOf {
    CosmicTypography(
        display = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            letterSpacing = (-0.3).sp,
            lineHeight = 32.sp
        ),
        command = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            letterSpacing = 0.8.sp,
            lineHeight = 18.sp
        ),
        body = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 24.sp
        ),
        metric = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            letterSpacing = (-0.5).sp,
            lineHeight = 40.sp
        ),
        label = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            letterSpacing = 0.3.sp,
            lineHeight = 18.sp
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
    )
}

val LocalCosmicGradients = staticCompositionLocalOf {
    CosmicGradients(
        primary = Brush.verticalGradient(listOf(Amber, AmberLight)),
        secondary = Brush.horizontalGradient(listOf(Ocean, Color(0xFF7BA8D4))),
        danger = Brush.linearGradient(listOf(Crimson, Color(0xFFD4884E))),
        success = Brush.linearGradient(listOf(Sage, Color(0xFF8FCFAC))),
        voidDepth = Brush.verticalGradient(listOf(Slate900, Slate850))
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

package vn.edu.uit.devorbit.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val GroundedColorScheme = darkColorScheme(
    primary = Amber,
    secondary = Ocean,
    tertiary = Sage,
    background = Slate900,
    surface = Slate850,
    onPrimary = Slate900,
    onSecondary = TextHigh,
    onTertiary = TextHigh,
    onBackground = TextHigh,
    onSurface = TextHigh,
    surfaceVariant = Slate800,
    outline = BorderSubtle,
    outlineVariant = BorderMedium,
)

@Composable
fun DevOrbitTheme(
    isBurnedOut: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (isBurnedOut) {
        // Warm, muted tones for burnout: reduce amber intensity, soften contrast
        LocalCosmicColors.current.copy(
            plasma = Color(0xFFB8906A),       // muted warm brown
            void = Color(0xFF14161C),          // slightly warmer base
            nebula = Color(0xFF1A1D24),        // slightly warmer surface
            textPrimary = Color(0xFFD0D4DA),   // reduced contrast
            supernova = Color(0xFFB87070)      // softer red
        )
    } else {
        LocalCosmicColors.current
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as android.app.Activity).window
        window.statusBarColor = Color.Transparent.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }

    CompositionLocalProvider(
        LocalCosmicColors provides colors,
        LocalCosmicTypography provides LocalCosmicTypography.current,
        LocalCosmicGradients provides LocalCosmicGradients.current,
        LocalCosmicSpacing provides LocalCosmicSpacing.current
    ) {
        MaterialTheme(
            colorScheme = GroundedColorScheme,
            content = content
        )
    }
}

@Composable
fun CosmicBackground(content: @Composable () -> Unit) {
    val gradient = CosmicTheme.gradients.voidDepth
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        content()
    }
}

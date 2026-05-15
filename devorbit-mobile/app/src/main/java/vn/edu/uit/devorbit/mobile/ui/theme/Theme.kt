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

private val CosmicColorScheme = darkColorScheme(
    primary = Color(0xFF7B61FF), // Plasma
    secondary = Color(0xFF00D2FF),
    tertiary = Color(0xFF00F5A0),
    background = Color(0xFF05050A), // Void
    surface = Color(0xFF10101A), // Nebula
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun DevOrbitTheme(
    isBurnedOut: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (isBurnedOut) {
        // Soften colors in burnout state to reduce cognitive load
        LocalCosmicColors.current.copy(
            plasma = Color(0xFF9182C4),
            void = Color(0xFF0A0A10)
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
            colorScheme = CosmicColorScheme,
            content = content
        )
    }
}
@Composable
fun CosmicBackground(content: @Composable () -> Unit) {
    val voidDepth = CosmicTheme.gradients.voidDepth
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(voidDepth)
    ) {
        content()
    }
}

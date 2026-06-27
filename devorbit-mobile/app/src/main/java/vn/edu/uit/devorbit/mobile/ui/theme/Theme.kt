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
    content: @Composable () -> Unit
) {
    val colors = LocalCosmicColors.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        val activity = view.context as? android.app.Activity
        activity?.window?.let { window ->
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
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

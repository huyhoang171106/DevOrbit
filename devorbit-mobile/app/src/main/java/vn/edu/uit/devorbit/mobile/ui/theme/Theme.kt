package vn.edu.uit.devorbit.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val GroundedLightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    primaryContainer = PrimaryBlueContainer,
    secondary = SecondarySky,
    tertiary = AccentCyan,
    background = PureWhite,
    surface = WarmWhite,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onBackground = TextCharcoalPrimary,
    onSurface = TextCharcoalPrimary,
    surfaceVariant = SlateWhite,
    outline = SubtleGrayBlueBorder,
    error = DiagnosticRed,
    errorContainer = DiagnosticRedContainer
)

@Composable
fun DevOrbitTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val activity = view.context as? android.app.Activity
        activity?.window?.let { window ->
            // Transparent status bar for edge-to-edge layouts
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            
            val insetsController = WindowCompat.getInsetsController(window, view)
            // Enable dark icons on status and navigation bars for light mode
            insetsController.isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightNavigationBars = true
        }
    }

    CompositionLocalProvider(
        LocalCosmicColors provides CosmicColors(
            void = PureWhite,
            nebula = WarmWhite,
            plasma = PrimaryBlue,
            supernova = DiagnosticRed,
            aurora = FreshGreen,
            glass = Color(0x99FFFFFF), // Refined white transparency for floating panels
            glassBorder = SubtleGrayBlueBorder,
            textPrimary = TextCharcoalPrimary,
            textSecondary = TextSlateSecondary,
            textTertiary = TextCoolGrayTertiary
        ),
        LocalCosmicTypography provides LocalCosmicTypography.current,
        LocalCosmicSpacing provides LocalCosmicSpacing.current
    ) {
        MaterialTheme(
            colorScheme = GroundedLightColorScheme,
            content = content
        )
    }
}

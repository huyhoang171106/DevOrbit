package vn.edu.uit.devorbit.admin.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val OrbitColorScheme = darkColorScheme(
    primary = OrbitColors.PrimaryElectricBlue,
    onPrimary = OrbitColors.TextPrimary,
    primaryContainer = OrbitColors.SurfaceInteractive,
    onPrimaryContainer = OrbitColors.PrimaryBright,
    secondary = OrbitColors.PrimaryBright,
    onSecondary = OrbitColors.BackgroundDeep,
    tertiary = OrbitColors.CyanSignal,
    onTertiary = OrbitColors.BackgroundDeep,
    background = OrbitColors.BackgroundDeep,
    onBackground = OrbitColors.TextPrimary,
    surface = OrbitColors.SurfaceBase,
    onSurface = OrbitColors.TextPrimary,
    surfaceVariant = OrbitColors.SurfaceRaised,
    onSurfaceVariant = OrbitColors.TextSecondary,
    outline = OrbitColors.BorderSubtle,
    error = OrbitColors.Error,
    onError = OrbitColors.TextPrimary,
    outlineVariant = OrbitColors.BorderSubtle,
    inverseSurface = OrbitColors.TextPrimary,
    inverseOnSurface = OrbitColors.BackgroundDeep,
)

@Composable
fun OrbitTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = OrbitColorScheme,
        typography = OrbitTypography,
        shapes = OrbitShapes.Material,
        content = content,
    )
}

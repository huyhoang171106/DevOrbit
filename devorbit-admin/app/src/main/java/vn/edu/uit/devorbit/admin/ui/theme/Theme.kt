package vn.edu.uit.devorbit.admin.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = UITBlue,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = UITBlueSoft,
    onPrimaryContainer = TextPrimary,
    secondary = TextSecondary,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    background = AppBackground,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSecondary,
    onSurfaceVariant = TextSecondary,
    error = Danger,
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = DangerSoft,
    onErrorContainer = Danger,
    outline = Border,
    outlineVariant = Divider,
)

@Composable
fun DevOrbitAdminTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AdminTypography,
        content = content
    )
}

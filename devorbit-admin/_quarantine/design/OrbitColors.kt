package vn.edu.uit.devorbit.admin.design

import androidx.compose.ui.graphics.Color

// ── Core Palette ──────────────────────────────────────────────────────────────
object OrbitColors {
    // Backgrounds
    val BackgroundDeep = Color(0xFF030711)
    val BackgroundNavy = Color(0xFF06101F)
    val SurfaceBase = Color(0xFF0A1628)
    val SurfaceRaised = Color(0xFF0E1D33)
    val SurfaceInteractive = Color(0xFF112744)

    // Primary / Signal
    val PrimaryElectricBlue = Color(0xFF2787FF)
    val PrimaryBright = Color(0xFF52A8FF)
    val CyanSignal = Color(0xFF73D7FF)

    // Text
    val TextPrimary = Color(0xFFF4F8FF)
    val TextSecondary = Color(0xFF9AACBF)
    val TextMuted = Color(0xFF607188)

    // Borders
    val BorderSubtle = Color(0xFF19304C)

    // Semantic
    val Success = Color(0xFF49D6A0)
    val Warning = Color(0xFFFFCA68)
    val Error = Color(0xFFFF667A)

    // Chart / Visualization
    val ChartBlue = Color(0xFF2787FF)
    val ChartCyan = Color(0xFF73D7FF)
    val ChartGreen = Color(0xFF49D6A0)
    val ChartYellow = Color(0xFFFFCA68)
    val ChartOrange = Color(0xFFFF8A50)
    val ChartRed = Color(0xFFFF667A)
    val ChartPurple = Color(0xFFA78BFA)
    val ChartMuted = Color(0xFF19304C)

    // Node states
    val NodeCompleted = PrimaryElectricBlue
    val NodeActive = PrimaryBright
    val NodeLocked = Color(0xFF19304C)
    val NodeCurrent = CyanSignal
}

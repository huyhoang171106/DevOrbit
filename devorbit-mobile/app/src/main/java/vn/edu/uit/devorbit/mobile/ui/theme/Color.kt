package vn.edu.uit.devorbit.mobile.ui.theme

import androidx.compose.ui.graphics.Color

// UIT 2026 Light Edition Color System Tokens
val PrimaryBlue = Color(0xFF0056B3)
val PrimaryBlueContainer = Color(0xFFE6F0FA)
val SecondarySky = Color(0xFF4FA3E3)
val AccentCyan = Color(0xFF00B4D8)

// Canvas Mappings
val PureWhite = Color(0xFFFFFFFF)
val WarmWhite = Color(0xFFFAFAFA)
val SlateWhite = Color(0xFFF0F4F8)

// Borders & Outlines
val SubtleGrayBlueBorder = Color(0xFFE1E8F0)

// High-contrast Neutral Text Mappings
val TextCharcoalPrimary = Color(0xFF1A1D20)
val TextSlateSecondary = Color(0xFF4A5560)
val TextCoolGrayTertiary = Color(0xFF82909E)

// Semantic Diagnostics
val FreshGreen = Color(0xFF2D6A4F)
val FreshGreenContainer = Color(0xFFE8F5E9)
val AlertAmber = Color(0xFFD97706)
val AlertAmberContainer = Color(0xFFFEF3C7)
val DiagnosticRed = Color(0xFFDC2626)
val DiagnosticRedContainer = Color(0xFFFEE2E2)

// Compatibility Mappings to preserve compile-safety during transition
val Slate900 = PureWhite
val Slate850 = WarmWhite
val Slate800 = SlateWhite
val Slate700 = SlateWhite
val Slate600 = TextCoolGrayTertiary
val Slate500 = TextCoolGrayTertiary
val Slate400 = TextCoolGrayTertiary
val Slate300 = TextCoolGrayTertiary

val Amber = PrimaryBlue
val AmberLight = SecondarySky
val AmberFaint = PrimaryBlueContainer

val Sage = FreshGreen
val SageLight = FreshGreenContainer
val SageFaint = FreshGreenContainer

val Crimson = DiagnosticRed
val CrimsonFaint = DiagnosticRedContainer

val Ochre = AlertAmber
val OchreFaint = AlertAmberContainer

val Ocean = SecondarySky
val OceanFaint = PrimaryBlueContainer

val TextHigh = TextCharcoalPrimary
val TextMedium = TextSlateSecondary
val TextLow = TextCoolGrayTertiary

val BorderSubtle = SubtleGrayBlueBorder
val BorderMedium = SubtleGrayBlueBorder

val Divider = SubtleGrayBlueBorder

val CosmicDeepSpace = PureWhite
val CosmicNebulaPurple = PrimaryBlue
val CosmicStarBlue = SecondarySky
val CosmicGlowPurple = PrimaryBlueContainer
val CosmicGlowBlue = FreshGreen
val GlassWhite = Color(0x99FFFFFF)
val GlassBlack = Color(0x0A000000)
val GlassBorder = SubtleGrayBlueBorder
val TextPrimary = TextCharcoalPrimary
val TextSecondary = TextSlateSecondary

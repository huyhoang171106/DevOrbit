package vn.edu.uit.devorbit.admin.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Accent color palette used by ObsidianComponents and screens.
 * Colors match the UIT Command design system — white + UIT-blue base.
 * Soft/background variants use the semantic Soft tokens from Color.kt.
 */
object ObsidianPalette {
    // Primary blues
    val Blue500 = UITBlue           // #2F6BFF
    val Blue700 = UITBlueStrong     // #1D5BEF

    // Green (success)
    val Green50 = SuccessSoft       // #EAF8F0
    val Green500 = Success          // #22B36B
    val Green600 = Color(0xFF1A8F55) // darker green for text
    val Green700 = Color(0xFF15734A) // darkest green for text

    // Red (danger)
    val Red50 = DangerSoft          // #FFF0F1
    val Red500 = Danger             // #E5484D
    val Red700 = Color(0xFFC53030)  // darker red for text

    // Amber (warning)
    val Amber500 = Warning          // #F2A93B

    // Neutral gray
    val Gray300 = Color(0xFFB8C4D0) // light gray for unfilled states
    val Gray500 = TextMuted         // #8A98AD
}

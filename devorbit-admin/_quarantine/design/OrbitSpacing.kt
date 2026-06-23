package vn.edu.uit.devorbit.admin.design

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 4dp base grid spacing system.
 *
 *  -  4dp: micro spacing (icon-to-edge, inline badges)
 *  -  8dp: icon-to-label, compact internal
 *  - 12dp: compact internal spacing
 *  - 16dp: standard component padding
 *  - 20dp: screen horizontal padding
 *  - 24dp: section separation
 *  - 32dp: major content separation
 *  - 48dp: hero / large section separation
 */
data class OrbitSpacingValues(
    val micro: Dp = 4.dp,
    val compact: Dp = 8.dp,
    val compactInternal: Dp = 12.dp,
    val standard: Dp = 16.dp,
    val screenHorizontal: Dp = 20.dp,
    val section: Dp = 24.dp,
    val major: Dp = 32.dp,
    val hero: Dp = 48.dp,
    val contentGap: Dp = 12.dp,
    val listItemGap: Dp = 8.dp,
    val dockIconMargin: Dp = 6.dp,
)

val OrbitSpacing = OrbitSpacingValues()

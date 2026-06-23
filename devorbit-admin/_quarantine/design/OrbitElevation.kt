package vn.edu.uit.devorbit.admin.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Custom elevation values for Orbital Intelligence surfaces.
 * Uses colored shadows (dark blue tones) instead of grey.
 */
object OrbitElevation {
    val level0 = 0.dp
    val level1 = 1.dp
    val level2 = 4.dp
    val level3 = 8.dp
    val level4 = 16.dp

    /** Dock elevation — the highest floating element */
    val dock = 24.dp

    /** Modal / sheet elevation */
    val modal = 32.dp
}

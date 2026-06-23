package vn.edu.uit.devorbit.admin.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Orbital Intelligence shape system.
 *
 * Families:
 *  - Primary action:        14dp
 *  - Content panel:         18dp
 *  - Compact control:       10dp
 *  - Navigation dock:       22dp
 *  - Status chip:           pill (50% round)
 *  - Large canvas:          24dp or edge-aligned
 *  - Technical data rows:    6-10dp
 */
object OrbitShapes {
    // Material mapping (used by Material 3 components)
    val Material = Shapes(
        extraSmall = RoundedCornerShape(6.dp),
        small = RoundedCornerShape(10.dp),
        medium = RoundedCornerShape(14.dp),
        large = RoundedCornerShape(18.dp),
        extraLarge = RoundedCornerShape(24.dp),
    )

    // Semantic shapes for custom components
    val primaryAction = RoundedCornerShape(14.dp)
    val contentPanel = RoundedCornerShape(18.dp)
    val compactControl = RoundedCornerShape(10.dp)
    val navigationDock = RoundedCornerShape(22.dp)
    val statusChip = RoundedCornerShape(percent = 50)
    val largeCanvas = RoundedCornerShape(24.dp)
    val technicalRow = RoundedCornerShape(8.dp)
    val dataCard = RoundedCornerShape(6.dp)
}

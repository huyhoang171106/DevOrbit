package vn.edu.uit.devorbit.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.design.OrbitColors

/**
 * Thin horizontal divider with optional accent start mark.
 */
@Composable
fun TechnicalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: androidx.compose.ui.graphics.Color = OrbitColors.BorderSubtle,
    accentColor: androidx.compose.ui.graphics.Color = OrbitColors.PrimaryElectricBlue,
    showAccent: Boolean = true,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (showAccent) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(thickness)
                    .background(accentColor),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(thickness)
                .background(color),
        )
    }
}

/**
 * Vertical divider for use in technical data rows.
 */
@Composable
fun TechnicalVerticalDivider(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    thickness: Dp = 1.dp,
    color: androidx.compose.ui.graphics.Color = OrbitColors.BorderSubtle,
) {
    Box(
        modifier = modifier
            .width(thickness)
            .height(height)
            .background(color),
    )
}

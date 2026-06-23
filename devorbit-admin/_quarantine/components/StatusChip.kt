package vn.edu.uit.devorbit.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.admin.design.OrbitColors

@Composable
fun StatusChip(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = OrbitColors.PrimaryElectricBlue,
    background: Color = color.copy(alpha = 0.12f),
    textColor: Color = color,
    shape: Shape = RoundedCornerShape(percent = 50),
) {
    Box(
        modifier = modifier
            .background(background, shape)
            .border(width = 0.5.dp, color = color.copy(alpha = 0.3f), shape = shape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            letterSpacing = 0.5.sp,
        )
    }
}

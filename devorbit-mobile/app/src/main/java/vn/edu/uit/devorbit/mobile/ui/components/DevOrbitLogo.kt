package vn.edu.uit.devorbit.mobile.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

private fun DrawScope.drawCube(tint: Color) {
    val s = size.minDimension
    val ox = (size.width - s) / 2f
    val oy = (size.height - s) / 2f

    fun p(x: Float, y: Float): Offset = Offset(ox + x * s, oy + y * s)

    val b = 256f
    val T  = p(135.68f / b, 18f / b)
    val UR = p(223.68f / b, 66.15f / b)
    val BR = p(223.68f / b, 161.79f / b)
    val B  = p(135.68f / b, 209.98f / b)
    val BL = p(47.68f / b, 161.81f / b)
    val UL = p(47.68f / b, 66.17f / b)
    val C  = p(128f / b, 120f / b)

    val lineW = (2.5f).dp.toPx()

    val p = { pts: List<Offset> ->
        Path().apply {
            moveTo(pts[0].x, pts[0].y)
            for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y)
            close()
        }
    }

    drawPath(p(listOf(T, UR, C, UL)), tint.copy(alpha = 0.85f))
    drawPath(p(listOf(C, UR, BR, B)), tint.copy(alpha = 0.55f))
    drawPath(p(listOf(C, UL, BL, B)), tint.copy(alpha = 0.35f))
    drawPath(p(listOf(T, UR, BR, B, BL, UL)), tint, style = Stroke(width = lineW))
    drawLine(tint, C, B, strokeWidth = lineW)
    drawLine(tint, C, UL, strokeWidth = lineW)
    drawLine(tint, C, UR, strokeWidth = lineW)
}

@Composable
fun DevOrbitMark(
    modifier: Modifier = Modifier,
    tint: Color = CosmicTheme.colors.plasma,
    size: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tint.copy(alpha = 0.1f))
            .border(1.dp, tint.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            drawCube(tint)
        }
    }
}

@Composable
fun DevOrbitLogo(
    withText: Boolean = true,
    modifier: Modifier = Modifier,
    tint: Color = CosmicTheme.colors.plasma
) {
    if (withText) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            DevOrbitMark(size = 32.dp)
            Spacer(Modifier.width(12.dp))
            Text(
                text = "DevOrbit",
                color = CosmicTheme.colors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        DevOrbitMark(modifier = modifier, tint = tint)
    }
}

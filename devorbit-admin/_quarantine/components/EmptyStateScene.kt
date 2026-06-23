package vn.edu.uit.devorbit.admin.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.design.OrbitColors

/**
 * Empty state scene with a subtle drawn visual and contextual message.
 */
@Composable
fun EmptyStateScene(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        EmptyStateCanvas()

        Spacer(Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = OrbitColors.TextPrimary,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = OrbitColors.TextSecondary,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(16.dp))

        action()
    }
}

@Composable
private fun EmptyStateCanvas() {
    Canvas(modifier = Modifier.size(80.dp)) {
        val c = Offset(size.width / 2, size.height / 2)
        val r = minOf(size.width, size.height) * 0.35f

        drawCircle(
            color = OrbitColors.BorderSubtle.copy(alpha = 0.3f),
            radius = r,
            center = c,
            style = Stroke(width = 1.5f),
        )
        drawCircle(
            color = OrbitColors.BorderSubtle.copy(alpha = 0.15f),
            radius = r * 0.6f,
            center = c,
            style = Stroke(width = 1f),
        )
        drawCircle(
            color = OrbitColors.TextMuted.copy(alpha = 0.3f),
            radius = 4f,
            center = c,
        )
    }
}

/**
 * Loading state — structural skeleton preserving final layout.
 */
@Composable
fun LoadingStructure(
    modifier: Modifier = Modifier,
    lines: Int = 4,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(24.dp)
                .background(OrbitColors.SurfaceRaised, RoundedCornerShape(4.dp)),
        )
        repeat(lines) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(OrbitColors.SurfaceBase, RoundedCornerShape(4.dp)),
            )
        }
    }
}

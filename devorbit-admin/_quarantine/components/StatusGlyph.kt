package vn.edu.uit.devorbit.admin.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.design.OrbitColors

/**
 * Minimal status indicator glyph drawn with Canvas.
 *
 * States:
 *  - active     → filled bright blue capsule
 *  - completed  → filled electric blue check
 *  - paused     → outlined with dot
 *  - locked     → faint outline
 *  - error      → filled red
 *  - warning    → filled yellow
 */
enum class GlyphState {
    Active, Completed, Paused, Locked, Error, Warning
}

@Composable
fun StatusGlyph(
    state: GlyphState,
    modifier: Modifier = Modifier,
    size: Dp = 8.dp,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (state == GlyphState.Active || state == GlyphState.Completed) 1f else 0f,
        animationSpec = tween(300),
        label = "glyph",
    )

    Canvas(modifier = modifier.size(size)) {
        val strokeW = size.toPx() * 0.18f
        val gap = strokeW * 2f
        val rect = Size(size.toPx() - gap * 2, size.toPx() - gap * 2)

        when (state) {
            GlyphState.Active -> {
                drawRoundRect(
                    color = OrbitColors.NodeActive,
                    topLeft = Offset(gap, gap),
                    size = rect,
                    cornerRadius = CornerRadius(rect.width / 2),
                    alpha = animatedProgress,
                )
            }

            GlyphState.Completed -> {
                drawRoundRect(
                    color = OrbitColors.NodeCompleted,
                    topLeft = Offset(gap, gap),
                    size = rect,
                    cornerRadius = CornerRadius(rect.width / 2),
                )
                // Check mark
                val cx = size.toPx() / 2
                val cy = size.toPx() / 2
                val check = size.toPx() * 0.22f
                drawLine(
                    color = OrbitColors.BackgroundDeep,
                    start = Offset(cx - check, cy),
                    end = Offset(cx - check * 0.3f, cy + check * 0.6f),
                    strokeWidth = strokeW * 1.2f,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                )
                drawLine(
                    color = OrbitColors.BackgroundDeep,
                    start = Offset(cx - check * 0.3f, cy + check * 0.6f),
                    end = Offset(cx + check, cy - check * 0.4f),
                    strokeWidth = strokeW * 1.2f,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                )
            }

            GlyphState.Paused -> {
                drawRoundRect(
                    color = OrbitColors.TextMuted,
                    topLeft = Offset(gap, gap),
                    size = rect,
                    cornerRadius = CornerRadius(rect.width / 2),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(strokeW),
                )
                val dot = size.toPx() * 0.12f
                drawCircle(color = OrbitColors.TextMuted, radius = dot)
            }

            GlyphState.Locked -> {
                drawRoundRect(
                    color = OrbitColors.NodeLocked,
                    topLeft = Offset(gap, gap),
                    size = rect,
                    cornerRadius = CornerRadius(rect.width / 2),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(strokeW * 0.5f),
                )
            }

            GlyphState.Error -> {
                drawRoundRect(
                    color = OrbitColors.Error,
                    topLeft = Offset(gap, gap),
                    size = rect,
                    cornerRadius = CornerRadius(rect.width / 2),
                )
            }

            GlyphState.Warning -> {
                drawRoundRect(
                    color = OrbitColors.Warning,
                    topLeft = Offset(gap, gap),
                    size = rect,
                    cornerRadius = CornerRadius(rect.width / 2),
                )
            }
        }
    }
}

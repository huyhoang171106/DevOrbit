package vn.edu.uit.devorbit.admin.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import vn.edu.uit.devorbit.admin.design.OrbitColors

data class OrbitalNode(
    val id: String,
    val label: String,
    val progress: Float,
    val color: Color,
    val isCurrent: Boolean = false,
    val isCompleted: Boolean = false,
    val isLocked: Boolean = false,
)

@Composable
fun OrbitalLearningMap(
    nodes: List<OrbitalNode>,
    selectedNodeId: String?,
    onNodeSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 280.dp,
) {
    var rotationState by remember { mutableStateOf(0f) }
    val rotation = rotationState
    val haptic = LocalHapticFeedback.current

    val animatedProgresses = remember(nodes.size) {
        nodes.map { node ->
            Animatable(node.progress)
        }
    }

    nodes.forEachIndexed { i, node ->
        LaunchedEffect(node.progress) {
            animatedProgresses[i].animateTo(
                targetValue = node.progress,
                animationSpec = tween(600),
            )
        }
    }

    Canvas(
        modifier = modifier
            .size(size)
            .pointerInput(nodes) {
                detectTapGestures { offset ->
                    val cx = size.toPx() / 2f
                    val cy = size.toPx() / 2f
                    val baseRadius = minOf(cx, cy) * 0.45f
                    val nodeCount = nodes.size.coerceAtLeast(1)
                    val angleStep = 2.0 * PI / nodeCount.toDouble()

                    nodes.forEachIndexed { i, node ->
                        val rad = rotation.toDouble() + i.toDouble() * angleStep
                        val nx = cx + baseRadius * cos(rad).toFloat()
                        val ny = cy + baseRadius * sin(rad).toFloat()
                        val dist = (offset - Offset(nx, ny)).getDistance()
                        if (dist < 22f) {
                            if (selectedNodeId == node.id) {
                                onNodeSelected(null)
                            } else {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNodeSelected(node.id)
                            }
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    rotationState += dragAmount.x * 0.008f
                }
            },
    ) {
        val cx = size.toPx() / 2f
        val cy = size.toPx() / 2f
        val baseRadius = minOf(cx, cy) * 0.45f
        val nodeCount = nodes.size.coerceAtLeast(1)
        val angleStep = 2.0 * PI / nodeCount.toDouble()

        drawOrbitalRings(cx, cy, baseRadius)

        nodes.forEachIndexed { i, node ->
            val rad = rotation.toDouble() + i.toDouble() * angleStep
            val nx = cx + baseRadius * cos(rad).toFloat()
            val ny = cy + baseRadius * sin(rad).toFloat()

            drawLine(
                color = node.color.copy(alpha = 0.06f),
                start = Offset(cx, cy),
                end = Offset(nx, ny),
                strokeWidth = 0.5f,
            )
        }

        nodes.forEachIndexed { i, node ->
            val rad = rotation.toDouble() + i.toDouble() * angleStep
            val nx = cx + baseRadius * cos(rad).toFloat()
            val ny = cy + baseRadius * sin(rad).toFloat()
            val isSelected = node.id == selectedNodeId
            val nodeRadius = if (isSelected) 18f else if (node.isCurrent) 14f else 10f

            if (node.isCurrent || isSelected) {
                drawCircle(
                    color = node.color.copy(alpha = 0.15f),
                    radius = nodeRadius + 10f,
                    center = Offset(nx, ny),
                )
            }

            drawCircle(
                color = when {
                    node.isLocked -> OrbitColors.NodeLocked
                    node.isCompleted -> OrbitColors.NodeCompleted
                    node.isCurrent -> OrbitColors.NodeCurrent
                    else -> node.color
                },
                radius = nodeRadius,
                center = Offset(nx, ny),
            )

            if (!node.isLocked && !node.isCompleted) {
                val arcRadius = nodeRadius + 3f
                val sweep = animatedProgresses[i].value * 360f
                drawArc(
                    color = node.color,
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(nx - arcRadius, ny - arcRadius),
                    size = Size(arcRadius * 2f, arcRadius * 2f),
                    style = Stroke(width = 2f, cap = StrokeCap.Round),
                )
            }

            if (isSelected || node.isCurrent) {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#F4F8FF")
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
                drawContext.canvas.nativeCanvas.drawText(
                    node.label,
                    nx,
                    ny + nodeRadius + 8f + 14f,
                    paint,
                )
            }
        }
    }
}

private fun DrawScope.drawOrbitalRings(cx: Float, cy: Float, baseRadius: Float) {
    for (i in 0 until 3) {
        val radius = baseRadius * (0.4f + i * 0.3f)
        drawCircle(
            color = OrbitColors.BorderSubtle.copy(alpha = 0.3f),
            radius = radius,
            center = Offset(cx, cy),
            style = Stroke(width = 0.5f),
        )
    }
}

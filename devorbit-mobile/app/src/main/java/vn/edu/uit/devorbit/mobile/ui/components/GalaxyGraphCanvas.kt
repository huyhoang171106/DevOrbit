package vn.edu.uit.devorbit.mobile.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.domain.model.GraphLink
import vn.edu.uit.devorbit.mobile.domain.model.GraphNode
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun GalaxyGraphCanvas(
    nodes: List<GraphNode>,
    links: List<GraphLink>,
    selectedNode: GraphNode?,
    onNodeClick: (GraphNode) -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableFloatStateOf(0.8f) }

    val nodePositions = remember(nodes) {
        nodes.associate { node ->
            val angle = Random.nextDouble() * 2.0 * Math.PI
            val distance = (node.level * 180.0) + Random.nextDouble(40.0, 120.0)
            node.id to Offset(
                (cos(angle) * distance).toFloat(),
                (sin(angle) * distance).toFloat()
            )
        }
    }

    // Subtle breathing animation for nodes
    val infiniteTransition = rememberInfiniteTransition(label = "NodeBreath")
    val breath by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Breath"
    )

    val amberColor = CosmicTheme.colors.plasma
    val dangerColor = CosmicTheme.colors.supernova
    val sageColor = CosmicTheme.colors.aurora

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    offset += pan
                    scale = (scale * zoom).coerceIn(0.2f, 3f)
                }
            }
            .pointerInput(nodes) {
                detectTapGestures { tapOffset ->
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val worldTap = (tapOffset - offset - Offset(centerX.toFloat(), centerY.toFloat())) / scale
                    nodePositions.forEach { (id, pos) ->
                        if ((worldTap - pos).getDistance() < 50f) {
                            nodes.find { it.id == id }?.let { onNodeClick(it) }
                        }
                    }
                }
            }
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        drawIntoCanvas { canvas ->
            canvas.save()
            canvas.translate(centerX + offset.x, centerY + offset.y)
            canvas.scale(scale, scale)

            // Connections: thin solid lines
            links.forEach { link ->
                val start = nodePositions[link.sourceId]
                val end = nodePositions[link.targetId]
                if (start != null && end != null) {
                    drawLine(
                        color = amberColor.copy(alpha = 0.1f),
                        start = start,
                        end = end,
                        strokeWidth = 1.dp.toPx() / scale
                    )
                }
            }

            // Nodes
            nodes.forEach { node ->
                val pos = nodePositions[node.id] ?: Offset.Zero
                val isSelected = node.id == selectedNode?.id

                val nodeColor = when {
                    isSelected -> dangerColor
                    node.impactScore >= 7.0 -> amberColor
                    node.impactScore >= 4.0 -> sageColor
                    else -> amberColor.copy(alpha = 0.6f)
                }

                // Outer glow (subtle, only for selected or high-impact)
                if (isSelected || node.impactScore >= 7.0) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                nodeColor.copy(alpha = 0.15f * breath),
                                androidx.compose.ui.graphics.Color.Transparent
                            ),
                            center = pos,
                            radius = (if (isSelected) 36.dp else 20.dp).toPx()
                        ),
                        radius = (if (isSelected) 36.dp else 20.dp).toPx(),
                        center = pos
                    )
                }

                // Node core
                drawCircle(
                    color = if (isSelected) androidx.compose.ui.graphics.Color.White else nodeColor,
                    radius = (if (isSelected) 6.dp else 3.5.dp).toPx(),
                    center = pos
                )
            }

            canvas.restore()
        }
    }
}

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
import vn.edu.uit.devorbit.mobile.model.domain.GraphLink
import vn.edu.uit.devorbit.mobile.model.domain.GraphNode
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
    // Spatial positioning state
    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableFloatStateOf(0.8f) }
    
    // Physics-based node positions (randomized for now, normally would use force-directed layout)
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

    val infiniteTransition = rememberInfiniteTransition(label = "StarTwinkle")
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Twinkle"
    )

    val plasmaColor = CosmicTheme.colors.plasma
    val supernovaColor = CosmicTheme.colors.supernova
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

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
                    // Hit testing in world space
                    val worldTap = (tapOffset - offset - androidx.compose.ui.geometry.Offset(centerX.toFloat(), centerY.toFloat())) / scale
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

            // Draw Connections (Constellations)
            links.forEach { link ->
                val start = nodePositions[link.sourceId]
                val end = nodePositions[link.targetId]
                if (start != null && end != null) {
                    drawLine(
                        color = plasmaColor.copy(alpha = 0.15f),
                        start = start,
                        end = end,
                        strokeWidth = 1.dp.toPx() / scale,
                        pathEffect = dashEffect
                    )
                }
            }

            // Draw Nodes (Stars)
            nodes.forEach { node ->
                val pos = nodePositions[node.id] ?: Offset.Zero
                val isSelected = node.id == selectedNode?.id
                val accentColor = if (isSelected) supernovaColor else plasmaColor
                
                // Outer Glow - Nebula atmosphere
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.25f * twinkle),
                            Color.Transparent
                        ),
                        center = pos,
                        radius = (if (isSelected) 40.dp else 20.dp).toPx()
                    ),
                    radius = (if (isSelected) 40.dp else 20.dp).toPx(),
                    center = pos
                )

                // Star Core
                drawCircle(
                    color = if (isSelected) supernovaColor else Color.White,
                    radius = (if (isSelected) 6.dp else 3.dp).toPx(),
                    center = pos
                )
            }
            
            canvas.restore()
        }
    }
}

package vn.edu.uit.devorbit.admin.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.admin.design.OrbitColors

data class PathNode(
    val id: String,
    val label: String,
    val state: PathNodeState,
    val description: String = "",
)

enum class PathNodeState { Completed, Current, Locked, Available }

@Composable
fun LearningPath(
    nodes: List<PathNode>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        nodes.forEachIndexed { index, node ->
            LearningPathNode(
                node = node,
                isLast = index == nodes.size - 1,
                isFirst = index == 0,
            )
        }
    }
}

@Composable
private fun LearningPathNode(
    node: PathNode,
    isLast: Boolean,
    isFirst: Boolean,
) {
    val nodeColor = when (node.state) {
        PathNodeState.Completed -> OrbitColors.NodeCompleted
        PathNodeState.Current -> OrbitColors.NodeCurrent
        PathNodeState.Available -> OrbitColors.PrimaryBright
        PathNodeState.Locked -> OrbitColors.NodeLocked
    }
    val bgColor = when (node.state) {
        PathNodeState.Locked -> OrbitColors.BackgroundDeep.copy(alpha = 0.5f)
        else -> OrbitColors.SurfaceBase
    }
    val textColor = when (node.state) {
        PathNodeState.Locked -> OrbitColors.TextMuted
        else -> OrbitColors.TextPrimary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(if (isLast) 28.dp else 60.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            Canvas(modifier = Modifier.size(32.dp, 60.dp)) {
                val centerX = size.width / 2
                val dotR = 5f

                if (!isFirst) {
                    drawLine(
                        color = OrbitColors.BorderSubtle.copy(alpha = 0.5f),
                        start = Offset(centerX, 0f),
                        end = Offset(centerX, dotR),
                        strokeWidth = 1.5f,
                    )
                }

                if (!isLast) {
                    drawLine(
                        color = if (node.state == PathNodeState.Completed || node.state == PathNodeState.Current)
                            nodeColor.copy(alpha = 0.4f) else OrbitColors.BorderSubtle.copy(alpha = 0.3f),
                        start = Offset(centerX, dotR * 2),
                        end = Offset(centerX, size.height),
                        strokeWidth = 1.5f,
                    )
                }
            }

            // Node dot
            Canvas(modifier = Modifier.size(16.dp)) {
                val c = Offset(size.width / 2, size.height / 2)
                val bgRadius = 8f
                val fgRadius = if (node.state == PathNodeState.Current) 5f else 3.5f

                if (node.state == PathNodeState.Current) {
                    drawCircle(
                        color = nodeColor.copy(alpha = 0.2f),
                        radius = bgRadius,
                        center = c,
                    )
                }

                drawCircle(
                    color = nodeColor,
                    radius = fgRadius,
                    center = c,
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(bgColor)
                .padding(12.dp),
        ) {
            Column {
                Text(
                    text = node.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = textColor,
                )
                if (node.description.isNotEmpty()) {
                    Text(
                        text = node.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = OrbitColors.TextSecondary,
                    )
                }
                if (node.state == PathNodeState.Current) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "IN PROGRESS",
                        style = MaterialTheme.typography.labelSmall,
                        color = OrbitColors.CyanSignal,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp,
                    )
                }
            }
        }
    }
}

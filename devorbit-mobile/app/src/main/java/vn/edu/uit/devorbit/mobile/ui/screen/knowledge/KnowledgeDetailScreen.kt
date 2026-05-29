package vn.edu.uit.devorbit.mobile.ui.screen.knowledge

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.domain.model.GraphNode
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@Composable
fun KnowledgeDetailScreen(
    node: GraphNode,
    learningPath: List<GraphNode>,
    onSimulate: () -> Unit,
    onBack: () -> Unit,
    onViewResources: (String) -> Unit = {},
    onToggleComplete: (Long) -> Unit = {},
    isCompleted: Boolean = false,
    simulationMode: Boolean = false,
    simulationFailedIds: Set<Long> = emptySet(),
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Back button
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = CosmicTheme.colors.textTertiary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Chi tiết kiến thức",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Node info card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CosmicTheme.colors.nebula,
            border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = node.name,
                    style = CosmicTheme.typography.display,
                    color = CosmicTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = node.code,
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.plasma
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Impact score
                val impactColor = when {
                    node.impactScore > 7.0 -> CosmicTheme.colors.supernova
                    node.impactScore > 4.0 -> CosmicTheme.colors.plasma
                    else -> CosmicTheme.colors.aurora
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Mức độ ảnh hưởng",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textTertiary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "%.1f / 10".format(node.impactScore),
                        style = CosmicTheme.typography.body.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = impactColor
                    )
                }

                // Impact bar
                Spacer(modifier = Modifier.height(8.dp))
                val fraction = (node.impactScore / 10.0).toFloat().coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                ) {
                    Surface(color = CosmicTheme.colors.glassBorder, modifier = Modifier.fillMaxSize()) {}
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction)
                            .clip(RoundedCornerShape(3.dp))
                    ) {
                        Surface(color = impactColor, modifier = Modifier.fillMaxSize()) {}
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Level indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Cấp độ",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textTertiary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CosmicTheme.colors.plasma.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${node.level}",
                            style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                            color = CosmicTheme.colors.plasma,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Prerequisite chain
        if (learningPath.isNotEmpty()) {
            Text(
                text = "Lộ trình tiên quyết",
                style = CosmicTheme.typography.command,
                color = CosmicTheme.colors.textTertiary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CosmicTheme.colors.nebula,
                border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    learningPath.forEachIndexed { index, pathNode ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (pathNode.id == node.id)
                                    CosmicTheme.colors.plasma.copy(alpha = 0.15f)
                                else
                                    CosmicTheme.colors.aurora.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                                    color = if (pathNode.id == node.id) CosmicTheme.colors.plasma else CosmicTheme.colors.aurora,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = pathNode.name,
                                    style = CosmicTheme.typography.body.copy(
                                        fontWeight = if (pathNode.id == node.id) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = CosmicTheme.colors.textPrimary
                                )
                                Text(
                                    text = pathNode.code,
                                    style = CosmicTheme.typography.label,
                                    color = CosmicTheme.colors.textTertiary
                                )
                            }
                        }
                        if (index < learningPath.lastIndex) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .width(1.dp)
                                    .height(12.dp)
                            ) {
                                Surface(color = CosmicTheme.colors.glassBorder, modifier = Modifier.fillMaxSize()) {}
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Simulate button
        OutlinedButton(
            onClick = onSimulate,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, CosmicTheme.colors.supernova.copy(alpha = 0.3f)),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = CosmicTheme.colors.supernova
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mô phỏng rủi ro",
                style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                color = CosmicTheme.colors.supernova
            )
        }
    }
}

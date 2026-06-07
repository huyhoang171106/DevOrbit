package vn.edu.uit.devorbit.mobile.ui.screen.knowledge

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.domain.model.GraphLink
import vn.edu.uit.devorbit.mobile.domain.model.GraphNode
import vn.edu.uit.devorbit.mobile.ui.components.GalaxyGraphCanvas
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun KnowledgeGraphScreen(
    nodes: List<GraphNode>,
    links: List<GraphLink>,
    learningPath: List<GraphNode>,
    selectedNode: GraphNode?,
    onNodeClick: (GraphNode) -> Unit,
    onInfoClick: (GraphNode) -> Unit = {},
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    selectedSemester: Int? = null,
    onSemesterFilterChange: (Int?) -> Unit = {},
    completedNodeIds: Set<Long> = emptySet(),
    onToggleComplete: (Long) -> Unit = {},
    simulationMode: Boolean = false,
    simulationFailedIds: Set<Long> = emptySet(),
    onToggleSimulation: () -> Unit = {},
    onClearSelection: () -> Unit = {},
    totalNodeCount: Int = 0,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Interactive graph canvas
        GalaxyGraphCanvas(
            nodes = nodes,
            links = links,
            selectedNode = selectedNode,
            onNodeClick = onNodeClick
        )

        // Header overlay
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text(
                text = "Vũ trụ tri thức",
                style = CosmicTheme.typography.display,
                color = CosmicTheme.colors.textPrimary
            )
            Text(
                text = "${nodes.size} thể thức · ${nodes.groupBy { it.level }.size} cấp độ",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Chạm vào một nút để xem chi tiết",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary.copy(alpha = 0.7f)
            )
        }

        // Selected node detail card
        AnimatedVisibility(
            visible = selectedNode != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .padding(bottom = 80.dp)
        ) {
            selectedNode?.let { node ->
                KnowledgePortalCard(node = node)
            }
        }
    }
}

@Composable
private fun KnowledgePortalCard(node: GraphNode) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Node code badge
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = CosmicTheme.colors.plasma.copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = node.code.takeLast(3),
                            color = CosmicTheme.colors.plasma,
                            style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = node.name,
                        style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = CosmicTheme.colors.textPrimary,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hệ số ảnh hưởng: ",
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.textTertiary
                        )
                        Text(
                            text = String.format("%.1f", node.impactScore),
                            style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                            color = CosmicTheme.colors.plasma
                        )
                    }
                }

                IconButton(
                    onClick = {},
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Chi tiết",
                        tint = CosmicTheme.colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

package vn.edu.uit.devorbit.mobile.ui.screen.knowledge

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.domain.model.GraphLink
import vn.edu.uit.devorbit.mobile.domain.model.GraphNode
import vn.edu.uit.devorbit.mobile.ui.components.GalaxyGraphCanvas
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun KnowledgeGraphScreen(
    nodes: List<GraphNode>,
    links: List<GraphLink>,
    learningPath: List<GraphNode>,
    selectedNode: GraphNode?,
    onNodeClick: (GraphNode) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // The interactive Galaxy - Spatial knowledge rendering
        GalaxyGraphCanvas(
            nodes = nodes,
            links = links,
            selectedNode = selectedNode,
            onNodeClick = onNodeClick
        )

        // Header Overlay - Cosmic Context
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(CosmicTheme.spacing.medium)
        ) {
            Text(
                text = "VŨ TRỤ TRI THỨC",
                style = CosmicTheme.typography.command,
                color = CosmicTheme.colors.plasma
            )
            Text(
                text = "${nodes.size} THỂ THỰC TRI THỨC . ${nodes.groupBy { it.level }.size} CẤP ĐỘ",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textSecondary
            )
        }

        // Selected Node Detail Card - The Knowledge Portal
        AnimatedVisibility(
            visible = selectedNode != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(CosmicTheme.spacing.medium)
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
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(CosmicTheme.spacing.small)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Identity Orb
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.plasma.copy(alpha = 0.3f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = node.code.takeLast(3),
                            color = CosmicTheme.colors.plasma,
                            style = CosmicTheme.typography.command,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(CosmicTheme.spacing.medium))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = node.name.uppercase(),
                        style = CosmicTheme.typography.body,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hệ số ảnh hưởng:",
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", node.impactScore),
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.aurora,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                IconButton(
                    onClick = { /* Action handled via currentScreen in App.kt */ },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = CosmicTheme.colors.glassBorder)
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Detail", tint = Color.White)
                }
            }
        }
    }
}

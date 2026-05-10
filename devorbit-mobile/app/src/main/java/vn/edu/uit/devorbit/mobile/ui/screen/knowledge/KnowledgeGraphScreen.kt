package vn.edu.uit.devorbit.mobile.ui.screen.knowledge

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.model.domain.GraphLink
import vn.edu.uit.devorbit.mobile.model.domain.GraphNode
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
    val grouped = nodes.groupBy { it.level }.toSortedMap()

    if (nodes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Dang tai do thi tri thuc...", color = TextSecondary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp, bottom = 80.dp)
    ) {
        // Header
        item {
            Text(
                text = "Do thi kien thuc",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${nodes.size} mon hoc . ${grouped.size} cap do",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // Learning path section (when a node is selected)
        if (selectedNode != null && learningPath.isNotEmpty()) {
            item {
                LearningPathSection(selectedNode = selectedNode, learningPath = learningPath)
            }
        }

        // Nodes grouped by level
        grouped.forEach { (level, levelNodes) ->
            item {
                Text(
                    text = "Cap do $level",
                    color = CosmicGlowBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }
            items(levelNodes, key = { it.id }) { node ->
                NodeCard(
                    node = node,
                    isSelected = selectedNode?.id == node.id,
                    onClick = { onNodeClick(node) }
                )
            }
        }
    }
}

@Composable
private fun NodeCard(node: GraphNode, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) CosmicGlowPurple else GlassBorder

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = node.name,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = node.code,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                LevelBadge(level = node.level)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Impact score bar
            ImpactBar(score = node.impactScore)
        }
    }
}

@Composable
private fun LevelBadge(level: Int) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CosmicNebulaPurple.copy(alpha = 0.25f)
    ) {
        Text(
            text = "L$level",
            color = CosmicNebulaPurple,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun ImpactBar(score: Double) {
    val barColor = when {
        score > 7.0 -> Color(0xFFEF5350)
        score > 4.0 -> Color(0xFFFFB74D)
        else -> CosmicGlowBlue
    }
    val fraction = (score / 10.0).toFloat().coerceIn(0f, 1f)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Anh huong", color = TextSecondary, fontSize = 11.sp)
            Text(
                text = "%.1f".format(score),
                color = barColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
        ) {
            Surface(color = GlassWhite, modifier = Modifier.fillMaxSize()) {}
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .clip(RoundedCornerShape(2.dp))
            ) {
                Surface(color = barColor.copy(alpha = 0.6f), modifier = Modifier.fillMaxSize()) {}
            }
        }
    }
}

@Composable
private fun LearningPathSection(selectedNode: GraphNode, learningPath: List<GraphNode>) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = "Lo trinh hoc: ${selectedNode.name}",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cac mon can hoan thanh truoc",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            learningPath.forEachIndexed { index, node ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (node.id == selectedNode.id) CosmicGlowPurple.copy(alpha = 0.3f)
                        else CosmicStarBlue.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = if (node.id == selectedNode.id) CosmicGlowPurple else CosmicStarBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = node.name,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = if (node.id == selectedNode.id) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(text = node.code, color = TextSecondary, fontSize = 11.sp)
                    }
                }
                if (index < learningPath.lastIndex) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(12.dp)
                            .padding(start = 8.dp)
                    ) {
                        Surface(color = GlassBorder, modifier = Modifier.fillMaxSize()) {}
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

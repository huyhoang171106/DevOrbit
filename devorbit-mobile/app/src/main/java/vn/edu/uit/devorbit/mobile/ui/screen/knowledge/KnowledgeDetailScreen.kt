package vn.edu.uit.devorbit.mobile.ui.screen.knowledge

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
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
import vn.edu.uit.devorbit.mobile.model.domain.GraphNode
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun KnowledgeDetailScreen(
    node: GraphNode,
    learningPath: List<GraphNode>,
    onSimulate: () -> Unit,
    onBack: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Back button + header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Quay lai",
                    tint = TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Chi tiet kien thuc", color = TextSecondary, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Node info card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = node.name,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = node.code, color = TextSecondary, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Impact score
                val impactColor = when {
                    node.impactScore > 7.0 -> Color(0xFFEF5350)
                    node.impactScore > 4.0 -> Color(0xFFFFB74D)
                    else -> CosmicGlowBlue
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Muc do anh huong", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "%.1f / 10".format(node.impactScore),
                        color = impactColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Impact bar
                Spacer(modifier = Modifier.height(6.dp))
                val fraction = (node.impactScore / 10.0).toFloat().coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                ) {
                    Surface(color = GlassWhite, modifier = Modifier.fillMaxSize()) {}
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction)
                            .clip(RoundedCornerShape(3.dp))
                    ) {
                        Surface(
                            color = impactColor.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxSize()
                        ) {}
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Level indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Cap do", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CosmicNebulaPurple.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = "${node.level}",
                            color = CosmicNebulaPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Prerequisite chain
        if (learningPath.isNotEmpty()) {
            Text(
                text = "Lo trinh tien quyet",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                learningPath.forEachIndexed { index, pathNode ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (pathNode.id == node.id) CosmicGlowPurple.copy(alpha = 0.3f)
                            else CosmicStarBlue.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = if (pathNode.id == node.id) CosmicGlowPurple else CosmicStarBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = pathNode.name,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = if (pathNode.id == node.id) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = pathNode.code,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    if (index < learningPath.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(12.dp)
                                .padding(start = 8.dp)
                        ) {
                            Surface(color = GlassBorder, modifier = Modifier.fillMaxSize()) {}
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Simulate button
        Button(
            onClick = onSimulate,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEF5350).copy(alpha = 0.15f),
                contentColor = Color(0xFFEF5350)
            )
        ) {
            Icon(Icons.Default.Warning, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mo phong rui ro",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

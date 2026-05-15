package vn.edu.uit.devorbit.mobile.ui.screen.plan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.domain.model.StudyItem
import vn.edu.uit.devorbit.mobile.domain.model.StudyPhase
import vn.edu.uit.devorbit.mobile.domain.model.StudyPlan
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun StudyPlannerScreen(
    studyPlan: StudyPlan?,
    onGeneratePlan: () -> Unit,
    onToggleItem: (String) -> Unit,
    onBreakdownTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicDeepSpace)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header + Generate button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Kế hoạch học tập",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Button(
                onClick = onGeneratePlan,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CosmicNebulaPurple,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Tạo kế hoạch")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (studyPlan == null) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(modifier = Modifier.fillMaxWidth().padding(32.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Chưa có kế hoạch nào",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nhấn \"Tạo kế hoạch\" để bắt đầu lộ trình học tập của bạn",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            // Plan summary
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${studyPlan.phases.size}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicGlowPurple
                        )
                        Text("Giai đoạn", fontSize = 12.sp, color = TextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${studyPlan.totalHours.toInt()}h",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicStarBlue
                        )
                        Text("Tổng thời gian", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Phases list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(studyPlan.phases) { phase ->
                    PhaseCard(
                        phase = phase,
                        onToggleItem = onToggleItem,
                        onBreakdownTask = onBreakdownTask
                    )
                }
            }
        }
    }
}

@Composable
private fun PhaseCard(
    phase: StudyPhase,
    onToggleItem: (String) -> Unit,
    onBreakdownTask: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    GlassCard(modifier = Modifier.fillMaxWidth().animateContentSize()) {
        Column {
            // Phase header — clickable to expand/collapse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = phase.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Ngày ${phase.startDay} - Ngày ${phase.endDay}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Thu gọn" else "Mở rộng",
                    tint = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Progress bar
            val completedCount = phase.items.count { it.completed }
            val totalCount = phase.items.size
            val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = CosmicGlowBlue,
                trackColor = GlassWhite,
            )

            Spacer(modifier = Modifier.height(4.dp))

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    phase.items.forEach { item ->
                        StudyItemRow(
                            item = item,
                            onToggle = { onToggleItem(item.id) },
                            onBreakdown = { onBreakdownTask(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyItemRow(
    item: StudyItem,
    onToggle: () -> Unit,
    onBreakdown: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (item.completed) Icons.Default.CheckCircle else Icons.Default.CheckCircle,
            contentDescription = if (item.completed) "Đã hoàn thành" else "Chưa hoàn thành",
            tint = if (item.completed) CosmicGlowBlue else TextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                color = if (item.completed) TextSecondary else TextPrimary,
                fontWeight = if (item.completed) FontWeight.Normal else FontWeight.Medium
            )
            Text(
                text = "${item.estimatedHours}h",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        DifficultyBadge(difficulty = item.difficulty)
        Spacer(modifier = Modifier.width(6.dp))
        TextButton(onClick = onBreakdown, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
            Text("Chi tiết", fontSize = 12.sp, color = CosmicNebulaPurple)
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: String) {
    val (label, color) = when (difficulty.lowercase()) {
        "easy" -> "Dễ" to CosmicGlowBlue
        "hard" -> "Khó" to CosmicGlowPurple
        else -> "TB" to Color(0xFFFBC02D)
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

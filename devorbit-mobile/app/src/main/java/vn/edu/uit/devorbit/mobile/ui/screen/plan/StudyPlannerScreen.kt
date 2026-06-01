package vn.edu.uit.devorbit.mobile.ui.screen.plan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Kế hoạch học tập",
                style = CosmicTheme.typography.display,
                color = CosmicTheme.colors.textPrimary
            )
            Button(
                onClick = onGeneratePlan,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CosmicTheme.colors.plasma,
                    contentColor = CosmicTheme.colors.void
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("Tạo kế hoạch", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (studyPlan == null) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = CosmicTheme.colors.nebula,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "Chưa có kế hoạch nào",
                            style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                            color = CosmicTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nhấn \"Tạo kế hoạch\" để bắt đầu lộ trình học tập của bạn",
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.textTertiary
                        )
                    }
                }
            }
        } else {
            // Plan summary
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CosmicTheme.colors.nebula,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${studyPlan.phases.size}",
                            style = CosmicTheme.typography.metric.copy(fontSize = 28.sp),
                            color = CosmicTheme.colors.plasma
                        )
                        Text("Giai đoạn", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${studyPlan.totalHours.toInt()}h",
                            style = CosmicTheme.typography.metric.copy(fontSize = 28.sp),
                            color = CosmicTheme.colors.aurora
                        )
                        Text("Tổng thời gian", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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

    Surface(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(14.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = CosmicTheme.colors.textPrimary
                    )
                    Text(
                        text = "Ngày ${phase.startDay} - Ngày ${phase.endDay}",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textTertiary
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Thu gọn" else "Mở rộng",
                    tint = CosmicTheme.colors.textTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            val completedCount = phase.items.count { it.completed }
            val totalCount = phase.items.size
            val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = CosmicTheme.colors.aurora,
                trackColor = CosmicTheme.colors.glassBorder,
            )

            Spacer(modifier = Modifier.height(10.dp))

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = if (item.completed) "Đã hoàn thành" else "Chưa hoàn thành",
            tint = if (item.completed) CosmicTheme.colors.aurora else CosmicTheme.colors.textTertiary.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = CosmicTheme.typography.body.copy(
                    fontWeight = if (item.completed) FontWeight.Normal else FontWeight.Medium
                ),
                color = if (item.completed) CosmicTheme.colors.textTertiary else CosmicTheme.colors.textPrimary
            )
            Text(
                text = "${item.estimatedHours}h",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary
            )
        }
        DifficultyBadge(difficulty = item.difficulty)
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(
            onClick = onBreakdown,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                "Chi tiết",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.plasma
            )
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: String) {
    val (label, color) = when (difficulty.lowercase()) {
        "easy" -> "Dễ" to CosmicTheme.colors.aurora
        "hard" -> "Khó" to CosmicTheme.colors.supernova
        else -> "TB" to CosmicTheme.colors.plasma
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

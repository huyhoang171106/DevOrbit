package vn.edu.uit.devorbit.mobile.ui.screen.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.data.local.entity.TaskEntity
import vn.edu.uit.devorbit.mobile.domain.model.AcademicHealth
import vn.edu.uit.devorbit.mobile.domain.model.BreakdownStep
import vn.edu.uit.devorbit.mobile.domain.model.StudyRecommendation
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun DashboardScreen(
    academicHealth: AcademicHealth,
    nextAction: BreakdownStep?,
    recommendations: List<StudyRecommendation>,
    todayTasks: List<TaskEntity>,
    focusTask: TaskEntity?,
    onStartFocus: (TaskEntity) -> Unit,
    onCompleteTask: (TaskEntity) -> Unit,
    onBreakdownGoal: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = 24.dp, bottom = 100.dp
        )
    ) {
        // Header
        item {
            Text(
                text = "Tổng quan",
                style = CosmicTheme.typography.display,
                color = CosmicTheme.colors.textPrimary
            )
        }

        // Metrics grid: 3 columns
        item {
            MetricsGrid(health = academicHealth)
        }

        // Next action
        if (nextAction != null) {
            item {
                NextActionCard(nextAction = nextAction, onStart = onBreakdownGoal)
            }
        }

        // Tasks
        item {
            SectionLabel("Hành trình hôm nay")
        }

        if (todayTasks.filter { !it.completed }.isEmpty()) {
            item {
                EmptyTasksCard()
            }
        } else {
            items(todayTasks.filter { !it.completed }) { task ->
                TaskRow(
                    task = task,
                    onComplete = { onCompleteTask(task) },
                    onFocus = { onStartFocus(task) }
                )
            }
        }

        // Recommendations
        if (recommendations.isNotEmpty()) {
            item {
                SectionLabel("Gợi ý từ copilot")
            }
            items(recommendations.take(3)) { rec ->
                RecommendationRow(rec = rec)
            }
        }
    }
}

@Composable
private fun MetricsGrid(health: AcademicHealth) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetricTile(
            label = "Sức khỏe",
            value = "${(health.score * 10).toInt()}",
            accent = when {
                health.score >= 0.7f -> CosmicTheme.colors.aurora
                health.score >= 0.4f -> CosmicTheme.colors.plasma
                else -> CosmicTheme.colors.supernova
            },
            modifier = Modifier.weight(1f)
        )
        MetricTile(
            label = "GPA dự báo",
            value = "3.85",
            accent = CosmicTheme.colors.aurora,
            modifier = Modifier.weight(1f)
        )
        MetricTile(
            label = "Rủi ro",
            value = "Thấp",
            accent = CosmicTheme.colors.plasma,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary
            )
            Text(
                text = value,
                style = CosmicTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    lineHeight = 26.sp
                ),
                color = accent
            )
        }
    }
}

@Composable
private fun NextActionCard(nextAction: BreakdownStep, onStart: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CosmicTheme.colors.plasma.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.plasma.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Bước tiếp theo",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.plasma
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = nextAction.title,
                style = CosmicTheme.typography.body.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                ),
                color = CosmicTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CosmicTheme.colors.plasma,
                    contentColor = CosmicTheme.colors.void
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bắt đầu", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title,
        style = CosmicTheme.typography.command,
        color = CosmicTheme.colors.textTertiary
    )
}

@Composable
private fun TaskRow(task: TaskEntity, onComplete: () -> Unit, onFocus: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 44dp touch target for checkbox
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clickable { onComplete() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(
                            if (task.completed) CosmicTheme.colors.aurora else Color.Transparent,
                            CircleShape
                        )
                        .border(
                            1.5.dp,
                            if (task.completed) CosmicTheme.colors.aurora else CosmicTheme.colors.textTertiary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (task.completed) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = CosmicTheme.colors.void
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = task.title,
                style = CosmicTheme.typography.body,
                color = if (task.completed) CosmicTheme.colors.textTertiary else CosmicTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            // 44dp touch target for focus
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clickable { onFocus() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Bắt đầu tập trung",
                    tint = CosmicTheme.colors.plasma,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyTasksCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Không có nhiệm vụ nào hôm nay",
                style = CosmicTheme.typography.body,
                color = CosmicTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Thêm nhiệm vụ từ kế hoạch học tập để bắt đầu",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary
            )
        }
    }
}

@Composable
private fun RecommendationRow(rec: StudyRecommendation) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Amber accent dot
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(6.dp)
                    .background(CosmicTheme.colors.plasma, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = rec.title,
                    style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = CosmicTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = rec.description,
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textSecondary
                )
            }
        }
    }
}

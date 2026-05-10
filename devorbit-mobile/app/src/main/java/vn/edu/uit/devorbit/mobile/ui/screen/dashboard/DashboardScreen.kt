package vn.edu.uit.devorbit.mobile.ui.screen.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.model.domain.AcademicHealth
import vn.edu.uit.devorbit.mobile.model.domain.BreakdownStep
import vn.edu.uit.devorbit.mobile.model.domain.LearningTask
import vn.edu.uit.devorbit.mobile.model.domain.StudyRecommendation
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun DashboardScreen(
    academicHealth: AcademicHealth,
    nextAction: BreakdownStep?,
    recommendations: List<StudyRecommendation>,
    todayTasks: List<LearningTask>,
    focusTask: LearningTask?,
    onStartFocus: (LearningTask) -> Unit,
    onCompleteTask: (LearningTask) -> Unit,
    onBreakdownGoal: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp)
        ) {
            // Greeting + health score
            item {
                GreetingSection(academicHealth)
            }

            // Next action card
            if (nextAction != null) {
                item {
                    NextActionCard(nextAction = nextAction, onBreakdownGoal = onBreakdownGoal)
                }
            }

            // Recommendations
            if (recommendations.isNotEmpty()) {
                item {
                    Text(
                        text = "De xuat cho ban",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                items(recommendations.take(3)) { rec ->
                    RecommendationCard(rec = rec)
                }
            }

            // Today's tasks
            item {
                Text(
                    text = "Viec can lam hom nay",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }
            items(todayTasks.filter { !it.completed }) { task ->
                TaskCard(
                    task = task,
                    onComplete = { onCompleteTask(task) },
                    onFocus = { onStartFocus(task) }
                )
            }
            if (todayTasks.none { !it.completed }) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Hom nay ban da hoan thanh het viec!",
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Focus FAB
        if (focusTask != null) {
            FloatingActionButton(
                onClick = { onStartFocus(focusTask) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = CosmicGlowPurple,
                contentColor = CosmicDeepSpace
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Focus Mode")
            }
        }
    }
}

@Composable
private fun GreetingSection(academicHealth: AcademicHealth) {
    val score = academicHealth.score
    val healthColor = when {
        score >= 7.0 -> CosmicGlowBlue
        score >= 4.0 -> Color(0xFFFFB74D)
        else -> Color(0xFFEF5350)
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Xin chao,", color = TextSecondary, fontSize = 14.sp)
            Text(text = "Sinh vien UIT", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(72.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 6.dp.toPx()
                val arcSize = size - Offset(strokeWidth, strokeWidth) * 2
                drawArc(
                    color = GlassWhite,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(strokeWidth, strokeWidth),
                    size = Size(arcSize.width, arcSize.height),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                drawArc(
                    color = healthColor,
                    startAngle = -90f,
                    sweepAngle = (score / 10.0 * 360.0).toFloat(),
                    useCenter = false,
                    topLeft = Offset(strokeWidth, strokeWidth),
                    size = Size(arcSize.width, arcSize.height),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            Text(
                text = "${score.toInt()}",
                color = healthColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun NextActionCard(nextAction: BreakdownStep, onBreakdownGoal: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Hanh dong tiep theo", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = nextAction.title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "~${nextAction.estimatedMinutes} phut",
                    color = CosmicStarBlue,
                    fontSize = 13.sp
                )
            }
            FilledTonalButton(onClick = onBreakdownGoal) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CosmicDeepSpace)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bat dau", color = CosmicDeepSpace)
            }
        }
    }
}

@Composable
private fun RecommendationCard(rec: StudyRecommendation) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().clickable { /* TODO: navigate to detail */ }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rec.title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = rec.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            PriorityBadge(priority = rec.priority)
        }
    }
}

@Composable
private fun PriorityBadge(priority: Int) {
    val color = when {
        priority >= 8 -> Color(0xFFEF5350)
        priority >= 5 -> Color(0xFFFFB74D)
        else -> CosmicGlowBlue
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = "$priority",
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun TaskCard(task: LearningTask, onComplete: () -> Unit, onFocus: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = task.completed,
                onCheckedChange = { onComplete() },
                colors = CheckboxDefaults.colors(
                    checkedColor = CosmicGlowBlue,
                    uncheckedColor = TextSecondary
                )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = if (task.completed) TextSecondary else TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (task.estimatedMinutes > 0) {
                    Text(
                        text = "${task.achievedMinutes}/${task.estimatedMinutes} phut",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
            if (!task.completed) {
                IconButton(onClick = onFocus) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Focus", tint = CosmicGlowPurple)
                }
            }
        }
    }
}

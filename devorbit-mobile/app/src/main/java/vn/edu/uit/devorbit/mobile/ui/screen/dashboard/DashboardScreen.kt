package vn.edu.uit.devorbit.mobile.ui.screen.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.*
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
        verticalArrangement = Arrangement.spacedBy(CosmicTheme.spacing.medium),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Hero Section: Academic Health
        item {
            AcademicHealthGauge(academicHealth)
        }

        // The "Next Best Action" - Immersive Card
        if (nextAction != null) {
            item {
                ExecutionCard(nextAction = nextAction, onStart = onBreakdownGoal)
            }
        }

        // Adaptive Widgets Row
        item {
            MetricsRow()
        }

        // Tasks Section
        item {
            SectionHeader("HÀNH TRÌNH HÔM NAY")
        }
        
        items(todayTasks.filter { !it.completed }) { task ->
            TaskExecutionCard(
                task = task,
                onComplete = { onCompleteTask(task) },
                onFocus = { onStartFocus(task) }
            )
        }
        
        // Recommendations Section
        if (recommendations.isNotEmpty()) {
            item {
                SectionHeader("GỢI Ý TỪ COPILOT")
            }
            items(recommendations.take(3)) { rec ->
                RecommendationGlassCard(rec = rec)
            }
        }
    }
}

@Composable
private fun AcademicHealthGauge(health: AcademicHealth) {
    val infiniteTransition = rememberInfiniteTransition(label = "HealthPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    val plasmaColor = CosmicTheme.colors.plasma

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = CosmicTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
            // Background Glow
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            plasmaColor.copy(alpha = 0.15f * pulse),
                            Color.Transparent
                        )
                    )
                )
            }
            
            // Health Value
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(health.score * 10).toInt()}",
                    style = CosmicTheme.typography.metric,
                    color = Color.White
                )
                Text(
                    text = "ACADEMIC HEALTH",
                    style = CosmicTheme.typography.command,
                    color = CosmicTheme.colors.plasma
                )
            }
        }
    }
}

@Composable
private fun ExecutionCard(nextAction: BreakdownStep, onStart: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = CosmicTheme.spacing.medium)
            .background(CosmicTheme.gradients.primary, RoundedCornerShape(24.dp)),
        color = Color.Transparent,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(CosmicTheme.spacing.large)) {
            Text(
                text = "THỰC THI TIẾP THEO",
                style = CosmicTheme.typography.label,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(CosmicTheme.spacing.small))
            Text(
                text = nextAction.title,
                style = CosmicTheme.typography.display.copy(fontSize = 24.sp),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(CosmicTheme.spacing.medium))
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = CosmicTheme.colors.plasma),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bắt đầu ngay", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MetricsRow() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = CosmicTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(CosmicTheme.spacing.medium)
    ) {
        MetricWidget("Dự báo GPA", "3.85", CosmicTheme.colors.aurora, Modifier.weight(1f))
        MetricWidget("Rủi ro", "THẤP", CosmicTheme.colors.plasma, Modifier.weight(1f))
    }
}

@Composable
private fun MetricWidget(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier) {
        Column {
            Text(text = label, style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = CosmicTheme.typography.display.copy(fontSize = 20.sp), color = accent)
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = CosmicTheme.typography.command,
        color = CosmicTheme.colors.textSecondary,
        modifier = Modifier.padding(horizontal = CosmicTheme.spacing.medium, vertical = CosmicTheme.spacing.small)
    )
}

@Composable
private fun TaskExecutionCard(task: TaskEntity, onComplete: () -> Unit, onFocus: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = CosmicTheme.spacing.medium),
        color = CosmicTheme.colors.nebula,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Row(
            modifier = Modifier.padding(CosmicTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(if (task.completed) CosmicTheme.colors.aurora else Color.Transparent, CircleShape)
                    .border(1.dp, if (task.completed) CosmicTheme.colors.aurora else CosmicTheme.colors.textTertiary, CircleShape)
                    .clickable { onComplete() },
                contentAlignment = Alignment.Center
            ) {
                if (task.completed) Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = CosmicTheme.colors.void)
            }
            Spacer(modifier = Modifier.width(CosmicTheme.spacing.medium))
            Text(
                text = task.title,
                style = CosmicTheme.typography.body,
                color = if (task.completed) CosmicTheme.colors.textTertiary else CosmicTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onFocus) {
                Icon(Icons.Default.PlayArrow, null, tint = CosmicTheme.colors.plasma)
            }
        }
    }
}

@Composable
private fun RecommendationGlassCard(rec: StudyRecommendation) {
    GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = CosmicTheme.spacing.medium)) {
        Column {
            Text(text = rec.title, style = CosmicTheme.typography.body, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = rec.description, style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary)
        }
    }
}

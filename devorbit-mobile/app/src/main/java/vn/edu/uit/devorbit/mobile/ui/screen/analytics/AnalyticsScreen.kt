package vn.edu.uit.devorbit.mobile.ui.screen.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.model.domain.ProcrastinationPattern
import vn.edu.uit.devorbit.mobile.model.domain.StudyAnalytics
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun AnalyticsScreen(
    analytics: StudyAnalytics,
    onRefresh: () -> Unit = {},
    onNavigateToGpa: () -> Unit = {},
    onNavigateToTimeline: () -> Unit = {},
    onNavigateToWorkload: () -> Unit = {},
) {
    CosmicBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Phân tích học tập",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // Consistency score — circular progress
            ConsistencyGauge(score = analytics.consistencyScore)

            // Navigate to sub-screens
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToGpa,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("GPA", color = CosmicNebulaPurple) }
                OutlinedButton(
                    onClick = onNavigateToTimeline,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Dòng thời gian", color = CosmicNebulaPurple) }
                OutlinedButton(
                    onClick = onNavigateToWorkload,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Khối lượng", color = CosmicNebulaPurple) }
            }

            // Stat cards row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "Tập trung TB",
                    value = "${analytics.focusDuration.averageMinutes}p",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Hiệu suất",
                    value = "${(analytics.studyEfficiency * 100).toInt()}%",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Buổi học",
                    value = analytics.focusDuration.dailyBreakdown
                        .sumOf { it.sessions }.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            // Strongest subjects
            SubjectsSection(
                title = "Môn mạnh nhất",
                subjects = analytics.strongestSubjects,
                color = CosmicGlowBlue
            )

            // Weakest subjects
            SubjectsSection(
                title = "Môn yếu nhất",
                subjects = analytics.weakestSubjects,
                color = Color(0xFFFF6B6B)
            )

            // Procrastination insight
            ProcrastinationCard(pattern = analytics.procrastinationPattern)
        }
    }
}

@Composable
private fun ConsistencyGauge(score: Double) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Điểm nhất quán",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 14f
                    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                    // Background arc
                    drawArc(
                        color = GlassWhite,
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Progress arc
                    drawArc(
                        color = CosmicNebulaPurple,
                        startAngle = 135f,
                        sweepAngle = (score * 270).toFloat(),
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(score * 100).toInt()}%",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "nhất quán",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = CosmicNebulaPurple
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SubjectsSection(
    title: String,
    subjects: List<String>,
    color: Color
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            subjects.forEach { subject ->
                Surface(
                    color = color.copy(alpha = 0.15f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = subject,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = color,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ProcrastinationCard(pattern: ProcrastinationPattern) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Thói quen trì hoãn",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Điểm trì hoãn: ${(pattern.score * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                text = "Giờ trễ đỉnh: ${pattern.peakDelayHours}h",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            if (pattern.commonTriggers.isNotEmpty()) {
                Text(
                    text = "Tác nhân thường gặp:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                pattern.commonTriggers.forEach { trigger ->
                    Text(
                        text = "  • $trigger",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFFB74D)
                    )
                }
            }
        }
    }
}

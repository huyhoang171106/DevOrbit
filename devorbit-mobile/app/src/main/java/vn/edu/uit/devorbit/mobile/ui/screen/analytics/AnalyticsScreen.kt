package vn.edu.uit.devorbit.mobile.ui.screen.analytics

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(CosmicTheme.spacing.medium),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Hero: Consistency Spectrum
        item {
            ConsistencySpectrum(analytics.consistencyScore)
        }

        // Quick Insights Row
        item {
            QuickInsightsRow(analytics)
        }

        // Navigation Nodes
        item {
            AnalyticsNavigationOrbit(onNavigateToGpa, onNavigateToTimeline, onNavigateToWorkload)
        }

        // Performance Distribution
        item {
            SectionHeader("PHÂN PHỐI NĂNG LỰC")
        }
        
        item {
            SubjectPerformanceWidget(analytics.strongestSubjects, analytics.weakestSubjects)
        }

        // Procrastination Forensics
        item {
            SectionHeader("PHÂN TÍCH TRÌ HOÃN")
        }
        
        item {
            ProcrastinationForensicsCard(analytics.procrastinationPattern)
        }
    }
}

@Composable
private fun ConsistencySpectrum(score: Double) {
    val plasmaColor = CosmicTheme.colors.plasma
    val auroraColor = CosmicTheme.colors.aurora
    val trackAlphaColor = CosmicTheme.colors.plasma.copy(alpha = 0.1f)
    val auroraAlphaColor = CosmicTheme.colors.aurora.copy(alpha = 0.1f)
    val strokeWidthDp = 8.dp

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = CosmicTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.height(180.dp).fillMaxWidth()) {
            Canvas(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp)
                .drawWithCache {
                    val strokeWidth = strokeWidthDp.toPx()
                    val trackBrush = Brush.horizontalGradient(
                        colors = listOf(trackAlphaColor, auroraAlphaColor)
                    )
                    val progressBrush = Brush.horizontalGradient(
                        colors = listOf(plasmaColor, auroraColor)
                    )
                    
                    onDrawBehind {
                        // Track
                        drawArc(
                            brush = trackBrush,
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        
                        // Progress
                        drawArc(
                            brush = progressBrush,
                            startAngle = 180f,
                            sweepAngle = (score * 180).toFloat(),
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }
            ) {
                // Logic moved to drawWithCache
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.align(Alignment.BottomCenter)) {
                Text(
                    text = "${(score * 100).toInt()}%",
                    style = CosmicTheme.typography.metric.copy(fontSize = 48.sp),
                    color = Color.White
                )
                Text(
                    text = "CONSISTENCY SCORE",
                    style = CosmicTheme.typography.command,
                    color = CosmicTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun QuickInsightsRow(analytics: StudyAnalytics) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = CosmicTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(CosmicTheme.spacing.small)
    ) {
        InsightWidget("AVG FOCUS", "${analytics.focusDuration.averageMinutes}m", Modifier.weight(1f))
        InsightWidget("EFFICIENCY", "${(analytics.studyEfficiency * 100).toInt()}%", Modifier.weight(1f))
    }
}

@Composable
private fun InsightWidget(label: String, value: String, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier) {
        Column {
            Text(text = label, style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary)
            Text(text = value, style = CosmicTheme.typography.body, fontWeight = FontWeight.Bold, color = CosmicTheme.colors.plasma)
        }
    }
}

@Composable
private fun AnalyticsNavigationOrbit(onGpa: () -> Unit, onTimeline: () -> Unit, onWorkload: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = CosmicTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(CosmicTheme.spacing.small)
    ) {
        OrbitButton("GPA", onGpa, Modifier.weight(1f))
        OrbitButton("TIMELINE", onTimeline, Modifier.weight(1f))
        OrbitButton("WORKLOAD", onWorkload, Modifier.weight(1f))
    }
}

@Composable
private fun SubjectPerformanceWidget(strongest: List<String>, weakest: List<String>) {
    GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = CosmicTheme.spacing.medium)) {
        Column(verticalArrangement = Arrangement.spacedBy(CosmicTheme.spacing.medium)) {
            PerformanceRow("STRONGEST", strongest, CosmicTheme.colors.aurora)
            HorizontalDivider(color = CosmicTheme.colors.glassBorder)
            PerformanceRow("WEAKEST", weakest, CosmicTheme.colors.plasma)
        }
    }
}

@Composable
private fun PerformanceRow(label: String, subjects: List<String>, accent: Color) {
    Column {
        Text(text = label, style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            subjects.take(3).forEach { subject ->
                Surface(
                    color = accent.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = subject,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = CosmicTheme.typography.label,
                        color = accent
                    )
                }
            }
        }
    }
}

@Composable
private fun ProcrastinationForensicsCard(pattern: vn.edu.uit.devorbit.mobile.model.domain.ProcrastinationPattern) {
    GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = CosmicTheme.spacing.medium)) {
        Column {
            Text(text = "PROCRASTINATION INDEX", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary)
            Text(text = "${(pattern.score * 100).toInt()}%", style = CosmicTheme.typography.display.copy(fontSize = 32.sp), color = if (pattern.score > 0.5) CosmicTheme.colors.plasma else CosmicTheme.colors.aurora)
            Spacer(modifier = Modifier.height(CosmicTheme.spacing.small))
            Text(text = "Peak Delay: ${pattern.peakDelayHours}h", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary)
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
private fun OrbitButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(48.dp).clickable { onClick() },
        color = CosmicTheme.colors.nebula,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = label, style = CosmicTheme.typography.command.copy(fontSize = 12.sp), color = Color.White)
        }
    }
}

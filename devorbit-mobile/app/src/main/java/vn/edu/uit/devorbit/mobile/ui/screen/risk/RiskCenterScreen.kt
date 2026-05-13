package vn.edu.uit.devorbit.mobile.ui.screen.risk

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.rotate
import vn.edu.uit.devorbit.mobile.model.domain.*
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RiskCenterScreen(
    riskProfile: RiskProfile,
    onNavigateToBurnout: () -> Unit = {},
    onNavigateToTwin: () -> Unit = {},
    onNavigateToRecommendations: () -> Unit = {},
    onNavigateToSimulation: () -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(CosmicTheme.spacing.medium),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Hero: Multi-dimensional Risk Radar
        item {
            RiskRadarSection(riskProfile)
        }

        // Action Orbit
        item {
            ActionOrbitRow(
                onNavigateToBurnout,
                onNavigateToTwin,
                onNavigateToRecommendations,
                onNavigateToSimulation
            )
        }

        // Risk Factors Section
        item {
            SectionHeader("CÁC BIẾN SỐ RỦI RO")
        }
        
        items(riskProfile.riskFactors) { factor ->
            FactorGlassCard(factor)
        }

        // Subject Analysis
        item {
            SectionHeader("PHÂN TÍCH ĐỊNH LƯỢNG THEO MÔN")
        }
        
        items(riskProfile.subjectRisks) { subject ->
            SubjectRiskGlassCard(subject)
        }
    }
}

@Composable
private fun RiskRadarSection(profile: RiskProfile) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = CosmicTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(260.dp)) {
            // Cosmic Radar Chart
            RadarChart(
                metrics = listOf(
                    "Academic" to profile.academicRisk,
                    "Health" to profile.healthRisk,
                    "Consistency" to profile.consistencyRisk,
                    "Social" to profile.socialRisk,
                    "Mental" to profile.mentalRisk
                )
            )
            
            // Central Risk Level
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = profile.overallRisk.name,
                    style = CosmicTheme.typography.command,
                    color = getRiskColor(profile.overallRisk)
                )
                Text(
                    text = "OVERALL RISK",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun RadarChart(metrics: List<Pair<String, Double>>) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val sweep by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Sweep"
    )

    val plasmaColor = CosmicTheme.colors.plasma
    val glassBorderColor = CosmicTheme.colors.glassBorder
    val paddingDp = 40.dp
    val axisStrokeWidth = 1.dp
    val polygonStrokeWidth = 2.dp

    Canvas(modifier = Modifier
        .fillMaxSize()
        .drawWithCache {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.minDimension / 2 - paddingDp.toPx()
            
            // Pre-calculate data polygon path
            val dataPath = Path()
            metrics.forEachIndexed { index, metric ->
                val valueFraction = (metric.second / 10.0).toFloat().coerceIn(0f, 1f)
                val angle = (index * (360f / metrics.size) - 90f) * (Math.PI / 180f).toFloat()
                val x = centerX + radius * valueFraction * cos(angle)
                val y = centerY + radius * valueFraction * sin(angle)
                if (index == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
            }
            dataPath.close()

            val dataBrush = Brush.radialGradient(
                colors = listOf(
                    plasmaColor.copy(alpha = 0.4f),
                    plasmaColor.copy(alpha = 0.1f)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            )

            val sweepBrush = Brush.sweepGradient(
                0f to Color.Transparent,
                0.1f to plasmaColor.copy(alpha = 0.2f),
                0.2f to Color.Transparent
            )

            onDrawBehind {
                // Background Web
                for (i in 1..4) {
                    val r = radius * (i / 4f)
                    drawCircle(
                        color = glassBorderColor.copy(alpha = 0.2f),
                        radius = r,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = axisStrokeWidth.toPx())
                    )
                }
                
                // Axis Lines
                metrics.forEachIndexed { index, _ ->
                    val angle = (index * (360f / metrics.size) - 90f) * (Math.PI / 180f).toFloat()
                    val endX = centerX + radius * cos(angle)
                    val endY = centerY + radius * sin(angle)
                    drawLine(
                        color = glassBorderColor.copy(alpha = 0.3f),
                        start = Offset(centerX, centerY),
                        end = Offset(endX, endY),
                        strokeWidth = axisStrokeWidth.toPx()
                    )
                }
                
                // Draw Data Polygon
                drawPath(
                    path = dataPath,
                    brush = dataBrush
                )
                drawPath(
                    path = dataPath,
                    color = plasmaColor,
                    style = Stroke(width = polygonStrokeWidth.toPx())
                )
                
                // Radar Sweep Effect
                rotate(degrees = sweep, pivot = Offset(centerX, centerY)) {
                    drawArc(
                        brush = sweepBrush,
                        startAngle = 0f,
                        sweepAngle = 45f,
                        useCenter = true,
                        size = Size(radius * 2, radius * 2),
                        topLeft = Offset(centerX - radius, centerY - radius)
                    )
                }
            }
        }
    ) {
        // Logic moved to drawWithCache
    }
}

@Composable
private fun ActionOrbitRow(
    onBurnout: () -> Unit,
    onTwin: () -> Unit,
    onRecs: () -> Unit,
    onSim: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = CosmicTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(CosmicTheme.spacing.small)
    ) {
        OrbitButton("BURN", onBurnout, Modifier.weight(1f))
        OrbitButton("TWIN", onTwin, Modifier.weight(1f))
        OrbitButton("RECS", onRecs, Modifier.weight(1f))
        OrbitButton("SIM", onSim, Modifier.weight(1f))
    }
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

@Composable
private fun FactorGlassCard(factor: RiskFactor) {
    GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = CosmicTheme.spacing.medium)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = getRiskColor(factor.severity).copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Warning, null, modifier = Modifier.size(20.dp), tint = getRiskColor(factor.severity))
                }
            }
            Spacer(modifier = Modifier.width(CosmicTheme.spacing.medium))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = factor.type.uppercase(), style = CosmicTheme.typography.command.copy(fontSize = 13.sp), color = Color.White)
                Text(text = factor.description, style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary)
            }
        }
    }
}

@Composable
private fun SubjectRiskGlassCard(subject: SubjectRisk) {
    GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = CosmicTheme.spacing.medium)) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = subject.subjectName, style = CosmicTheme.typography.body, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(text = subject.riskLevel.name, style = CosmicTheme.typography.label, color = getRiskColor(subject.riskLevel))
            }
            Spacer(modifier = Modifier.height(CosmicTheme.spacing.small))
            LinearProgressIndicator(
                progress = { (subject.consistency / 1.0).toFloat() },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                color = CosmicTheme.colors.aurora,
                trackColor = CosmicTheme.colors.glassBorder
            )
        }
    }
}

private fun getRiskColor(level: RiskLevel): Color = when (level) {
    RiskLevel.LOW -> Color(0xFF00F5A0)
    RiskLevel.MEDIUM -> Color(0xFFFFB74D)
    RiskLevel.HIGH -> Color(0xFFFF7043)
    RiskLevel.CRITICAL -> Color(0xFFEF5350)
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

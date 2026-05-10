package vn.edu.uit.devorbit.mobile.ui.screen.risk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.model.domain.RiskLevel
import vn.edu.uit.devorbit.mobile.model.domain.RiskProfile
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun RiskCenterScreen(
    riskProfile: RiskProfile,
    onNavigateToBurnout: () -> Unit = {},
    onNavigateToTwin: () -> Unit = {},
    onNavigateToRecommendations: () -> Unit = {},
    onNavigateToSimulation: () -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicDeepSpace)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { RiskHeader() }
        item { OverallRiskBanner(riskProfile.overallRisk) }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToBurnout,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Kiệt sức", color = CosmicNebulaPurple, fontSize = 12.sp) }
                OutlinedButton(
                    onClick = onNavigateToTwin,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Digital Twin", color = CosmicNebulaPurple, fontSize = 12.sp) }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToRecommendations,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Khuyến nghị", color = CosmicNebulaPurple, fontSize = 12.sp) }
                OutlinedButton(
                    onClick = onNavigateToSimulation,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Mô phỏng", color = CosmicNebulaPurple, fontSize = 12.sp) }
            }
        }
        item {
            Text(
                text = "Yếu tố rủi ro",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        items(riskProfile.riskFactors) { factor ->
            RiskFactorCard(factor.type, factor.description, factor.severity)
        }
        item { Spacer(Modifier.height(4.dp)) }
        item {
            Text(
                text = "Phân tích theo môn",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        items(riskProfile.subjectRisks) { subject ->
            SubjectRiskCard(
                name = subject.subjectName,
                riskLevel = subject.riskLevel,
                overdueTasks = subject.overdueTasks,
                consistency = subject.consistency
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun RiskHeader() {
    Text(
        text = "Trung tâm rủi ro",
        color = TextPrimary,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = "Tổng quan các rủi ro học tập của bạn",
        color = TextSecondary,
        fontSize = 14.sp
    )
}

@Composable
private fun OverallRiskBanner(level: RiskLevel) {
    val (bgColor, label) = when (level) {
        RiskLevel.LOW -> CosmicGlowBlue.copy(alpha = 0.25f) to "Thấp"
        RiskLevel.MEDIUM -> Color(0xFFFFA726).copy(alpha = 0.25f) to "Trung bình"
        RiskLevel.HIGH -> Color(0xFFFF7043).copy(alpha = 0.25f) to "Cao"
        RiskLevel.CRITICAL -> Color(0xFFEF5350).copy(alpha = 0.25f) to "Nghiêm trọng"
    }
    val textColor = when (level) {
        RiskLevel.LOW -> CosmicGlowBlue
        RiskLevel.MEDIUM -> Color(0xFFFFA726)
        RiskLevel.HIGH -> Color(0xFFFF7043)
        RiskLevel.CRITICAL -> Color(0xFFEF5350)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(16.dp)
    ) {
        Column {
            Text(text = "Mức rủi ro tổng thể", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            Text(text = label, color = textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RiskFactorCard(type: String, description: String, severity: RiskLevel) {
    val severityColor = when (severity) {
        RiskLevel.LOW -> CosmicGlowBlue
        RiskLevel.MEDIUM -> Color(0xFFFFA726)
        RiskLevel.HIGH -> Color(0xFFFF7043)
        RiskLevel.CRITICAL -> Color(0xFFEF5350)
    }
    val severityLabel = when (severity) {
        RiskLevel.LOW -> "Thấp"
        RiskLevel.MEDIUM -> "TB"
        RiskLevel.HIGH -> "Cao"
        RiskLevel.CRITICAL -> "Nặng"
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(severityColor.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "!", color = severityColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = type, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                Spacer(Modifier.height(2.dp))
                Text(text = description, color = TextSecondary, fontSize = 13.sp)
            }
            Spacer(Modifier.width(8.dp))
            Text(text = severityLabel, color = severityColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SubjectRiskCard(
    name: String,
    riskLevel: RiskLevel,
    overdueTasks: Int,
    consistency: Double
) {
    val badgeColor = when (riskLevel) {
        RiskLevel.LOW -> CosmicGlowBlue
        RiskLevel.MEDIUM -> Color(0xFFFFA726)
        RiskLevel.HIGH -> Color(0xFFFF7043)
        RiskLevel.CRITICAL -> Color(0xFFEF5350)
    }
    val badgeLabel = when (riskLevel) {
        RiskLevel.LOW -> "An toàn"
        RiskLevel.MEDIUM -> "Cảnh báo"
        RiskLevel.HIGH -> "Nguy hiểm"
        RiskLevel.CRITICAL -> "Nghiêm trọng"
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = badgeLabel, color = badgeColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Quá hạn", color = TextSecondary, fontSize = 12.sp)
                    Text(text = "$overdueTasks việc", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Tính nhất quán", color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    ConsistencyBar(consistency)
                }
            }
        }
    }
}

@Composable
private fun ConsistencyBar(value: Double) {
    val barColor = when {
        value >= 0.8 -> CosmicGlowBlue
        value >= 0.5 -> Color(0xFFFFA726)
        else -> Color(0xFFEF5350)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(GlassWhite)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = value.toFloat().coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(3.dp))
                .background(barColor)
        )
    }
    Text(
        text = "${(value * 100).toInt()}%",
        color = TextSecondary,
        fontSize = 11.sp
    )
}

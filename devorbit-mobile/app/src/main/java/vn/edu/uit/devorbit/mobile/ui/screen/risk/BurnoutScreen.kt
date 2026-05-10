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
import vn.edu.uit.devorbit.mobile.model.domain.BurnoutRisk
import vn.edu.uit.devorbit.mobile.model.domain.BurnoutStatus
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun BurnoutScreen(burnout: BurnoutStatus) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicDeepSpace)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BurnoutHeader() }
        item { BurnoutBanner(burnout.riskLevel) }
        item {
            Text(
                text = "Chỉ số kiệt sức",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        items(burnout.indicators) { indicator ->
            IndicatorCard(
                name = indicator.name,
                value = indicator.value,
                threshold = indicator.threshold,
                triggered = indicator.triggered
            )
        }
        if (burnout.recommendation.isNotBlank()) {
            item {
                Spacer(Modifier.height(4.dp))
                RecommendationBox(text = burnout.recommendation)
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun BurnoutHeader() {
    Text(
        text = "Phát hiện kiệt sức",
        color = TextPrimary,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = "Theo dõi các dấu hiệu kiệt sức học tập",
        color = TextSecondary,
        fontSize = 14.sp
    )
}

@Composable
private fun BurnoutBanner(risk: BurnoutRisk) {
    val (bgColor, textColor, label) = when (risk) {
        BurnoutRisk.NONE -> CosmicGlowBlue.copy(alpha = 0.25f) to CosmicGlowBlue to "Không có"
        BurnoutRisk.LOW -> Color(0xFFFFA726).copy(alpha = 0.25f) to Color(0xFFFFA726) to "Thấp"
        BurnoutRisk.MODERATE -> Color(0xFFFF7043).copy(alpha = 0.25f) to Color(0xFFFF7043) to "Trung bình"
        BurnoutRisk.HIGH -> Color(0xFFEF5350).copy(alpha = 0.25f) to Color(0xFFEF5350) to "Cao"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(16.dp)
    ) {
        Column {
            Text(text = "Mức kiệt sức", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            Text(text = label, color = textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun IndicatorCard(name: String, value: Double, threshold: Double, triggered: Boolean) {
    val statusColor = if (triggered) Color(0xFFEF5350) else CosmicGlowBlue
    val statusIcon = if (triggered) "!" else "✓"
    val statusLabel = if (triggered) "Đã vượt ngưỡng" else "Trong ngưỡng"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(statusColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = statusIcon, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(text = "Giá trị: ", color = TextSecondary, fontSize = 13.sp)
                    Text(
                        text = if (value == value.toLong().toDouble()) "${value.toInt()}" else String.format("%.1f", value),
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row {
                    Text(text = "Ngưỡng: ", color = TextSecondary, fontSize = 13.sp)
                    Text(
                        text = if (threshold == threshold.toLong().toDouble()) "${threshold.toInt()}" else String.format("%.1f", threshold),
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = statusLabel,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RecommendationBox(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CosmicNebulaPurple.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "Khuyến nghị", color = CosmicNebulaPurple, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text(text = text, color = TextPrimary, fontSize = 13.sp)
        }
    }
}

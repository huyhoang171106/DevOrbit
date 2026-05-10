package vn.edu.uit.devorbit.mobile.ui.screen.risk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.model.domain.AcademicTwin
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun DigitalTwinScreen(twin: AcademicTwin?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicDeepSpace)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { TwinHeader() }
        if (twin == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Chưa có dữ liệu học tập", color = TextSecondary, fontSize = 16.sp)
                }
            }
        } else {
            item { ProjectedGpaCard(twin.projectedGpa) }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GraduationYearCard(twin.graduationYear, modifier = Modifier.weight(1f))
                    OnTrackCard(twin.onTrack, modifier = Modifier.weight(1f))
                }
            }
            item { OverloadBar(twin.overloadProbability) }
            item { HealthGauge(twin.academicHealthScore) }
            if (twin.insights.isNotEmpty()) {
                item {
                    Text(
                        text = "Thông tin chi tiết",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(twin.insights) { insight ->
                    InsightCard(insight)
                }
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun TwinHeader() {
    Text(
        text = "Digital Twin học tập",
        color = TextPrimary,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = "Hình ảnh phản chiếu học thuật của bạn",
        color = TextSecondary,
        fontSize = 14.sp
    )
}

@Composable
private fun ProjectedGpaCard(gpa: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "GPA dự kiến", color = TextSecondary, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = String.format("%.2f", gpa),
                color = if (gpa >= 3.0) CosmicGlowBlue else if (gpa >= 2.0) Color(0xFFFFA726) else Color(0xFFEF5350),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Thang điểm 4.0",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun GraduationYearCard(year: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Tốt nghiệp", color = TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                text = "$year",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun OnTrackCard(onTrack: Boolean, modifier: Modifier = Modifier) {
    val icon = if (onTrack) "✓" else "✕"
    val label = if (onTrack) "Đúng hướng" else "Lệch hướng"
    val color = if (onTrack) CosmicGlowBlue else Color(0xFFEF5350)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(6.dp))
            Text(text = label, color = color, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun OverloadBar(probability: Double) {
    val barColor = when {
        probability < 0.3 -> CosmicGlowBlue
        probability < 0.6 -> Color(0xFFFFA726)
        else -> Color(0xFFEF5350)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Text(text = "Xác suất quá tải", color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.weight(1f))
                Text(
                    text = "${(probability * 100).toInt()}%",
                    color = barColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(GlassWhite)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = probability.toFloat().coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(barColor)
                )
            }
        }
    }
}

@Composable
private fun HealthGauge(score: Double) {
    val gaugeColor = when {
        score >= 0.7 -> CosmicGlowBlue
        score >= 0.4 -> Color(0xFFFFA726)
        else -> Color(0xFFEF5350)
    }
    val label = when {
        score >= 0.7 -> "Tốt"
        score >= 0.4 -> "Trung bình"
        else -> "Yếu"
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Sức khỏe học tập", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(gaugeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(score * 100).toInt()}",
                            color = gaugeColor,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = "/100", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(text = label, color = gaugeColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun InsightCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Text(text = "•", color = CosmicNebulaPurple, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Text(text = text, color = TextPrimary, fontSize = 13.sp)
        }
    }
}

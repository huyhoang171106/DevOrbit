package vn.edu.uit.devorbit.mobile.ui.screen.analytics

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.model.domain.WorkloadProfile
import vn.edu.uit.devorbit.mobile.model.domain.WorkloadStatus
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.*

private fun WorkloadStatus.toVietnamese(): String = when (this) {
    WorkloadStatus.UNDERLOADED -> "Nhàn"
    WorkloadStatus.BALANCED -> "Cân bằng"
    WorkloadStatus.HEAVY -> "Nặng"
    WorkloadStatus.CRITICAL -> "Quá tải"
}

private fun WorkloadStatus.color(): Color = when (this) {
    WorkloadStatus.UNDERLOADED -> CosmicGlowBlue
    WorkloadStatus.BALANCED -> CosmicNebulaPurple
    WorkloadStatus.HEAVY -> Color(0xFFFFB74D)
    WorkloadStatus.CRITICAL -> Color(0xFFFF6B6B)
}

private fun hoursBarColor(hours: Double, maxHours: Double): Color {
    val ratio = if (maxHours > 0) hours / maxHours else 0.0
    return when {
        ratio > 0.8 -> Color(0xFFFF6B6B)
        ratio > 0.5 -> Color(0xFFFFB74D)
        ratio > 0.2 -> CosmicNebulaPurple
        else -> CosmicGlowBlue
    }
}

@Composable
fun WorkloadScreen(workload: WorkloadProfile?) {
    CosmicBackground {
        if (workload == null) {
            EmptyState()
            return@CosmicBackground
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Khối lượng công việc",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatusBanner(workload)

            Spacer(modifier = Modifier.height(16.dp))

            val maxHours = workload.weeks.maxOfOrNull { it.totalHours } ?: 1.0

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(workload.weeks, key = { it.weekNumber }) { week ->
                    WeekCard(
                        week = week,
                        maxHours = maxHours,
                        isPeak = week.weekNumber in workload.peakWeeks
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Chưa có dữ liệu khối lượng công việc",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatusBanner(workload: WorkloadProfile) {
    val statusColor = workload.overallStatus.color()
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Trạng thái tổng thể",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary
                )
                Text(
                    text = workload.overallStatus.toVietnamese(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
            Surface(
                color = statusColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${workload.weeks.size} tuần",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun WeekCard(
    week: vn.edu.uit.devorbit.mobile.model.domain.WeekWorkload,
    maxHours: Double,
    isPeak: Boolean
) {
    val barColor = hoursBarColor(week.totalHours, maxHours)
    val fraction = if (maxHours > 0) (week.totalHours / maxHours).toFloat().coerceIn(0f, 1f) else 0f
    val bgColor = when {
        isPeak && week.totalHours / maxHours > 0.8 -> Color(0xFFFF6B6B).copy(alpha = 0.10f)
        isPeak -> Color(0xFFFFB74D).copy(alpha = 0.08f)
        else -> Color.Transparent
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isPeak) Modifier.background(bgColor, RoundedCornerShape(24.dp))
                else Modifier
            )
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = week.label.ifEmpty { "Tuần ${week.weekNumber}" },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${week.taskCount} nhiệm vụ",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Text(
                    text = "${"%.1f".format(week.totalHours)}h",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPeak) Color(0xFFFF6B6B) else TextPrimary
                )
            }

            // Simplified bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(GlassWhite)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction)
                        .clip(RoundedCornerShape(4.dp))
                        .background(barColor)
                )
            }

            if (isPeak) {
                Text(
                    text = "Peak — tuần cao điểm",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFFB74D),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

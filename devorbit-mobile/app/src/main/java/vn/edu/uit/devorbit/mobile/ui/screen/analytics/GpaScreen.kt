package vn.edu.uit.devorbit.mobile.ui.screen.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.model.domain.GpaImpact
import vn.edu.uit.devorbit.mobile.model.domain.SubjectGpaImpact
import vn.edu.uit.devorbit.mobile.ui.components.CosmicBackground
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun GpaScreen(
    gpaImpact: GpaImpact,
    onEditGpa: () -> Unit = {}
) {
    CosmicBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Tác động GPA",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Current vs Forecast
            item {
                GpaComparisonCard(
                    current = gpaImpact.currentGpa,
                    forecast = gpaImpact.forecastGpa,
                    onEditGpa = onEditGpa
                )
            }

            // Subject breakdown
            item {
                Text(
                    text = "Chi tiết môn học",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            items(gpaImpact.bySubject) { subject ->
                SubjectImpactCard(subject)
            }

            // Recommendations
            if (gpaImpact.recommendations.isNotEmpty()) {
                item {
                    Text(
                        text = "Gợi ý cải thiện",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(gpaImpact.recommendations) { rec ->
                    RecommendationCard(rec)
                }
            }
        }
    }
}

@Composable
private fun GpaComparisonCard(
    current: Double,
    forecast: Double,
    onEditGpa: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Current GPA
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Hiện tại",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "%.2f".format(current),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (current >= 3.0) CosmicGlowBlue
                        else if (current >= 2.0) CosmicNebulaPurple
                        else Color(0xFFFF6B6B)
                    )
                }

                // Arrow
                Text(
                    text = "→",
                    fontSize = 32.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 20.dp)
                )

                // Forecast GPA
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Dự báo",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "%.2f".format(forecast),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (forecast >= current) CosmicGlowBlue
                        else Color(0xFFFF6B6B)
                    )
                }
            }

            // Edit GPA button
            OutlinedButton(
                onClick = onEditGpa,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Chỉnh sửa GPA", color = CosmicNebulaPurple)
            }
        }
    }
}

@Composable
private fun SubjectImpactCard(subject: SubjectGpaImpact) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subject.subjectName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${subject.credits} tín chỉ",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            // Weight bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Trọng số: ${"%.1f".format(subject.weight * 100)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                if (subject.currentScore != null) {
                    Text(
                        text = "Điểm: ${"%.1f".format(subject.currentScore)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            }

            // Potential drop
            if (subject.potentialDrop > 0) {
                val dropColor = when {
                    subject.potentialDrop > 0.3 -> Color(0xFFFF6B6B)
                    subject.potentialDrop > 0.1 -> Color(0xFFFFB74D)
                    else -> CosmicGlowBlue
                }
                Surface(
                    color = dropColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Nguy cơ tụt: ${"%.1f".format(subject.potentialDrop * 100)}%",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = dropColor
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(text: String) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "→",
                fontSize = 16.sp,
                color = CosmicNebulaPurple,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
    }
}

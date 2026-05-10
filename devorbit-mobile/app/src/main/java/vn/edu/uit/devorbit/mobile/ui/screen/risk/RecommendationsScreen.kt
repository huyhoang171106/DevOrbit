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
import vn.edu.uit.devorbit.mobile.model.domain.RecommendationCategory
import vn.edu.uit.devorbit.mobile.model.domain.StudyRecommendation
import vn.edu.uit.devorbit.mobile.ui.theme.*

private data class CategoryStyle(
    val icon: String,
    val color: Color,
    val label: String
)

private fun categoryStyle(category: RecommendationCategory): CategoryStyle = when (category) {
    RecommendationCategory.PREREQUISITE_REVIEW -> CategoryStyle("P", CosmicGlowPurple, "Tiên quyết")
    RecommendationCategory.FOCUS_SUBJECT -> CategoryStyle("F", CosmicStarBlue, "Trọng tâm")
    RecommendationCategory.TIME_MANAGEMENT -> CategoryStyle("T", Color(0xFFFFA726), "Thời gian")
    RecommendationCategory.WORKLOAD_REDUCTION -> CategoryStyle("W", Color(0xFFFF7043), "Tải việc")
    RecommendationCategory.CONSISTENCY_IMPROVEMENT -> CategoryStyle("C", CosmicGlowBlue, "Đều đặn")
    RecommendationCategory.BREAK_SUGGESTION -> CategoryStyle("B", CosmicNebulaPurple, "Nghỉ ngơi")
}

@Composable
fun RecommendationsScreen(recommendations: List<StudyRecommendation>) {
    val sorted = recommendations.sortedBy { it.priority }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicDeepSpace)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { RecommendationHeader() }
        item {
            Text(
                text = "Tất cả khuyến nghị",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        items(sorted) { rec ->
            RecommendationCard(rec)
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun RecommendationHeader() {
    Text(
        text = "Khuyến nghị học tập",
        color = TextPrimary,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = "Các đề xuất được sắp xếp theo mức độ ưu tiên",
        color = TextSecondary,
        fontSize = 14.sp
    )
}

@Composable
private fun RecommendationCard(recommendation: StudyRecommendation) {
    val style = categoryStyle(recommendation.category)
    val priorityColor = when (recommendation.priority) {
        1 -> Color(0xFFEF5350)
        2 -> Color(0xFFFF7043)
        3 -> Color(0xFFFFA726)
        else -> TextSecondary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(style.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = style.icon, color = style.color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(priorityColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "#${recommendation.priority}",
                            color = priorityColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(style.color.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = style.label,
                            color = style.color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(text = recommendation.title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(Modifier.height(4.dp))
                Text(text = recommendation.description, color = TextSecondary, fontSize = 13.sp)
            }
        }
    }
}

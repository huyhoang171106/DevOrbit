package vn.edu.uit.devorbit.mobile.ui.screen.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.model.domain.BreakdownStep
import vn.edu.uit.devorbit.mobile.model.domain.TaskBreakdown
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.*

@Composable
fun TaskBreakdownScreen(
    breakdown: TaskBreakdown?,
    goal: String,
    difficulty: String,
    onGoalChange: (String) -> Unit,
    onDifficultyChange: (String) -> Unit,
    onBreakdown: () -> Unit,
    onCreateTasks: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicDeepSpace)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Phân tích nhiệm vụ",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Goal input
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text("Mục tiêu", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = goal,
                    onValueChange = onGoalChange,
                    placeholder = { Text("VD: Học xong chương 3 môn CSDL", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CosmicNebulaPurple,
                        unfocusedBorderColor = GlassBorder,
                        cursorColor = CosmicNebulaPurple
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Độ khó", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("easy", "medium", "hard").forEach { level ->
                        DifficultyChip(
                            label = when (level) {
                                "easy" -> "Dễ"
                                "hard" -> "Khó"
                                else -> "Trung bình"
                            },
                            selected = difficulty == level,
                            onClick = { onDifficultyChange(level) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onBreakdown,
                    enabled = goal.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicNebulaPurple,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Phân tích")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (breakdown != null) {
            // Breakdown results
            Text(
                text = "Các bước thực hiện",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                itemsIndexed(breakdown.steps) { index, step ->
                    BreakdownStepCard(index = index, step = step)
                }

                item {
                    // Total summary
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tổng thời gian ước tính",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                            Text(
                                text = "${breakdown.totalEstimatedMinutes} phút",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicGlowBlue
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onCreateTasks,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CosmicGlowBlue.copy(alpha = 0.15f),
                            contentColor = CosmicGlowBlue
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tạo task từ breakdown")
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) CosmicNebulaPurple.copy(alpha = 0.3f) else GlassWhite,
        border = if (selected) null else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) CosmicNebulaPurple else TextSecondary
        )
    }
}

@Composable
private fun BreakdownStepCard(index: Int, step: BreakdownStep) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            // Step number
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CosmicNebulaPurple.copy(alpha = 0.25f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${index + 1}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicNebulaPurple
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = step.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (step.isNextAction) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CosmicStarBlue.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Flag,
                                    contentDescription = null,
                                    tint = CosmicStarBlue,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Next action",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CosmicStarBlue
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${step.estimatedMinutes} phút",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    DifficultyBadge(step.difficulty)
                }
            }
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: String) {
    val (label, color) = when (difficulty.lowercase()) {
        "easy" -> "Dễ" to CosmicGlowBlue
        "hard" -> "Khó" to CosmicGlowPurple
        else -> "TB" to Color(0xFFFBC02D)
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

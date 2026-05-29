package vn.edu.uit.devorbit.mobile.ui.screen.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.domain.model.BreakdownStep
import vn.edu.uit.devorbit.mobile.domain.model.TaskBreakdown
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
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Phân tích nhiệm vụ",
            style = CosmicTheme.typography.display,
            color = CosmicTheme.colors.textPrimary,
            modifier = Modifier.padding(top = 12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Goal input card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = CosmicTheme.colors.nebula,
            border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Mục tiêu", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = goal,
                    onValueChange = onGoalChange,
                    placeholder = { Text("VD: Học xong chương 3 môn CSDL", color = CosmicTheme.colors.textTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CosmicTheme.colors.textPrimary,
                        unfocusedTextColor = CosmicTheme.colors.textPrimary,
                        focusedBorderColor = CosmicTheme.colors.plasma,
                        unfocusedBorderColor = CosmicTheme.colors.glassBorder,
                        cursorColor = CosmicTheme.colors.plasma
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Độ khó", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("easy" to "Dễ", "medium" to "Trung bình", "hard" to "Khó").forEach { (level, label) ->
                        DifficultyChip(
                            label = label,
                            selected = difficulty == level,
                            onClick = { onDifficultyChange(level) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBreakdown,
                    enabled = goal.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicTheme.colors.plasma,
                        contentColor = CosmicTheme.colors.void
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text("Phân tích", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (breakdown != null) {
            Text(
                text = "Các bước thực hiện",
                style = CosmicTheme.typography.command,
                color = CosmicTheme.colors.textTertiary
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                itemsIndexed(breakdown.steps) { index, step ->
                    BreakdownStepCard(index = index, step = step)
                }

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CosmicTheme.colors.nebula,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tổng thời gian ước tính",
                                style = CosmicTheme.typography.body,
                                color = CosmicTheme.colors.textSecondary
                            )
                            Text(
                                text = "${breakdown.totalEstimatedMinutes} phút",
                                style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                                color = CosmicTheme.colors.aurora
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = onCreateTasks,
                        modifier = Modifier.fillMaxWidth(),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.plasma.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = CosmicTheme.colors.plasma
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Tạo task từ breakdown",
                            color = CosmicTheme.colors.plasma,
                            fontWeight = FontWeight.SemiBold
                        )
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
        shape = RoundedCornerShape(8.dp),
        color = if (selected) CosmicTheme.colors.plasma.copy(alpha = 0.15f) else CosmicTheme.colors.nebula,
        border = if (!selected) androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = CosmicTheme.typography.label.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
            color = if (selected) CosmicTheme.colors.plasma else CosmicTheme.colors.textTertiary
        )
    }
}

@Composable
private fun BreakdownStepCard(index: Int, step: BreakdownStep) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Step number
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CosmicTheme.colors.plasma.copy(alpha = 0.12f),
                modifier = Modifier.size(30.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${index + 1}",
                        style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                        color = CosmicTheme.colors.plasma
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = step.title,
                        style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Medium),
                        color = CosmicTheme.colors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (step.isNextAction) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CosmicTheme.colors.plasma.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = CosmicTheme.colors.plasma,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Tiếp theo",
                                    style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                                    color = CosmicTheme.colors.plasma
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${step.estimatedMinutes} phút",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textTertiary
                    )
                    DifficultySmallBadge(difficulty = step.difficulty)
                }
            }
        }
    }
}

@Composable
private fun DifficultySmallBadge(difficulty: String) {
    val (label, color) = when (difficulty.lowercase()) {
        "easy" -> "Dễ" to CosmicTheme.colors.aurora
        "hard" -> "Khó" to CosmicTheme.colors.supernova
        else -> "TB" to CosmicTheme.colors.plasma
    }
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
            style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

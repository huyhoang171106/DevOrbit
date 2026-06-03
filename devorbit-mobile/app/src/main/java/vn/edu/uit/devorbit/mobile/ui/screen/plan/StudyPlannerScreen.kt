package vn.edu.uit.devorbit.mobile.ui.screen.plan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.domain.model.StudyItem
import vn.edu.uit.devorbit.mobile.domain.model.StudyPhase
import vn.edu.uit.devorbit.mobile.domain.model.StudyPlan
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@Composable
fun StudyPlannerScreen(
    studyPlan: StudyPlan?,
    loading: Boolean = false,
    error: String? = null,
    onGeneratePlan: (String, String) -> Unit,
    onToggleItem: (String) -> Unit,
    onBreakdownTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showGenerateDialog by remember { mutableStateOf(false) }
    var learningGoals by remember { mutableStateOf("") }
    var careerPath by remember { mutableStateOf("") }

    if (showGenerateDialog) {
        AlertDialog(
            onDismissRequest = { showGenerateDialog = false },
            title = { Text("Tao lo trinh") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = learningGoals,
                        onValueChange = { learningGoals = it },
                        label = { Text("Muc tieu hoc tap") },
                        minLines = 2
                    )
                    OutlinedTextField(
                        value = careerPath,
                        onValueChange = { careerPath = it },
                        label = { Text("Dinh huong nghe nghiep") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = learningGoals.isNotBlank() && careerPath.isNotBlank(),
                    onClick = {
                        onGeneratePlan(learningGoals, careerPath)
                        showGenerateDialog = false
                    }
                ) {
                    Text("Tao")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGenerateDialog = false }) {
                    Text("Huy")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ke hoach hoc tap",
                style = CosmicTheme.typography.display,
                color = CosmicTheme.colors.textPrimary
            )
            Button(
                onClick = { showGenerateDialog = true },
                enabled = !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CosmicTheme.colors.plasma,
                    contentColor = CosmicTheme.colors.void
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(if (loading) "Dang tao..." else "Tao ke hoach", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        error?.let {
            ErrorPanel(it)
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (studyPlan == null) {
            EmptyPlanState()
        } else {
            PlanSummary(studyPlan)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(studyPlan.phases) { phase ->
                    PhaseCard(
                        phase = phase,
                        onToggleItem = onToggleItem,
                        onBreakdownTask = onBreakdownTask
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorPanel(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CosmicTheme.colors.supernova.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.supernova.copy(alpha = 0.35f))
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(14.dp),
            style = CosmicTheme.typography.label,
            color = CosmicTheme.colors.textPrimary
        )
    }
}

@Composable
private fun EmptyPlanState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = CosmicTheme.colors.nebula,
            border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "Chua co ke hoach nao",
                    style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = CosmicTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nhan Tao ke hoach de sinh lo trinh hoc tap bang AI",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textTertiary
                )
            }
        }
    }
}

@Composable
private fun PlanSummary(studyPlan: StudyPlan) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${studyPlan.phases.size}",
                    style = CosmicTheme.typography.metric.copy(fontSize = 28.sp),
                    color = CosmicTheme.colors.plasma
                )
                Text("Giai doan", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${studyPlan.totalHours.toInt()}h",
                    style = CosmicTheme.typography.metric.copy(fontSize = 28.sp),
                    color = CosmicTheme.colors.aurora
                )
                Text("Tong thoi gian", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
            }
        }
    }
}

@Composable
private fun PhaseCard(
    phase: StudyPhase,
    onToggleItem: (String) -> Unit,
    onBreakdownTask: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(14.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = phase.title,
                        style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = CosmicTheme.colors.textPrimary
                    )
                    Text(
                        text = "Ngay ${phase.startDay} - Ngay ${phase.endDay}",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textTertiary
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = CosmicTheme.colors.textTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val completedCount = phase.items.count { it.completed }
            val totalCount = phase.items.size
            val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = CosmicTheme.colors.aurora,
                trackColor = CosmicTheme.colors.glassBorder,
            )

            Spacer(modifier = Modifier.height(10.dp))

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    phase.items.forEach { item ->
                        StudyItemRow(
                            item = item,
                            onToggle = { onToggleItem(item.id) },
                            onBreakdown = { onBreakdownTask(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyItemRow(
    item: StudyItem,
    onToggle: () -> Unit,
    onBreakdown: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (item.completed) CosmicTheme.colors.aurora else CosmicTheme.colors.textTertiary.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = CosmicTheme.typography.body.copy(
                    fontWeight = if (item.completed) FontWeight.Normal else FontWeight.Medium
                ),
                color = if (item.completed) CosmicTheme.colors.textTertiary else CosmicTheme.colors.textPrimary
            )
            Text(
                text = "${item.estimatedHours}h",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary
            )
        }
        DifficultyBadge(difficulty = item.difficulty)
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(
            onClick = onBreakdown,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text("Chi tiet", style = CosmicTheme.typography.label, color = CosmicTheme.colors.plasma)
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: String) {
    val (label, color) = when (difficulty.lowercase()) {
        "easy" -> "De" to CosmicTheme.colors.aurora
        "hard" -> "Kho" to CosmicTheme.colors.supernova
        else -> "TB" to CosmicTheme.colors.plasma
    }
    Surface(shape = RoundedCornerShape(6.dp), color = color.copy(alpha = 0.12f)) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

package vn.edu.uit.devorbit.mobile.ui.screen.risk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.model.domain.SimulationResult
import vn.edu.uit.devorbit.mobile.ui.theme.*

data class CourseOption(
    val id: Long,
    val name: String
)

@Composable
fun SimulationScreen(
    courses: List<CourseOption>,
    simulationResult: SimulationResult?,
    onSimulate: (Long) -> Unit
) {
    var selectedCourseId by remember { mutableLongStateOf(-1L) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicDeepSpace)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SimulationHeader() }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GlassWhite),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Chọn môn học", color = TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    CourseDropdown(
                        courses = courses,
                        selectedId = selectedCourseId,
                        onSelected = { selectedCourseId = it }
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (selectedCourseId > 0) onSimulate(selectedCourseId)
                        },
                        enabled = selectedCourseId > 0,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF5350).copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Mô phỏng", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        if (simulationResult != null) {
            item { SimulationResultHeader() }
            item { FailedCourseCard(simulationResult.failedSubjectName) }
            if (simulationResult.blockedSubjects.isNotEmpty()) {
                item {
                    Text(text = "Môn bị chặn", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                items(simulationResult.blockedSubjects) { blocked ->
                    BlockedSubjectCard(blocked.name, blocked.reason)
                }
            }
            item { GraduationDelayCard(simulationResult.graduationDelay) }
            if (simulationResult.timelineImpact.isNotEmpty()) {
                item {
                    Text(text = "Tác động theo học kỳ", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                items(simulationResult.timelineImpact) { entry ->
                    TimelineImpactCard(entry.semesterName, entry.subjects, entry.note)
                }
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun SimulationHeader() {
    Text(
        text = "Mô phỏng: Nếu tôi trượt",
        color = TextPrimary,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = "Xem tác động dây chuyền nếu bạn trượt một môn",
        color = TextSecondary,
        fontSize = 14.sp
    )
}

@Composable
private fun CourseDropdown(
    courses: List<CourseOption>,
    selectedId: Long,
    onSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = courses.find { it.id == selectedId }?.name ?: "Chọn môn học"

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
        ) {
            Text(text = selectedName, modifier = Modifier.weight(1f))
            Text(text = if (expanded) "▲" else "▼", fontSize = 12.sp)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            courses.forEach { course ->
                DropdownMenuItem(
                    text = { Text(course.name) },
                    onClick = {
                        onSelected(course.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SimulationResultHeader() {
    Text(
        text = "Kết quả mô phỏng",
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun FailedCourseCard(name: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEF5350).copy(alpha = 0.15f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEF5350).copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✕", color = Color(0xFFEF5350), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(text = "Môn đã trượt", color = TextSecondary, fontSize = 12.sp)
                Text(text = name, color = Color(0xFFEF5350), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BlockedSubjectCard(name: String, reason: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFF7043).copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "!", color = Color(0xFFFF7043), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(text = name, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(text = reason, color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun GraduationDelayCard(delay: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Chậm tốt nghiệp", color = TextSecondary, fontSize = 13.sp)
                Text(
                    text = if (delay > 0) "$delay học kỳ" else "Không bị chậm",
                    color = if (delay > 0) Color(0xFFFF7043) else CosmicGlowBlue,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TimelineImpactCard(semesterName: String, subjects: List<String>, note: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = semesterName, color = CosmicNebulaPurple, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(Modifier.height(6.dp))
            subjects.forEach { subject ->
                Text(text = "• $subject", color = TextPrimary, fontSize = 13.sp)
            }
            if (note.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(text = note, color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

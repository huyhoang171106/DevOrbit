package vn.edu.uit.devorbit.mobile.ui.screen.gpa

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.domain.gpa.GpaCalculator
import vn.edu.uit.devorbit.mobile.domain.gpa.GpaCourseInput
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

data class GpaCourseRowState(
    val id: String,
    val name: String = "",
    val credits: String = "",
    val grade: String = ""
)

data class GpaScreenState(
    val courses: List<GpaCourseRowState>,
    val currentGpa: String = "",
    val completedCredits: String = "",
    val targetGpa: String = "",
    val nextId: Int
) {
    fun addCourse(): GpaScreenState {
        return copy(
            courses = courses + GpaCourseRowState(id = nextId.toString()),
            nextId = nextId + 1
        )
    }

    fun removeCourse(id: String): GpaScreenState {
        if (courses.size == 1) return this
        return copy(courses = courses.filterNot { it.id == id })
    }

    fun updateCourse(id: String, transform: (GpaCourseRowState) -> GpaCourseRowState): GpaScreenState {
        return copy(courses = courses.map { if (it.id == id) transform(it) else it })
    }

    companion object {
        fun initial(): GpaScreenState = GpaScreenState(
            courses = listOf(
                GpaCourseRowState(id = "1"),
                GpaCourseRowState(id = "2")
            ),
            nextId = 3
        )
    }
}

@Composable
fun GpaScreen() {
    var state by remember { mutableStateOf(GpaScreenState.initial()) }
    val semester = remember(state.courses) {
        GpaCalculator.semester(
            state.courses.map {
                GpaCourseInput(name = it.name, credits = it.credits, grade = it.grade)
            }
        )
    }
    val cumulative = remember(state.currentGpa, state.completedCredits, semester) {
        GpaCalculator.projectCumulative(
            currentGpa = state.currentGpa,
            completedCredits = state.completedCredits,
            semesterGpa = semester.gpa,
            semesterCredits = semester.totalCredits
        )
    }
    val target = remember(state.currentGpa, state.completedCredits, state.targetGpa, semester.totalCredits) {
        GpaCalculator.requiredSemesterGpa(
            currentGpa = state.currentGpa,
            completedCredits = state.completedCredits,
            targetGpa = state.targetGpa,
            semesterCredits = semester.totalCredits
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "GPA",
                style = CosmicTheme.typography.display,
                color = CosmicTheme.colors.textPrimary
            )
            Text(
                text = "Tinh GPA hoc ky va uoc luong GPA tich luy",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary
            )
        }

        items(state.courses, key = { it.id }) { course ->
            GpaCourseRow(
                row = course,
                canRemove = state.courses.size > 1,
                onNameChange = { value ->
                    state = state.updateCourse(course.id) { it.copy(name = value) }
                },
                onCreditsChange = { value ->
                    state = state.updateCourse(course.id) { it.copy(credits = value) }
                },
                onGradeChange = { value ->
                    state = state.updateCourse(course.id) { it.copy(grade = value) }
                },
                onRemove = {
                    state = state.removeCourse(course.id)
                }
            )
        }

        item {
            OutlinedButton(
                onClick = { state = state.addCourse() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Them mon")
            }
        }

        item {
            SummarySection(
                title = "GPA hoc ky",
                rows = listOf(
                    "GPA" to if (semester.valid) formatGpa(semester.gpa) else "--",
                    "Tin chi hop le" to semester.totalCredits.toString(),
                    "Dong bo qua" to semester.ignoredRows.toString()
                )
            )
        }

        item {
            ProjectionInputs(
                currentGpa = state.currentGpa,
                completedCredits = state.completedCredits,
                targetGpa = state.targetGpa,
                onCurrentGpaChange = { state = state.copy(currentGpa = it) },
                onCompletedCreditsChange = { state = state.copy(completedCredits = it) },
                onTargetGpaChange = { state = state.copy(targetGpa = it) }
            )
        }

        item {
            SummarySection(
                title = "Uoc luong tich luy",
                rows = listOf(
                    "GPA sau ky nay" to if (cumulative.valid) formatGpa(cumulative.projectedGpa) else "--",
                    "Tong tin chi" to if (cumulative.valid) cumulative.totalCredits.toString() else "--"
                )
            )
        }

        item {
            SummarySection(
                title = "Muc tieu GPA",
                rows = listOf(
                    "GPA ky nay can dat" to if (target.valid) formatGpa(target.requiredGpa) else "--",
                    "Trang thai" to when {
                        !target.valid -> "--"
                        target.infeasible -> "Vuot qua 10.0"
                        target.requiredGpa < 0.0 -> "Da dat muc tieu"
                        else -> "Co the dat"
                    }
                )
            )
        }
    }
}

@Composable
private fun GpaCourseRow(
    row: GpaCourseRowState,
    canRemove: Boolean,
    onNameChange: (String) -> Unit,
    onCreditsChange: (String) -> Unit,
    onGradeChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Mon ${row.id}",
                    style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                    color = CosmicTheme.colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRemove, enabled = canRemove) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = "Xoa mon",
                        tint = if (canRemove) CosmicTheme.colors.supernova else CosmicTheme.colors.textTertiary
                    )
                }
            }

            OutlinedTextField(
                value = row.name,
                onValueChange = onNameChange,
                label = { Text("Ten mon") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = gpaFieldColors()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = row.credits,
                    onValueChange = onCreditsChange,
                    label = { Text("Tin chi") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = gpaFieldColors()
                )
                OutlinedTextField(
                    value = row.grade,
                    onValueChange = onGradeChange,
                    label = { Text("Diem 10") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    colors = gpaFieldColors()
                )
            }
        }
    }
}

@Composable
private fun ProjectionInputs(
    currentGpa: String,
    completedCredits: String,
    targetGpa: String,
    onCurrentGpaChange: (String) -> Unit,
    onCompletedCreditsChange: (String) -> Unit,
    onTargetGpaChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "GPA tich luy",
                style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                color = CosmicTheme.colors.textPrimary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = currentGpa,
                    onValueChange = onCurrentGpaChange,
                    label = { Text("GPA hien tai") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    colors = gpaFieldColors()
                )
                OutlinedTextField(
                    value = completedCredits,
                    onValueChange = onCompletedCreditsChange,
                    label = { Text("Tin chi da co") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = gpaFieldColors()
                )
            }
            OutlinedTextField(
                value = targetGpa,
                onValueChange = onTargetGpaChange,
                label = { Text("GPA muc tieu") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = gpaFieldColors()
            )
        }
    }
}

@Composable
private fun SummarySection(
    title: String,
    rows: List<Pair<String, String>>
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = title,
                style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                color = CosmicTheme.colors.textPrimary
            )
            rows.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label, style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                    Text(value, style = CosmicTheme.typography.body, color = CosmicTheme.colors.plasma)
                }
            }
        }
    }
}

@Composable
private fun gpaFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CosmicTheme.colors.plasma,
    unfocusedBorderColor = CosmicTheme.colors.glassBorder,
    cursorColor = CosmicTheme.colors.plasma,
    focusedTextColor = CosmicTheme.colors.textPrimary,
    unfocusedTextColor = CosmicTheme.colors.textPrimary
)

private fun formatGpa(value: Double): String = "%.2f".format(value)

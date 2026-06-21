package vn.edu.uit.devorbit.mobile.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.TaskEntity
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.DashboardViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.WeekDay

@Composable
fun DashboardScreen(
    onNavigateToCourse: (Long) -> Unit = {},
    onNavigateToPlan: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadAllCourses()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = 24.dp, bottom = 100.dp
        )
    ) {
        item {
            GreetingSection(
                greeting = state.greeting,
                dateText = state.dateText,
                studyHours = state.studyHoursToday,
                streakCount = state.streakCount
            )
        }

        item {
            SectionLabel("Nhiệm vụ hôm nay")
        }

        item {
            Text(
                text = "Hôm nay: ${state.totalTaskCount} nhiệm vụ, ${state.completedTaskCount} hoàn thành",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (state.sortedTasks.isEmpty()) {
            item {
                EmptyTaskState(onNavigateToPlan = onNavigateToPlan)
            }
        } else {
            items(state.sortedTasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onClick = { onNavigateToPlan() }
                )
            }
        }

        item {
            SemesterCoursesSection(
                courses = state.semesterCourses,
                allCourses = state.allCourses,
                onAddCourse = { viewModel.addSemesterCourse(it) },
                onRemoveCourse = { viewModel.removeSemesterCourse(it) },
                onCourseClick = onNavigateToCourse
            )
        }

        item {
            StreakSection(
                weekDates = state.weekDates,
                currentWeekOffset = state.currentWeekOffset,
                maxWeekOffset = state.maxWeekOffset,
                onPrevWeek = { viewModel.navigateWeek(1) },
                onNextWeek = { viewModel.navigateWeek(-1) }
            )
        }
    }
}

// ── Section 1: Greeting ──────────────────────────────────────────

@Composable
private fun GreetingSection(
    greeting: String,
    dateText: String,
    studyHours: Int,
    streakCount: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = greeting,
                    style = CosmicTheme.typography.body.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        lineHeight = 26.sp
                    ),
                    color = CosmicTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = dateText,
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textSecondary
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (studyHours >= 1) {
                    StatBadge(
                        value = "${studyHours}h",
                        label = "Thời gian học hôm nay",
                        color = CosmicTheme.colors.plasma
                    )
                }
                StatBadge(
                    value = "$streakCount",
                    label = "Streak",
                    color = CosmicTheme.colors.aurora
                )
            }
        }
    }
}

@Composable
private fun StatBadge(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = CosmicTheme.typography.metric.copy(fontSize = 24.sp),
            color = color
        )
        Text(
            text = label,
            style = CosmicTheme.typography.label,
            color = CosmicTheme.colors.textTertiary
        )
    }
}

// ── Section 2: Semester Courses ──────────────────────────────────

@Composable
private fun SemesterCoursesSection(
    courses: List<CourseEntity>,
    allCourses: List<CourseEntity>,
    onAddCourse: (Long) -> Unit,
    onRemoveCourse: (Long) -> Unit,
    onCourseClick: (Long) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Môn học kỳ này",
                    style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = CosmicTheme.colors.textPrimary
                )
                TextButton(onClick = { showDialog = true }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = CosmicTheme.colors.plasma
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Thêm", color = CosmicTheme.colors.plasma)
                }
            }
            if (courses.isEmpty()) {
                Text(
                    text = "Bạn chưa có môn học nào",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textTertiary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "Nhấn + Thêm để bắt đầu",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.plasma,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            } else {
                courses.forEach { course ->
                    CourseChip(
                        course = course,
                        onRemove = { onRemoveCourse(course.id) },
                        onClick = { onCourseClick(course.id) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddCourseDialog(
            allCourses = allCourses,
            selectedIds = courses.map { it.id }.toSet(),
            onAdd = { id ->
                onAddCourse(id)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun CourseChip(
    course: CourseEntity,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = CosmicTheme.colors.plasma.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.tenMH,
                    style = CosmicTheme.typography.body,
                    color = CosmicTheme.colors.textPrimary
                )
                Text(
                    text = course.maMH,
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textSecondary
                )
            }
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Xoá",
                    tint = CosmicTheme.colors.textTertiary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun AddCourseDialog(
    allCourses: List<CourseEntity>,
    selectedIds: Set<Long>,
    onAdd: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember { mutableStateOf<Long?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredCourses = remember(allCourses, selectedIds, searchQuery) {
        allCourses.filter { it.id !in selectedIds }
            .filter {
                searchQuery.isBlank() ||
                it.maMH.contains(searchQuery, ignoreCase = true) ||
                it.tenMH.contains(searchQuery, ignoreCase = true)
            }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Thêm môn học", style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            if (allCourses.isEmpty()) {
                Text(
                    "Không có môn học nào để thêm",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textTertiary
                )
            } else {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Tìm môn học...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null,
                                tint = CosmicTheme.colors.textTertiary)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            cursorColor = CosmicTheme.colors.plasma,
                            focusedTextColor = CosmicTheme.colors.textPrimary,
                            unfocusedTextColor = CosmicTheme.colors.textPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (filteredCourses.isEmpty() && searchQuery.isNotBlank()) {
                        Text(
                            "Không tìm thấy môn học",
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.textTertiary,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 300.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(filteredCourses) { course ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selected = course.id },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selected == course.id)
                                        CosmicTheme.colors.plasma.copy(alpha = 0.12f)
                                    else Color.Transparent
                                ) {
                                    Text(
                                        text = "${course.maMH} - ${course.tenMH}",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        style = CosmicTheme.typography.body,
                                        color = CosmicTheme.colors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { selected?.let(onAdd) },
                enabled = selected != null
            ) {
                Text("Thêm", color = CosmicTheme.colors.plasma)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huỷ", color = CosmicTheme.colors.textSecondary)
            }
        }
    )
}

// ── Section 3: Streak ─────────────────────────────────────────────

@Composable
private fun StreakSection(
    weekDates: List<WeekDay>,
    currentWeekOffset: Int,
    maxWeekOffset: Int,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Streak",
                    style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = CosmicTheme.colors.textPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (currentWeekOffset < maxWeekOffset) {
                        IconButton(onClick = onPrevWeek, modifier = Modifier.size(28.dp)) {
                            Icon(
                                Icons.Default.KeyboardArrowLeft,
                                contentDescription = "Tuần trước",
                                tint = CosmicTheme.colors.textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    if (currentWeekOffset > 0) {
                        IconButton(onClick = onNextWeek, modifier = Modifier.size(28.dp)) {
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = "Tuần sau",
                                tint = CosmicTheme.colors.textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weekDates.forEach { day ->
                    DaySquare(day = day)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hãy xem 3 repositories để giữ chuỗi nhé!",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary
            )
        }
    }
}

@Composable
private fun DaySquare(day: WeekDay) {
    val color = when {
        day.activity == null -> Color.Transparent
        day.activity.tasksTotal == 0 -> Color.Transparent
        day.activity.tasksCompleted >= day.activity.tasksTotal -> Color(0xFF2E7D32).copy(alpha = 0.85f)
        day.activity.tasksCompleted > 0 -> Color(0xFFF9A825).copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    val hasActivity = day.activity != null && day.activity.tasksTotal > 0

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(36.dp)
    ) {
        Text(
            text = day.label,
            style = CosmicTheme.typography.label.copy(fontSize = 9.sp),
            color = CosmicTheme.colors.textTertiary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(color)
                .border(
                    1.dp,
                    when {
                        day.isToday -> CosmicTheme.colors.plasma
                        day.qualifiesForStreak -> Color(0xFF2E7D32).copy(alpha = 0.5f)
                        hasActivity -> Color.White.copy(alpha = 0.15f)
                        else -> Color.White.copy(alpha = 0.06f)
                    },
                    RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (day.qualifiesForStreak) {
                Text(text = "\uD83D\uDD25", fontSize = 12.sp)
            } else if (day.activity != null) {
                Text(
                    text = (day.activity.reposViewed.coerceAtMost(9)).toString(),
                    style = CosmicTheme.typography.label.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (day.activity.tasksCompleted > 0 || day.activity.reposViewed > 0)
                        Color.White else Color.Transparent
                )
            }
        }
    }
}

// ── Section 3: Tasks ─────────────────────────────────────────────

@Composable
private fun TaskCard(task: TaskEntity, onClick: () -> Unit) {
    val bgColor = if (task.completed)
        Color(0xFF2E7D32).copy(alpha = 0.08f)
    else
        Color(0xFFF9A825).copy(alpha = 0.08f)

    val borderColor = if (task.completed)
        Color(0xFF2E7D32).copy(alpha = 0.2f)
    else
        Color(0xFFF9A825).copy(alpha = 0.25f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        if (task.completed) Color(0xFF2E7D32) else Color.Transparent,
                        CircleShape
                    )
                    .border(
                        1.5.dp,
                        if (task.completed) Color(0xFF2E7D32) else CosmicTheme.colors.textTertiary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.completed) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = CosmicTheme.colors.void
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = task.title,
                style = CosmicTheme.typography.body,
                color = if (task.completed) CosmicTheme.colors.textTertiary else CosmicTheme.colors.textPrimary,
                textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun EmptyTaskState(onNavigateToPlan: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onNavigateToPlan),
        shape = RoundedCornerShape(12.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Không có nhiệm vụ nào",
                style = CosmicTheme.typography.body,
                color = CosmicTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Bấm vào đây để lập kế hoạch cho hôm nay",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.plasma,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title,
        style = CosmicTheme.typography.command,
        color = CosmicTheme.colors.textTertiary
    )
}

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.TechStackEntity
import vn.edu.uit.devorbit.mobile.domain.model.TaskItem

import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.DashboardViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.TaskFilter
import vn.edu.uit.devorbit.mobile.ui.viewmodel.WeekDay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigateToCourse: (Long) -> Unit = {},
    onNavigateToCreateTask: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filter by viewModel.taskFilter.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        val msg = state.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearError()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshDate()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAllCourses()
    }

    val tasksByDate = remember(state.sortedTasks) {
        state.sortedTasks
            .filter { it.deadline != null }
            .groupBy { task ->
                Instant.ofEpochMilli(task.deadline!!)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }
            .mapValues { it.value.size }
    }

    val selectedDate = state.selectedDate
    val dayTasks = remember(state.sortedTasks, selectedDate) {
        if (selectedDate != null) {
            state.sortedTasks.filter { task ->
                task.deadline != null && isTaskOnDate(task.deadline, selectedDate)
            }
        } else {
            emptyList()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            TaskFilterSection(
                selectedFilter = viewModel.taskFilter.collectAsStateWithLifecycle().value,
                onSelectFilter = { viewModel.setTaskFilter(it) },
                taskCount = state.totalTaskCount,
                completedCount = state.completedTaskCount,
                onNavigateToCreateTask = onNavigateToCreateTask
            )
        }

        if (filter == TaskFilter.WEEK) {
            item {
                WeekTaskGrid(
                    weekDates = state.weekDates,
                    selectedDate = selectedDate,
                    tasksByDate = tasksByDate,
                    onDayClick = { viewModel.selectDate(it) }
                )
            }
            if (selectedDate != null) {
                if (dayTasks.isEmpty()) {
                    item {
                        EmptyTaskState(onNavigateToCreateTask = onNavigateToCreateTask)
                    }
                } else {
                    items(dayTasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            filter = TaskFilter.TODAY,
                            onClick = { viewModel.toggleTask(task.id, !task.completed) }
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "Chọn một ngày để xem nhiệm vụ",
                        style = CosmicTheme.typography.body,
                        color = CosmicTheme.colors.textTertiary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        } else if (filter == TaskFilter.ALL) {
            item {
                MonthTaskGrid(
                    year = state.currentYear,
                    month = state.currentMonth,
                    tasksByDate = tasksByDate,
                    selectedDate = selectedDate,
                    onDateClick = { viewModel.selectDate(it) },
                    onNavigateMonth = { viewModel.navigateMonth(it) }
                )
            }
            if (selectedDate != null) {
                if (dayTasks.isEmpty()) {
                    item {
                        Text(
                            text = "Ngày ${selectedDate.substring(8)}/${selectedDate.substring(5, 7)} không có nhiệm vụ nào",
                            style = CosmicTheme.typography.body,
                            color = CosmicTheme.colors.textTertiary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                } else {
                    items(dayTasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            filter = TaskFilter.TODAY,
                            onClick = { viewModel.toggleTask(task.id, !task.completed) }
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "Chọn một ngày để xem nhiệm vụ",
                        style = CosmicTheme.typography.body,
                        color = CosmicTheme.colors.textTertiary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        } else if (state.sortedTasks.isEmpty()) {
            item {
                EmptyTaskState(onNavigateToCreateTask = onNavigateToCreateTask)
            }
        } else {
            items(state.sortedTasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    filter = filter,
                    onClick = { viewModel.toggleTask(task.id, !task.completed) }
                )
            }
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
            TechStackSection(
                techStacks = state.techStacks,
                allTechStacks = state.allTechStacks,
                onAddTechStack = { viewModel.addTechStack(it) },
                onRemoveTechStack = { viewModel.removeTechStack(it) }
            )
        }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp),
            snackbar = { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CosmicTheme.colors.nebula,
                    contentColor = CosmicTheme.colors.textPrimary
                )
            }
        )
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
                        label = "Thời gian học\nhôm nay",
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
        shape = RoundedCornerShape(20.dp),
        containerColor = CosmicTheme.colors.nebula,
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
            Button(
                onClick = { selected?.let(onAdd) },
                enabled = selected != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                    contentColor = CosmicTheme.colors.plasma
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Thêm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
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

// ── Section 4: Tech Stack ─────────────────────────────────────────

@Composable
private fun TechStackSection(
    techStacks: List<TechStackEntity>,
    allTechStacks: List<String>,
    onAddTechStack: (String) -> Unit,
    onRemoveTechStack: (Int) -> Unit
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
                    text = "Tech Stack bạn quan tâm",
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
            if (techStacks.isEmpty()) {
                Text(
                    text = "Bạn chưa có tech stack nào",
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
                techStacks.forEach { stack ->
                    TechStackChip(
                        name = stack.name,
                        onRemove = { onRemoveTechStack(stack.id) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddTechStackDialog(
            allTechStacks = allTechStacks,
            addedNames = techStacks.map { it.name }.toSet(),
            onAdd = { name ->
                onAddTechStack(name)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun TechStackChip(name: String, onRemove: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = CosmicTheme.colors.plasma.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = CosmicTheme.typography.body,
                color = CosmicTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
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
private fun AddTechStackDialog(
    allTechStacks: List<String>,
    addedNames: Set<String>,
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf<String?>(null) }

    val filtered = remember(allTechStacks, addedNames, search) {
        allTechStacks.filter { it !in addedNames }
            .filter { search.isBlank() || it.contains(search, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = CosmicTheme.colors.nebula,
        title = {
            Text("Thêm tech stack", style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it; selectedItem = null },
                    placeholder = { Text("Tìm tech stack...") },
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
                if (filtered.isEmpty()) {
                    Text(
                        if (search.isNotBlank()) "Không tìm thấy" else "Không còn tech stack nào để thêm",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textTertiary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filtered) { name ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedItem = name },
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedItem == name)
                                    CosmicTheme.colors.plasma.copy(alpha = 0.12f)
                                else Color.Transparent
                            ) {
                                Text(
                                    text = name,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    style = CosmicTheme.typography.body,
                                    color = CosmicTheme.colors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val name = selectedItem?.trim()
                    if (name != null && name !in addedNames) {
                        onAdd(name)
                    }
                },
                enabled = selectedItem != null && selectedItem!!.trim() !in addedNames,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                    contentColor = CosmicTheme.colors.plasma
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Thêm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Huỷ", color = CosmicTheme.colors.textSecondary)
            }
        }
    )
}

// ── Section 4: Tasks ─────────────────────────────────────────────

@Composable
private fun TaskCard(task: TaskItem, filter: TaskFilter, onClick: () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = CosmicTheme.typography.body,
                    color = if (task.completed) CosmicTheme.colors.textTertiary else CosmicTheme.colors.textPrimary,
                    textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                )
                if (task.isGroupTask) {
                    Text(
                        text = "Nhóm",
                        fontSize = 10.sp,
                        color = CosmicTheme.colors.plasma,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (task.deadline != null) {
                    Text(
                        text = "Hạn: ${formatDeadlineByFilter(task.deadline, filter)}",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyTaskState(onNavigateToCreateTask: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onNavigateToCreateTask),
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
                text = "Bấm vào đây để lập kế hoạch",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.plasma,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
private fun TaskFilterSection(
    selectedFilter: TaskFilter,
    onSelectFilter: (TaskFilter) -> Unit,
    taskCount: Int,
    completedCount: Int,
    onNavigateToCreateTask: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nhiệm vụ",
                style = CosmicTheme.typography.command,
                color = CosmicTheme.colors.textTertiary
            )
            IconButton(onClick = onNavigateToCreateTask) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Thêm nhiệm vụ",
                    tint = CosmicTheme.colors.plasma
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == TaskFilter.TODAY,
                onClick = { onSelectFilter(TaskFilter.TODAY) },
                label = { Text("Hôm nay", style = CosmicTheme.typography.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                    selectedLabelColor = CosmicTheme.colors.plasma
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = CosmicTheme.colors.glassBorder,
                    selectedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
                    enabled = true,
                    selected = selectedFilter == TaskFilter.TODAY
                )
            )
            FilterChip(
                selected = selectedFilter == TaskFilter.WEEK,
                onClick = { onSelectFilter(TaskFilter.WEEK) },
                label = { Text("Tuần này", style = CosmicTheme.typography.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                    selectedLabelColor = CosmicTheme.colors.plasma
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = CosmicTheme.colors.glassBorder,
                    selectedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
                    enabled = true,
                    selected = selectedFilter == TaskFilter.WEEK
                )
            )
            FilterChip(
                selected = selectedFilter == TaskFilter.ALL,
                onClick = { onSelectFilter(TaskFilter.ALL) },
                label = { Text("Tất cả", style = CosmicTheme.typography.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                    selectedLabelColor = CosmicTheme.colors.plasma
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = CosmicTheme.colors.glassBorder,
                    selectedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
                    enabled = true,
                    selected = selectedFilter == TaskFilter.ALL
                )
            )
        }

        Text(
            text = "$completedCount/$taskCount hoàn thành",
            style = CosmicTheme.typography.label,
            color = CosmicTheme.colors.textTertiary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}

// ── Week Task Grid (for "Tuần này" filter) ──────────────────────

@Composable
private fun WeekTaskGrid(
    weekDates: List<WeekDay>,
    selectedDate: String?,
    tasksByDate: Map<String, Int>,
    onDayClick: (String?) -> Unit
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Nhiệm vụ trong tuần",
                        style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = CosmicTheme.colors.textPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weekDates.forEach { day ->
                    DayTaskSquare(
                        day = day,
                        taskCount = tasksByDate[day.date] ?: 0,
                        isSelected = day.date == selectedDate,
                        onClick = {
                            if (day.date == selectedDate) {
                                onDayClick(null)
                            } else {
                                onDayClick(day.date)
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Chọn một ngày để xem nhiệm vụ chi tiết",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary
            )
        }
    }
}

@Composable
private fun DayTaskSquare(
    day: WeekDay,
    taskCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (taskCount > 0)
        CosmicTheme.colors.plasma.copy(alpha = 0.12f)
    else
        Color.Transparent

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
                .background(
                    if (isSelected) CosmicTheme.colors.plasma.copy(alpha = 0.25f) else bgColor
                )
                .border(
                    1.dp,
                    when {
                        isSelected -> CosmicTheme.colors.plasma
                        day.isToday -> CosmicTheme.colors.plasma.copy(alpha = 0.5f)
                        taskCount > 0 -> Color.White.copy(alpha = 0.15f)
                        else -> Color.White.copy(alpha = 0.06f)
                    },
                    RoundedCornerShape(6.dp)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (taskCount > 0) {
                Text(
                    text = taskCount.toString(),
                    style = CosmicTheme.typography.label.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isSelected) Color.White else CosmicTheme.colors.textPrimary
                )
            }
        }
    }
}

private data class MonthDayInfo(
    val dateStr: String,
    val dayOfMonth: Int,
    val isToday: Boolean,
    val hasTasks: Boolean
)

@Composable
private fun MonthTaskGrid(
    year: Int,
    month: Int,
    tasksByDate: Map<String, Int>,
    selectedDate: String?,
    onDateClick: (String?) -> Unit,
    onNavigateMonth: (Int) -> Unit
) {
    val canGoPrev = year > 2026 || (year == 2026 && month > 6)
    val monthName = java.time.Month.of(month).getDisplayName(java.time.format.TextStyle.FULL, Locale("vi", "VN"))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ── Header: Month + Year + Navigation ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (canGoPrev) {
                    IconButton(onClick = { onNavigateMonth(-1) }, modifier = Modifier.size(28.dp)) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Tháng trước",
                            tint = CosmicTheme.colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(28.dp))
                }
                Text(
                    text = "$monthName $year",
                    style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = CosmicTheme.colors.textPrimary
                )
                IconButton(onClick = { onNavigateMonth(1) }, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Tháng sau",
                        tint = CosmicTheme.colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Weekday headers ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach { label ->
                    Text(
                        text = label,
                        style = CosmicTheme.typography.label.copy(fontSize = 10.sp),
                        color = CosmicTheme.colors.textTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Calendar grid ──
            val dayRows = remember(year, month, tasksByDate) {
                generateMonthDays(year, month, tasksByDate)
            }

            dayRows.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    week.forEach { cell ->
                        if (cell != null) {
                            MonthDayCell(
                                day = cell,
                                isSelected = cell.dateStr == selectedDate,
                                onClick = {
                                    if (cell.dateStr == selectedDate) {
                                        onDateClick(null)
                                    } else {
                                        onDateClick(cell.dateStr)
                                    }
                                }
                            )
                        } else {
                            Spacer(modifier = Modifier.width(36.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun MonthDayCell(
    day: MonthDayInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (day.hasTasks)
        CosmicTheme.colors.plasma.copy(alpha = 0.12f)
    else
        Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(36.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    if (isSelected) CosmicTheme.colors.plasma.copy(alpha = 0.25f) else bgColor
                )
                .border(
                    1.dp,
                    when {
                        isSelected -> CosmicTheme.colors.plasma
                        day.isToday -> CosmicTheme.colors.plasma.copy(alpha = 0.5f)
                        day.hasTasks -> CosmicTheme.colors.plasma.copy(alpha = 0.3f)
                        else -> Color.White.copy(alpha = 0.06f)
                    },
                    RoundedCornerShape(6.dp)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.dayOfMonth.toString(),
                style = CosmicTheme.typography.label.copy(fontSize = 11.sp),
                color = when {
                    isSelected -> Color.White
                    day.hasTasks -> CosmicTheme.colors.plasma
                    else -> CosmicTheme.colors.textPrimary
                }
            )
        }
        if (day.hasTasks) {
            Text(
                text = ".",
                fontSize = 7.sp,
                color = CosmicTheme.colors.plasma,
                lineHeight = 7.sp
            )
        }
    }
}

private fun generateMonthDays(year: Int, month: Int, tasksByDate: Map<String, Int>): List<List<MonthDayInfo?>> {
    val firstDay = LocalDate.of(year, month, 1)
    val daysInMonth = firstDay.lengthOfMonth()
    val startCol = (firstDay.dayOfWeek.value % 7)
    val today = LocalDate.now()
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val rows = mutableListOf<List<MonthDayInfo?>>()
    var cells = mutableListOf<MonthDayInfo?>()

    repeat(startCol) { cells.add(null) }

    for (day in 1..daysInMonth) {
        val date = LocalDate.of(year, month, day)
        val dateStr = date.format(dateFormat)
        cells.add(
            MonthDayInfo(
                dateStr = dateStr,
                dayOfMonth = day,
                isToday = date == today,
                hasTasks = tasksByDate.containsKey(dateStr)
            )
        )
        if (cells.size == 7) {
            rows.add(cells)
            cells = mutableListOf()
        }
    }

    if (cells.isNotEmpty()) {
        while (cells.size < 7) cells.add(null)
        rows.add(cells)
    }

    return rows
}

private fun isTaskOnDate(epochMillis: Long, dateStr: String): Boolean {
    val date = Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    return date == dateStr
}

private fun formatDeadlineByFilter(millis: Long, filter: TaskFilter): String {
    val zdt = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
    return when (filter) {
        TaskFilter.TODAY -> zdt.format(DateTimeFormatter.ofPattern("HH:mm", Locale("vi", "VN")))
        TaskFilter.WEEK -> zdt.format(DateTimeFormatter.ofPattern("EEEE, dd/MM", Locale("vi", "VN")))
        TaskFilter.ALL -> zdt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("vi", "VN")))
    }
}



package vn.edu.uit.devorbit.mobile.ui.screen.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
import vn.edu.uit.devorbit.mobile.ui.viewmodel.WeekDay
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToCourse: (Long) -> Unit = {},
    onNavigateToCreateTask: () -> Unit = {},
    onNavigateToTaskManagement: () -> Unit = {},
    onNavigateToGroupPlan: (Long) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPlanner: () -> Unit = {},
    unreadCount: Int = 0,
    avatarUrl: String? = null,
    showRegistrationOnboarding: Boolean = false,
    onRegistrationOnboardingCompleted: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
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

    var expandedTaskIds by remember { mutableStateOf(setOf<Long>()) }
    var showQuickAddSheet by remember { mutableStateOf(false) }
    var quickAddDeadline by remember { mutableStateOf(
        LocalDate.now().atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    ) }


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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Logo / Title
                Text(
                    text = "DevOrbit",
                    style = CosmicTheme.typography.display.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = CosmicTheme.colors.plasma
                )

                // Actions (Notifications & Profile)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notifications
                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(
                                        containerColor = CosmicTheme.colors.plasma,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Notifications,
                                contentDescription = "Thông báo",
                                tint = CosmicTheme.colors.textSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.clickable(onClick = onNavigateToProfile),
                        shape = RoundedCornerShape(20.dp),
                        color = CosmicTheme.colors.nebula,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            CosmicTheme.colors.glassBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 4.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (avatarUrl != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(avatarUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Mở trang cá nhân",
                                    modifier = Modifier.size(32.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(CosmicTheme.colors.plasma.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Person,
                                        contentDescription = null,
                                        tint = CosmicTheme.colors.plasma,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Cá nhân",
                                style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.SemiBold),
                                color = CosmicTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        item {
            GreetingSection(
                greeting = state.greeting,
                dateText = state.dateText,
                studyHours = state.studyHoursToday,
                streakCount = state.streakCount
            )
        }

        item {
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
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Quản lý nhiệm vụ",
                        tint = CosmicTheme.colors.plasma
                    )
                }
            }
            Text(
                text = "${state.completedTaskCount}/${state.totalTaskCount} hoàn thành",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (state.sortedTasks.isEmpty()) {
            item {
                EmptyTaskState(onNavigateToCreateTask = onNavigateToCreateTask)
            }
        } else {
            items(state.sortedTasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    expanded = task.id in expandedTaskIds,
                    onToggleExpanded = { expandedTaskIds = if (task.id in expandedTaskIds) expandedTaskIds - task.id else expandedTaskIds + task.id },
                    onToggle = { viewModel.toggleTask(task.id, !task.completed) },
                    onDoubleClick = if (task.isGroupTask) { { task.groupPlanId?.let(onNavigateToGroupPlan) ?: Unit } } else onNavigateToTaskManagement
                )
            }
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CosmicTheme.colors.nebula,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder),
                    onClick = { showQuickAddSheet = true }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Thêm nhiệm vụ nhanh",
                            tint = CosmicTheme.colors.plasma,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Thêm nhiệm vụ nhanh",
                            style = CosmicTheme.typography.body,
                            color = CosmicTheme.colors.textSecondary
                        )
                    }
                }
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
                majorName = state.planActiveMajor,
                courses = state.semesterCourses,
                totalCourses = state.planTotalCourses,
                totalCredits = state.planTotalCredits,
                semesterCount = state.planSemesterCount,
                onCourseClick = onNavigateToCourse,
                onNavigateToPlanner = onNavigateToPlanner
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

        if (showQuickAddSheet) {
            QuickAddTaskSheet(
                initialDeadline = quickAddDeadline,
                onDeadlineChange = { quickAddDeadline = it },
                onDismiss = { showQuickAddSheet = false },
                onConfirm = { title, deadline ->
                    viewModel.createQuickTask(title, deadline)
                    showQuickAddSheet = false
                }
            )
        }

        if (showRegistrationOnboarding) {
            RegistrationOnboardingDialog(
                allCourses = state.allCourses,
                allTechStacks = state.allTechStacks,
                isSaving = state.isSavingOnboarding,
                onSave = { courseIds, techStacks ->
                    viewModel.saveOnboardingPreferences(
                        courseIds = courseIds,
                        techStackNames = techStacks,
                        onSuccess = onRegistrationOnboardingCompleted
                    )
                }
            )
        }
    }
}

@Composable
private fun RegistrationOnboardingDialog(
    allCourses: List<CourseEntity>,
    allTechStacks: List<String>,
    isSaving: Boolean,
    onSave: (Set<Long>, Set<String>) -> Unit
) {
    var selectedCourseIds by remember { mutableStateOf(emptySet<Long>()) }
    var selectedTechStacks by remember { mutableStateOf(emptySet<String>()) }
    var courseQuery by remember { mutableStateOf("") }
    var stackQuery by remember { mutableStateOf("") }

    val visibleCourses = remember(allCourses, courseQuery) {
        allCourses.filter {
            courseQuery.isBlank() ||
                it.maMH.contains(courseQuery, ignoreCase = true) ||
                it.tenMH.contains(courseQuery, ignoreCase = true)
        }
    }
    val visibleStacks = remember(allTechStacks, stackQuery) {
        allTechStacks.filter { stackQuery.isBlank() || it.contains(stackQuery, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = {},
        shape = RoundedCornerShape(20.dp),
        containerColor = CosmicTheme.colors.nebula,
        title = {
            Column {
                Text(
                    "Thiết lập gợi ý của bạn",
                    style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    "Chọn môn học kỳ này và công nghệ bạn quan tâm. DevOrbit sẽ báo khi có repo phù hợp được duyệt.",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textSecondary
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Môn học kỳ này", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = courseQuery,
                    onValueChange = { courseQuery = it },
                    placeholder = { Text("Tìm theo mã hoặc tên môn") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                LazyColumn(modifier = Modifier.heightIn(max = 180.dp)) {
                    items(visibleCourses, key = { it.id }) { course ->
                        val selected = course.id in selectedCourseIds
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCourseIds = if (selected) {
                                        selectedCourseIds - course.id
                                    } else {
                                        selectedCourseIds + course.id
                                    }
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = selected, onCheckedChange = null)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${course.maMH} · ${course.tenMH}", style = CosmicTheme.typography.label)
                                Text(
                                    "${course.repoCount} repo",
                                    style = CosmicTheme.typography.label,
                                    color = CosmicTheme.colors.textTertiary
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = CosmicTheme.colors.glassBorder)
                Text("Tech stack", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = stackQuery,
                    onValueChange = { stackQuery = it },
                    placeholder = { Text("Tìm công nghệ") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                LazyColumn(modifier = Modifier.heightIn(max = 140.dp)) {
                    items(visibleStacks, key = { it }) { stack ->
                        val selected = stack in selectedTechStacks
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTechStacks = if (selected) {
                                        selectedTechStacks - stack
                                    } else {
                                        selectedTechStacks + stack
                                    }
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = selected, onCheckedChange = null)
                            Text(stack, style = CosmicTheme.typography.label)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedCourseIds, selectedTechStacks) },
                enabled = selectedCourseIds.isNotEmpty() && selectedTechStacks.isNotEmpty() && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Lưu lựa chọn")
                }
            }
        }
    )
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
    majorName: String,
    courses: List<CourseEntity>,
    totalCourses: Int,
    totalCredits: Int,
    semesterCount: Int,
    onCourseClick: (Long) -> Unit,
    onNavigateToPlanner: () -> Unit
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
                    text = majorName.ifBlank { "Lộ trình học tập" },
                    style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                    color = CosmicTheme.colors.textPrimary
                )
                TextButton(onClick = onNavigateToPlanner) {
                    Text("Điều chỉnh lộ trình", color = CosmicTheme.colors.plasma)
                }
            }
            if (totalCourses > 0) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$totalCourses môn · $totalCredits tín chỉ · $semesterCount học kỳ",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textTertiary
                )
            }
            Spacer(Modifier.height(12.dp))
            if (courses.isEmpty()) {
                Text(
                    text = "Bạn chưa có môn học nào",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textTertiary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                courses.forEach { course ->
                    CourseChip(
                        course = course,
                        onClick = { onCourseClick(course.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseChip(
    course: CourseEntity,
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
                            unfocusedBorderColor = CosmicTheme.colors.glassBorder,
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
                .background(Color.Transparent)
                .border(
                    1.dp,
                    when {
                        day.isToday -> CosmicTheme.colors.plasma
                        day.qualifiesForStreak -> Color(0xFF2E7D32).copy(alpha = 0.5f)
                        else -> CosmicTheme.colors.glassBorder
                    },
                    RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (day.qualifiesForStreak) {
                Text(text = "\uD83D\uDD25", fontSize = 12.sp)
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
                        unfocusedBorderColor = CosmicTheme.colors.glassBorder,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskCard(
    task: TaskItem,
    expanded: Boolean,
    isLocked: Boolean = false,
    onToggleExpanded: () -> Unit,
    onToggle: () -> Unit,
    onDoubleClick: () -> Unit
) {
    val bgColor = if (task.completed)
        Color(0xFF2E7D32).copy(alpha = 0.08f)
    else if (isLocked)
        Color(0xFFF9A825).copy(alpha = 0.04f)
    else
        Color(0xFFF9A825).copy(alpha = 0.08f)

    val borderColor = if (task.completed)
        Color(0xFF2E7D32).copy(alpha = 0.2f)
    else if (isLocked)
        Color(0xFFF9A825).copy(alpha = 0.12f)
    else
        Color(0xFFF9A825).copy(alpha = 0.25f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onDoubleClick = onDoubleClick
            ),
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
                    .clip(CircleShape)
                    .clickable(onClick = { if (!isLocked) onToggle() })
                    .background(
                        if (task.completed) Color(0xFF2E7D32) else Color.Transparent,
                        CircleShape
                    )
                    .border(
                        1.5.dp,
                        if (task.completed) Color(0xFF2E7D32) else if (isLocked) CosmicTheme.colors.textTertiary.copy(alpha = 0.3f) else CosmicTheme.colors.textTertiary,
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
                    color = if (task.completed) CosmicTheme.colors.textTertiary else if (isLocked) CosmicTheme.colors.textTertiary else CosmicTheme.colors.textPrimary,
                    textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                    modifier = Modifier.clickable(onClick = onToggleExpanded)
                )
                AnimatedVisibility(visible = expanded) {
                    Column {
                        if (task.description.isNotBlank()) {
                            Text(
                                text = task.description,
                                style = CosmicTheme.typography.label,
                                color = CosmicTheme.colors.textSecondary,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
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
                        text = "Hạn: ${formatDeadline(task.deadline)}",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textTertiary
                    )
                }
            }}
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

private fun formatDeadline(millis: Long): String {
    val zdt = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
    return zdt.format(DateTimeFormatter.ofPattern("HH:mm", Locale("vi", "VN")))
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickAddTaskSheet(
    initialDeadline: Long,
    onDeadlineChange: (Long) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (title: String, deadline: Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDeadline)
    val timePickerState = rememberTimePickerState(
        initialHour = 23,
        initialMinute = 59,
        is24Hour = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CosmicTheme.colors.nebula
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Quick Add Task",
                style = CosmicTheme.typography.titleMedium,
                color = CosmicTheme.colors.textPrimary
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task title", color = CosmicTheme.colors.textTertiary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CosmicTheme.colors.textPrimary,
                    unfocusedTextColor = CosmicTheme.colors.textPrimary,
                    cursorColor = CosmicTheme.colors.plasma,
                    focusedBorderColor = CosmicTheme.colors.plasma,
                    unfocusedBorderColor = CosmicTheme.colors.glassBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = CosmicTheme.colors.textSecondary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (initialDeadline > 0L) {
                        val zdt = Instant.ofEpochMilli(initialDeadline)
                            .atZone(ZoneId.systemDefault())
                        "${zdt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("vi", "VN")))} ${zdt.format(DateTimeFormatter.ofPattern("HH:mm", Locale("vi", "VN")))}"
                    } else "Pick deadline",
                    color = CosmicTheme.colors.textSecondary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = CosmicTheme.colors.textSecondary)
                }
                Spacer(Modifier.width(8.dp))
                TextButton(
                    onClick = {
                        val deadline = datePickerState.selectedDateMillis
                            ?: initialDeadline
                        if (deadline > 0L) {
                            val zdt = Instant.ofEpochMilli(deadline)
                                .atZone(ZoneId.systemDefault())
                                .with(LocalTime.of(timePickerState.hour, timePickerState.minute))
                            onConfirm(title.trim(), zdt.toInstant().toEpochMilli())
                        }
                    },
                    enabled = title.isNotBlank()
                ) {
                    Text(
                        "Add",
                        color = if (title.isNotBlank()) CosmicTheme.colors.plasma
                        else CosmicTheme.colors.textTertiary
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDeadlineChange(it) }
                    showDatePicker = false
                    showTimePicker = true
                }) {
                    Text("Next", color = CosmicTheme.colors.plasma)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = CosmicTheme.colors.textSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val base = datePickerState.selectedDateMillis ?: initialDeadline
                    val zdt = Instant.ofEpochMilli(base)
                        .atZone(ZoneId.systemDefault())
                        .with(LocalTime.of(timePickerState.hour, timePickerState.minute))
                    onDeadlineChange(zdt.toInstant().toEpochMilli())
                    showTimePicker = false
                }) {
                    Text("OK", color = CosmicTheme.colors.plasma)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = CosmicTheme.colors.textSecondary)
                }
            },
            title = { Text("Pick time") },
            text = {
                TimeInput(state = timePickerState)
            }
        )
    }
}



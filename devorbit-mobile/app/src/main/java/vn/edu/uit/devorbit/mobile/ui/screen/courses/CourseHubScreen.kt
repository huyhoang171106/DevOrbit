package vn.edu.uit.devorbit.mobile.ui.screen.courses

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.domain.model.GraphNode
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.ui.CourseDetailScreen
import vn.edu.uit.devorbit.mobile.ui.RepoDetailScreen
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseHubScreen(
    viewModel: CourseViewModel = hiltViewModel(),
    onCreatePlan: () -> Unit = {},
    pendingCourseId: Long? = null,
    pendingRepoId: Long? = null,
    pendingGalaxy: Boolean = false,
    onPendingCleared: () -> Unit = {}
) {
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }
    var selectedTutorial by remember { mutableStateOf<vn.edu.uit.devorbit.mobile.data.remote.dto.CourseTutorial?>(null) }
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val selectedCourse by viewModel.selectedCourse.collectAsStateWithLifecycle()
    val selectedRepo by viewModel.selectedRepo.collectAsStateWithLifecycle()
    val repos by viewModel.detailRepos.collectAsStateWithLifecycle()
    val detailLoading by viewModel.detailLoading.collectAsStateWithLifecycle()
    val detailError by viewModel.detailError.collectAsStateWithLifecycle()
    val bookmarkedCourseIds by viewModel.bookmarkedCourseIds.collectAsStateWithLifecycle()
    val bookmarkedRepoIds by viewModel.bookmarkedRepoIds.collectAsStateWithLifecycle()
    val repoSummary by viewModel.repoSummary.collectAsStateWithLifecycle()
    val repoAdvice by viewModel.repoAdvice.collectAsStateWithLifecycle()
    val aiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()
    val repoSocialInfo by viewModel.repoSocialInfo.collectAsStateWithLifecycle()
    val userVote by viewModel.userVote.collectAsStateWithLifecycle()
    val socialLoading by viewModel.socialLoading.collectAsStateWithLifecycle()

    // Handle deep-linking from bookmarks
    var processedCourseId by remember { mutableStateOf<Long?>(null) }
    var processedRepoId by remember { mutableStateOf<Long?>(null) }
    var pendingUnopenedCourseId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(pendingCourseId) {
        if (pendingCourseId != null && pendingCourseId != processedCourseId) {
            val course = courses.find { it.id == pendingCourseId }
            if (course != null) {
                processedCourseId = pendingCourseId
                viewModel.openCourse(course)
                onPendingCleared()
            } else {
                pendingUnopenedCourseId = pendingCourseId
            }
        }
    }

    LaunchedEffect(courses) {
        val pid = pendingUnopenedCourseId ?: return@LaunchedEffect
        if (pid == processedCourseId) return@LaunchedEffect
        val course = courses.find { it.id == pid }
        if (course != null) {
            processedCourseId = pid
            pendingUnopenedCourseId = null
            viewModel.openCourse(course)
            onPendingCleared()
        }
    }

    LaunchedEffect(pendingGalaxy) {
        if (pendingGalaxy) {
            viewMode = ViewMode.GALAXY
            onPendingCleared()
        }
    }

    LaunchedEffect(pendingRepoId) {
        if (pendingRepoId != null && pendingRepoId != processedRepoId) {
            processedRepoId = pendingRepoId
            // Try to find repo in currently loaded repos
            val existing = viewModel.detailRepos.value.find { it.id == pendingRepoId }
            if (existing != null && selectedCourse != null) {
                viewModel.openRepo(existing)
                onPendingCleared()
            } else {
                // Fetch the repo to get its courseId
                viewModel.navigateToRepoFromBookmark(pendingRepoId)
            }
        }
    }

    when {
        selectedRepo != null -> RepoDetailScreen(
            repo = selectedRepo!!,
            aiSummary = repoSummary,
            aiAdvice = repoAdvice,
            aiLoading = aiLoading,
            socialInfo = repoSocialInfo,
            userVote = userVote,
            socialLoading = socialLoading,
            isBookmarked = selectedRepo!!.id in bookmarkedRepoIds,
            onVote = { viewModel.voteRepo(selectedRepo!!.id, it) },
            onBookmark = { viewModel.toggleRepoBookmark(selectedRepo!!) },
            onSubmitReview = { rating, comment -> viewModel.submitReview(selectedRepo!!.id, rating, comment) },
            onBack = { viewModel.backFromRepo() }
        )

        selectedCourse != null -> {
            if (detailLoading) {
                CourseDetailLoading(
                    courseName = selectedCourse!!.tenMH,
                    onBack = { viewModel.closeCourseDetail() }
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    detailError?.let { error ->
                        Text(
                            text = error,
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.supernova,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    CourseDetailScreen(
                        course = selectedCourse!!,
                        repos = repos,
                        bookmarked = selectedCourse!!.id in bookmarkedCourseIds,
                        bookmarkedRepoIds = bookmarkedRepoIds,
                        onBack = { viewModel.closeCourseDetail() },
                        onBookmarkClick = { viewModel.toggleCourseBookmark(selectedCourse!!) },
                        onRepoClick = { viewModel.openRepo(it) },
                        onCreatePlan = onCreatePlan
                    )
                }
            }
        }

        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)) {
                    Text(
                        text = "Môn học",
                        style = CosmicTheme.typography.display,
                        color = CosmicTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // View toggle
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.weight(1f)) {
                        SegmentedButton(
                            selected = viewMode == ViewMode.LIST,
                            onClick = { viewMode = ViewMode.LIST },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            icon = { Icon(Icons.Rounded.List, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        ) {
                            Text("Danh sách")
                        }
                        SegmentedButton(
                            selected = viewMode == ViewMode.GALAXY,
                            onClick = { viewMode = ViewMode.GALAXY },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            icon = { Icon(Icons.Rounded.Star, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        ) {
                            Text("Học kỳ")
                        }
                    }
                }
                }

                when (viewMode) {
                    ViewMode.LIST -> CourseListScreen(
                        viewModel = viewModel,
                        onCourseClick = { viewModel.openCourse(it) }
                    )
                    ViewMode.GALAXY -> SemesterGraphView(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun CourseDetailLoading(
    courseName: String,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp)) {
            Text("← Môn học", color = CosmicTheme.colors.textSecondary)
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = CosmicTheme.colors.plasma,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = courseName,
                    style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = CosmicTheme.colors.textPrimary
                )
                Text(
                    text = "Đang tải tài nguyên",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textTertiary
                )
            }
        }
    }
}

private val MAJOR_OPTIONS = listOf(
    "IT" to "Công nghệ Thông tin",
    "IS" to "Hệ thống Thông tin",
    "CS" to "Khoa học Máy tính",
    "SE" to "Kỹ thuật Phần mềm",
    "AI" to "Trí tuệ Nhân tạo",
    "CE" to "Kỹ thuật Máy tính",
    "IC" to "Thiết kế Vi mạch",
    "MM" to "Truyền thông Đa phương tiện",
    "NT" to "Mạng máy tính",
    "ATTT" to "An toàn Thông tin",
    "EC" to "Thương mại Điện tử",
    "DS" to "Khoa học Dữ liệu"
)

@Composable
private fun SemesterGraphView(viewModel: CourseViewModel) {
    val semesterPlan by viewModel.semesterPlan.collectAsState()
    val loading by viewModel.graphLoading.collectAsState()
    val error by viewModel.graphError.collectAsState()
    val allCourses by viewModel.courses.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        val msg = error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearGraphError()
    }

    val currentMajor by viewModel.currentMajor.collectAsState()
    val totalProgramCredits by viewModel.totalProgramCredits.collectAsState()
    val courseCatalogCredits by viewModel.courseCatalogCredits.collectAsState()
    val hasData = semesterPlan.values.any { it.isNotEmpty() }

    val creditMap = remember(allCourses, courseCatalogCredits) {
        val map = allCourses.associate { it.maMH to it.credits }.toMutableMap()
        map.putAll(courseCatalogCredits)
        map
    }
    val semesterCredits = remember(semesterPlan, creditMap) {
        semesterPlan.mapValues { (_, nodes) ->
            var total = 0
            for (n in nodes) { total += creditMap[n.code] ?: 0 }
            total
        }
    }

    var majorExpanded by remember { mutableStateOf(false) }
    var addTargetSemester by remember { mutableIntStateOf(1) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var expandedSemesters by remember { mutableStateOf(setOf<Int>()) }
    var moveTarget by remember { mutableStateOf<Pair<Int, String>?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Major selector + generate button
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = MAJOR_OPTIONS.firstOrNull { it.first == currentMajor }?.second ?: currentMajor,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Ngành") },
                        trailingIcon = {
                            IconButton(onClick = { majorExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    DropdownMenu(expanded = majorExpanded, onDismissRequest = { majorExpanded = false }) {
                        MAJOR_OPTIONS.forEach { (code, name) ->
                            DropdownMenuItem(
                                text = { Text("$code — $name") },
                                onClick = { viewModel.selectMajor(code); majorExpanded = false }
                            )
                        }
                    }
                }
                Button(
                    onClick = { viewModel.loadGraph(currentMajor) },
                    enabled = !loading,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text("Tạo lộ trình")
                }
                if (hasData) {
                    TextButton(
                        onClick = { showResetDialog = true },
                        enabled = !loading,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        Text("Reset", color = CosmicTheme.colors.supernova)
                    }
                }
            }

            // Linear progress during loading (inline, doesn't replace content)
            AnimatedVisibility(visible = loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    color = CosmicTheme.colors.plasma,
                    trackColor = CosmicTheme.colors.glassBorder
                )
            }

            // Content area
            when {
                !hasData && loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = CosmicTheme.colors.plasma,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }

                !hasData && !loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Chọn ngành và bấm \"Tạo lộ trình\"",
                        color = CosmicTheme.colors.textTertiary,
                        style = CosmicTheme.typography.body
                    )
                }

                hasData -> {
                    val totalCourses = semesterPlan.values.sumOf { it.size }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
                    ) {
                        item {
                            Text(
                                text = "$totalCourses môn · $totalProgramCredits tín chỉ · ${semesterPlan.size} học kỳ",
                                style = CosmicTheme.typography.label,
                                color = CosmicTheme.colors.textTertiary
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        (1..8).forEach { semester ->
                            val semesterNodes = semesterPlan[semester].orEmpty()
                            val totalCredits = semesterCredits[semester] ?: 0
                            item(key = semester) {
                                EditableSemesterCard(
                                    semester = semester,
                                    nodes = semesterNodes,
                                    totalCredits = totalCredits,
                                    creditMap = creditMap,
                                    expanded = semester in expandedSemesters,
                                    onToggle = {
                                        expandedSemesters = if (semester in expandedSemesters)
                                            expandedSemesters - semester
                                        else expandedSemesters + semester
                                    },
                                    onAddClick = {
                                        addTargetSemester = semester
                                        searchQuery = ""
                                        showAddDialog = true
                                    },
                                    onRemoveCourse = { courseCode ->
                                        viewModel.removeCourseFromSemester(semester, courseCode)
                                    },
                                    onMoveCourse = { courseCode ->
                                        moveTarget = semester to courseCode
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add course dialog
        if (showAddDialog) {
            val targetSemester = addTargetSemester
            val available = remember(searchQuery, semesterPlan) {
                viewModel.getAvailableCourses(searchQuery)
            }
            val semesterCourses = semesterPlan[targetSemester].orEmpty()

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Thêm môn - Học kỳ $targetSemester", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(
                            "${semesterCourses.size} môn",
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.textTertiary
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Tìm mã hoặc tên môn...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            items(available) { course ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        viewModel.addCourseToSemester(targetSemester, course.maMH)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = CosmicTheme.colors.nebula
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(course.maMH, style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold), color = CosmicTheme.colors.plasma)
                                            Text(course.tenMH, style = CosmicTheme.typography.body, color = CosmicTheme.colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                        Text("${course.credits} TC", style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.SemiBold), color = CosmicTheme.colors.aurora)
                                    }
                                }
                            }
                            if (available.isEmpty()) {
                                item {
                                    Text(
                                        if (searchQuery.isBlank()) "Tất cả môn đã được thêm" else "Không tìm thấy",
                                        modifier = Modifier.padding(16.dp),
                                        style = CosmicTheme.typography.label,
                                        color = CosmicTheme.colors.textTertiary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { showAddDialog = false }) { Text("Đóng") } }
            )
        }

        // Move course dialog
        moveTarget?.let { (fromSemester, courseCode) ->
            AlertDialog(
                onDismissRequest = { moveTarget = null },
                title = { Text("Chuyển môn", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Chọn học kỳ mới cho $courseCode:", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                        (1..8).filter { it != fromSemester }.forEach { sem ->
                            val count = semesterPlan[sem].orEmpty().size
                            var creds = 0
                            for (n in semesterPlan[sem].orEmpty()) { creds += creditMap[n.code] ?: 0 }
                            Surface(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickable {
                                    viewModel.moveCourse(fromSemester, sem, courseCode)
                                    moveTarget = null
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = CosmicTheme.colors.nebula,
                                border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                            ) {
                                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("Học kỳ $sem", style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Medium), color = CosmicTheme.colors.textPrimary, modifier = Modifier.weight(1f))
                                    Text("$count môn · ${creds}TC", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                                }
                            }
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { moveTarget = null }) { Text("Hủy") } }
            )
        }

        // Reset confirmation dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Reset kế hoạch", fontWeight = FontWeight.Bold) },
                text = { Text("Bạn có chắc muốn reset kế hoạch? Toàn bộ môn học sẽ được tạo lại từ chương trình đào tạo.", style = CosmicTheme.typography.body, color = CosmicTheme.colors.textSecondary) },
                confirmButton = {
                    TextButton(onClick = {
                        showResetDialog = false
                        viewModel.resetPlan()
                    }) {
                        Text("Xác nhận", color = CosmicTheme.colors.supernova)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Hủy")
                    }
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
            snackbar = { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CosmicTheme.colors.nebula,
                    contentColor = CosmicTheme.colors.supernova
                )
            }
        )
    }
}

@Composable
private fun EditableSemesterCard(
    semester: Int,
    nodes: List<GraphNode>,
    totalCredits: Int,
    creditMap: Map<String, Int>,
    expanded: Boolean,
    onToggle: () -> Unit,
    onAddClick: () -> Unit,
    onRemoveCourse: (courseCode: String) -> Unit,
    onMoveCourse: (courseCode: String) -> Unit
) {
    val semesterColor = when (semester) {
        1, 2 -> CosmicTheme.colors.aurora
        3, 4 -> CosmicTheme.colors.plasma
        5, 6 -> CosmicTheme.colors.plasma.copy(alpha = 0.7f)
        else -> CosmicTheme.colors.supernova
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(semesterColor)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "HK $semester",
                        style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                        color = CosmicTheme.colors.textPrimary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${nodes.size} môn · ${totalCredits}TC",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textTertiary
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = onAddClick, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Thêm môn", tint = CosmicTheme.colors.plasma, modifier = Modifier.size(18.dp))
                    }
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.Star else Icons.Rounded.List,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = CosmicTheme.colors.textTertiary
                    )
                }
            }

            if (expanded) {
                Divider(color = CosmicTheme.colors.glassBorder, thickness = 1.dp)
                nodes.forEach { node ->
                    val courseCredits = creditMap[node.code] ?: 0
                    EditableCourseRow(
                        node = node,
                        credits = courseCredits,
                        onRemove = { onRemoveCourse(node.code) },
                        onMove = { onMoveCourse(node.code) }
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun EditableCourseRow(node: GraphNode, credits: Int, onRemove: () -> Unit, onMove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = node.code,
                    style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                    color = CosmicTheme.colors.plasma
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "($credits TC)",
                    style = CosmicTheme.typography.label.copy(fontSize = 11.sp),
                    color = CosmicTheme.colors.aurora
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = node.name,
                    style = CosmicTheme.typography.body,
                    color = CosmicTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        IconButton(onClick = onMove, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.MoveDown, contentDescription = "Chuyển", tint = CosmicTheme.colors.plasma.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
        }
        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Bỏ môn", tint = CosmicTheme.colors.supernova.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
        }
    }
}

enum class ViewMode { LIST, GALAXY }

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    onPendingCleared: () -> Unit = {}
) {
    var viewMode by remember { mutableStateOf(ViewMode.LIST) }
    var selectedTutorial by remember { mutableStateOf<vn.edu.uit.devorbit.mobile.data.remote.dto.CourseTutorial?>(null) }
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val selectedCourse by viewModel.selectedCourse.collectAsStateWithLifecycle()
    val selectedRepo by viewModel.selectedRepo.collectAsStateWithLifecycle()
    val repos by viewModel.detailRepos.collectAsStateWithLifecycle()
    val tutorials by viewModel.detailTutorials.collectAsStateWithLifecycle()
    val videos by viewModel.detailVideos.collectAsStateWithLifecycle()
    val articles by viewModel.detailArticles.collectAsStateWithLifecycle()
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

    LaunchedEffect(pendingCourseId) {
        if (pendingCourseId != null && pendingCourseId != processedCourseId) {
            processedCourseId = pendingCourseId
            val course = courses.find { it.id == pendingCourseId }
            if (course != null) viewModel.openCourse(course)
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
                        tutorials = tutorials,
                        videos = videos,
                        articles = articles,
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

                if (viewMode == ViewMode.LIST) {
                    CourseListScreen(viewModel = viewModel, onCourseClick = { viewModel.openCourse(it) })
                } else {
                    SemesterGraphView(viewModel = viewModel)
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
    "SE" to "Kỹ thuật Phần mềm",
    "CS" to "Khoa học Máy tính",
    "DS" to "Khoa học Dữ liệu",
    "IS" to "Hệ thống Thông tin",
    "CE" to "Kỹ thuật Máy tính"
)

@Composable
private fun SemesterGraphView(viewModel: CourseViewModel) {
    val nodes by viewModel.graphNodes.collectAsState()
    val links by viewModel.graphLinks.collectAsState()
    val loading by viewModel.graphLoading.collectAsState()
    val error by viewModel.graphError.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        val msg = error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearGraphError()
    }

    val hasData = nodes.isNotEmpty()

    var selectedMajor by remember { mutableStateOf("SE") }
    var majorExpanded by remember { mutableStateOf(false) }

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
                        value = MAJOR_OPTIONS.firstOrNull { it.first == selectedMajor }?.second ?: selectedMajor,
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
                                onClick = { selectedMajor = code; majorExpanded = false }
                            )
                        }
                    }
                }
                Button(
                    onClick = { viewModel.loadGraph(selectedMajor) },
                    enabled = !loading,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text("Tạo kế hoạch")
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
                        "Chọn ngành và bấm \"Tạo kế hoạch\"",
                        color = CosmicTheme.colors.textTertiary,
                        style = CosmicTheme.typography.body
                    )
                }

                hasData -> {
                    val bySemester = remember(nodes) {
                        nodes.filter { it.semester != null && it.semester in 1..8 }
                            .groupBy { it.semester!! }.toSortedMap()
                    }

                    var expandedSemesters by remember { mutableStateOf(setOf<Int>()) }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
                    ) {
                        item {
                            Text(
                                text = "${nodes.size} môn · ${bySemester.size} học kỳ",
                                style = CosmicTheme.typography.label,
                                color = CosmicTheme.colors.textTertiary
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        items(bySemester.entries.toList()) { (semester, semesterNodes) ->
                            SemesterCard(
                                semester = semester,
                                nodes = semesterNodes,
                                expanded = semester in expandedSemesters,
                                onToggle = {
                                    expandedSemesters = if (semester in expandedSemesters)
                                        expandedSemesters - semester
                                    else expandedSemesters + semester
                                }
                            )
                        }
                    }
                }
            }
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
private fun SemesterCard(
    semester: Int,
    nodes: List<GraphNode>,
    expanded: Boolean,
    onToggle: () -> Unit
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
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
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
                        text = "${nodes.size} môn",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textTertiary
                    )
                    Spacer(Modifier.width(8.dp))
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
                    CourseNodeRow(node = node)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CourseNodeRow(node: GraphNode) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val dotColor = when {
            node.impactScore >= 7.0 -> CosmicTheme.colors.supernova
            node.impactScore >= 4.0 -> CosmicTheme.colors.plasma
            else -> CosmicTheme.colors.aurora
        }
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(dotColor)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = node.code,
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.plasma
            )
            Text(
                text = node.name,
                style = CosmicTheme.typography.body,
                color = CosmicTheme.colors.textPrimary,
                maxLines = 1
            )
        }
        if (node.impactScore > 0) {
            Text(
                text = "%.1f".format(node.impactScore),
                style = CosmicTheme.typography.label,
                color = dotColor
            )
        }
    }
}

enum class ViewMode { LIST, GALAXY }

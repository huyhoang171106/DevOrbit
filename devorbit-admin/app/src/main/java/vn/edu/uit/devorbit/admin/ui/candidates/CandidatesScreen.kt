package vn.edu.uit.devorbit.admin.ui.candidates

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.core.designsystem.*
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidatesScreen(
    viewModel: CandidatesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedCandidate by remember { mutableStateOf<RepoCandidateResponse?>(null) }
    var approveCandidateId by remember { mutableStateOf<Long?>(null) }
    var rejectCandidateId by remember { mutableStateOf<Long?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        ObsidianPageHeader(
            title = "Duyệt kho lưu tr\u1eef",
            subtitle = when (val s = state) {
                is CandidatesUiState.Content -> "${s.filteredCandidates.size} \u1ee9ng viên"
                else -> null
            }
        )

        when (val s = state) {
            is CandidatesUiState.Loading -> {
                InitialLoading()
            }
            is CandidatesUiState.Error -> {
                ErrorState(
                    title = "Lỗi tải dữ liệu",
                    subtitle = s.message,
                    onRetry = { viewModel.loadAll() },
                )
            }
            is CandidatesUiState.Content -> {
                CandidatesContent(
                    state = s,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onCourseFilterChange = { viewModel.setCourseFilter(it) },
                    onReviewerFilterChange = { viewModel.setReviewerFilter(it) },
                    onSortByChange = { viewModel.setSortBy(it) },
                    onCandidateClick = { selectedCandidate = it },
                    onApprove = { approveCandidateId = it.id },
                    onReject = { rejectCandidateId = it.id },
                )
            }
        }
    }

    // Detail bottom sheet
    selectedCandidate?.let { candidate ->
        CandidateDetailSheet(
            candidate = candidate,
            onDismiss = { selectedCandidate = null },
            onApprove = {
                selectedCandidate = null
                approveCandidateId = candidate.id
            },
            onReject = {
                selectedCandidate = null
                rejectCandidateId = candidate.id
            },
        )
    }

    // Approve dialog
    approveCandidateId?.let { id ->
        val candidate = when (val s = state) {
            is CandidatesUiState.Content -> s.candidates.find { it.id == id }
            else -> null
        }
        if (candidate != null) {
            ApproveCandidateDialog(
                candidate = candidate,
                courses = when (val s = state) {
                    is CandidatesUiState.Content -> s.allCourses
                    else -> emptyList()
                },
                onConfirm = { description, techStacks, reviewNote ->
                    viewModel.approve(id, description, techStacks, reviewNote)
                    approveCandidateId = null
                },
                onDismiss = { approveCandidateId = null }
            )
        }
    }

    // Reject confirmation sheet
    rejectCandidateId?.let { id ->
        ConfirmationSheet(
            visible = true,
            title = "T\u1eeb ch\u1ed1i \u1ee9ng viên",
            message = "B\u1ea1n có ch\u1eafc mu\u1ed1n t\u1eeb ch\u1ed1i kho lưu tr\u1eef này? Hành \u0111\u1ed9ng này không th\u1ec3 hoàn tác.",
            confirmLabel = "T\u1eeb ch\u1ed1i",
            confirmDanger = true,
            onDismiss = { rejectCandidateId = null },
            onConfirm = {
                viewModel.reject(id)
                rejectCandidateId = null
            },
        )
    }
}

@Composable
private fun CandidatesContent(
    state: CandidatesUiState.Content,
    onSearchQueryChange: (String) -> Unit,
    onCourseFilterChange: (Long?) -> Unit,
    onReviewerFilterChange: (String?) -> Unit,
    onSortByChange: (SortBy) -> Unit,
    onCandidateClick: (RepoCandidateResponse) -> Unit,
    onApprove: (RepoCandidateResponse) -> Unit,
    onReject: (RepoCandidateResponse) -> Unit,
) {
    // Reviewer stats row
    if (state.reviewerStats.isNotEmpty()) {
        ObsidianSectionHeader(title = "Phân bổ reviewer")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            state.reviewerStats.forEach { stat ->
                val name = stat.reviewer ?: "Chưa gán"
                val completed = stat.completed
                val remaining = stat.remaining
                val total = completed + remaining
                StatusBadge(
                    label = "$name: $completed/$total",
                    type = when {
                        remaining == 0L -> StatusType.SUCCESS
                        remaining < completed -> StatusType.WARNING
                        else -> StatusType.INFO
                    },
                    filled = remaining == 0L,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
    }

    // Search
    AdminSearchField(
        query = state.searchQuery,
        onQueryChange = onSearchQueryChange,
        placeholder = "Tìm theo tên, chủ nhân, ngôn ngữ, môn học...",
        modifier = Modifier.padding(horizontal = 16.dp),
    )
    Spacer(Modifier.height(8.dp))

    // Course filter chips
    val courseOptions = buildList {
        add(FilterOption("all", "Tất cả"))
        state.allCourses.forEach { course ->
            add(FilterOption(course.id.toString(), "${course.code ?: ""} - ${course.name ?: ""}"))
        }
    }
    val selectedCourseId = state.selectedCourseId?.toString() ?: "all"
    FilterChipGroup(
        options = courseOptions,
        selectedId = selectedCourseId,
        onSelected = { id ->
            onCourseFilterChange(id.toLongOrNull())
        },
    )

    Spacer(Modifier.height(8.dp))

    // Sort row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Sắp xếp:",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
        )
        SortBy.entries.forEach { sort ->
            val selected = state.sortBy == sort
            val label = when (sort) {
                SortBy.RECENT -> "Mới nhất"
                SortBy.OLDEST -> "Cũ nhất"
                SortBy.STARS_DESC -> "Sao giảm dần"
                SortBy.STARS_ASC -> "Sao tăng dần"
            }
            FilterChip(
                selected = selected,
                onClick = { onSortByChange(sort) },
                label = {
                    Text(label, style = MaterialTheme.typography.labelMedium)
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = UITBlueSoft,
                    selectedLabelColor = UITBlue,
                    containerColor = Surface,
                    labelColor = TextSecondary,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Border,
                    selectedBorderColor = UITBlueBorder,
                    enabled = true,
                    selected = selected,
                ),
            )
        }
    }

    Spacer(Modifier.height(4.dp))

    // Candidate list or empty state
    if (state.filteredCandidates.isEmpty()) {
        if (state.searchQuery.isNotBlank() || state.selectedCourseId != null) {
            NoResultsState(query = state.searchQuery.ifBlank { "bộ lọc" })
        } else {
            EmptyState(
                title = "Không có \u1ee9ng viên nào",
                subtitle = "Dùng Quét GitHub \u0111\u1ec3 tìm kho lưu tr\u1eef mới",
                icon = Icons.Rounded.Inventory2,
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(state.filteredCandidates, key = { it.id }) { candidate ->
                RepoCandidateItem(
                    candidate = candidate,
                    onClick = { onCandidateClick(candidate) },
                    onApprove = { onApprove(candidate) },
                    onReject = { onReject(candidate) },
                )
            }
        }
    }
}

@Composable
private fun RepoCandidateItem(
    candidate: RepoCandidateResponse,
    onClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: avatar + name + status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                ObsidianAvatar(name = candidate.githubOwner ?: "?")
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = candidate.githubName ?: "unknown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                    Text(
                        text = candidate.githubOwner ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
                StatusBadge(
                    label = repoStatusLabel(candidate.status),
                    type = when (candidate.status?.uppercase()) {
                        "APPROVED" -> StatusType.SUCCESS
                        "REJECTED" -> StatusType.DANGER
                        "PENDING", null -> StatusType.WARNING
                        else -> StatusType.NEUTRAL
                    },
                )
            }

            // Course
            if (candidate.courseCode != null || candidate.courseName != null) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.School,
                        contentDescription = null,
                        tint = UITBlue,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${candidate.courseCode ?: ""} - ${candidate.courseName ?: ""}",
                        style = MaterialTheme.typography.labelMedium,
                        color = UITBlue,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Meta row: language, stars, age
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                candidate.primaryLanguage?.let { lang ->
                    StatusBadge(label = lang, type = StatusType.INFO)
                }
                if (candidate.stars > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Star,
                            contentDescription = null,
                            tint = Warning,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            "${candidate.stars}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                        )
                    }
                }
                if (candidate.lastPushedAt != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = candidate.lastPushedAt.take(10),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                        )
                    }
                }
            }

            // Description
            candidate.description?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Assigned reviewer
            candidate.assignedReviewer?.let { reviewer ->
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Person,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Người review: $reviewer",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Divider)

            // Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (candidate.githubUrl != null) {
                    TextButton(
                        onClick = { uriHandler.openUri(candidate.githubUrl) },
                    ) {
                        Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("GitHub", style = MaterialTheme.typography.labelMedium)
                    }
                }
                Spacer(Modifier.weight(1f))
                AdminSecondaryButton(
                    text = "Từ chối",
                    onClick = onReject,
                    icon = Icons.Rounded.Close,
                )
                AdminPrimaryButton(
                    text = "Duyệt",
                    onClick = onApprove,
                    icon = Icons.Rounded.CheckCircle,
                )
            }
        }
    }
}

// ── Detail Bottom Sheet ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CandidateDetailSheet(
    candidate: RepoCandidateResponse,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = Surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Divider) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            // Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                ObsidianAvatar(name = candidate.githubOwner ?: "?", size = 48)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = candidate.githubName ?: "unknown",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    Text(
                        text = candidate.githubOwner ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
                StatusBadge(
                    label = repoStatusLabel(candidate.status),
                    type = when (candidate.status?.uppercase()) {
                        "APPROVED" -> StatusType.SUCCESS
                        "REJECTED" -> StatusType.DANGER
                        "PENDING", null -> StatusType.WARNING
                        else -> StatusType.NEUTRAL
                    },
                )
            }

            Spacer(Modifier.height(16.dp))

            // GitHub URL
            if (candidate.githubUrl != null) {
                DetailRow(label = "GitHub", value = candidate.githubUrl)
            }

            // Course
            val courseStr = listOfNotNull(candidate.courseCode, candidate.courseName)
                .joinToString(" - ")
            if (courseStr.isNotBlank()) {
                DetailRow(label = "Môn học", value = courseStr)
            }

            // Description
            candidate.description?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Mô tả",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                )
            }

            Spacer(Modifier.height(12.dp))

            // Languages & Tech
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                candidate.primaryLanguage?.let { lang ->
                    Column {
                        Text(
                            "Ngôn ngữ",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                        )
                        Spacer(Modifier.height(4.dp))
                        StatusBadge(label = lang, type = StatusType.INFO, filled = true)
                    }
                }
                candidate.topics?.takeIf { it.isNotBlank() }?.let { topics ->
                    Column {
                        Text(
                            "Công nghệ",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            topics.split(",").take(5).forEach { t ->
                                StatusBadge(label = t.trim(), type = StatusType.NEUTRAL)
                            }
                            val count = topics.split(",").size
                            if (count > 5) {
                                StatusBadge(label = "+${count - 5}", type = StatusType.NEUTRAL)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Stats: stars, forks, last pushed
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                DetailStat(label = "Sao", value = "${candidate.stars}")
                DetailStat(label = "Fork", value = "${candidate.forks}")
                candidate.lastPushedAt?.let {
                    DetailStat(label = "Đẩy cuối", value = it.take(10))
                }
            }

            // File tree (evidence)
            candidate.fileTree?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Cấu trúc dự án",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                )
                Spacer(Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceSecondary,
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(12.dp),
                        maxLines = 8,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            // Readme excerpt (summary)
            candidate.readmeExcerpt?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Tóm tắt README",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Review note
            candidate.reviewNote?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Ghi chú review",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                )
                Spacer(Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = WarningSoft,
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }

            // Assigned reviewer
            candidate.assignedReviewer?.let { reviewer ->
                Spacer(Modifier.height(8.dp))
                DetailRow(label = "Người review", value = reviewer)
            }

            Spacer(Modifier.height(20.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (candidate.githubUrl != null) {
                    OutlinedButton(
                        onClick = { uriHandler.openUri(candidate.githubUrl) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("M\u1edf GitHub")
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AdminSecondaryButton(
                    text = "Từ chối",
                    onClick = onReject,
                    icon = Icons.Rounded.Close,
                    modifier = Modifier.weight(1f),
                )
                AdminPrimaryButton(
                    text = "Duyệt",
                    onClick = onApprove,
                    icon = Icons.Rounded.CheckCircle,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

// ── Approve Dialog ────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApproveCandidateDialog(
    candidate: RepoCandidateResponse,
    courses: List<CourseSummaryResponse>,
    onConfirm: (description: String?, techStacks: List<String>?, reviewNote: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var description by remember { mutableStateOf(candidate.description ?: "") }
    var reviewNote by remember { mutableStateOf("") }
    var techStacksInput by remember { mutableStateOf("") }
    var selectedCourseId by remember { mutableStateOf(candidate.courseId) }
    var courseDropdownExpanded by remember { mutableStateOf(false) }

    val selectedCourseLabel = courses.find { it.id == selectedCourseId }
        ?.let { "${it.code ?: ""} - ${it.name ?: ""}" }
        ?: "Không gán"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Duyệt: ${candidate.githubName}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Course selector
                Text(
                    "Gán môn học (tuỳ chọn)",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                )
                ExposedDropdownMenuBox(
                    expanded = courseDropdownExpanded,
                    onExpandedChange = { courseDropdownExpanded = it },
                ) {
                    OutlinedTextField(
                        value = selectedCourseLabel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        singleLine = true,
                    )
                    ExposedDropdownMenu(
                        expanded = courseDropdownExpanded,
                        onDismissRequest = { courseDropdownExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Không gán") },
                            onClick = { selectedCourseId = null; courseDropdownExpanded = false },
                        )
                        courses.forEach { c ->
                            DropdownMenuItem(
                                text = {
                                    Text("${c.code ?: ""} - ${c.name ?: ""}")
                                },
                                onClick = { selectedCourseId = c.id; courseDropdownExpanded = false },
                            )
                        }
                    }
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    minLines = 2,
                    maxLines = 4,
                )

                // Tech stacks
                OutlinedTextField(
                    value = techStacksInput,
                    onValueChange = { techStacksInput = it },
                    label = { Text("Công nghệ (phẩy phân cách)") },
                    placeholder = { Text("Kotlin, Jetpack Compose, Retrofit") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    minLines = 2,
                    maxLines = 3,
                )

                // Review note
                OutlinedTextField(
                    value = reviewNote,
                    onValueChange = { reviewNote = it },
                    label = { Text("Ghi chú review") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    minLines = 2,
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            AdminPrimaryButton(
                text = "Duyệt",
                onClick = {
                    val desc = description.ifBlank { null }
                    val techs = techStacksInput
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .takeIf { it.isNotEmpty() }
                    val note = reviewNote.ifBlank { null }
                    onConfirm(desc, techs, note)
                },
            )
        },
        dismissButton = {
            AdminSecondaryButton(
                text = "Huỷ",
                onClick = onDismiss,
            )
        },
    )
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "$label: ",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
        )
    }
}

@Composable
private fun DetailStat(
    label: String,
    value: String,
) {
    Column {
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
        )
    }
}

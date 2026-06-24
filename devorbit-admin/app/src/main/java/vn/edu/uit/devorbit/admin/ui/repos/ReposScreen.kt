package vn.edu.uit.devorbit.admin.ui.repos

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.core.designsystem.*
import vn.edu.uit.devorbit.admin.ui.theme.*
import vn.edu.uit.devorbit.admin.ui.components.ObsidianPageHeader
import vn.edu.uit.devorbit.admin.data.remote.dto.ApprovedRepoUpdateRequest
import vn.edu.uit.devorbit.admin.data.remote.dto.CourseSummaryResponse
import vn.edu.uit.devorbit.admin.data.remote.dto.RepoSummaryResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReposScreen(
    viewModel: ReposViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var editingRepo by remember { mutableStateOf<RepoSummaryResponse?>(null) }
    var deleteRepoId by remember { mutableStateOf<Long?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        ObsidianPageHeader(
            title = "Kho lưu trữ",
            subtitle = when (val s = state) {
                is ReposUiState.Content -> "${s.filteredRepos.size} kho"
                else -> null
            },
            actions = {
                AdminPrimaryButton(
                    text = "Đánh giá",
                    onClick = { viewModel.evaluateAll() },
                    icon = Icons.Rounded.Refresh,
                )
            }
        )

        when (val s = state) {
            is ReposUiState.Loading -> {
                InitialLoading()
            }
            is ReposUiState.Error -> {
                ErrorState(
                    title = "Lỗi tải dữ liệu",
                    subtitle = s.message,
                    onRetry = { viewModel.loadRepos() },
                )
            }
            is ReposUiState.Content -> {
                ReposContent(
                    state = s,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onCourseFilterChange = { viewModel.setCourseFilter(it) },
                    onSync = { viewModel.syncRepo(it) },
                    onEdit = { editingRepo = it },
                    onDelete = { deleteRepoId = it.id },
                )
            }
        }
    }

    // Edit dialog
    editingRepo?.let { repo ->
        val courses = when (val s = state) {
            is ReposUiState.Content -> s.allCourses
            else -> emptyList()
        }
        EditRepoDialog(
            repo = repo,
            courses = courses,
            onDismiss = { editingRepo = null },
            onSave = { request ->
                viewModel.updateRepo(repo.id, request)
                editingRepo = null
            },
        )
    }

    // Delete confirmation sheet
    deleteRepoId?.let { id ->
        ConfirmationSheet(
            visible = true,
            title = "Xoá kho lưu trữ",
            message = "Bạn có chắc muốn xoá kho lưu trữ này? Hành động này không thể hoàn tác.",
            confirmLabel = "Xoá",
            confirmDanger = true,
            onDismiss = { deleteRepoId = null },
            onConfirm = {
                viewModel.deleteRepo(id)
                deleteRepoId = null
            },
        )
    }
}

@Composable
private fun ReposContent(
    state: ReposUiState.Content,
    onSearchQueryChange: (String) -> Unit,
    onCourseFilterChange: (Long?) -> Unit,
    onSync: (Long) -> Unit,
    onEdit: (RepoSummaryResponse) -> Unit,
    onDelete: (RepoSummaryResponse) -> Unit,
) {
    // Search
    AdminSearchField(
        query = state.searchQuery,
        onQueryChange = onSearchQueryChange,
        placeholder = "Tìm theo tên, mô tả, ngôn ngữ...",
        modifier = Modifier.padding(horizontal = 16.dp),
    )
    Spacer(Modifier.height(8.dp))

    // Course filter - extract unique courses from repos
    val courseOptions = remember(state.repos) {
        val courseMap = LinkedHashMap<Long, String>()
        state.repos.forEach { repo ->
            repo.courseId?.let { id ->
                val label = "${repo.courseCode ?: ""} - ${repo.courseName ?: ""}"
                if (!courseMap.containsKey(id)) {
                    courseMap[id] = label
                }
            }
        }
        buildList {
            add(FilterOption("all", "Tất cả"))
            courseMap.forEach { (id, label) ->
                add(FilterOption(id.toString(), label))
            }
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

    Spacer(Modifier.height(4.dp))

    // List or empty state
    if (state.filteredRepos.isEmpty()) {
        if (state.searchQuery.isNotBlank() || state.selectedCourseId != null) {
            NoResultsState(query = state.searchQuery.ifBlank { "bộ lọc" })
        } else {
            EmptyState(
                title = "Chưa có kho nào",
                subtitle = "Các kho đã duyệt sẽ xuất hiện ở đây",
                icon = Icons.Rounded.Inventory2,
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(state.filteredRepos, key = { it.id }) { repo ->
                RepoApprovedItem(
                    repo = repo,
                    onSync = { onSync(repo.id) },
                    onEdit = { onEdit(repo) },
                    onDelete = { onDelete(repo) },
                )
            }
        }
    }
}

@Composable
private fun RepoApprovedItem(
    repo: RepoSummaryResponse,
    onSync: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        repo.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                    repo.description?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = TextSecondary,
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Meta badges row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repo.primaryLanguage?.let { lang ->
                    StatusBadge(label = lang, type = StatusType.INFO)
                }
                repo.stars?.let {
                    StatusBadge(
                        label = "$it \u2605",
                        type = StatusType.WARNING,
                    )
                }
                repo.averageRating?.let { rating ->
                    StatusBadge(
                        label = "${"%.1f".format(rating)} \u2B50",
                        type = StatusType.SUCCESS,
                    )
                }
                repo.usefulnessScore?.let { score ->
                    StatusBadge(
                        label = "$score \u2699",
                        type = StatusType.NEUTRAL,
                    )
                }
                repo.usefulnessRating?.let { rating ->
                    StatusBadge(
                        label = rating,
                        type = when (rating.uppercase()) {
                            "HIGH" -> StatusType.SUCCESS
                            "MEDIUM" -> StatusType.WARNING
                            else -> StatusType.NEUTRAL
                        },
                    )
                }
            }

            // Tech stacks
            if (repo.techStacks.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repo.techStacks.take(5).forEach { ts ->
                        StatusBadge(
                            label = ts.name,
                            type = StatusType.NEUTRAL,
                        )
                    }
                    if (repo.techStacks.size > 5) {
                        StatusBadge(
                            label = "+${repo.techStacks.size - 5}",
                            type = StatusType.NEUTRAL,
                        )
                    }
                }
            }

            // Course
            if (repo.courseCode != null || repo.courseName != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "${repo.courseCode ?: ""} - ${repo.courseName ?: ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = UITBlue,
                )
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = Divider)

            // Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                FilledTonalButton(
                    onClick = onSync,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = UITBlueSoft,
                        contentColor = UITBlue,
                    ),
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Đồng bộ", style = MaterialTheme.typography.labelMedium)
                }
                OutlinedButton(
                    onClick = onEdit,
                ) {
                    Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Sửa", style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.weight(1f))
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = Danger),
                ) {
                    Icon(Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Xoá", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

// ── Edit Dialog ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditRepoDialog(
    repo: RepoSummaryResponse,
    courses: List<CourseSummaryResponse>,
    onDismiss: () -> Unit,
    onSave: (ApprovedRepoUpdateRequest) -> Unit,
) {
    var displayName by remember { mutableStateOf(repo.displayName) }
    var description by remember { mutableStateOf(repo.description ?: "") }
    var techStacksInput by remember { mutableStateOf(repo.techStacks.joinToString(", ") { it.name }) }
    var selectedCourseId by remember { mutableStateOf(repo.courseId) }
    var courseDropdownExpanded by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val selectedCourseLabel = courses.find { it.id == selectedCourseId }
        ?.let { "${it.code ?: ""} - ${it.name ?: ""}" }
        ?: "Không gán"

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = {
            Text(
                "Sửa kho lưu trữ",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Name
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Tên hiển thị") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                )
                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả") },
                    maxLines = 4,
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                )
                // Tech stacks
                OutlinedTextField(
                    value = techStacksInput,
                    onValueChange = { techStacksInput = it },
                    label = { Text("Công nghệ (phẩy phân cách)") },
                    placeholder = { Text("Kotlin, Jetpack Compose, Retrofit") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                )
                // Course selector
                Text(
                    "Gán môn học",
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
            }
        },
        confirmButton = {
            AdminPrimaryButton(
                text = "Lưu",
                onClick = {
                    isSubmitting = true
                    val techs = techStacksInput
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .takeIf { it.isNotEmpty() }
                    onSave(
                        ApprovedRepoUpdateRequest(
                            displayName = displayName,
                            description = description.ifBlank { null },
                            techStacks = techs,
                            courseId = selectedCourseId,
                        )
                    )
                },
                enabled = displayName.isNotBlank() && !isSubmitting,
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

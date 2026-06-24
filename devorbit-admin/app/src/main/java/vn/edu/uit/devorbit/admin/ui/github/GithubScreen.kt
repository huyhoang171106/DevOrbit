package vn.edu.uit.devorbit.admin.ui.github

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.core.designsystem.*
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GithubScreen(
    viewModel: GithubViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedCourseId by remember { mutableStateOf<Long?>(null) }
    var queryInput by remember { mutableStateOf("") }
    var courseDropdownExpanded by remember { mutableStateOf(false) }
    var showClearLogsConfirmation by remember { mutableStateOf(false) }

    val isScanning = state is GithubUiState.Scanning

    Column(modifier = Modifier.fillMaxSize()) {
        ObsidianPageHeader(
            title = "Quét GitHub",
            subtitle = if (isScanning) "Đang quét..." else "Tìm & quét kho lưu trữ từ GitHub",
            actions = {
                if (state is GithubUiState.ScanResult) {
                    IconButton(onClick = { viewModel.clearResults() }) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = "Quay lại nhật ký",
                            tint = TextSecondary,
                        )
                    }
                }
            },
        )

        when (val s = state) {
            is GithubUiState.Loading -> {
                InitialLoading()
            }
            is GithubUiState.Error -> {
                ErrorState(
                    title = "Có lỗi xảy ra",
                    subtitle = s.message,
                    onRetry = { viewModel.clearError() },
                )
            }
            is GithubUiState.Idle -> {
                GithubIdleContent(
                    scanLogs = s.scanLogs,
                    allCourses = s.allCourses,
                    selectedCourseId = selectedCourseId,
                    onCourseSelected = { selectedCourseId = it },
                    queryInput = queryInput,
                    onQueryChange = { queryInput = it },
                    courseDropdownExpanded = courseDropdownExpanded,
                    onCourseDropdownExpandedChange = { courseDropdownExpanded = it },
                    onScan = {
                        selectedCourseId?.let { courseId ->
                            if (queryInput.isNotBlank()) {
                                viewModel.scan(courseId, queryInput.trim())
                            }
                        }
                    },
                    onScanAll = { viewModel.scanAll() },
                    onClearLogs = { showClearLogsConfirmation = true },
                )
            }
            is GithubUiState.Scanning -> {
                GithubScanningContent(
                    isScanAll = s.isScanAll,
                    courseId = s.courseId,
                    query = s.query,
                    allCourses = s.allCourses,
                    scanLogs = s.scanLogs,
                )
            }
            is GithubUiState.ScanResult -> {
                GithubScanResultContent(
                    results = s.results,
                    scanLogs = s.scanLogs,
                    scanMessage = s.scanMessage,
                    onDismissMessage = { viewModel.clearScanMessage() },
                    onClearResults = { viewModel.clearResults() },
                    onClearLogs = { showClearLogsConfirmation = true },
                )
            }
        }
    }

    // Clear logs confirmation sheet
    ConfirmationSheet(
        visible = showClearLogsConfirmation,
        title = "Xoá nhật ký quét",
        message = "Bạn có chắc muốn xoá toàn bộ nhật ký quét? Hành động này không thể hoàn tác.",
        confirmLabel = "Xoá",
        confirmDanger = true,
        onDismiss = { showClearLogsConfirmation = false },
        onConfirm = {
            viewModel.clearLogs()
            showClearLogsConfirmation = false
        },
    )
}

// ── Idle Content ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GithubIdleContent(
    scanLogs: List<String>,
    allCourses: List<CourseSummaryResponse>,
    selectedCourseId: Long?,
    onCourseSelected: (Long?) -> Unit,
    queryInput: String,
    onQueryChange: (String) -> Unit,
    courseDropdownExpanded: Boolean,
    onCourseDropdownExpandedChange: (Boolean) -> Unit,
    onScan: () -> Unit,
    onScanAll: () -> Unit,
    onClearLogs: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Scan form
        ObsidianSectionHeader(title = "Quét theo môn học")

        // Course dropdown + query input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Course dropdown
            ExposedDropdownMenuBox(
                expanded = courseDropdownExpanded,
                onExpandedChange = onCourseDropdownExpandedChange,
                modifier = Modifier.weight(1f),
            ) {
                val selectedLabel = allCourses.find { it.id == selectedCourseId }
                    ?.let { "${it.code ?: ""} - ${it.name ?: ""}" }
                    ?: "Chọn môn học"

                OutlinedTextField(
                    value = selectedLabel,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseDropdownExpanded)
                    },
                    modifier = Modifier.menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = true,
                )
                ExposedDropdownMenu(
                    expanded = courseDropdownExpanded,
                    onDismissRequest = { onCourseDropdownExpandedChange(false) },
                ) {
                    DropdownMenuItem(
                        text = { Text("Chọn môn học") },
                        onClick = {
                            onCourseSelected(null)
                            onCourseDropdownExpandedChange(false)
                        },
                    )
                    allCourses.forEach { course ->
                        DropdownMenuItem(
                            text = {
                                Text("${course.code ?: ""} - ${course.name ?: ""}")
                            },
                            onClick = {
                                onCourseSelected(course.id)
                                onCourseDropdownExpandedChange(false)
                            },
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = queryInput,
                onValueChange = onQueryChange,
                label = { Text("Truy vấn") },
                placeholder = { Text("kotlin compose graphql...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
            )
            AdminPrimaryButton(
                text = "Quét",
                onClick = onScan,
                enabled = selectedCourseId != null && queryInput.isNotBlank(),
                icon = Icons.Rounded.Search,
            )
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = Divider, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))

        // Scan All + Clear Logs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AdminPrimaryButton(
                text = "Quét tất cả",
                onClick = onScanAll,
                icon = Icons.Rounded.PlayArrow,
                modifier = Modifier.weight(1f),
            )
            AdminSecondaryButton(
                text = "Xoá log",
                onClick = onClearLogs,
                icon = Icons.Rounded.DeleteSweep,
                enabled = scanLogs.isNotEmpty(),
            )
        }

        Spacer(Modifier.height(12.dp))

        // Scan logs
        if (scanLogs.isEmpty()) {
            EmptyState(
                title = "Chưa có nhật ký nào",
                subtitle = "Quét kho lưu trữ để xem nhật ký",
                icon = Icons.Rounded.List,
                modifier = Modifier.weight(1f),
            )
        } else {
            ObsidianSectionHeader(
                title = "Nhật ký quét",
                action = {
                    StatusBadge(
                        label = "${scanLogs.size} bản ghi",
                        type = StatusType.NEUTRAL,
                    )
                },
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f),
            ) {
                itemsIndexed(scanLogs, key = { index, log -> "$index:$log" }) { _, log ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceTertiary.copy(alpha = 0.5f),
                    ) {
                        Text(
                            text = log,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

// ── Scanning Content ──────────────────────────────────────────────────────────

@Composable
private fun GithubScanningContent(
    isScanAll: Boolean,
    courseId: Long?,
    query: String?,
    allCourses: List<CourseSummaryResponse>,
    scanLogs: List<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(
            color = UITBlue,
            strokeWidth = 3.dp,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (isScanAll) "Đang quét tất cả môn học..."
            else "Đang quét GitHub...",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
        if (!isScanAll && courseId != null) {
            val courseLabel = allCourses.find { it.id == courseId }
                ?.let { "${it.code ?: ""} - ${it.name ?: ""}" }
            if (courseLabel != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = courseLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
        }
        if (!isScanAll && query != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Truy vấn: \"$query\"",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )
        }
        Spacer(Modifier.height(24.dp))

        // Show recent logs while scanning
        if (scanLogs.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SurfaceSecondary,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Nhật ký gần đây",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                    )
                    Spacer(Modifier.height(4.dp))
                    scanLogs.takeLast(5).forEach { log ->
                        Text(
                            text = log,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

// ── Scan Result Content ───────────────────────────────────────────────────────

@Composable
private fun GithubScanResultContent(
    results: List<RepoCandidateResponse>,
    scanLogs: List<String>,
    scanMessage: String?,
    onDismissMessage: () -> Unit,
    onClearResults: () -> Unit,
    onClearLogs: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Success banner
        scanMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = SuccessSoft),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = Success,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(
                        onClick = onDismissMessage,
                        modifier = Modifier.size(20.dp),
                    ) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Đóng",
                            tint = Success.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }

        if (results.isNotEmpty()) {
            ObsidianSectionHeader(
                title = "Kết quả quét (${results.size})",
                action = {
                    TextButton(onClick = onClearResults) {
                        Text("Quay lại", style = MaterialTheme.typography.labelMedium)
                    }
                },
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(results, key = { it.id }) { candidate ->
                    ScanResultCard(candidate = candidate)
                }
            }
        } else if (scanMessage == null) {
            // No results but no scan message either (single scan with no results)
            EmptyState(
                title = "Không tìm thấy kết quả",
                subtitle = "Thử truy vấn khác hoặc quét tất cả",
                icon = Icons.Rounded.SearchOff,
                action = {
                    AdminSecondaryButton(
                        text = "Quay lại",
                        onClick = onClearResults,
                    )
                },
                modifier = Modifier.weight(1f),
            )
        }

        // Logs section below results
        if (scanLogs.isNotEmpty()) {
            HorizontalDivider(color = Divider, modifier = Modifier.padding(horizontal = 16.dp))
            ObsidianSectionHeader(
                title = "Nhật ký quét",
                action = {
                    AdminSecondaryButton(
                        text = "Xoá log",
                        onClick = onClearLogs,
                        icon = Icons.Rounded.DeleteSweep,
                    )
                },
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(if (results.isEmpty()) 1f else 0.4f),
            ) {
                itemsIndexed(scanLogs, key = { index, log -> "$index:$log" }) { _, log ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceTertiary.copy(alpha = 0.5f),
                    ) {
                        Text(
                            text = log,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

// ── Scan Result Card ──────────────────────────────────────────────────────────

@Composable
private fun ScanResultCard(candidate: RepoCandidateResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: avatar + name + status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
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

            Spacer(Modifier.height(8.dp))

            // Meta: language, stars, forks, course
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
                if (candidate.forks > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.CallSplit,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            "${candidate.forks}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                        )
                    }
                }
                if (candidate.courseCode != null) {
                    StatusBadge(label = candidate.courseCode, type = StatusType.SUCCESS)
                }
            }

            // Topics
            candidate.topics?.takeIf { it.isNotBlank() }?.let { topics ->
                Spacer(Modifier.height(6.dp))
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

            // README excerpt
            candidate.readmeExcerpt?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

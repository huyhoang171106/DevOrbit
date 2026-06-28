package vn.edu.uit.devorbit.admin.ui.candidates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.*

@Composable
fun CandidatesScreen(
    viewModel: CandidatesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showApproveDialog by remember { mutableStateOf<Long?>(null) }
    var rejectId by remember { mutableStateOf<Long?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredCandidates = remember(state.candidates, searchQuery) {
        if (searchQuery.isBlank()) state.candidates
        else state.candidates.filter {
            it.githubName.orEmpty().contains(searchQuery, ignoreCase = true) ||
                it.githubOwner.orEmpty().contains(searchQuery, ignoreCase = true) ||
                it.primaryLanguage.orEmpty().contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ObsidianPageHeader(
            title = "Duyệt kho lưu trữ",
            subtitle = "${state.candidates.size} ứng viên đang chờ"
        )

        ObsidianDivider()

        // Reviewer stats row
        if (state.reviewerStats.isNotEmpty()) {
            ObsidianSectionHeader(title = "Phân bổ reviewer")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.reviewerStats.forEach { stat ->
                    val name = stat.reviewer ?: "Chưa gán"
                    val completed = stat.completed
                    val remaining = stat.remaining
                    ObsidianBadge(
                        text = "$name: $completed/${
                            completed + remaining
                        }",
                        color = when {
                            remaining == 0L -> ObsidianPalette.Green500
                            remaining < completed -> ObsidianPalette.Amber500
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
        }

        // Search
        ObsidianSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Tìm theo tên, chủ nhân, ngôn ngữ...",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(8.dp))

        if (state.isLoading) {
            ObsidianLoadingBox()
        } else if (state.error != null) {
            ObsidianEmptyState(
                message = "Lỗi tải dữ liệu",
                subtitle = state.error,
                icon = Icons.Rounded.ErrorOutline
            )
        } else if (filteredCandidates.isEmpty()) {
            ObsidianEmptyState(
                message = "Không có ứng viên nào chờ duyệt",
                subtitle = "Dùng Quét GitHub để tìm kho lưu trữ mới",
                icon = Icons.Rounded.Inventory2
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCandidates, key = { it.id }) { candidate ->
                    CandidateRow(
                        candidate = candidate,
                        onApprove = { showApproveDialog = candidate.id },
                        onReject = { rejectId = candidate.id }
                    )
                }
            }
        }
    }

    // Approve dialog
    showApproveDialog?.let { id ->
        val candidate = state.candidates.find { it.id == id }
        if (candidate != null) {
            ApproveCandidateDialog(
                candidate = candidate,
                courses = state.allCourses,
                onConfirm = { description, techStacks, reviewNote ->
                    viewModel.approve(id, description, techStacks, reviewNote)
                    showApproveDialog = null
                },
                onDismiss = { showApproveDialog = null }
            )
        }
    }

    // Reject confirm
    rejectId?.let { id ->
        ObsidianConfirmDialog(
            title = "Từ chối ứng viên",
            message = "Bạn có chắc muốn từ chối kho lưu trữ này? Hành động này không thể hoàn tác.",
            confirmLabel = "Từ chối",
            isDestructive = true,
            onConfirm = {
                viewModel.reject(id)
                rejectId = null
            },
            onDismiss = { rejectId = null }
        )
    }
}

// ── Candidate Row ──────────────────────────────────────────────────────────
@Composable
private fun CandidateRow(
    candidate: RepoCandidateResponse,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ObsidianShape.md,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row: avatar + name + status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ObsidianAvatar(name = candidate.githubOwner ?: "?")

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = candidate.githubName ?: "unknown",
                        style = ObsidianType.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = candidate.githubOwner ?: "",
                        style = ObsidianType.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                CandidateStatusBadge(status = candidate.status)
            }

            // Description
            candidate.description?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    style = ObsidianType.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(8.dp))

            // Meta row: language, stars, forks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                candidate.primaryLanguage?.let { lang ->
                    ObsidianBadge(text = lang, color = ObsidianPalette.Blue500)
                }
                if (candidate.stars > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Star,
                            contentDescription = null,
                            tint = ObsidianPalette.Amber500,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${candidate.stars}",
                            style = ObsidianType.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (candidate.forks > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.CallSplit,
                            contentDescription = null,
                            tint = ObsidianPalette.Gray500,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${candidate.forks}",
                            style = ObsidianType.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                candidate.courseCode?.let { code ->
                    ObsidianBadge(text = code, color = ObsidianPalette.Green500)
                }
            }

            // Topics
            candidate.topics?.takeIf { it.isNotBlank() }?.let { topics ->
                Spacer(Modifier.height(6.dp))
                Text(
                    text = topics,
                    style = ObsidianType.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // README excerpt
            candidate.readmeExcerpt?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    style = ObsidianType.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Assigned reviewer
            candidate.assignedReviewer?.let { reviewer ->
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Person,
                        contentDescription = null,
                        tint = ObsidianPalette.Gray500,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Người review: $reviewer",
                        style = ObsidianType.labelSmall,
                        color = ObsidianPalette.Gray500
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            ObsidianDivider()

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (candidate.githubUrl != null) {
                    TextButton(
                        onClick = { uriHandler.openUri(candidate.githubUrl) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("GitHub", style = ObsidianType.labelMedium)
                    }
                }
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ObsidianPalette.Red500)
                ) {
                    Text("Từ chối", style = ObsidianType.labelMedium)
                }
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Duyệt", style = ObsidianType.labelMedium)
                }
            }
        }
    }
}

// ── Status Badge ───────────────────────────────────────────────────────────
@Composable
private fun CandidateStatusBadge(status: String?) {
    ObsidianBadge(text = repoStatusLabel(status), color = when (status?.uppercase()) {
        "APPROVED" -> ObsidianPalette.Green500
        "REJECTED" -> ObsidianPalette.Red500
        "PENDING", null -> ObsidianPalette.Amber500
        else -> ObsidianPalette.Gray500
    })
}

// ── Approve Dialog ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApproveCandidateDialog(
    candidate: RepoCandidateResponse,
    courses: List<CourseSummaryResponse>,
    onConfirm: (description: String?, techStacks: List<String>?, reviewNote: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var reviewNote by remember { mutableStateOf("") }
    var techStacksInput by remember { mutableStateOf("") }
    var selectedCourseId by remember { mutableStateOf(candidate.courseId) }
    var courseDropdownExpanded by remember { mutableStateOf(false) }

    val selectedCourseLabel = courses.find { it.id == selectedCourseId }
        ?.let { "${it.code ?: ""} - ${it.name ?: ""}" }
        ?: "Không gán"

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ObsidianShape.md,
        title = {
            Text(
                "Duyệt: ${candidate.githubName}",
                style = ObsidianType.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Course selector
                Text("Gán môn học (tuỳ chọn)", style = ObsidianType.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                ExposedDropdownMenuBox(
                    expanded = courseDropdownExpanded,
                    onExpandedChange = { courseDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCourseLabel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = ObsidianShape.sm,
                        textStyle = ObsidianType.bodyMedium,
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = courseDropdownExpanded,
                        onDismissRequest = { courseDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Không gán", style = ObsidianType.bodyMedium) },
                            onClick = { selectedCourseId = null; courseDropdownExpanded = false }
                        )
                        courses.forEach { c ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "${c.code ?: ""} - ${c.name ?: ""}",
                                        style = ObsidianType.bodyMedium
                                    )
                                },
                                onClick = { selectedCourseId = c.id; courseDropdownExpanded = false }
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
                    shape = ObsidianShape.sm,
                    textStyle = ObsidianType.bodyMedium,
                    minLines = 2,
                    maxLines = 4
                )

                // Tech stacks
                OutlinedTextField(
                    value = techStacksInput,
                    onValueChange = { techStacksInput = it },
                    label = { Text("Công nghệ (phẩy phân cách)", style = ObsidianType.bodyMedium) },
                    placeholder = { Text("Kotlin, Jetpack Compose, Retrofit") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = ObsidianShape.sm,
                    textStyle = ObsidianType.bodyMedium,
                    minLines = 2,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = ObsidianPalette.Blue500
                )
            ) {
                Text("Duyệt", style = ObsidianType.labelMedium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huỷ", style = ObsidianType.labelMedium)
            }
        }
    )
}

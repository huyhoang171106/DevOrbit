package vn.edu.uit.devorbit.admin.ui.students

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.data.remote.dto.AdminStudentResponse
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.*

@Composable
fun StudentsScreen(
    viewModel: StudentsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var selectedStudent by remember { mutableStateOf<AdminStudentResponse?>(null) }
    var toggleTarget by remember { mutableStateOf<AdminStudentResponse?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Show toggle errors via Snackbar
    val toggleError = (state as? StudentsUiState.Success)?.toggleError
    LaunchedEffect(toggleError) {
        if (toggleError != null) {
            snackbarHostState.showSnackbar(
                message = toggleError,
                actionLabel = "Đóng"
            )
            viewModel.clearToggleError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Page Header ─────────────────────────────────────────────
            ObsidianPageHeader(
                title = "Quản lý sinh viên",
                subtitle = (state as? StudentsUiState.Success)?.let {
                    "${it.students.size} sinh viên"
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = "Làm mới",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )

            ObsidianDivider()

            // ── Search ──────────────────────────────────────────────────
            ObsidianSearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = "Tìm theo mã, tên hoặc email..."
            )

            // ── Content ─────────────────────────────────────────────────
            when (val currentState = state) {
                is StudentsUiState.InitialLoading -> {
                    StudentListSkeleton(modifier = Modifier.weight(1f))
                }

                is StudentsUiState.Error -> {
                    StudentsErrorState(
                        message = currentState.message,
                        onRetry = {
                            viewModel.loadStudents(searchQuery.ifBlank { null })
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                is StudentsUiState.Empty -> {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        ObsidianEmptyState(
                            message = "Không tìm thấy sinh viên",
                            subtitle = if (searchQuery.isNotBlank())
                                "Thử lại với từ khóa khác"
                            else
                                "Chưa có dữ liệu sinh viên",
                            icon = Icons.Rounded.Person,
                        )
                    }
                }

                is StudentsUiState.Success -> {
                    val successState = currentState

                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                top = 4.dp,
                                bottom = 24.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            items(
                                items = successState.students,
                                key = { it.id }
                            ) { student ->
                                StudentItem(
                                    student = student,
                                    onClick = { selectedStudent = student },
                                    onToggleActive = { toggleTarget = student }
                                )
                            }
                        }

                        // Pull-to-refresh indicator shown when refreshing
                        if (successState.isRefreshing) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter),
                                color = ObsidianPalette.Blue500,
                                trackColor = ObsidianPalette.Blue500.copy(alpha = 0.15f)
                            )
                        }
                    }
                }
            }

            // ── Extra spacing at bottom ─────────────────────────────────
            Spacer(Modifier.height(8.dp))
        }
    }

    // ── Student Detail Dialog ──────────────────────────────────────────
    selectedStudent?.let { student ->
        StudentDetailDialog(
            student = student,
            onDismiss = { selectedStudent = null },
            onToggleActive = {
                toggleTarget = student
                selectedStudent = null
            }
        )
    }

    // ── Toggle Confirmation Dialog ─────────────────────────────────────
    toggleTarget?.let { student ->
        ObsidianConfirmDialog(
            title = if (student.active) "Vô hiệu hóa sinh viên" else "Kích hoạt sinh viên",
            message = buildToggleMessage(student),
            onConfirm = {
                viewModel.toggleActive(student.id)
                toggleTarget = null
            },
            onDismiss = { toggleTarget = null },
            confirmLabel = if (student.active) "Vô hiệu hóa" else "Kích hoạt",
            isDestructive = student.active
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// STUDENT ITEM
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun StudentItem(
    student: AdminStudentResponse,
    onClick: () -> Unit,
    onToggleActive: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = ObsidianShape.sm,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // ── Top row: avatar + info ─────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                ObsidianAvatar(name = student.fullName, size = 40)

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Full name
                    Text(
                        text = student.fullName,
                        style = ObsidianType.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // MSSV (monospace)
                    Text(
                        text = student.studentCode,
                        style = ObsidianType.bodySmall.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Email
                    Text(
                        text = student.email,
                        style = ObsidianType.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // ── Bottom row: badges + toggle ────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Active / Inactive badge
                ObsidianBadge(
                    text = if (student.active) "Hoạt động" else "Vô hiệu",
                    color = if (student.active) ObsidianPalette.Green500
                    else ObsidianPalette.Red500
                )

                // Verified / Unverified badge
                ObsidianBadge(
                    text = if (student.emailVerified) "Đã xác thực" else "Chưa xác thực",
                    color = if (student.emailVerified) ObsidianPalette.Blue500
                    else ObsidianPalette.Gray500
                )

                Spacer(Modifier.weight(1f))

                // Toggle active switch
                Switch(
                    checked = student.active,
                    onCheckedChange = { onToggleActive() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ObsidianPalette.Green500,
                        checkedTrackColor = ObsidianPalette.Green500.copy(alpha = 0.25f),
                        checkedBorderColor = ObsidianPalette.Green500.copy(alpha = 0.4f),
                        uncheckedThumbColor = ObsidianPalette.Red500,
                        uncheckedTrackColor = ObsidianPalette.Red500.copy(alpha = 0.15f),
                        uncheckedBorderColor = ObsidianPalette.Red500.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.height(28.dp)
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// STUDENT DETAIL DIALOG
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun StudentDetailDialog(
    student: AdminStudentResponse,
    onDismiss: () -> Unit,
    onToggleActive: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            ObsidianAvatar(name = student.fullName, size = 48)
        },
        title = {
            Text(
                text = student.fullName,
                style = ObsidianType.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                // ID
                ObsidianDataRow(
                    label = "ID",
                    value = "#${student.id}"
                )
                // MSSV
                ObsidianDataRow(
                    label = "MSSV",
                    value = student.studentCode
                )
                // Email
                ObsidianDataRow(
                    label = "Email",
                    value = student.email
                )
                Spacer(Modifier.height(4.dp))
                // Active status
                ObsidianDataRow(
                    label = "Trạng thái",
                    value = if (student.active) "Đang hoạt động" else "Đã vô hiệu"
                )
                // Email verified
                ObsidianDataRow(
                    label = "Xác thực email",
                    value = if (student.emailVerified) "Đã xác thực" else "Chưa xác thực"
                )

                Spacer(Modifier.height(16.dp))

                // Toggle active button
                Button(
                    onClick = onToggleActive,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (student.active) ObsidianPalette.Red500
                        else ObsidianPalette.Green500
                    )
                ) {
                    Icon(
                        imageVector = if (student.active) Icons.Rounded.Block
                        else Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (student.active) "Vô hiệu hóa" else "Kích hoạt lại",
                        style = ObsidianType.labelLarge
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng", style = ObsidianType.labelLarge)
            }
        }
    )
}

// ══════════════════════════════════════════════════════════════════════════════
// LOADING SKELETON
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun StudentListSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar skeleton
                ObsidianSkeleton(width = 0.1f, height = 40)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    // Name skeleton
                    ObsidianSkeleton(width = 0.55f, height = 16)
                    Spacer(Modifier.height(6.dp))
                    // Code skeleton
                    ObsidianSkeleton(width = 0.35f, height = 12)
                    Spacer(Modifier.height(4.dp))
                    // Email skeleton
                    ObsidianSkeleton(width = 0.5f, height = 12)
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// ERROR STATE
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun StudentsErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Warning,
            contentDescription = null,
            tint = ObsidianPalette.Red500.copy(alpha = 0.5f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Đã xảy ra lỗi",
            style = ObsidianType.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = message,
            style = ObsidianType.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text("Thử lại", style = ObsidianType.labelLarge)
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// HELPERS
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Builds a detailed confirmation message explaining consequences
 * of toggling a student's active status, in Vietnamese.
 */
private fun buildToggleMessage(student: AdminStudentResponse): String {
    return if (student.active) {
        buildString {
            append("Bạn có chắc muốn vô hiệu hóa ")
            append(student.fullName)
            append(" (")
            append(student.studentCode)
            append(")?\n\nHậu quả:\n")
            append("• Sinh viên sẽ không thể đăng nhập vào hệ thống\n")
            append("• Toàn bộ quyền truy cập khóa học sẽ bị thu hồi\n")
            append("• Dữ liệu cá nhân và lịch sử hoạt động vẫn được giữ nguyên\n\n")
            append("Bạn có thể kích hoạt lại bất cứ lúc nào.")
        }
    } else {
        buildString {
            append("Bạn có chắc muốn kích hoạt lại ")
            append(student.fullName)
            append(" (")
            append(student.studentCode)
            append(")?\n\nSinh viên sẽ được:\n")
            append("• Khôi phục quyền đăng nhập\n")
            append("• Truy cập lại các khóa học đã đăng ký\n\n")
            append("Thao tác này có thể hoàn tác.")
        }
    }
}

package vn.edu.uit.devorbit.admin.ui.students

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.admin.data.remote.dto.AdminStudentResponse
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianPalette
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianShape
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianType
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun StudentsScreen(
    viewModel: StudentsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var toggleTarget by remember { mutableStateOf<AdminStudentResponse?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // ── Page Header ───────────────────────────────────────────────
        ObsidianPageHeader(
            title = "Sinh viên",
            subtitle = if (!state.isLoading && state.students.isNotEmpty())
                "${state.students.size} sinh viên" else null,
        )

        ObsidianDivider()

        // ── Search bar ────────────────────────────────────────────────
        ObsidianSearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                viewModel.search(it)
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = "Tìm theo mã, tên, email..."
        )

        // ── Content ───────────────────────────────────────────────────
        when {
            state.isLoading -> {
                ObsidianLoadingBox(modifier = Modifier.weight(1f))
            }

            state.students.isEmpty() -> {
                ObsidianEmptyState(
                    message = "Không tìm thấy sinh viên",
                    subtitle = if (searchQuery.isNotBlank())
                        "Thử từ khóa khác" else "Chưa có dữ liệu",
                    icon = Icons.Rounded.Person,
                    modifier = Modifier.weight(1f)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(
                        items = state.students,
                        key = { it.id }
                    ) { student ->
                        StudentRow(
                            student = student,
                            onToggleActive = { toggleTarget = student }
                        )
                    }
                }
            }
        }
    }

    // ── Confirm Dialog ──────────────────────────────────────────────
    toggleTarget?.let { student ->
        ObsidianConfirmDialog(
            title = if (student.active) "Vô hiệu sinh viên" else "Kích hoạt sinh viên",
            message = if (student.active)
                "Vô hiệu hoá tài khoản của ${student.fullName} (${student.studentCode})?"
            else
                "Kích hoạt lại tài khoản của ${student.fullName} (${student.studentCode})?",
            onConfirm = {
                viewModel.toggleActive(student.id)
                toggleTarget = null
            },
            onDismiss = { toggleTarget = null },
            confirmLabel = if (student.active) "Vô hiệu" else "Kích hoạt",
            isDestructive = student.active
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// STUDENT ROW
// ══════════════════════════════════════════════════════════════════════════════


@Composable
private fun StudentRow(
    student: AdminStudentResponse,
    onToggleActive: () -> Unit
) {
    val isActive = student.active

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = ObsidianShape.sm,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive)
                ObsidianPalette.Green50.copy(alpha = 0.35f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Avatar with status ring ────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(44.dp)
            ) {
                ObsidianAvatar(name = student.fullName, size = 36)
                // Status dot
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) ObsidianPalette.Green500
                            else ObsidianPalette.Red500
                        )
                        .padding(2.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            // ── Info ───────────────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = student.fullName,
                        style = ObsidianType.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isActive)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = if (isActive) "Đang hoạt động" else "Đã vô hiệu",
                        style = ObsidianType.labelSmall,
                        color = if (isActive) ObsidianPalette.Green600 else ObsidianPalette.Red500,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.height(1.dp))
                Text(
                    text = "${student.studentCode} · ${student.email}",
                    style = ObsidianType.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = if (isActive) 0.9f else 0.45f
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(8.dp))

            // ── Toggle Switch ──────────────────────────────────────────
            Switch(
                checked = isActive,
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

package vn.edu.uit.devorbit.admin.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Note
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.admin.data.remote.dto.NoteResponse
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var deleteId by remember { mutableStateOf<Long?>(null) }

    Column(Modifier.fillMaxSize()) {
        ObsidianPageHeader(
            title = "Ghi chú sinh viên",
            subtitle = "${state.notes.size} ghi chú"
        )

        ObsidianDivider()

        when {
            state.isLoading -> ObsidianLoadingBox()
            state.error != null -> ObsidianEmptyState(
                message = "Lỗi tải dữ liệu",
                subtitle = state.error
            )
            state.notes.isEmpty() -> ObsidianEmptyState(
                message = "Chưa có ghi chú nào",
                subtitle = "Ghi chú sẽ xuất hiện khi sinh viên tạo",
                icon = Icons.Rounded.Note
            )
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.notes, key = { it.id }) { note ->
                    NoteCard(
                        note = note,
                        onDelete = { deleteId = note.id }
                    )
                }
            }
        }
    }

    // ── Delete confirmation ──────────────────────────────────────────────
    deleteId?.let { id ->
        ObsidianConfirmDialog(
            title = "Xoá ghi chú",
            message = "Bạn có chắc muốn xoá ghi chú này? Hành động không thể hoàn tác.",
            onConfirm = { viewModel.delete(id); deleteId = null },
            onDismiss = { deleteId = null },
            isDestructive = true
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// NOTE CARD
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun NoteCard(
    note: NoteResponse,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ObsidianShape.md,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ── Header row ───────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        note.title ?: "Không có tiêu đề",
                        style = ObsidianType.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        note.studentName,
                        style = ObsidianType.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    note.studentCode.let { code ->
                        Text(
                            code,
                            style = ObsidianType.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = "Xoá",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            ObsidianDivider()
            Spacer(Modifier.height(8.dp))

            // ── Content preview ──────────────────────────────────────────
            note.contentMarkdown?.let { md ->
                Text(
                    md,
                    style = ObsidianType.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // ── Badges row ───────────────────────────────────────────────
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Target type badge
                note.targetType?.let { type ->
                    ObsidianBadge(
                        text = noteTargetTypeLabel(type),
                        color = when (type) {
                            "COURSE" -> MaterialTheme.colorScheme.primary
                            "REPO" -> ObsidianPalette.Green500
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                // Snippets count badge
                if (note.snippets.isNotEmpty()) {
                    ObsidianBadge(
                        text = snippetCountLabel(note.snippets.size),
                        color = ObsidianPalette.Amber500
                    )
                }

                // Timestamps
                note.updatedAt?.let {
                    Spacer(Modifier.weight(1f))
                    Text(
                        "Cập nhật: $it",
                        style = ObsidianType.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

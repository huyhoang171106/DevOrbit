package vn.edu.uit.devorbit.admin.ui.repos

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.data.remote.dto.ApprovedRepoUpdateRequest
import vn.edu.uit.devorbit.admin.data.remote.dto.RepoSummaryResponse
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianPalette
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianShape
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianType

@Composable
fun ReposScreen(
    viewModel: ReposViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var editingRepo by remember { mutableStateOf<RepoSummaryResponse?>(null) }
    var deleteId by remember { mutableStateOf<Long?>(null) }

    Column(Modifier.fillMaxSize()) {
        ObsidianPageHeader(
            title = "Kho lưu trữ",
            subtitle = "${state.repos.size} kho",
            actions = {
                OutlinedButton(onClick = { viewModel.evaluateAll() }) {
                    Icon(Icons.Rounded.Refresh, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Đánh giá")
                }
            }
        )

        when {
            state.isLoading && state.repos.isEmpty() -> ObsidianLoadingBox()
            state.repos.isEmpty() -> ObsidianEmptyState(message = "Chưa có kho nào")
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.repos, key = { it.id }) { repo ->
                        RepoCard(
                            repo = repo,
                            onSync = { viewModel.syncRepo(repo.id) },
                            onEdit = { editingRepo = repo },
                            onDelete = { deleteId = repo.id }
                        )
                    }
                }
            }
        }
    }

    editingRepo?.let { repo ->
        EditRepoDialog(
            repo = repo,
            onDismiss = { editingRepo = null },
            onSave = { request ->
                viewModel.updateRepo(repo.id, request)
                editingRepo = null
            }
        )
    }

    deleteId?.let { id ->
        ObsidianConfirmDialog(
            title = "Xoá kho",
            message = "Bạn có chắc muốn xoá kho này?",
            onConfirm = { viewModel.deleteRepo(id); deleteId = null },
            onDismiss = { deleteId = null },
            isDestructive = true
        )
    }
}

@Composable
private fun RepoCard(
    repo: RepoSummaryResponse,
    onSync: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = ObsidianShape.md,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(repo.displayName, style = ObsidianType.titleLarge)
                    repo.description?.let { Text(it, style = ObsidianType.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repo.primaryLanguage?.let { ObsidianBadge(text = it, color = ObsidianPalette.Blue500) }
                repo.stars?.let { ObsidianBadge(text = "$it ★", color = ObsidianPalette.Amber500) }
                repo.reviewCount?.let { ObsidianBadge(text = "$it đánh giá", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }

            if (repo.techStacks.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repo.techStacks.take(5).forEach { ts ->
                        ObsidianBadge(text = ts.name, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (repo.techStacks.size > 5) {
                        ObsidianBadge(text = "+${repo.techStacks.size - 5}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (repo.courseName != null || repo.courseCode != null) {
                Spacer(Modifier.height(4.dp))
                Text("${repo.courseCode ?: ""} - ${repo.courseName ?: ""}", style = ObsidianType.labelSmall, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilledTonalButton(onClick = onSync) {
                    Icon(Icons.Rounded.Refresh, null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Đồng bộ", style = ObsidianType.labelMedium)
                }
                OutlinedButton(onClick = onEdit) { Text("Sửa", style = ObsidianType.labelMedium) }
                TextButton(onClick = onDelete) { Text("Xoá", color = MaterialTheme.colorScheme.error, style = ObsidianType.labelMedium) }
            }
        }
    }
}

@Composable
private fun EditRepoDialog(
    repo: RepoSummaryResponse,
    onDismiss: () -> Unit,
    onSave: (ApprovedRepoUpdateRequest) -> Unit
) {
    var displayName by remember { mutableStateOf(repo.displayName) }
    var description by remember { mutableStateOf(repo.description ?: "") }
    var stars by remember { mutableStateOf(repo.stars?.toString() ?: "") }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Sửa kho", style = ObsidianType.headlineSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = displayName, onValueChange = { displayName = it }, label = { Text("Tên hiển thị") }, singleLine = true, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Mô tả") }, maxLines = 3, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
                OutlinedTextField(value = stars, onValueChange = { stars = it }, label = { Text("Sao") }, singleLine = true, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isSubmitting = true
                    onSave(ApprovedRepoUpdateRequest(displayName = displayName, description = description.ifBlank { null }, stars = stars.toIntOrNull()))
                },
                enabled = displayName.isNotBlank() && !isSubmitting
            ) { Text("Lưu") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Huỷ") } }
    )
}

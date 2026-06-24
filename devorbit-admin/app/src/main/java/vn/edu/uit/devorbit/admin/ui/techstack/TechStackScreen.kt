package vn.edu.uit.devorbit.admin.ui.techstack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.admin.data.remote.dto.AdminTechStackResponse
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TechStackScreen(
    viewModel: TechStackViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf<Long?>(null) }

    Column(Modifier.fillMaxSize()) {
        ObsidianPageHeader(
            title = "Công nghệ",
            subtitle = "${state.items.size} công nghệ",
            actions = {
                FilledTonalButton(
                    onClick = { showCreateDialog = true },
                    shape = ObsidianShape.sm,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Thêm", style = ObsidianType.labelLarge)
                }
            }
        )

        ObsidianDivider()

        when {
            state.isLoading -> ObsidianLoadingBox()
            state.error != null -> ObsidianEmptyState(
                message = "Lỗi tải dữ liệu",
                subtitle = state.error
            )
            state.items.isEmpty() -> ObsidianEmptyState(
                message = "Chưa có công nghệ nào",
                subtitle = "Thêm công nghệ để bắt đầu",
                icon = Icons.Rounded.Code
            )
            else -> TechStackList(
                items = state.items,
                onDelete = { deleteId = it }
            )
        }
    }

    // ── Create dialog ────────────────────────────────────────────────────
    if (showCreateDialog) {
        CreateTechStackDialog(
            onDismiss = { showCreateDialog = false },
            onSubmit = { name ->
                viewModel.create(name)
                showCreateDialog = false
            }
        )
    }

    // ── Delete confirmation ──────────────────────────────────────────────
    deleteId?.let { id ->
        ObsidianConfirmDialog(
            title = "Xoá công nghệ",
            message = "Bạn có chắc muốn xoá công nghệ này? Hành động không thể hoàn tác.",
            onConfirm = { viewModel.delete(id); deleteId = null },
            onDismiss = { deleteId = null },
            isDestructive = true
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// TECH STACK LIST
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TechStackList(
    items: List<AdminTechStackResponse>,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(items, key = { it.id }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = ObsidianShape.md,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(ObsidianShape.sm)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Code,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        item.name,
                        style = ObsidianType.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onDelete(item.id) },
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
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// CREATE DIALOG
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun CreateTechStackDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ObsidianShape.lg,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text("Thêm công nghệ", style = ObsidianType.headlineSmall)
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên công nghệ", style = ObsidianType.bodyMedium) },
                singleLine = true,
                shape = ObsidianShape.sm,
                textStyle = ObsidianType.bodyMedium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onSubmit(name.trim()) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Tạo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huỷ")
            }
        }
    )
}

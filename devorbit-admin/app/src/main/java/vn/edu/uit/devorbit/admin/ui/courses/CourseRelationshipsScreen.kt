package vn.edu.uit.devorbit.admin.ui.courses

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.data.remote.dto.CourseRelationshipRequest
import vn.edu.uit.devorbit.admin.data.remote.dto.CourseSummaryResponse
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianPalette
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianShape
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianType
import vn.edu.uit.devorbit.admin.ui.theme.relationTypeLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseRelationshipsScreen(
    onBack: () -> Unit,
    viewModel: CourseRelationshipsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf<Long?>(null) }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Quan hệ môn học", style = ObsidianType.headlineSmall) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Quay lại") } },
            actions = {
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Thêm")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        when {
            state.isLoading && state.relationships.isEmpty() -> ObsidianLoadingBox()
            state.relationships.isEmpty() -> ObsidianEmptyState(
                message = "Chưa có quan hệ nào",
                subtitle = "Nhấn + để thêm quan hệ giữa các môn học"
            )
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.relationships, key = { it.id }) { rel ->
                        Card(
                            shape = ObsidianShape.md,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(rel.courseCode ?: "", style = ObsidianType.titleSmall, color = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.width(4.dp))
                                        Text(rel.courseName ?: "", style = ObsidianType.bodySmall)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    ObsidianBadge(
                                        text = relationTypeLabel(rel.relationType),
                                        color = when (rel.relationType) {
                                            "PREREQUISITE" -> ObsidianPalette.Amber500
                                            "COMPLEMENTARY" -> ObsidianPalette.Green500
                                            "COREQUISITE" -> ObsidianPalette.Blue500
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.ArrowForward, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.width(4.dp))
                                        Text(rel.relatedCourseCode ?: "", style = ObsidianType.titleSmall, color = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.width(4.dp))
                                        Text(rel.relatedCourseName ?: "", style = ObsidianType.bodySmall)
                                    }
                                }
                                IconButton(onClick = { deleteId = rel.id }) {
                                    Icon(Icons.Rounded.Delete, contentDescription = "Xoá", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateRelationshipDialog(
            courses = state.courses,
            onDismiss = { showCreateDialog = false },
            onCreate = { courseId, relatedId, type ->
                viewModel.createRelationship(courseId, relatedId, type)
                showCreateDialog = false
            }
        )
    }

    deleteId?.let { id ->
        ObsidianConfirmDialog(
            title = "Xoá quan hệ",
            message = "Bạn có chắc muốn xoá quan hệ này?",
            onConfirm = { viewModel.deleteRelationship(id); deleteId = null },
            onDismiss = { deleteId = null },
            isDestructive = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateRelationshipDialog(
    courses: List<CourseSummaryResponse>,
    onDismiss: () -> Unit,
    onCreate: (Long, Long, String) -> Unit
) {
    var selectedSource by remember { mutableStateOf<Long?>(null) }
    var selectedTarget by remember { mutableStateOf<Long?>(null) }
    var relationType by remember { mutableStateOf("PREREQUISITE") }
    var sourceExpanded by remember { mutableStateOf(false) }
    var targetExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Thêm quan hệ", style = ObsidianType.headlineSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Source course
                ExposedDropdownMenuBox(expanded = sourceExpanded, onExpandedChange = { sourceExpanded = it }) {
                    OutlinedTextField(
                        value = courses.find { it.id == selectedSource }?.let { "${it.code} - ${it.name}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Môn học nguồn") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = ObsidianShape.sm,
                        textStyle = ObsidianType.bodyMedium
                    )
                    ExposedDropdownMenu(expanded = sourceExpanded, onDismissRequest = { sourceExpanded = false }) {
                        courses.forEach { c ->
                            DropdownMenuItem(text = { Text("${c.code ?: ""} - ${c.name ?: ""}") }, onClick = { selectedSource = c.id; sourceExpanded = false })
                        }
                    }
                }

                // Target course
                ExposedDropdownMenuBox(expanded = targetExpanded, onExpandedChange = { targetExpanded = it }) {
                    OutlinedTextField(
                        value = courses.find { it.id == selectedTarget }?.let { "${it.code} - ${it.name}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Môn học đích") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = ObsidianShape.sm,
                        textStyle = ObsidianType.bodyMedium
                    )
                    ExposedDropdownMenu(expanded = targetExpanded, onDismissRequest = { targetExpanded = false }) {
                        courses.forEach { c ->
                            DropdownMenuItem(text = { Text("${c.code ?: ""} - ${c.name ?: ""}") }, onClick = { selectedTarget = c.id; targetExpanded = false })
                        }
                    }
                }

                // Relation type
                Text("Kiểu quan hệ", style = ObsidianType.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("PREREQUISITE", "COMPLEMENTARY", "COREQUISITE").forEach { t ->
                        FilterChip(
                            selected = relationType == t,
                            onClick = { relationType = t },
                            label = { Text(relationTypeLabel(t), style = ObsidianType.labelSmall) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { selectedSource?.let { s -> selectedTarget?.let { t -> onCreate(s, t, relationType) } } },
                enabled = selectedSource != null && selectedTarget != null
            ) { Text("Tạo") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Huỷ") } }
    )
}

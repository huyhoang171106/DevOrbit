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
import vn.edu.uit.devorbit.admin.data.remote.dto.CourseRelationshipResponse
import vn.edu.uit.devorbit.admin.ui.components.*

@Composable
fun CourseRelationshipsScreen(
    onBack: () -> Unit,
    viewModel: CourseRelationshipsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf<Long?>(null) }

    Column(Modifier.fillMaxSize()) {
        SmallTopAppBar(
            title = { Text("Quan hệ môn học", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Thêm")
                }
            }
        )

        if (state.isLoading) {
            LoadingBox()
        } else if (state.relationships.isEmpty()) {
            EmptyState("Chưa có quan hệ nào", subtitle = "Nhấn + để thêm quan hệ giữa các môn học")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.relationships) { rel ->
                    RelationshipCard(
                        rel = rel,
                        onDelete = { deleteId = rel.id }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateRelationshipDialog(
            courses = state.courses,
            onDismiss = { showCreateDialog = false },
            onCreate = { from, to, type ->
                viewModel.createRelationship(from, to, type)
                showCreateDialog = false
            }
        )
    }

    deleteId?.let { id ->
        ConfirmDialog(
            title = "Xoá quan hệ",
            message = "Bạn có chắc muốn xoá quan hệ này?",
            onConfirm = { viewModel.deleteRelationship(id); deleteId = null },
            onDismiss = { deleteId = null },
            isDestructive = true
        )
    }
}

@Composable
private fun RelationshipCard(rel: CourseRelationshipResponse, onDelete: () -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${rel.fromCourseCode ?: "?"} - ${rel.toCourseCode ?: "?"}",
                    fontWeight = FontWeight.Medium)
                Text("Kiểu: ${rel.relationType}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary)
                Text("${rel.fromCourseName ?: "?"} → ${rel.toCourseName ?: "?"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1)
            }
            TextButton(onClick = onDelete) {
                Text("Xoá", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun CreateRelationshipDialog(
    courses: List<vn.edu.uit.devorbit.admin.data.remote.dto.CourseSummaryResponse>,
    onDismiss: () -> Unit,
    onCreate: (Long, Long, String) -> Unit
) {
    var expanded1 by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }
    var selectedFrom by remember { mutableStateOf<Long?>(null) }
    var selectedTo by remember { mutableStateOf<Long?>(null) }
    var selectedType by remember { mutableStateOf("PREREQUISITE") }
    var typeExpanded by remember { mutableStateOf(false) }
    val types = listOf("PREREQUISITE", "COMPLEMENTARY", "COREQUISITE")

    val fromLabel = courses.find { it.id == selectedFrom }?.let { "${it.code ?: ""} - ${it.tenMH}" } ?: "Chọn môn nguồn"
    val toLabel = courses.find { it.id == selectedTo }?.let { "${it.code ?: ""} - ${it.tenMH}" } ?: "Chọn môn đích"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm quan hệ") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(expanded = expanded1, onExpandedChange = { expanded1 = it }) {
                    OutlinedTextField(value = fromLabel, onValueChange = {}, readOnly = true,
                        label = { Text("Môn nguồn") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded1) },
                        modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = expanded1, onDismissRequest = { expanded1 = false }) {
                        courses.forEach { c ->
                            DropdownMenuItem(
                                text = { Text("${c.code ?: ""} - ${c.tenMH}") },
                                onClick = { selectedFrom = c.id; expanded1 = false }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(expanded = expanded2, onExpandedChange = { expanded2 = it }) {
                    OutlinedTextField(value = toLabel, onValueChange = {}, readOnly = true,
                        label = { Text("Môn đích") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded2) },
                        modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = expanded2, onDismissRequest = { expanded2 = false }) {
                        courses.forEach { c ->
                            DropdownMenuItem(
                                text = { Text("${c.code ?: ""} - ${c.tenMH}") },
                                onClick = { selectedTo = c.id; expanded2 = false }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                    OutlinedTextField(value = selectedType, onValueChange = {}, readOnly = true,
                        label = { Text("Kiểu") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                        types.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = { selectedType = t; typeExpanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedFrom != null && selectedTo != null) {
                        onCreate(selectedFrom!!, selectedTo!!, selectedType)
                    }
                },
                enabled = selectedFrom != null && selectedTo != null
            ) { Text("Tạo") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Huỷ") }
        }
    )
}

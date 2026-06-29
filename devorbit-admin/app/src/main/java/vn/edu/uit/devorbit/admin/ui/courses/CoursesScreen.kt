package vn.edu.uit.devorbit.admin.ui.courses

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.data.remote.dto.AdminCourseUpsertRequest
import vn.edu.uit.devorbit.admin.data.remote.dto.CourseSummaryResponse
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianShape
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianType
import vn.edu.uit.devorbit.admin.ui.theme.subjectTypeLabel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun CoursesScreen(
    viewModel: CoursesViewModel = hiltViewModel(),
    onCourseClick: (Long) -> Unit = {},
    onRelationshipsClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf<Long?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(Modifier.fillMaxSize()) {
        ObsidianPageHeader(
            title = "Môn học",
            subtitle = "${state.courses.size} môn học",
            actions = {
                OutlinedButton(
                    onClick = onRelationshipsClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Rounded.Share, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    ObsidianButtonText("Quan hệ", style = ObsidianType.labelMedium)
                }
                Button(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Rounded.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    ObsidianButtonText("Thêm", style = ObsidianType.labelMedium)
                }
            }
        )

        ObsidianSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(8.dp))

        when {
            state.isLoading && state.courses.isEmpty() -> ObsidianLoadingBox()
            state.courses.isEmpty() -> ObsidianEmptyState(
                message = "Chưa có môn học nào",
                subtitle = "Nhấn Thêm để tạo môn học mới"
            )
            else -> {
                val filtered = state.courses.filter {
                    searchQuery.isBlank() || it.name?.contains(searchQuery, ignoreCase = true) == true || it.code?.contains(searchQuery, ignoreCase = true) == true
                }
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.id }) { course ->
                        CourseRow(
                            course = course,
                            onClick = { onCourseClick(course.id) },
                            onDelete = { deleteId = course.id }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateCourseDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { request ->
                viewModel.createCourse(request)
                showCreateDialog = false
            }
        )
    }

    deleteId?.let { id ->
        ObsidianConfirmDialog(
            title = "Xoá môn học",
            message = "Bạn có chắc muốn xoá môn học này?",
            onConfirm = {
                deleteId = null
                viewModel.deleteCourse(id) {
                    Toast.makeText(context, "Đã xoá môn học thành công", Toast.LENGTH_SHORT).show()
                }
            },
            onDismiss = { deleteId = null },
            isDestructive = true
        )
    }
}

@Composable
private fun CourseRow(course: CourseSummaryResponse, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = ObsidianShape.md,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(course.name ?: "", style = ObsidianType.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(course.code ?: "", style = ObsidianType.labelMedium, color = MaterialTheme.colorScheme.primary)
                    if (course.credits > 0) {
                        Text("${course.credits} tín chỉ", style = ObsidianType.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (course.semester != null) {
                        Text("HK${course.semester}", style = ObsidianType.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (course.repoCount > 0) {
                    Spacer(Modifier.height(2.dp))
                    Text("${course.repoCount} kho", style = ObsidianType.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            TextButton(onClick = onDelete) {
                Text("Xoá", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun CreateCourseDialog(
    onDismiss: () -> Unit,
    onCreate: (AdminCourseUpsertRequest) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var credits by remember { mutableStateOf("") }
    var subjectType by remember { mutableStateOf("CHUYEN_NGANH") }
    var description by remember { mutableStateOf("") }
    var managementUnit by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val isValid = code.isNotBlank() && name.isNotBlank() && credits.isNotBlank() && credits.toIntOrNull() != null

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Thêm môn học", style = ObsidianType.headlineSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Mã môn học") },
                    singleLine = true,
                    shape = ObsidianShape.sm,
                    textStyle = ObsidianType.bodyMedium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên môn học") },
                    singleLine = true,
                    shape = ObsidianShape.sm,
                    textStyle = ObsidianType.bodyMedium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                OutlinedTextField(
                    value = credits,
                    onValueChange = { credits = it },
                    label = { Text("Số tín chỉ") },
                    singleLine = true,
                    shape = ObsidianShape.sm,
                    textStyle = ObsidianType.bodyMedium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text(subjectTypeLabel(subjectType), style = ObsidianType.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Selection(listOf("DAI_CUONG", "CO_SO", "CHUYEN_NGANH"), subjectType) { subjectType = it }
                }
                OutlinedTextField(
                    value = managementUnit,
                    onValueChange = { managementUnit = it },
                    label = { Text("Đơn vị quản lý") },
                    singleLine = true,
                    shape = ObsidianShape.sm,
                    textStyle = ObsidianType.bodyMedium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả") },
                    maxLines = 3,
                    shape = ObsidianShape.sm,
                    textStyle = ObsidianType.bodyMedium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isSubmitting = true
                    onCreate(AdminCourseUpsertRequest(code = code, name = name, credits = credits.toInt(), subjectType = subjectType, managementUnit = managementUnit.ifBlank { null }, description = description.ifBlank { null }, isOpen = true))
                },
                enabled = isValid && !isSubmitting
            ) { Text("Tạo", color = MaterialTheme.colorScheme.onPrimary) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Huỷ") } }
    )
}

@Composable
private fun Selection(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    options.forEach { opt ->
        FilterChip(
            selected = opt == selected,
            onClick = { onSelect(opt) },
            label = { Text(subjectTypeLabel(opt), style = ObsidianType.labelSmall, color = MaterialTheme.colorScheme.onSurface) }
        )
    }
}

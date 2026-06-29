package vn.edu.uit.devorbit.admin.ui.courses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianPalette
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianShape
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianType
import vn.edu.uit.devorbit.admin.ui.theme.subjectTypeLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: Long,
    onBack: () -> Unit,
    viewModel: CourseDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(courseId) { viewModel.loadCourse(courseId) }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(state.course?.name ?: "Chi tiết môn học", style = ObsidianType.headlineSmall) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Quay lại") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        when {
            state.isLoading && state.course == null -> ObsidianLoadingBox()
            state.error != null -> ObsidianEmptyState(message = state.error!!, icon = Icons.Rounded.Warning)
            state.course != null -> {
                val course = state.course!!
                val tabs = listOf("Thông tin", "Kho repo")

                TabRow(selectedTabIndex = state.selectedTab) {
                    tabs.forEachIndexed { i, title ->
                        Tab(selected = state.selectedTab == i, onClick = { viewModel.selectTab(i) }, text = { Text(title, style = ObsidianType.labelMedium) })
                    }
                }

                when (state.selectedTab) {
                    0 -> CourseInfoTab(course, onEdit = { viewModel.updateCourse(it) })
                    1 -> {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = { viewModel.syncRepos(courseId) }) {
                                    Icon(Icons.Rounded.Refresh, contentDescription = "Đồng bộ repo")
                                }
                            }
                            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(course.repos, key = { it.id }) { repo ->
                                    Card(shape = ObsidianShape.sm, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(repo.displayName, style = ObsidianType.titleSmall)
                                                repo.primaryLanguage?.let { ObsidianBadge(text = it, modifier = Modifier.padding(top = 4.dp)) }
                                            }
                                            repo.stars?.let { Text("$it ★", style = ObsidianType.labelSmall, color = ObsidianPalette.Amber500) }
                                            IconButton(onClick = { viewModel.deleteRepo(repo.id) }) {
                                                Icon(Icons.Rounded.Delete, contentDescription = "Xóa repo", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseInfoTab(course: CourseDetailResponse, onEdit: (AdminCourseUpsertRequest) -> Unit) {
    var showEditDialog by remember { mutableStateOf(false) }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        item {
            Card(shape = ObsidianShape.md, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ObsidianDataRow(label = "Mã môn", value = course.code ?: "")
                    ObsidianDataRow(label = "Tên (EN)", value = course.nameEn ?: "—")
                    ObsidianDataRow(label = "Số tín chỉ", value = "${course.credits}")
                    ObsidianDataRow(label = "Loại", value = subjectTypeLabel(course.subjectType))
                    ObsidianDataRow(label = "Lý thuyết", value = course.theoryHours?.let { "$it tiết" } ?: "—")
                    ObsidianDataRow(label = "Thực hành", value = course.practiceHours?.let { "$it tiết" } ?: "—")
                    ObsidianDataRow(label = "Đơn vị", value = course.managementUnit ?: "—")
                    ObsidianDataRow(label = "Trạng thái", value = if (course.isOpen) "Đang mở" else "Đã đóng")
                }
            }
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Sửa")
                }
            }
        }
    }
    if (showEditDialog) {
        EditCourseDialog(
            course = course,
            onDismiss = { showEditDialog = false },
            onSave = { request ->
                onEdit(request)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun <T> ResourcesTab(
    items: List<T>,
    itemLabel: (T) -> String,
    itemSubtitle: ((T) -> String?)? = null,
    itemId: (T) -> Any = { it.hashCode() },
    itemField2: (T) -> String = { "" },
    onAdd: () -> Unit,
    onEdit: ((T, String, String) -> Unit)? = null,
    onDelete: (T) -> Unit,
    addDialogTitle: String,
    addLabel1: String,
    addLabel2: String,
    addFn: (String, String) -> Unit,
    editDialogTitle: String = "Sửa",
    editLabel1: String = addLabel1,
    editLabel2: String = addLabel2
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var deleteItem by remember { mutableStateOf<T?>(null) }
    var editItem by remember { mutableStateOf<T?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (items.isEmpty()) {
            ObsidianEmptyState(message = "Chưa có dữ liệu", subtitle = "Nhấn + để thêm")
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(items, key = { itemId(it) }) { item ->
                    Card(shape = ObsidianShape.sm, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(itemLabel(item), style = ObsidianType.titleSmall)
                                itemSubtitle?.invoke(item)?.let { Text(it, style = ObsidianType.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            }
                            if (onEdit != null) {
                                IconButton(onClick = { editItem = item }) {
                                    Icon(Icons.Rounded.Edit, contentDescription = "Sửa", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }
                            }
                            IconButton(onClick = { deleteItem = item }) {
                                Icon(Icons.Rounded.Delete, contentDescription = "Xoá", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Thêm")
        }
    }

    if (showAddDialog) {
        AddResourceDialog(title = addDialogTitle, label1 = addLabel1, label2 = addLabel2, onDismiss = { showAddDialog = false }, onSubmit = { t, u -> addFn(t, u); showAddDialog = false })
    }

    editItem?.let { item ->
        var editV1 by remember(item) { mutableStateOf(itemLabel(item)) }
        var editV2 by remember(item) { mutableStateOf(itemField2(item)) }
        AlertDialog(
            onDismissRequest = { editItem = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text(editDialogTitle, style = ObsidianType.headlineSmall) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = editV1, onValueChange = { editV1 = it }, label = { Text(editLabel1) }, singleLine = true, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
                    OutlinedTextField(value = editV2, onValueChange = { editV2 = it }, label = { Text(editLabel2) }, singleLine = true, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
                }
            },
            confirmButton = { Button(onClick = { onEdit?.invoke(item, editV1, editV2); editItem = null }, enabled = editV1.isNotBlank() && editV2.isNotBlank()) { ObsidianButtonText("Lưu") } },
            dismissButton = { TextButton(onClick = { editItem = null }) { ObsidianButtonText("Huỷ") } }
        )
    }

    deleteItem?.let { item ->
        ObsidianConfirmDialog(title = "Xoá", message = "Bạn có chắc muốn xoá?", onConfirm = { onDelete(item); deleteItem = null }, onDismiss = { deleteItem = null }, isDestructive = true)
    }
}

@Composable
private fun EditCourseDialog(
    course: CourseDetailResponse,
    onDismiss: () -> Unit,
    onSave: (AdminCourseUpsertRequest) -> Unit
) {
    var code by remember { mutableStateOf(course.code ?: "") }
    var name by remember { mutableStateOf(course.name ?: "") }
    var nameEn by remember { mutableStateOf(course.nameEn ?: "") }
    var credits by remember { mutableStateOf(course.credits.toString()) }
    var subjectType by remember { mutableStateOf(course.subjectType ?: "DAI_CUONG") }
    var lectureHours by remember { mutableStateOf(course.theoryHours?.toString() ?: "") }
    var practiceHours by remember { mutableStateOf(course.practiceHours?.toString() ?: "") }
    var managementUnit by remember { mutableStateOf(course.managementUnit ?: "") }
    val subjectTypeOptions = remember { listOf("DAI_CUONG", "CO_SO", "CO_SO_NGANH", "CHUYEN_NGANH") }
    var isOpen by remember { mutableStateOf(course.isOpen) }
    var description by remember { mutableStateOf(course.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Sửa môn học", style = ObsidianType.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Mã môn") }, singleLine = true, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên (VN)") }, singleLine = true, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
                OutlinedTextField(value = nameEn, onValueChange = { nameEn = it }, label = { Text("Tên (EN)") }, singleLine = true, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
                OutlinedTextField(value = credits, onValueChange = { credits = it }, label = { Text("Số tín chỉ") }, singleLine = true, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
                OutlinedTextField(value = lectureHours, onValueChange = { lectureHours = it }, label = { Text("Giờ lý thuyết") }, singleLine = true, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
                OutlinedTextField(value = practiceHours, onValueChange = { practiceHours = it }, label = { Text("Giờ thực hành") }, singleLine = true, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
                OutlinedTextField(value = managementUnit, onValueChange = { managementUnit = it }, label = { Text("Đơn vị") }, singleLine = true, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
                Text("Loại môn học", style = ObsidianType.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    subjectTypeOptions.forEach { opt ->
                        FilterChip(
                            selected = opt == subjectType,
                            onClick = { subjectType = opt },
                            label = { Text(subjectTypeLabel(opt), style = ObsidianType.labelSmall) }
                        )
                    }
                }
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Mô tả") }, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isOpen, onCheckedChange = { isOpen = it })
                    Spacer(Modifier.width(8.dp))
                    Text("Đang mở", style = ObsidianType.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        AdminCourseUpsertRequest(
                            code = code,
                            name = name,
                            nameEn = nameEn.ifBlank { null },
                            credits = credits.toIntOrNull() ?: course.credits,
                            lectureHours = lectureHours.toIntOrNull(),
                            practiceHours = practiceHours.toIntOrNull(),
                            subjectType = subjectType,
                            isOpen = isOpen,
                            managementUnit = managementUnit.ifBlank { null },
                            description = description.ifBlank { null },
                            codeOld = course.codeOld,
                            equivalentMH = course.equivalentMH,
                            prerequisiteMH = course.prerequisiteMH,
                            previousMH = course.previousMH,
                            learningObjectives = course.learningObjectives,
                            gradingCriteria = course.gradingCriteria
                        )
                    )
                },
                enabled = code.isNotBlank() && name.isNotBlank()
            ) { ObsidianButtonText("Lưu") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { ObsidianButtonText("Huỷ") } }
    )
}

@Composable
private fun AddResourceDialog(
    title: String,
    label1: String,
    label2: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var v1 by remember { mutableStateOf("") }
    var v2 by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(title, style = ObsidianType.headlineSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = v1, onValueChange = { v1 = it }, label = { Text(label1) }, singleLine = true, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
                OutlinedTextField(value = v2, onValueChange = { v2 = it }, label = { Text(label2) }, singleLine = true, shape = ObsidianShape.sm, textStyle = ObsidianType.bodyMedium)
            }
        },
        confirmButton = { Button(onClick = { onSubmit(v1, v2) }, enabled = v1.isNotBlank() && v2.isNotBlank()) { ObsidianButtonText("Thêm") } },
        dismissButton = { TextButton(onClick = onDismiss) { ObsidianButtonText("Huỷ") } }
    )
}

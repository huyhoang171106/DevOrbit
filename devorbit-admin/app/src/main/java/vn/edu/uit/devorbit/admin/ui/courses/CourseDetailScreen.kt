package vn.edu.uit.devorbit.admin.ui.courses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
                val tabs = listOf("Thông tin", "Hướng dẫn", "Danh sách phát", "Bài viết")

                TabRow(selectedTabIndex = state.selectedTab) {
                    tabs.forEachIndexed { i, title ->
                        Tab(selected = state.selectedTab == i, onClick = { viewModel.selectTab(i) }, text = { Text(title, style = ObsidianType.labelMedium) })
                    }
                }

                when (state.selectedTab) {
                    0 -> CourseInfoTab(course, state, viewModel)
                    1 -> ResourcesTab(state.tutorials, itemLabel = { it.title }, itemSubtitle = { it.description }, itemKey = { it.id }, onAdd = { /* tutorial dialog */ }, onDelete = { viewModel.deleteTutorial(it.id) }, addDialogTitle = "Thêm hướng dẫn", addLabel1 = "Tiêu đề", addLabel2 = "URL", addFn = { t, u -> viewModel.addTutorial(t, u) })
                    2 -> ResourcesTab(state.playlists, itemLabel = { it.title }, itemSubtitle = { it.channelName }, itemKey = { it.id }, onAdd = { /* playlist dialog */ }, onDelete = { viewModel.deletePlaylist(it.id) }, addDialogTitle = "Thêm danh sách phát", addLabel1 = "Tiêu đề", addLabel2 = "URL", addFn = { t, u -> viewModel.addPlaylist(t, u) })
                    3 -> ResourcesTab(state.articles, itemLabel = { it.title }, itemSubtitle = { it.author }, itemKey = { it.id }, onAdd = { /* article dialog */ }, onDelete = { viewModel.deleteArticle(it.id) }, addDialogTitle = "Thêm bài viết", addLabel1 = "Tiêu đề", addLabel2 = "URL", addFn = { t, u -> viewModel.addArticle(t, u) })
                }
            }
        }
    }
}

@Composable
private fun CourseInfoTab(course: CourseDetailResponse, state: CourseDetailUiState, viewModel: CourseDetailViewModel) {
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
        }

        if (course.repos.isNotEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
                ObsidianSectionHeader(title = "Kho liên kết (${course.repos.size})")
            }
            items(course.repos, key = { it.id }) { repo ->
                Card(shape = ObsidianShape.sm, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(repo.displayName, style = ObsidianType.titleSmall)
                            repo.primaryLanguage?.let { ObsidianBadge(text = it, modifier = Modifier.padding(top = 4.dp)) }
                        }
                        repo.stars?.let { Text("$it ★", style = ObsidianType.labelSmall, color = ObsidianPalette.Amber500) }
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> ResourcesTab(
    items: List<T>,
    itemLabel: (T) -> String,
    itemSubtitle: ((T) -> String?)? = null,
    itemKey: (T) -> Any = { it.hashCode() },
    onAdd: () -> Unit,
    onDelete: (T) -> Unit,
    addDialogTitle: String,
    addLabel1: String,
    addLabel2: String,
    addFn: (String, String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var deleteItem by remember { mutableStateOf<T?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (items.isEmpty()) {
            ObsidianEmptyState(message = "Chưa có dữ liệu", subtitle = "Nhấn + để thêm")
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(items, key = { itemKey(it) }) { item ->
                    Card(shape = ObsidianShape.sm, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(itemLabel(item), style = ObsidianType.titleSmall)
                                itemSubtitle?.invoke(item)?.let { Text(it, style = ObsidianType.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
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

    deleteItem?.let { item ->
        ObsidianConfirmDialog(title = "Xoá", message = "Bạn có chắc muốn xoá?", onConfirm = { onDelete(item); deleteItem = null }, onDismiss = { deleteItem = null }, isDestructive = true)
    }
}

@Composable
private fun AddResourceDialog(title: String, label1: String, label2: String, onDismiss: () -> Unit, onSubmit: (String, String) -> Unit) {
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
        confirmButton = { Button(onClick = { onSubmit(v1, v2) }, enabled = v1.isNotBlank() && v2.isNotBlank()) { Text("Thêm") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Huỷ") } }
    )
}


package vn.edu.uit.devorbit.admin.ui.courses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.admin.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: Long,
    onBack: () -> Unit,
    viewModel: CourseDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showAddTutorial by remember { mutableStateOf(false) }
    var showAddVideo by remember { mutableStateOf(false) }
    var showAddArticle by remember { mutableStateOf(false) }

    LaunchedEffect(courseId) { viewModel.loadCourse(courseId) }

    val tabs = listOf("Thông tin", "Tutorial", "Video", "Bài viết")

    Column(Modifier.fillMaxSize()) {
        SmallTopAppBar(
            title = { Text(state.course?.tenMH ?: "Chi tiết môn học", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Sửa")
                }
            }
        )

        TabRow(selectedTabIndex = state.selectedTab) {
            tabs.forEachIndexed { i, title ->
                Tab(selected = state.selectedTab == i, onClick = { viewModel.selectTab(i) },
                    text = { Text(title) })
            }
        }

        if (state.isLoading) {
            LoadingBox()
        } else when (state.selectedTab) {
            0 -> CourseInfoTab(state, viewModel, onBack)
            1 -> ResourcesTab(
                items = state.tutorials.map { it.title to null },
                onAdd = { showAddTutorial = true },
                onDelete = { viewModel.deleteTutorial(state.tutorials[it].id) }
            )
            2 -> ResourcesTab(
                items = state.videos.map { it.title to null },
                onAdd = { showAddVideo = true },
                onDelete = { viewModel.deleteVideo(state.videos[it].id) }
            )
            3 -> ResourcesTab(
                items = state.articles.map { it.title to null },
                onAdd = { showAddArticle = true },
                onDelete = { viewModel.deleteArticle(state.articles[it].id) }
            )
        }
    }

    state.snackbarMessage?.let { msg ->
        LaunchedEffect(msg) { viewModel.clearSnackbar() }
    }

    if (showEditDialog && state.course != null) {
        EditCourseDialog(
            course = state.course!!,
            onDismiss = { showEditDialog = false },
            onSave = { viewModel.updateCourse(it); showEditDialog = false }
        )
    }
    if (showAddTutorial) {
        AddResourceDialog(
            title = "Thêm Tutorial",
            onDismiss = { showAddTutorial = false },
            onSubmit = { t, u -> viewModel.addTutorial(t, u); showAddTutorial = false }
        )
    }
    if (showAddVideo) {
        AddResourceDialog(
            title = "Thêm Video",
            onDismiss = { showAddVideo = false },
            onSubmit = { t, u -> viewModel.addVideo(t, u); showAddVideo = false },
            label1 = "Tiêu đề", label2 = "Playlist URL"
        )
    }
    if (showAddArticle) {
        AddResourceDialog(
            title = "Thêm Bài viết",
            onDismiss = { showAddArticle = false },
            onSubmit = { t, u -> viewModel.addArticle(t, u); showAddArticle = false }
        )
    }
}

@Composable
private fun CourseInfoTab(
    state: CourseDetailUiState,
    viewModel: CourseDetailViewModel,
    onBack: () -> Unit
) {
    val course = state.course ?: return
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            DetailCard("Thông tin cơ bản") {
                InfoRow("Mã MH", course.code ?: "N/A")
                InfoRow("Tên MH", course.tenMH ?: "N/A")
                InfoRow("Loại", course.loaiMonHoc ?: "N/A")
                InfoRow("Học kỳ", course.semester?.toString() ?: "N/A")
                InfoRow("Đơn vị", course.managementUnit ?: "N/A")
                InfoRow("Trạng thái", if (course.active) "Hoạt động" else "Vô hiệu")
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { viewModel.deleteCourse(state.courseId); onBack() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Xoá môn học") }
            }
        }
    }
}

@Composable
private fun ResourcesTab(
    items: List<Pair<String, String?>>,
    onAdd: () -> Unit,
    onDelete: (Int) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Button(onClick = onAdd) { Text("Thêm mới") }
        }
        if (items.isEmpty()) {
            item { EmptyState("Chưa có dữ liệu") }
        } else {
            items(items.size) { idx ->
                val (title, _) = items[idx]
                ResourceItem(title = title, onDelete = { onDelete(idx) })
            }
        }
    }
}

@Composable
private fun EditCourseDialog(
    course: vn.edu.uit.devorbit.admin.data.remote.dto.CourseDetailResponse,
    onDismiss: () -> Unit,
    onSave: (vn.edu.uit.devorbit.admin.data.remote.dto.AdminCourseUpsertRequest) -> Unit
) {
    var name by remember { mutableStateOf(course.tenMH ?: "") }
    var type by remember { mutableStateOf(course.loaiMonHoc ?: "") }
    var semester by remember { mutableStateOf(course.semester?.toString() ?: "") }
    var unit by remember { mutableStateOf(course.managementUnit ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sửa môn học") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text("Tên MH") }, singleLine = true)
                OutlinedTextField(value = type, onValueChange = { type = it },
                    label = { Text("Loại") }, singleLine = true)
                OutlinedTextField(value = semester, onValueChange = { semester = it },
                    label = { Text("Học kỳ") }, singleLine = true)
                OutlinedTextField(value = unit, onValueChange = { unit = it },
                    label = { Text("Đơn vị") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(vn.edu.uit.devorbit.admin.data.remote.dto.AdminCourseUpsertRequest(
                    tenMH = name, loaiMonHoc = type,
                    semester = semester.toIntOrNull(), tinChi = null,
                    managementUnit = unit.ifBlank { null }, moTa = null
                ))
            }) { Text("Lưu") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Huỷ") }
        }
    )
}

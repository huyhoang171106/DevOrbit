package vn.edu.uit.devorbit.mobile.ui.screen.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.data.remote.dto.GroupTaskResponse
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.GroupPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupPlanDetailScreen(
    planId: Long,
    onNavigateBack: () -> Unit = {},
    onNavigateLeave: () -> Unit = {},
    viewModel: GroupPlanViewModel = hiltViewModel()
) {
    val state by viewModel.detail.collectAsStateWithLifecycle()
    var showAddTaskSheet by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showLeaveDialog by remember { mutableStateOf(false) }
    var showDeletePlanDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<GroupTaskResponse?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(planId) {
        viewModel.loadPlan(planId)
    }

    // Task delete confirmation
    val taskToDeleteSafe = taskToDelete
    if (taskToDeleteSafe != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Xác nhận xoá", color = CosmicTheme.colors.textPrimary) },
            text = { Text("Yêu cầu xoá nhiệm vụ \"${taskToDeleteSafe.title}\"?", color = CosmicTheme.colors.textSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.requestDeleteTask(taskToDelete!!.id)
                    taskToDelete = null
                }) {
                    Text("Xác nhận", color = CosmicTheme.colors.supernova)
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Huỷ", color = CosmicTheme.colors.textSecondary)
                }
            },
            containerColor = CosmicTheme.colors.nebula
        )
    }

    // Leave plan confirmation
    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false; viewModel.clearActionError() },
            title = { Text("Rời kế hoạch", color = CosmicTheme.colors.textPrimary) },
            text = {
                Column {
                    Text("Bạn có chắc muốn rời kế hoạch này?", color = CosmicTheme.colors.textSecondary)
                    if (state.actionError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.actionError!!, color = CosmicTheme.colors.supernova, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.leavePlan(planId) { showLeaveDialog = false; onNavigateLeave() }
                    },
                    enabled = !state.actionLoading
                ) {
                    if (state.actionLoading) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = CosmicTheme.colors.plasma)
                    } else {
                        Text("Rời", color = CosmicTheme.colors.supernova)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLeaveDialog = false; viewModel.clearActionError() },
                    enabled = !state.actionLoading
                ) {
                    Text("Huỷ", color = CosmicTheme.colors.textSecondary)
                }
            },
            containerColor = CosmicTheme.colors.nebula
        )
    }

    // Delete plan confirmation / approve delete request
    if (showDeletePlanDialog) {
        val isDeleteRequest = state.plan?.deleteRequested == true
        AlertDialog(
            onDismissRequest = { showDeletePlanDialog = false; viewModel.clearActionError() },
            title = {
                Text(
                    if (isDeleteRequest) "Duyệt yêu cầu xoá" else "Xoá kế hoạch",
                    color = CosmicTheme.colors.textPrimary
                )
            },
            text = {
                Column {
                    Text(
                        if (isDeleteRequest) {
                            "${state.plan?.deleteRequestedBy} muốn xoá kế hoạch này. Bạn có chấp nhận?"
                        } else {
                            "Xoá kế hoạch này sẽ ảnh hưởng đến tất cả thành viên. Bạn có chắc?"
                        },
                        color = CosmicTheme.colors.textSecondary
                    )
                    if (state.actionError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.actionError!!, color = CosmicTheme.colors.supernova, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (isDeleteRequest) {
                            viewModel.approveDeletePlan(planId, approved = true) {
                                showDeletePlanDialog = false; onNavigateLeave()
                            }
                        } else {
                            viewModel.deletePlan(planId) { showDeletePlanDialog = false; onNavigateLeave() }
                        }
                    },
                    enabled = !state.actionLoading
                ) {
                    if (state.actionLoading) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = CosmicTheme.colors.plasma)
                    } else {
                        Text("Xoá", color = CosmicTheme.colors.supernova)
                    }
                }
            },
            dismissButton = {
                if (isDeleteRequest) {
                    TextButton(
                        onClick = {
                            viewModel.approveDeletePlan(planId, approved = false) {
                                showDeletePlanDialog = false
                            }
                        },
                        enabled = !state.actionLoading
                    ) {
                        Text("Từ chối", color = CosmicTheme.colors.textSecondary)
                    }
                } else {
                    TextButton(
                        onClick = { showDeletePlanDialog = false; viewModel.clearActionError() },
                        enabled = !state.actionLoading
                    ) {
                        Text("Huỷ", color = CosmicTheme.colors.textSecondary)
                    }
                }
            },
            containerColor = CosmicTheme.colors.nebula
        )
    }

    Scaffold(
        floatingActionButton = {
            if (!state.loading && state.error == null) {
                FloatingActionButton(
                    onClick = { showAddTaskSheet = true },
                    containerColor = CosmicTheme.colors.plasma,
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm nhiệm vụ")
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = CosmicTheme.colors.textPrimary)
                }
                Text(
                    text = state.plan?.title ?: "Kế hoạch nhóm",
                    color = CosmicTheme.colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            // Menu button
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Tuỳ chọn", tint = CosmicTheme.colors.textPrimary)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    if (state.isCreator) {
                        DropdownMenuItem(
                            text = { Text("Xoá kế hoạch", color = CosmicTheme.colors.supernova) },
                            onClick = { showMenu = false; showDeletePlanDialog = true },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = CosmicTheme.colors.supernova) }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Rời kế hoạch", color = CosmicTheme.colors.supernova) },
                            onClick = { showMenu = false; showLeaveDialog = true },
                            leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = CosmicTheme.colors.supernova) }
                        )
                    }
                }
            }
        }

        if (state.loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CosmicTheme.colors.plasma, strokeWidth = 2.dp)
            }
            return@Column
        }

        if (state.error != null) {
            val errorMsg = state.error ?: ""
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = errorMsg, color = CosmicTheme.colors.supernova, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { viewModel.loadPlan(planId) }) {
                        Text("Thử lại", color = CosmicTheme.colors.plasma)
                    }
                }
            }
            return@Column
        }

        // ── Header info ──
        if (state.plan != null) {
            val plan = state.plan!!
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CosmicTheme.colors.nebula
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(plan.title, color = CosmicTheme.colors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    if (plan.description != null) {
                        Text(plan.description, color = CosmicTheme.colors.textSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Người tạo: ${plan.creatorStudentCode}", color = CosmicTheme.colors.textTertiary, fontSize = 12.sp)
                        if (plan.deadline != null) {
                            val formattedDeadline = try {
                                val date = java.time.LocalDate.parse(plan.deadline)
                                date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            } catch (_: Exception) { plan.deadline }
                            Text("Hạn: $formattedDeadline", color = CosmicTheme.colors.textTertiary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Members section ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Thành viên (${state.members.count { it.status == "ACCEPTED" }})",
                color = CosmicTheme.colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = { showInviteDialog = true }) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp), tint = CosmicTheme.colors.plasma)
                Spacer(Modifier.width(4.dp))
                Text("Mời", color = CosmicTheme.colors.plasma, fontSize = 13.sp)
            }
        }

        if (state.members.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.members.forEach { member ->
                    val statusColor = when (member.status) {
                        "ACCEPTED" -> CosmicTheme.colors.aurora
                        "PENDING" -> CosmicTheme.colors.plasma
                        else -> CosmicTheme.colors.textTertiary
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(60.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(statusColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = member.studentCode.take(2).uppercase(),
                                color = statusColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = member.studentCode,
                            color = CosmicTheme.colors.textTertiary,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Tab: Chưa hoàn thành / Đã hoàn thành ──
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = CosmicTheme.colors.plasma,
            divider = { HorizontalDivider(color = CosmicTheme.colors.glassBorder) }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Chưa hoàn thành", fontSize = 13.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Đã hoàn thành", fontSize = 13.sp) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Task list ──
        val filteredTasks = state.tasks
            .filter { if (selectedTab == 0) !it.completed else it.completed }
            .sortedByDescending { it.createdAt?.let { c -> try { java.time.LocalDateTime.parse(c, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() } catch (_: Exception) { 0L } } ?: 0L }

        if (filteredTasks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedTab == 0) "Chưa có nhiệm vụ nào" else "Chưa có nhiệm vụ hoàn thành",
                    color = CosmicTheme.colors.textTertiary,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filteredTasks, key = { it.id }) { task ->
                    GroupTaskItem(
                        task = task,
                        onToggle = { viewModel.toggleTask(task.id, !task.completed) },
                        onDeleteRequest = { taskToDelete = task }
                    )
                }
            }
        }
    }
    } // Scaffold


    // ── Add Task BottomSheet ──
    if (showAddTaskSheet) {
        AddTaskSheet(
            creatorStudentCode = state.plan?.creatorStudentCode ?: "",
            members = state.members.map { it.studentCode },
            loading = state.actionLoading,
            onDismiss = { showAddTaskSheet = false },
            onConfirm = { title, description, assignedTo, deadline ->
                viewModel.addTask(planId, title, description, assignedTo, deadline)
                showAddTaskSheet = false
            }
        )
    }

    // ── Invite Dialog ──
    if (showInviteDialog) {
        InviteMemberDialog(
            inviteCode = state.inviteCode,
            onCodeChange = { viewModel.updateInviteCode(it) },
            loading = state.inviteLoading,
            error = state.inviteError,
            onDismiss = {
                showInviteDialog = false
                viewModel.clearInviteError()
            },
            onConfirm = {
                viewModel.inviteMember(planId, state.inviteCode)
            }
        )
    }

    // ── Invite Success Dialog ──
    val inviteSuccessCode = state.inviteSuccessCode
    if (inviteSuccessCode != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearInviteSuccess() },
            title = { Text("Mời thành viên", color = CosmicTheme.colors.textPrimary) },
            text = {
                Text(
                    text = "Đã mời thành viên $inviteSuccessCode thành công, bạn có muốn tiếp tục mời?",
                    color = CosmicTheme.colors.textSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearInviteSuccess()
                    showInviteDialog = true
                }) {
                    Text("Tiếp tục", color = CosmicTheme.colors.plasma)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.clearInviteSuccess() }) {
                    Text("Huỷ", color = CosmicTheme.colors.textSecondary)
                }
            },
            containerColor = CosmicTheme.colors.nebula
        )
    }
}

@Composable
private fun GroupTaskItem(
    task: GroupTaskResponse,
    onToggle: () -> Unit,
    onDeleteRequest: () -> Unit
) {
    val bgColor = if (task.completed)
        Color(0xFF2E7D32).copy(alpha = 0.08f)
    else
        Color(0xFFF9A825).copy(alpha = 0.08f)

    val borderColor = if (task.completed)
        Color(0xFF2E7D32).copy(alpha = 0.2f)
    else
        Color(0xFFF9A825).copy(alpha = 0.25f)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onToggle)
                    .background(
                        if (task.completed) Color(0xFF2E7D32) else Color.Transparent,
                        CircleShape
                    )
                    .border(
                        1.5.dp,
                        if (task.completed) Color(0xFF2E7D32) else CosmicTheme.colors.textTertiary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.completed) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = if (task.completed) CosmicTheme.colors.textTertiary else CosmicTheme.colors.textPrimary,
                    fontSize = 14.sp,
                    textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (task.assignedTo != null) {
                    Text(
                        text = "Giao cho: ${task.assignedTo}",
                        color = CosmicTheme.colors.textTertiary,
                        fontSize = 11.sp
                    )
                }
                if (task.deadline != null) {
                    Text(
                        text = "Hạn: ${task.deadline}",
                        color = CosmicTheme.colors.textTertiary,
                        fontSize = 11.sp
                    )
                }
            }
                if (task.deleteRequested) {
                Text(
                    text = "Đang chờ",
                    color = CosmicTheme.colors.plasma,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            } else if (!task.completed) {
                IconButton(onClick = onDeleteRequest, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Xoá", tint = CosmicTheme.colors.textTertiary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun InviteMemberDialog(
    inviteCode: String,
    onCodeChange: (String) -> Unit,
    loading: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mời thành viên", color = CosmicTheme.colors.textPrimary) },
        text = {
            Column {
                Text("Nhập tên đăng nhập:", color = CosmicTheme.colors.textSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = inviteCode,
                    onValueChange = onCodeChange,
                    placeholder = { Text("Tên đăng nhập") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        cursorColor = CosmicTheme.colors.plasma,
                        focusedTextColor = CosmicTheme.colors.textPrimary,
                        unfocusedTextColor = CosmicTheme.colors.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                if (error != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = error, color = CosmicTheme.colors.supernova, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = inviteCode.isNotBlank() && !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = CosmicTheme.colors.plasma)
                } else {
                    Text("Mời", color = CosmicTheme.colors.plasma)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huỷ", color = CosmicTheme.colors.textSecondary)
            }
        },
        containerColor = CosmicTheme.colors.nebula
    )
}

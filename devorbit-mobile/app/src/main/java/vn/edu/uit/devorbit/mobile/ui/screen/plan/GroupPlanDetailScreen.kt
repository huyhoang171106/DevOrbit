package vn.edu.uit.devorbit.mobile.ui.screen.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import vn.edu.uit.devorbit.mobile.data.remote.dto.GroupPlanMemberResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.GroupTaskResponse
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.GroupPlanViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.TaskFilter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    var showPendingMembers by remember { mutableStateOf(false) }
    var showRemoveMemberDialog by remember { mutableStateOf(false) }
    var memberToRemove by remember { mutableStateOf<GroupPlanMemberResponse?>(null) }
    var showMemberListDialog by remember { mutableStateOf(false) }

    val acceptedMembers = state.members.filter { it.status == "ACCEPTED" }
    val pendingMembers = state.members.filter { it.status == "PENDING" }

    LaunchedEffect(planId) {
        viewModel.loadPlan(planId)
    }

    // Task delete confirmation
    val taskToDeleteSafe = taskToDelete
    if (taskToDeleteSafe != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            shape = RoundedCornerShape(20.dp),
            title = { Text("Xác nhận xoá", color = CosmicTheme.colors.textPrimary) },
            text = { Text("Yêu cầu xoá nhiệm vụ \"${taskToDeleteSafe.title}\"?", color = CosmicTheme.colors.textSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.requestDeleteTask(taskToDelete!!.id)
                        taskToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicTheme.colors.supernova.copy(alpha = 0.15f),
                        contentColor = CosmicTheme.colors.supernova
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
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
            shape = RoundedCornerShape(20.dp),
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
                Button(
                    onClick = {
                        viewModel.leavePlan(planId) { showLeaveDialog = false; onNavigateLeave() }
                    },
                    enabled = !state.actionLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicTheme.colors.supernova.copy(alpha = 0.15f),
                        contentColor = CosmicTheme.colors.supernova
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.actionLoading) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = CosmicTheme.colors.supernova)
                    } else {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Rời")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLeaveDialog = false; viewModel.clearActionError() },
                    enabled = !state.actionLoading
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
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
            shape = RoundedCornerShape(20.dp),
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
                Button(
                    onClick = {
                        if (isDeleteRequest) {
                            viewModel.approveDeletePlan(planId, approved = true) {
                                showDeletePlanDialog = false; onNavigateLeave()
                            }
                        } else {
                            viewModel.deletePlan(planId) { showDeletePlanDialog = false; onNavigateLeave() }
                        }
                    },
                    enabled = !state.actionLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicTheme.colors.supernova.copy(alpha = 0.15f),
                        contentColor = CosmicTheme.colors.supernova
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.actionLoading) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = CosmicTheme.colors.supernova)
                    } else {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Xoá")
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
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Từ chối", color = CosmicTheme.colors.textSecondary)
                    }
                } else {
                    TextButton(
                        onClick = { showDeletePlanDialog = false; viewModel.clearActionError() },
                        enabled = !state.actionLoading
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Huỷ", color = CosmicTheme.colors.textSecondary)
                    }
                }
            },
            containerColor = CosmicTheme.colors.nebula
        )
    }

    // ── Remove member confirmation ──
    memberToRemove?.let { member ->
        AlertDialog(
            onDismissRequest = { showRemoveMemberDialog = false; memberToRemove = null },
            title = { Text("Xoá thành viên", color = CosmicTheme.colors.textPrimary) },
            text = {
                Text(
                    "Xoá thành viên ${member.studentCode} khỏi kế hoạch?",
                    color = CosmicTheme.colors.textSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeMember(planId, member.id)
                        showRemoveMemberDialog = false
                        memberToRemove = null
                    },
                    enabled = !state.actionLoading
                ) {
                    if (state.actionLoading) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = CosmicTheme.colors.supernova)
                    } else {
                        Text("Xoá", color = CosmicTheme.colors.supernova)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRemoveMemberDialog = false; memberToRemove = null },
                    enabled = !state.actionLoading
                ) {
                    Text("Huỷ", color = CosmicTheme.colors.textSecondary)
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
                    onDismissRequest = { showMenu = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.isCreator) {
                        DropdownMenuItem(
                            text = { Text("Rời kế hoạch", color = CosmicTheme.colors.supernova) },
                            onClick = { showMenu = false; viewModel.showTransferDialog() },
                            leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = CosmicTheme.colors.supernova) }
                        )
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
                shape = RoundedCornerShape(16.dp),
                color = CosmicTheme.colors.nebula
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(plan.title, color = CosmicTheme.colors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    if (plan.description != null) {
                        Text(plan.description, color = CosmicTheme.colors.textSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Trưởng nhóm: ${plan.creatorStudentCode}", color = CosmicTheme.colors.textTertiary, fontSize = 12.sp)
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
                text = "Thành viên (${acceptedMembers.size})",
                color = CosmicTheme.colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { showMemberListDialog = true }
            )
            TextButton(onClick = { showInviteDialog = true }) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp), tint = CosmicTheme.colors.plasma)
                Spacer(Modifier.width(4.dp))
                Text("Mời", color = CosmicTheme.colors.plasma, fontSize = 13.sp)
            }
        }

        if (acceptedMembers.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(acceptedMembers) { member ->
                    val isRemovable = state.isCreator && member.studentCode != state.currentUserCode
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(60.dp)
                            .then(
                                if (isRemovable) Modifier.combinedClickable(
                                    onClick = {},
                                    onLongClick = {
                                        memberToRemove = member
                                        showRemoveMemberDialog = true
                                    }
                                ) else Modifier
                            )
                    ) {
                        val avatarColor = memberAvatarColor(member.studentCode)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(avatarColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = member.studentCode.take(2).uppercase(),
                                color = avatarColor,
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

        // ── Pending members section ──
        if (pendingMembers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPendingMembers = !showPendingMembers },
                shape = RoundedCornerShape(12.dp),
                color = CosmicTheme.colors.nebula.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (showPendingMembers) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = CosmicTheme.colors.plasma,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Đang mời (${pendingMembers.size})",
                            color = CosmicTheme.colors.plasma,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            if (showPendingMembers) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pendingMembers.forEach { member ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(60.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CosmicTheme.colors.plasma.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = member.studentCode.take(2).uppercase(),
                                    color = CosmicTheme.colors.plasma,
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
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Time filter ──
        FilterTabs(
            selectedFilter = state.timeFilter,
            onSelectFilter = { viewModel.setTimeFilter(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Member filter ──
        AssignedMemberFilter(
            members = acceptedMembers.map { it.studentCode },
            selectedMember = state.memberFilter,
            onSelectMember = { viewModel.setMemberFilter(it) }
        )

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
        val nowDate = java.time.LocalDate.now()
        val startOfWeek = nowDate.with(java.time.DayOfWeek.MONDAY)
        val endOfWeek = startOfWeek.plusDays(6)

        val filteredTasks = state.tasks
            .filter { task ->
                val matchTab = if (selectedTab == 0) !task.completed else task.completed
                val matchMember = state.memberFilter == null || task.assignedTo == state.memberFilter
                val matchTime = when (state.timeFilter) {
                    TaskFilter.TODAY -> {
                        task.deadline?.let { d ->
                            try { java.time.LocalDate.parse(d) == nowDate } catch (_: Exception) { false }
                        } ?: false
                    }
                    TaskFilter.WEEK -> {
                        task.deadline?.let { d ->
                            try {
                                val date = java.time.LocalDate.parse(d)
                                !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek)
                            } catch (_: Exception) { false }
                        } ?: false
                    }
                    TaskFilter.ALL -> true
                }
                matchTab && matchMember && matchTime
            }
            .sortedWith(compareBy<GroupTaskResponse> {
                when {
                    it.completed -> 2
                    it.deadline?.let { d ->
                        try { java.time.LocalDate.parse(d).isBefore(nowDate) } catch (_: Exception) { false }
                    } == true -> 1
                    else -> 0
                }
            }.thenBy {
                it.deadline?.let { d ->
                    try { java.time.LocalDate.parse(d).toEpochDay() } catch (_: Exception) { Long.MAX_VALUE }
                } ?: Long.MAX_VALUE
            })

        val completedCount = filteredTasks.count { it.completed }
        Text(
            text = "$completedCount/${filteredTasks.size} hoàn thành",
            color = CosmicTheme.colors.textTertiary,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

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
            members = acceptedMembers.map { it.studentCode },
            loading = state.actionLoading,
            onDismiss = { showAddTaskSheet = false },
                onConfirm = { title, description, assignedTo, deadline ->
                    viewModel.addTask(planId, title, description, assignedTo, deadline) {
                        showAddTaskSheet = false
                    }
                }
        )
    }

    // ── Member List Dialog ──
    if (showMemberListDialog) {
        MemberListDialog(
            members = acceptedMembers,
            isCreator = state.isCreator,
            currentUserCode = state.currentUserCode,
            onDismiss = { showMemberListDialog = false },
            onRemoveMember = { member ->
                memberToRemove = member
                showRemoveMemberDialog = true
                showMemberListDialog = false
            }
        )
    }

    // ── Transfer Ownership Dialog ──
    if (state.showTransferDialog) {
        val acceptedMembers = state.members.filter { it.status == "ACCEPTED" && it.studentCode != state.currentUserCode }
        var selectedMember by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { viewModel.hideTransferDialog(); selectedMember = null },
            shape = RoundedCornerShape(20.dp),
            title = { Text("Chuyển quyền trưởng nhóm", color = CosmicTheme.colors.textPrimary) },
            text = {
                Column {
                    Text("Bạn sắp rời kế hoạch. Hãy chọn trưởng nhóm mới:", color = CosmicTheme.colors.textSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (acceptedMembers.isEmpty()) {
                        Text("Không có thành viên nào để chuyển quyền.", color = CosmicTheme.colors.supernova)
                    } else {
                        acceptedMembers.forEach { member ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (selectedMember == member.studentCode)
                                            CosmicTheme.colors.plasma.copy(alpha = 0.1f)
                                        else Color.Transparent
                                    )
                                    .clickable { selectedMember = member.studentCode }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedMember == member.studentCode,
                                    onClick = { selectedMember = member.studentCode },
                                    colors = RadioButtonDefaults.colors(selectedColor = CosmicTheme.colors.plasma)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(member.studentCode, color = CosmicTheme.colors.textPrimary)
                            }
                        }
                    }
                    if (state.actionError != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(state.actionError!!, color = CosmicTheme.colors.supernova, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedMember?.let { code ->
                            viewModel.transferOwnership(planId, code, onSuccess = { onNavigateLeave() })
                        }
                    },
                    enabled = selectedMember != null && !state.transferLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicTheme.colors.supernova.copy(alpha = 0.15f),
                        contentColor = CosmicTheme.colors.supernova
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.transferLoading) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = CosmicTheme.colors.supernova)
                    } else {
                        Text("Chuyển & Rời")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideTransferDialog(); selectedMember = null },
                    enabled = !state.transferLoading
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Huỷ", color = CosmicTheme.colors.textSecondary)
                }
            },
            containerColor = CosmicTheme.colors.nebula
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

    // ── Auto-close invite dialog on success ──
    LaunchedEffect(state.inviteSuccessCode) {
        if (state.inviteSuccessCode != null) {
            showInviteDialog = false
        }
    }

    // ── Invite Success Dialog ──
    val inviteSuccessCode = state.inviteSuccessCode
    if (inviteSuccessCode != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearInviteSuccess() },
            shape = RoundedCornerShape(20.dp),
            title = { Text("Mời thành viên", color = CosmicTheme.colors.textPrimary) },
            text = {
                Text(
                    text = "Đã mời thành viên $inviteSuccessCode thành công, bạn có muốn tiếp tục mời?",
                    color = CosmicTheme.colors.textSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearInviteSuccess()
                        showInviteDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                        contentColor = CosmicTheme.colors.plasma
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Tiếp tục")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.clearInviteSuccess() }) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Huỷ", color = CosmicTheme.colors.textSecondary)
                }
            },
            containerColor = CosmicTheme.colors.nebula
        )
    }
}

private val avatarColors = listOf(
    Color(0xFF66BB6A),
    Color(0xFFFF7043),
    Color(0xFF42A5F5),
    Color(0xFFAB47BC),
    Color(0xFFEF5350),
    Color(0xFF26C6DA),
    Color(0xFFFFCA28),
)

private fun memberAvatarColor(studentCode: String): Color {
    val idx = kotlin.math.abs(studentCode.hashCode()) % avatarColors.size
    return avatarColors[idx]
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
                if (!task.description.isNullOrBlank()) {
                    Text(
                        text = task.description,
                        color = CosmicTheme.colors.textSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (task.assignedTo != null) {
                    Text(
                        text = "Giao cho: ${task.assignedTo}",
                        color = CosmicTheme.colors.textTertiary,
                        fontSize = 11.sp
                    )
                }
                if (task.deadline != null) {
                    val isOverdue = !task.completed && try {
                        java.time.LocalDate.parse(task.deadline).isBefore(java.time.LocalDate.now())
                    } catch (_: Exception) { false }
                    Text(
                        text = "Hạn: ${
                            try {
                                java.time.LocalDate.parse(task.deadline)
                                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            } catch (_: Exception) { task.deadline }
                        }",
                        color = if (isOverdue) CosmicTheme.colors.supernova else CosmicTheme.colors.textTertiary,
                        fontSize = 11.sp
                    )
                }
            }
            if (task.deleteRequested) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CosmicTheme.colors.plasma.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Đang chờ",
                        color = CosmicTheme.colors.plasma,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            } else if (!task.completed) {
                IconButton(onClick = onDeleteRequest, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Xoá", tint = CosmicTheme.colors.textTertiary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun MemberListDialog(
    members: List<GroupPlanMemberResponse>,
    isCreator: Boolean,
    currentUserCode: String,
    onDismiss: () -> Unit,
    onRemoveMember: (GroupPlanMemberResponse) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("Thành viên (${members.size})", color = CosmicTheme.colors.textPrimary)
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(members) { member ->
                    val isRemovable = isCreator && member.studentCode != currentUserCode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val avatarColor = memberAvatarColor(member.studentCode)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(avatarColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = member.studentCode.take(2).uppercase(),
                                color = avatarColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = member.studentCode,
                                color = CosmicTheme.colors.textPrimary,
                                fontSize = 14.sp
                            )
                        }
                        if (isRemovable) {
                            IconButton(
                                onClick = { onRemoveMember(member) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.PersonRemove,
                                    contentDescription = "Xoá thành viên",
                                    tint = CosmicTheme.colors.supernova,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng", color = CosmicTheme.colors.plasma)
            }
        },
        containerColor = CosmicTheme.colors.nebula
    )
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
        shape = RoundedCornerShape(20.dp),
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
            Button(
                onClick = onConfirm,
                enabled = inviteCode.isNotBlank() && !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                    contentColor = CosmicTheme.colors.plasma
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = CosmicTheme.colors.plasma)
                } else {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Mời")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Huỷ", color = CosmicTheme.colors.textSecondary)
            }
        },
        containerColor = CosmicTheme.colors.nebula
    )
}

@Composable
private fun FilterTabs(
    selectedFilter: TaskFilter,
    onSelectFilter: (TaskFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == TaskFilter.TODAY,
            onClick = { onSelectFilter(TaskFilter.TODAY) },
            label = { Text("Hôm nay", fontSize = 13.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                selectedLabelColor = CosmicTheme.colors.plasma
            ),
            border = FilterChipDefaults.filterChipBorder(
                borderColor = CosmicTheme.colors.glassBorder,
                selectedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
                enabled = true,
                selected = selectedFilter == TaskFilter.TODAY
            )
        )
        FilterChip(
            selected = selectedFilter == TaskFilter.WEEK,
            onClick = { onSelectFilter(TaskFilter.WEEK) },
            label = { Text("Tuần này", fontSize = 13.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                selectedLabelColor = CosmicTheme.colors.plasma
            ),
            border = FilterChipDefaults.filterChipBorder(
                borderColor = CosmicTheme.colors.glassBorder,
                selectedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
                enabled = true,
                selected = selectedFilter == TaskFilter.WEEK
            )
        )
        FilterChip(
            selected = selectedFilter == TaskFilter.ALL,
            onClick = { onSelectFilter(TaskFilter.ALL) },
            label = { Text("Tất cả", fontSize = 13.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                selectedLabelColor = CosmicTheme.colors.plasma
            ),
            border = FilterChipDefaults.filterChipBorder(
                borderColor = CosmicTheme.colors.glassBorder,
                selectedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
                enabled = true,
                selected = selectedFilter == TaskFilter.ALL
            )
        )
    }
}

@Composable
private fun AssignedMemberFilter(
    members: List<String>,
    selectedMember: String?,
    onSelectMember: (String?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedMember == null,
            onClick = { onSelectMember(null) },
            label = { Text("Tất cả", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                selectedLabelColor = CosmicTheme.colors.plasma
            ),
            border = FilterChipDefaults.filterChipBorder(
                borderColor = CosmicTheme.colors.glassBorder,
                selectedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
                enabled = true,
                selected = selectedMember == null
            )
        )
        members.forEach { member ->
            FilterChip(
                selected = selectedMember == member,
                onClick = {
                    if (selectedMember == member) onSelectMember(null) else onSelectMember(member)
                },
                label = { Text(member, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CosmicTheme.colors.aurora.copy(alpha = 0.15f),
                    selectedLabelColor = CosmicTheme.colors.aurora
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = CosmicTheme.colors.glassBorder,
                    selectedBorderColor = CosmicTheme.colors.aurora.copy(alpha = 0.5f),
                    enabled = true,
                    selected = selectedMember == member
                )
            )
        }
    }
}

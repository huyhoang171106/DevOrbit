package vn.edu.uit.devorbit.mobile.ui.screen.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.domain.model.TaskItem
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.TaskFilter
import vn.edu.uit.devorbit.mobile.ui.viewmodel.TaskManagementViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagementScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToGroupPlan: (Long) -> Unit = {},
    viewModel: TaskManagementViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showAddTaskModal by remember { mutableStateOf(false) }

    // Create plan dialog
    if (state.showCreatePlanDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideCreatePlanDialog() },
            title = { Text("Tạo kế hoạch nhóm", color = CosmicTheme.colors.textPrimary) },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.planTitle,
                        onValueChange = { viewModel.updatePlanTitle(it) },
                        placeholder = { Text("Tên kế hoạch", color = CosmicTheme.colors.textTertiary) },
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
                    if (state.planError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(state.planError!!, color = CosmicTheme.colors.supernova, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.createGroupPlan(onNavigateToGroupPlan) },
                    enabled = state.planTitle.isNotBlank() && !state.creatingPlan
                ) {
                    if (state.creatingPlan) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = CosmicTheme.colors.plasma)
                    } else {
                        Text("Tạo", color = CosmicTheme.colors.plasma)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideCreatePlanDialog() }) {
                    Text("Huỷ", color = CosmicTheme.colors.textSecondary)
                }
            },
            containerColor = CosmicTheme.colors.nebula
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Header ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quản lý nhiệm vụ",
                    style = CosmicTheme.typography.display.copy(fontSize = 22.sp),
                    color = CosmicTheme.colors.textPrimary
                )
                TextButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Đóng",
                        tint = CosmicTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Search Bar ──
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Tìm nhiệm vụ...", color = CosmicTheme.colors.textTertiary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CosmicTheme.colors.textSecondary) },
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

            Spacer(modifier = Modifier.height(8.dp))

            // ── Group Plan Section ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kế hoạch nhóm",
                    color = CosmicTheme.colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = { viewModel.showCreatePlanDialog() }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = CosmicTheme.colors.plasma)
                    Spacer(Modifier.width(4.dp))
                    Text("Tạo mới", color = CosmicTheme.colors.plasma, fontSize = 13.sp)
                }
            }
            if (state.groupPlansLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp), color = CosmicTheme.colors.plasma)
                }
            } else if (state.groupPlans.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CosmicTheme.colors.nebula
                ) {
                    Text(
                        text = "Bạn chưa tham gia kế hoạch nhóm nào",
                        color = CosmicTheme.colors.textTertiary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                state.groupPlans.forEach { plan ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToGroupPlan(plan.id) },
                        shape = RoundedCornerShape(12.dp),
                        color = CosmicTheme.colors.nebula
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = CosmicTheme.colors.plasma, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(plan.title, color = CosmicTheme.colors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                if (plan.deadline != null) {
                                    Text("Hạn: ${plan.deadline}", color = CosmicTheme.colors.textTertiary, fontSize = 11.sp)
                                }
                            }
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = CosmicTheme.colors.textTertiary, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Filter Tabs ──
            FilterTabs(
                selectedFilter = state.filter,
                onSelectFilter = { viewModel.setFilter(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Task List ──
            Box(modifier = Modifier.weight(1f)) {
                if (state.tasks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Không có nhiệm vụ nào",
                            style = CosmicTheme.typography.body,
                            color = CosmicTheme.colors.textTertiary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(state.tasks, key = { it.id }) { task ->
                            TaskItem(
                                task = task,
                                onToggle = { viewModel.toggleTask(task.id, !task.completed) },
                                onEdit = {
                                    viewModel.startEdit(task)
                                    showAddTaskModal = true
                                }
                            )
                        }
                    }
                }
            }
        }

        // ── FAB ──
        FloatingActionButton(
            onClick = {
                viewModel.resetInput()
                showAddTaskModal = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = CosmicTheme.colors.plasma,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.Add, contentDescription = "Thêm nhiệm vụ")
        }
    }

    // ── Add/Edit Task Modal ──
    if (showAddTaskModal || state.isEditing) {
        key(state.editingTaskId ?: "new") {
            AddTaskModal(
                isEditing = state.isEditing,
                title = state.inputTitle,
                onTitleChange = { viewModel.updateTitle(it) },
                description = state.inputDescription,
                onDescriptionChange = { viewModel.updateDescription(it) },
                deadline = state.inputDeadline,
                onDeadlineClick = if (state.inputRecurrence != null) { { viewModel.showTimePicker() } } else { { viewModel.showDatePicker() } },
                showDatePicker = state.showDatePicker,
                onDateSelected = { viewModel.updateDeadline(it) },
                onDismissDatePicker = { viewModel.hideDatePicker() },
                showTimePicker = state.showTimePicker,
                onTimeSelected = { h, m -> viewModel.updateTime(h, m) },
                onDismissTimePicker = { viewModel.hideTimePicker() },
                recurrence = state.inputRecurrence,
                onRecurrenceChange = { viewModel.updateRecurrence(it) },
                recurrenceDays = state.inputRecurrenceDays,
                onRecurrenceDayToggle = { viewModel.toggleRecurrenceDay(it) },
                recurrenceStartDate = state.inputRecurrenceStartDate,
                onRecurrenceStartDateClick = { viewModel.showRecurrenceStartPicker() },
                showRecurrenceStartPicker = state.showRecurrenceStartPicker,
                onRecurrenceStartDateSelected = { viewModel.updateRecurrenceStartDate(it) },
                onDismissRecurrenceStartPicker = { viewModel.hideRecurrenceStartPicker() },
                recurrenceEndDate = state.inputRecurrenceEndDate,
                onRecurrenceEndDateClick = { viewModel.showRecurrenceEndPicker() },
                showRecurrenceEndPicker = state.showRecurrenceEndPicker,
                onRecurrenceEndDateSelected = { viewModel.updateRecurrenceEndDate(it) },
                onDismissRecurrenceEndPicker = { viewModel.hideRecurrenceEndPicker() },
                onConfirm = {
                    viewModel.saveTask()
                    showAddTaskModal = false
                },
                onDelete = state.isEditing.takeIf { it }?.let {
                    {
                        state.editingTaskId?.let { viewModel.deleteTask(it) }
                        viewModel.resetInput()
                        showAddTaskModal = false
                    }
                },
                onDismiss = {
                    viewModel.resetInput()
                    showAddTaskModal = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecurrenceDropdown(
    recurrence: String?,
    recurrenceDays: Int?,
    onRecurrenceChange: (String?) -> Unit,
    onDayToggle: (DayOfWeek) -> Unit
) {
    val days = listOf(
        DayOfWeek.MONDAY to "Mỗi thứ 2",
        DayOfWeek.TUESDAY to "Mỗi thứ 3",
        DayOfWeek.WEDNESDAY to "Mỗi thứ 4",
        DayOfWeek.THURSDAY to "Mỗi thứ 5",
        DayOfWeek.FRIDAY to "Mỗi thứ 6",
        DayOfWeek.SATURDAY to "Mỗi thứ 7",
        DayOfWeek.SUNDAY to "Mỗi chủ nhật"
    )

    val isKhongLap = recurrence == null
    val isMoiNgay = recurrence == "DAILY"

    val summary = when {
        isKhongLap -> "Không lặp"
        isMoiNgay -> "Mỗi ngày"
        else -> {
            days.filter { (day, _) ->
                val bit = 1 shl (day.value - 1)
                recurrenceDays != null && recurrenceDays and bit != 0
            }.joinToString(", ") { it.second }
        }
    }

    val isDayDisabled = isKhongLap || isMoiNgay

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = summary,
            onValueChange = {},
            readOnly = true,
            label = { Text("Lặp lại", color = CosmicTheme.colors.textTertiary) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                cursorColor = CosmicTheme.colors.plasma,
                focusedTextColor = CosmicTheme.colors.textPrimary,
                unfocusedTextColor = CosmicTheme.colors.textPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isKhongLap,
                            onCheckedChange = {
                                onRecurrenceChange(null)
                                expanded = false
                            },
                            colors = CheckboxDefaults.colors(checkedColor = CosmicTheme.colors.plasma)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Không lặp")
                    }
                },
                onClick = {
                    onRecurrenceChange(null)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isMoiNgay,
                            onCheckedChange = {
                                onRecurrenceChange("DAILY")
                                expanded = false
                            },
                            colors = CheckboxDefaults.colors(checkedColor = CosmicTheme.colors.plasma)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Mỗi ngày")
                    }
                },
                onClick = {
                    onRecurrenceChange("DAILY")
                    expanded = false
                }
            )
            HorizontalDivider()
            days.forEach { (day, label) ->
                val bit = 1 shl (day.value - 1)
                val isSelected = recurrenceDays != null && recurrenceDays and bit != 0
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    if (!isDayDisabled) onDayToggle(day)
                                },
                                enabled = !isDayDisabled,
                                colors = CheckboxDefaults.colors(checkedColor = CosmicTheme.colors.plasma)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(label, color = if (isDayDisabled) CosmicTheme.colors.textTertiary else Color.Unspecified)
                        }
                    },
                    onClick = {
                        if (!isDayDisabled) onDayToggle(day)
                    },
                    enabled = !isDayDisabled
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskModal(
    isEditing: Boolean,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    deadline: Long?,
    onDeadlineClick: () -> Unit,
    showDatePicker: Boolean,
    onDateSelected: (Long?) -> Unit,
    onDismissDatePicker: () -> Unit,
    showTimePicker: Boolean,
    onTimeSelected: (Int, Int) -> Unit,
    onDismissTimePicker: () -> Unit,
    recurrence: String?,
    recurrenceStartDate: Long?,
    onRecurrenceStartDateClick: () -> Unit,
    showRecurrenceStartPicker: Boolean,
    onRecurrenceStartDateSelected: (Long?) -> Unit,
    onDismissRecurrenceStartPicker: () -> Unit,
    recurrenceEndDate: Long?,
    onRecurrenceEndDateClick: () -> Unit,
    showRecurrenceEndPicker: Boolean,
    onRecurrenceEndDateSelected: (Long?) -> Unit,
    onDismissRecurrenceEndPicker: () -> Unit,
    onRecurrenceChange: (String?) -> Unit,
    recurrenceDays: Int?,
    onRecurrenceDayToggle: (DayOfWeek) -> Unit,
    onConfirm: () -> Unit,
    onDelete: (() -> Unit)? = null,
    deleteLoading: Boolean = false,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = deadline ?: System.currentTimeMillis()
    )

    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = recurrenceStartDate ?: System.currentTimeMillis()
    )

    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = recurrenceEndDate ?: System.currentTimeMillis()
    )

    val timePickerState = rememberTimePickerState(
        initialHour = 0,
        initialMinute = 0,
        is24Hour = true
    )

    var showDeleteConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CosmicTheme.colors.nebula,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = if (isEditing) "Chỉnh sửa nhiệm vụ" else "Thêm nhiệm vụ",
                color = CosmicTheme.colors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = { Text("Tên nhiệm vụ", color = CosmicTheme.colors.textTertiary) },
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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                placeholder = { Text("Mô tả (không bắt buộc)", color = CosmicTheme.colors.textTertiary) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    cursorColor = CosmicTheme.colors.plasma,
                    focusedTextColor = CosmicTheme.colors.textPrimary,
                    unfocusedTextColor = CosmicTheme.colors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Recurrence Dropdown ──
            RecurrenceDropdown(
                recurrence = recurrence,
                recurrenceDays = recurrenceDays,
                onRecurrenceChange = onRecurrenceChange,
                onDayToggle = onRecurrenceDayToggle
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (recurrence == null) {
                // ── Deadline (date + time) ──
                OutlinedCard(
                    onClick = onDeadlineClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = Color.White.copy(alpha = 0.03f)
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = CosmicTheme.colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (deadline != null) formatDeadline(deadline) else "Chọn deadline",
                            style = CosmicTheme.typography.body,
                            color = if (deadline != null) CosmicTheme.colors.textPrimary else CosmicTheme.colors.textTertiary
                        )
                    }
                }
            } else {
                // ── Time only (for recurring tasks) ──
                OutlinedCard(
                    onClick = onDeadlineClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = Color.White.copy(alpha = 0.03f)
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = CosmicTheme.colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (deadline != null) formatTime(deadline) else "Chọn giờ",
                            style = CosmicTheme.typography.body,
                            color = if (deadline != null) CosmicTheme.colors.textPrimary else CosmicTheme.colors.textTertiary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── Start date ──
                OutlinedCard(
                    onClick = onRecurrenceStartDateClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = Color.White.copy(alpha = 0.03f)
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = CosmicTheme.colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (recurrenceStartDate != null) formatDate(recurrenceStartDate) else "Ngày bắt đầu",
                            style = CosmicTheme.typography.body,
                            color = if (recurrenceStartDate != null) CosmicTheme.colors.textPrimary else CosmicTheme.colors.textTertiary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── End date ──
                OutlinedCard(
                    onClick = onRecurrenceEndDateClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = Color.White.copy(alpha = 0.03f)
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = CosmicTheme.colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (recurrenceEndDate != null) formatDate(recurrenceEndDate) else "Ngày kết thúc",
                            style = CosmicTheme.typography.body,
                            color = if (recurrenceEndDate != null) CosmicTheme.colors.textPrimary else CosmicTheme.colors.textTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isEditing && onDelete != null) {
                TextButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Xoá nhiệm vụ", color = CosmicTheme.colors.supernova, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = onConfirm,
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CosmicTheme.colors.plasma,
                    disabledContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.3f)
                )
            ) {
                Text(if (isEditing) "Lưu" else "Thêm", color = Color.Black, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xoá nhiệm vụ", color = CosmicTheme.colors.textPrimary) },
            text = { Text("Bạn có chắc muốn xoá nhiệm vụ này?", color = CosmicTheme.colors.textSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (!deleteLoading) {
                            showDeleteConfirm = false
                            onDelete?.invoke()
                        }
                    },
                    enabled = !deleteLoading
                ) {
                    if (deleteLoading) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = CosmicTheme.colors.supernova)
                    } else {
                        Text("Xoá", color = CosmicTheme.colors.supernova)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Huỷ", color = CosmicTheme.colors.textSecondary)
                }
            },
            containerColor = CosmicTheme.colors.nebula
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = onDismissDatePicker,
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismissDatePicker()
                }) {
                    Text("Chọn", color = CosmicTheme.colors.plasma)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDateSelected(null)
                    onDismissDatePicker()
                }) {
                    Text("Bỏ", color = CosmicTheme.colors.textSecondary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = CosmicTheme.colors.nebula,
                titleContentColor = CosmicTheme.colors.textPrimary,
                headlineContentColor = CosmicTheme.colors.textPrimary,
                weekdayContentColor = CosmicTheme.colors.textSecondary,
                subheadContentColor = CosmicTheme.colors.textSecondary,
                yearContentColor = CosmicTheme.colors.textPrimary,
                currentYearContentColor = CosmicTheme.colors.plasma,
                selectedYearContentColor = Color.White,
                dayContentColor = CosmicTheme.colors.textPrimary,
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = CosmicTheme.colors.plasma,
                todayContentColor = CosmicTheme.colors.plasma,
                todayDateBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f)
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = onDismissTimePicker,
            title = { Text("Chọn giờ", color = CosmicTheme.colors.textPrimary) },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = CosmicTheme.colors.plasma.copy(alpha = 0.2f),
                        clockDialSelectedContentColor = CosmicTheme.colors.textPrimary,
                        clockDialUnselectedContentColor = CosmicTheme.colors.textSecondary,
                        selectorColor = CosmicTheme.colors.plasma,
                        timeSelectorSelectedContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.2f),
                        timeSelectorSelectedContentColor = CosmicTheme.colors.plasma,
                        timeSelectorUnselectedContainerColor = Color.White.copy(alpha = 0.05f),
                        timeSelectorUnselectedContentColor = CosmicTheme.colors.textSecondary
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                }) {
                    Text("Chọn", color = CosmicTheme.colors.plasma)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissTimePicker) {
                    Text("Huỷ", color = CosmicTheme.colors.textSecondary)
                }
            },
            containerColor = CosmicTheme.colors.nebula
        )
    }

    if (showRecurrenceStartPicker) {
        DatePickerDialog(
            onDismissRequest = onDismissRecurrenceStartPicker,
            confirmButton = {
                TextButton(onClick = {
                    onRecurrenceStartDateSelected(startDatePickerState.selectedDateMillis)
                    onDismissRecurrenceStartPicker()
                }) {
                    Text("Chọn", color = CosmicTheme.colors.plasma)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onRecurrenceStartDateSelected(null)
                    onDismissRecurrenceStartPicker()
                }) {
                    Text("Bỏ", color = CosmicTheme.colors.textSecondary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = CosmicTheme.colors.nebula,
                titleContentColor = CosmicTheme.colors.textPrimary,
                headlineContentColor = CosmicTheme.colors.textPrimary,
                weekdayContentColor = CosmicTheme.colors.textSecondary,
                subheadContentColor = CosmicTheme.colors.textSecondary,
                yearContentColor = CosmicTheme.colors.textPrimary,
                currentYearContentColor = CosmicTheme.colors.plasma,
                selectedYearContentColor = Color.White,
                dayContentColor = CosmicTheme.colors.textPrimary,
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = CosmicTheme.colors.plasma,
                todayContentColor = CosmicTheme.colors.plasma,
                todayDateBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f)
            )
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    if (showRecurrenceEndPicker) {
        DatePickerDialog(
            onDismissRequest = onDismissRecurrenceEndPicker,
            confirmButton = {
                TextButton(onClick = {
                    onRecurrenceEndDateSelected(endDatePickerState.selectedDateMillis)
                    onDismissRecurrenceEndPicker()
                }) {
                    Text("Chọn", color = CosmicTheme.colors.plasma)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onRecurrenceEndDateSelected(null)
                    onDismissRecurrenceEndPicker()
                }) {
                    Text("Bỏ", color = CosmicTheme.colors.textSecondary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = CosmicTheme.colors.nebula,
                titleContentColor = CosmicTheme.colors.textPrimary,
                headlineContentColor = CosmicTheme.colors.textPrimary,
                weekdayContentColor = CosmicTheme.colors.textSecondary,
                subheadContentColor = CosmicTheme.colors.textSecondary,
                yearContentColor = CosmicTheme.colors.textPrimary,
                currentYearContentColor = CosmicTheme.colors.plasma,
                selectedYearContentColor = Color.White,
                dayContentColor = CosmicTheme.colors.textPrimary,
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = CosmicTheme.colors.plasma,
                todayContentColor = CosmicTheme.colors.plasma,
                todayDateBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f)
            )
        ) {
            DatePicker(state = endDatePickerState)
        }
    }
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
            label = { Text("Hôm nay", style = CosmicTheme.typography.label) },
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
            label = { Text("Tuần này", style = CosmicTheme.typography.label) },
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
            label = { Text("Tất cả", style = CosmicTheme.typography.label) },
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
private fun TaskItem(
    task: TaskItem,
    onToggle: () -> Unit,
    onEdit: () -> Unit
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
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
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
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f).clickable(onClick = onEdit)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.title,
                        style = CosmicTheme.typography.body,
                        modifier = Modifier.weight(1f),
                        color = if (task.completed) CosmicTheme.colors.textTertiary else CosmicTheme.colors.textPrimary,
                        textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                    )
                    if (task.recurrence != null) {
                        Icon(
                            Icons.Default.Repeat,
                            contentDescription = "Lặp lại",
                            modifier = Modifier.size(16.dp),
                            tint = CosmicTheme.colors.plasma
                        )
                    }
                }
                if (task.deadline != null) {
                    Text(
                        text = "Hạn: ${formatDeadline(task.deadline)}",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textTertiary
                    )
                }
            }
        }
    }
}

private fun formatDeadline(millis: Long): String {
    val ldt = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
    return ldt.format(DateTimeFormatter.ofPattern("dd/MM HH:mm", Locale("vi", "VN")))
}

private fun formatTime(millis: Long): String {
    val lt = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalTime()
    return lt.format(DateTimeFormatter.ofPattern("HH:mm", Locale("vi", "VN")))
}

private fun formatDate(millis: Long): String {
    val ld = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    return ld.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("vi", "VN")))
}

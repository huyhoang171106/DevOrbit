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
import vn.edu.uit.devorbit.mobile.data.local.entity.TaskEntity
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
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.planDeadline,
                        onValueChange = { viewModel.updatePlanDeadline(it) },
                        placeholder = { Text("Thời hạn (YYYY-MM-DD, không bắt buộc)", color = CosmicTheme.colors.textTertiary) },
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

        // ── Input Section ──
        InputSection(
            title = state.inputTitle,
            onTitleChange = { viewModel.updateTitle(it) },
            deadline = state.inputDeadline,
            onDeadlineClick = { viewModel.showDatePicker() },
            onAdd = { viewModel.addTask() },
            showDatePicker = state.showDatePicker,
            onDateSelected = { viewModel.updateDeadline(it) },
            onDismissDatePicker = { viewModel.hideDatePicker() },
            showTimePicker = state.showTimePicker,
            onTimeSelected = { h, m -> viewModel.updateTime(h, m) },
            onDismissTimePicker = { viewModel.hideTimePicker() },
            recurrence = state.inputRecurrence,
            onRecurrenceChange = { viewModel.updateRecurrence(it) },
            recurrenceDays = state.inputRecurrenceDays,
            onRecurrenceDayToggle = { viewModel.toggleRecurrenceDay(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Group Plan Button ──
        OutlinedButton(
            onClick = { viewModel.showCreatePlanDialog() },
            enabled = !state.creatingPlan,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = CosmicTheme.colors.plasma,
                disabledContentColor = CosmicTheme.colors.textTertiary
            ),
            border = BorderStroke(1.dp, CosmicTheme.colors.plasma.copy(alpha = 0.4f))
        ) {
            if (state.creatingPlan) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp),
                    color = CosmicTheme.colors.plasma
                )
                Spacer(Modifier.width(8.dp))
            }
            Text("Tạo kế hoạch nhóm", style = CosmicTheme.typography.body)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Filter Tabs ──
        FilterTabs(
            selectedFilter = state.filter,
            onSelectFilter = { viewModel.setFilter(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Task List ──
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
                        onToggle = { viewModel.toggleTask(task.id, !task.completed) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputSection(
    title: String,
    onTitleChange: (String) -> Unit,
    deadline: Long?,
    onDeadlineClick: () -> Unit,
    onAdd: () -> Unit,
    showDatePicker: Boolean,
    onDateSelected: (Long?) -> Unit,
    onDismissDatePicker: () -> Unit,
    showTimePicker: Boolean,
    onTimeSelected: (Int, Int) -> Unit,
    onDismissTimePicker: () -> Unit,
    recurrence: String?,
    onRecurrenceChange: (String?) -> Unit,
    recurrenceDays: Int?,
    onRecurrenceDayToggle: (DayOfWeek) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = deadline ?: System.currentTimeMillis()
    )

    val timePickerState = rememberTimePickerState(
        initialHour = 0,
        initialMinute = 0,
        is24Hour = true
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = { Text("Tên nhiệm vụ", style = CosmicTheme.typography.body) },
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedCard(
                    onClick = onDeadlineClick,
                    modifier = Modifier.weight(1f),
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

                Button(
                    onClick = onAdd,
                    enabled = title.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicTheme.colors.plasma,
                        disabledContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.3f)
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Thêm", style = CosmicTheme.typography.body)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            RecurrenceSelector(
                recurrence = recurrence,
                recurrenceDays = recurrenceDays,
                onRecurrenceChange = onRecurrenceChange,
                onDayToggle = onRecurrenceDayToggle
            )
        }
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
}

@Composable
private fun RecurrenceSelector(
    recurrence: String?,
    recurrenceDays: Int?,
    onRecurrenceChange: (String?) -> Unit,
    onDayToggle: (DayOfWeek) -> Unit
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(
                null to "Không lặp",
                "DAILY" to "Mỗi ngày",
                "WEEKLY" to "Mỗi tuần",
                "WEEKLY_DAYS" to "Chọn thứ",
                "MONTHLY" to "Mỗi tháng"
            ).forEach { (value, label) ->
                val selected = when (value) {
                    null -> recurrence == null
                    "WEEKLY_DAYS" -> recurrence == "WEEKLY" && recurrenceDays != null
                    else -> recurrence == value && (value != "WEEKLY" || recurrenceDays == null)
                }
                FilterChip(
                    selected = selected,
                    onClick = {
                        when (value) {
                            null -> onRecurrenceChange(null)
                            "WEEKLY_DAYS" -> onRecurrenceChange("WEEKLY")
                            else -> onRecurrenceChange(value)
                        }
                    },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                        selectedLabelColor = CosmicTheme.colors.plasma
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = CosmicTheme.colors.glassBorder,
                        selectedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
                        enabled = true,
                        selected = selected
                    )
                )
            }
        }

        if (recurrence == "WEEKLY" && recurrenceDays != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                val days = listOf(
                    DayOfWeek.MONDAY to "T2",
                    DayOfWeek.TUESDAY to "T3",
                    DayOfWeek.WEDNESDAY to "T4",
                    DayOfWeek.THURSDAY to "T5",
                    DayOfWeek.FRIDAY to "T6",
                    DayOfWeek.SATURDAY to "T7",
                    DayOfWeek.SUNDAY to "CN"
                )
                days.forEach { (day, label) ->
                    val bit = 1 shl (day.value - 1)
                    val isDaySelected = recurrenceDays and bit != 0
                    FilterChip(
                        selected = isDaySelected,
                        onClick = { onDayToggle(day) },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                            selectedLabelColor = CosmicTheme.colors.plasma
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = CosmicTheme.colors.glassBorder,
                            selectedBorderColor = CosmicTheme.colors.plasma.copy(alpha = 0.5f),
                            enabled = true,
                            selected = isDaySelected
                        )
                    )
                }
            }
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
    task: TaskEntity,
    onToggle: () -> Unit
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
            modifier = Modifier
                .clickable(onClick = onToggle)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
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
            Column(modifier = Modifier.weight(1f)) {
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
    val formatter = if (ldt.hour == 0 && ldt.minute == 0) {
        DateTimeFormatter.ofPattern("dd/MM", Locale("vi", "VN"))
    } else {
        DateTimeFormatter.ofPattern("dd/MM HH:mm", Locale("vi", "VN"))
    }
    return ldt.format(formatter)
}

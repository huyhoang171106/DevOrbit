package vn.edu.uit.devorbit.mobile.ui.screen.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.data.remote.dto.GroupTaskResponse
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskSheet(
    creatorStudentCode: String,
    members: List<String>,
    loading: Boolean = false,
    editingTask: GroupTaskResponse? = null,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String?, assignedTo: String, deadline: String?) -> Unit,
    onUpdate: (taskId: Long, title: String, description: String?, assignedTo: String, deadline: String?) -> Unit = { _, _, _, _, _ -> }
) {
    val isEditing = editingTask != null

    val initialTitle = remember(editingTask) { editingTask?.title ?: "" }
    val initialDescription = remember(editingTask) { editingTask?.description ?: "" }
    val initialAssignedTo = remember(editingTask) { editingTask?.assignedTo ?: "" }
    val initialDeadlineMillis = remember(editingTask) {
        editingTask?.deadline?.let {
            try { LocalDate.parse(it).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() } catch (_: Exception) { null }
        }
    }

    var title by remember(editingTask) { mutableStateOf(initialTitle) }
    var description by remember(editingTask) { mutableStateOf(initialDescription) }
    var assignedTo by remember(editingTask) { mutableStateOf(initialAssignedTo) }
    var deadlineMillis by remember(editingTask) { mutableStateOf(initialDeadlineMillis) }
    var showMemberDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val apiFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val deadlineDisplay = deadlineMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().format(displayFormatter)
    } ?: ""

    val allMembers = remember(creatorStudentCode, members) {
        listOf(creatorStudentCode) + members.filter { it != creatorStudentCode }
    }

    val canSubmit = title.isNotBlank() && assignedTo.isNotBlank() && deadlineMillis != null

    if (showDatePicker) {
        val todayStartOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = deadlineMillis ?: System.currentTimeMillis(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= todayStartOfDay
                override fun isSelectableYear(year: Int): Boolean = true
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            shape = RoundedCornerShape(20.dp),
            confirmButton = {
                Button(
                    onClick = {
                        deadlineMillis = datePickerState.selectedDateMillis
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = CosmicTheme.colors.plasma)
                    Spacer(Modifier.width(4.dp))
                    Text("OK", color = CosmicTheme.colors.plasma)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CosmicTheme.colors.textSecondary.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = CosmicTheme.colors.textSecondary)
                    Spacer(Modifier.width(4.dp))
                    Text("Huỷ", color = CosmicTheme.colors.textSecondary)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = CosmicTheme.colors.nebula)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.Transparent,
                    selectedDayContainerColor = CosmicTheme.colors.plasma,
                    selectedDayContentColor = Color.Black
                )
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = CosmicTheme.colors.nebula,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
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

            // Title (required)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Tên nhiệm vụ *", color = CosmicTheme.colors.textTertiary) },
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

            // Description (optional)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
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

            // Assignee dropdown (required)
            ExposedDropdownMenuBox(
                expanded = showMemberDropdown,
                onExpandedChange = { showMemberDropdown = it }
            ) {
                OutlinedTextField(
                    value = assignedTo,
                    onValueChange = {},
                    readOnly = true,
                    enabled = !isEditing,
                    placeholder = { Text("Giao cho *", color = CosmicTheme.colors.textTertiary) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showMemberDropdown) },
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
                    expanded = showMemberDropdown,
                    onDismissRequest = { showMemberDropdown = false }
                ) {
                    allMembers.forEach { member ->
                        DropdownMenuItem(
                            text = { Text(member) },
                            onClick = {
                                assignedTo = member
                                showMemberDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Deadline — DatePicker
            OutlinedTextField(
                value = deadlineDisplay,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Thời hạn *", color = CosmicTheme.colors.textTertiary) },
                trailingIcon = {
                    TextButton(onClick = { showDatePicker = true }) {
                        Text("Chọn ngày", color = CosmicTheme.colors.plasma, fontSize = 13.sp)
                    }
                },
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

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val isoDeadline = deadlineMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().format(apiFormatter)
                    }
                    if (isEditing) {
                        editingTask?.let { task ->
                            onUpdate(task.id, title, description.ifBlank { null }, assignedTo, isoDeadline)
                        }
                    } else {
                        onConfirm(title, description.ifBlank { null }, assignedTo, isoDeadline)
                    }
                },
                enabled = canSubmit && !loading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CosmicTheme.colors.plasma,
                    disabledContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.3f)
                )
            ) {
                Text(if (isEditing) "Cập nhật" else "Lưu", color = Color.Black, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

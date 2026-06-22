package vn.edu.uit.devorbit.mobile.ui.screen.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskSheet(
    members: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String?, assignedTo: String?, deadline: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var assignedTo by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var showMemberDropdown by remember { mutableStateOf(false) }

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
                text = "Thêm nhiệm vụ",
                color = CosmicTheme.colors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
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

            // Assignee dropdown
            ExposedDropdownMenuBox(
                expanded = showMemberDropdown,
                onExpandedChange = { showMemberDropdown = it }
            ) {
                OutlinedTextField(
                    value = assignedTo,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Giao cho", color = CosmicTheme.colors.textTertiary) },
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
                    members.forEach { member ->
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

            OutlinedTextField(
                value = deadline,
                onValueChange = { deadline = it },
                placeholder = { Text("Thời hạn (YYYY-MM-DD, không bắt buộc)", color = CosmicTheme.colors.textTertiary) },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = CosmicTheme.colors.textSecondary)
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
                    onConfirm(
                        title,
                        description.ifBlank { null },
                        assignedTo.ifBlank { null },
                        deadline.ifBlank { null }
                    )
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CosmicTheme.colors.plasma,
                    disabledContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.3f)
                )
            ) {
                Text("Lưu", color = Color.Black, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

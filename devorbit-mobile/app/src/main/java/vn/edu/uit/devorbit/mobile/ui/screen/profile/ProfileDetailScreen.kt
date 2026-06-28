package vn.edu.uit.devorbit.mobile.ui.screen.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var nameInput by remember(state.studentName) { mutableStateOf(state.studentName) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadAvatar(it) }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CosmicTheme.colors.void
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = CosmicTheme.colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Thông tin cá nhân",
                    style = CosmicTheme.typography.titleMedium,
                    color = CosmicTheme.colors.textPrimary
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Avatar Upload Section
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    if (state.avatar != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(state.avatar)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .border(2.dp, CosmicTheme.colors.plasma, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    CosmicTheme.colors.plasma.copy(alpha = 0.1f),
                                    CircleShape
                                )
                                .border(2.dp, CosmicTheme.colors.plasma.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = CosmicTheme.colors.plasma
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(CosmicTheme.colors.plasma, CircleShape)
                            .border(2.dp, CosmicTheme.colors.void, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = "Tải ảnh lên",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                    }
                }

                if (state.isUploadingAvatar) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = CosmicTheme.colors.plasma
                    )
                }

                state.error?.let {
                    Text(
                        text = it,
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.supernova,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // General Read-Only Details
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Thông tin học tập",
                        style = CosmicTheme.typography.command,
                        color = CosmicTheme.colors.textTertiary
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CosmicTheme.colors.nebula,
                        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DetailRow(label = "Mã số sinh viên", value = state.studentCode)
                            HorizontalDivider(color = CosmicTheme.colors.glassBorder)
                            DetailRow(label = "Email sinh viên", value = state.email.ifEmpty { "Chưa cập nhật" })
                        }
                    }
                }

                // Edit Fields Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Chỉnh sửa thông tin",
                        style = CosmicTheme.typography.command,
                        color = CosmicTheme.colors.textTertiary
                    )

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Họ và tên") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CosmicTheme.colors.plasma,
                            unfocusedBorderColor = CosmicTheme.colors.glassBorder,
                            cursorColor = CosmicTheme.colors.plasma,
                            focusedTextColor = CosmicTheme.colors.textPrimary,
                            unfocusedTextColor = CosmicTheme.colors.textPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Đổi mật khẩu",
                        style = CosmicTheme.typography.command,
                        color = CosmicTheme.colors.textTertiary
                    )

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Mật khẩu hiện tại") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CosmicTheme.colors.plasma,
                            unfocusedBorderColor = CosmicTheme.colors.glassBorder,
                            cursorColor = CosmicTheme.colors.plasma,
                            focusedTextColor = CosmicTheme.colors.textPrimary,
                            unfocusedTextColor = CosmicTheme.colors.textPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Mật khẩu mới") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CosmicTheme.colors.plasma,
                            unfocusedBorderColor = CosmicTheme.colors.glassBorder,
                            cursorColor = CosmicTheme.colors.plasma,
                            focusedTextColor = CosmicTheme.colors.textPrimary,
                            unfocusedTextColor = CosmicTheme.colors.textPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Action Buttons
                Button(
                    onClick = {
                        if (nameInput.isNotBlank() && nameInput != state.studentName) {
                            viewModel.updateFullName(nameInput)
                        }
                        if (currentPassword.isNotBlank() && newPassword.isNotBlank()) {
                            viewModel.changePassword(currentPassword, newPassword)
                            currentPassword = ""
                            newPassword = ""
                        }
                    },
                    enabled = !state.isUpdatingName && !state.isChangingPassword && 
                            (nameInput.isNotBlank() || (currentPassword.isNotBlank() && newPassword.isNotBlank())),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.colors.plasma)
                ) {
                    if (state.isUpdatingName || state.isChangingPassword) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Lưu thay đổi",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (state.nameUpdateSuccess || state.passwordChangeSuccess) {
                    Text(
                        text = if (state.passwordChangeSuccess) "Đã cập nhật mật khẩu thành công!" else "Đã cập nhật tên thành công!",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.aurora,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = CosmicTheme.typography.label,
            color = CosmicTheme.colors.textSecondary
        )
        Text(
            text = value,
            style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Medium),
            color = CosmicTheme.colors.textPrimary
        )
    }
}

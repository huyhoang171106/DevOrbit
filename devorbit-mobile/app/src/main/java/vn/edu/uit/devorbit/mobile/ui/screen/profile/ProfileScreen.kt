package vn.edu.uit.devorbit.mobile.ui.screen.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import vn.edu.uit.devorbit.mobile.domain.repository.Bookmark
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showAvatarDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            showAvatarDialog = true
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 96.dp)
    ) {
        // Header
        item {
            Text(
                "Cá nhân",
                style = CosmicTheme.typography.display,
                color = CosmicTheme.colors.textPrimary
            )
        }

        // Student info with avatar
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CosmicTheme.colors.nebula,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar with upload overlay
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") }
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
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(CosmicTheme.colors.plasma.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                    tint = CosmicTheme.colors.plasma
                                )
                            }
                        }
                        
                        // Camera overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CosmicTheme.colors.nebula.copy(alpha = 0.7f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.CameraAlt,
                                contentDescription = "Đổi ảnh",
                                modifier = Modifier.size(24.dp),
                                tint = CosmicTheme.colors.plasma
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        if (state.studentName.isNotEmpty()) {
                            Text(
                                state.studentName,
                                style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                                color = CosmicTheme.colors.textPrimary
                            )
                            Text(
                                state.studentCode,
                                style = CosmicTheme.typography.label,
                                color = CosmicTheme.colors.textTertiary
                            )
                        } else {
                            Text(
                                "Chưa đăng nhập",
                                style = CosmicTheme.typography.body,
                                color = CosmicTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }
            
            if (state.isUploadingAvatar) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    color = CosmicTheme.colors.plasma
                )
            }
            
            if (state.avatarUploadSuccess) {
                Text(
                    "Đã cập nhật ảnh đại diện",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.plasma,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Bookmarks section
        item {
            Text(
                "Môn đã lưu",
                style = CosmicTheme.typography.command,
                color = CosmicTheme.colors.textTertiary
            )
        }
        if (state.bookmarks.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CosmicTheme.colors.nebula,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Chưa có bookmark nào",
                            style = CosmicTheme.typography.body,
                            color = CosmicTheme.colors.textSecondary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Lưu môn học từ tab Môn học để xem lại sau",
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.textTertiary
                        )
                    }
                }
            }
        }
        items(state.bookmarks) { bookmark ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CosmicTheme.colors.nebula,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            bookmark.title,
                            style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Medium),
                            color = CosmicTheme.colors.textPrimary
                        )
                        Text(
                            bookmark.targetType,
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.textTertiary
                        )
                    }
                    IconButton(onClick = { viewModel.removeBookmark(bookmark.id) }) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Bỏ lưu",
                            tint = CosmicTheme.colors.plasma,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Settings section
        item {
            Text(
                "Cài đặt",
                style = CosmicTheme.typography.command,
                color = CosmicTheme.colors.textTertiary
            )
        }
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CosmicTheme.colors.nebula,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Chế độ tối",
                        style = CosmicTheme.typography.body,
                        color = CosmicTheme.colors.textPrimary
                    )
                    Switch(
                        checked = state.darkMode,
                        onCheckedChange = { viewModel.toggleDarkMode() },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = CosmicTheme.colors.plasma.copy(alpha = 0.4f),
                            checkedThumbColor = CosmicTheme.colors.plasma
                        )
                    )
                }
            }
        }

        // Logout
        if (state.isLoggedIn) {
            item {
                Spacer(Modifier.height(4.dp))
                OutlinedButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.supernova.copy(alpha = 0.3f)),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = CosmicTheme.colors.supernova
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Đăng xuất",
                        color = CosmicTheme.colors.supernova,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Avatar confirmation dialog
    if (showAvatarDialog && selectedImageUri != null) {
        AlertDialog(
            onDismissRequest = { 
                showAvatarDialog = false
                selectedImageUri = null
            },
            title = {
                Text(
                    "Xác nhận ảnh đại diện",
                    style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Preview",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, CosmicTheme.colors.plasma, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedImageUri?.let { viewModel.uploadAvatar(it) }
                        showAvatarDialog = false
                        selectedImageUri = null
                    }
                ) {
                    Text("Lưu", color = CosmicTheme.colors.plasma)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAvatarDialog = false
                        selectedImageUri = null
                    }
                ) {
                    Text("Hủy", color = CosmicTheme.colors.textTertiary)
                }
            },
            containerColor = CosmicTheme.colors.nebula
        )
    }
}

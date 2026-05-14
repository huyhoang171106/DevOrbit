package vn.edu.uit.devorbit.mobile.ui.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.domain.repository.Bookmark
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        item {
            Text("Cá nhân", style = CosmicTheme.typography.display, color = CosmicTheme.colors.plasma)
        }

        // Student Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CosmicTheme.colors.nebula)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = CosmicTheme.colors.plasma
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (state.studentName.isNotEmpty()) {
                        Text(state.studentName, style = CosmicTheme.typography.body, color = CosmicTheme.colors.textPrimary)
                        Text(state.studentCode, style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary)
                    } else {
                        Text("Chưa đăng nhập", style = CosmicTheme.typography.body, color = CosmicTheme.colors.textSecondary)
                    }
                }
            }
        }

        // Bookmarks
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Môn đã lưu", style = CosmicTheme.typography.command, color = CosmicTheme.colors.textPrimary)
        }
        if (state.bookmarks.isEmpty()) {
            item {
                Text("Chưa có bookmark nào", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary)
            }
        }
        items(state.bookmarks) { bookmark ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CosmicTheme.colors.nebula)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(bookmark.title, style = CosmicTheme.typography.body, color = CosmicTheme.colors.textPrimary)
                        Text(bookmark.targetType, style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary)
                    }
                    IconButton(onClick = { viewModel.removeBookmark(bookmark.id) }) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Remove", tint = CosmicTheme.colors.plasma)
                    }
                }
            }
        }

        // Settings
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cài đặt", style = CosmicTheme.typography.command, color = CosmicTheme.colors.textPrimary)
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Chế độ tối", style = CosmicTheme.typography.body, color = CosmicTheme.colors.textPrimary)
                Switch(
                    checked = state.darkMode,
                    onCheckedChange = { viewModel.toggleDarkMode() },
                    colors = SwitchDefaults.colors(checkedTrackColor = CosmicTheme.colors.plasma)
                )
            }
        }
        if (state.isLoggedIn) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.logout() },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.colors.nebula),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đăng xuất", color = CosmicTheme.colors.supernova)
                }
            }
        }
    }
}

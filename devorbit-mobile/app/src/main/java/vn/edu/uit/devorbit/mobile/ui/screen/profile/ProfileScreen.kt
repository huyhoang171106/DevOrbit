package vn.edu.uit.devorbit.mobile.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import vn.edu.uit.devorbit.mobile.domain.repository.Bookmark
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToDetail: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 96.dp)
    ) {
        item {
            Text(
                "Cá nhân",
                style = CosmicTheme.typography.display,
                color = CosmicTheme.colors.textPrimary
            )
        }

        // Clickable profile card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToDetail() },
                shape = RoundedCornerShape(14.dp),
                color = CosmicTheme.colors.nebula,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.avatar != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(state.avatar)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(CosmicTheme.colors.plasma.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = CosmicTheme.colors.plasma
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
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
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = CosmicTheme.colors.textTertiary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Bookmarks — Courses
        val courseBookmarks = state.bookmarks.filter { it.targetType == "COURSE" }
        val repoBookmarks = state.bookmarks.filter { it.targetType == "REPO" }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Star, contentDescription = null, tint = CosmicTheme.colors.plasma, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Môn đã lưu (${courseBookmarks.size})", style = CosmicTheme.typography.command, color = CosmicTheme.colors.textTertiary)
            }
        }
        if (courseBookmarks.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CosmicTheme.colors.nebula,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Chưa có môn nào", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                    }
                }
            }
        }
        items(courseBookmarks, key = { it.id }) { bookmark ->
            BookmarkRow(bookmark = bookmark, onRemove = { viewModel.removeBookmark(bookmark.id) })
        }

        item { Spacer(Modifier.height(8.dp)) }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Code, contentDescription = null, tint = CosmicTheme.colors.plasma, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Repo đã lưu (${repoBookmarks.size})", style = CosmicTheme.typography.command, color = CosmicTheme.colors.textTertiary)
            }
        }
        if (repoBookmarks.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CosmicTheme.colors.nebula,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Chưa có repo nào", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                    }
                }
            }
        }
        items(repoBookmarks, key = { it.id }) { bookmark ->
            BookmarkRow(bookmark = bookmark, onRemove = { viewModel.removeBookmark(bookmark.id) })
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
}

@Composable
private fun BookmarkRow(bookmark: Bookmark, onRemove: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(bookmark.title, style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Medium), color = CosmicTheme.colors.textPrimary)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Favorite, contentDescription = "Bỏ lưu", tint = CosmicTheme.colors.plasma, modifier = Modifier.size(20.dp))
            }
        }
    }
}

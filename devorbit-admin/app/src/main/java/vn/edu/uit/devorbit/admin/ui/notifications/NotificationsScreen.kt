package vn.edu.uit.devorbit.admin.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.admin.data.remote.dto.NotificationResponse
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        // ── Header with unread count + mark-all-read ─────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Thông báo",
                    style = ObsidianType.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (state.unreadCount > 0) {
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.NotificationsActive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${state.unreadCount} chưa đọc",
                            style = ObsidianType.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            if (state.unreadCount > 0) {
                FilledTonalButton(
                    onClick = { viewModel.markAllRead() },
                    shape = ObsidianShape.sm,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Đọc tất cả", style = ObsidianType.labelLarge)
                }
            }
        }

        ObsidianDivider()

        // ── Content ───────────────────────────────────────────────────────
        when {
            state.isLoading -> ObsidianLoadingBox()
            state.error != null -> ObsidianEmptyState(
                message = "Lỗi tải thông báo",
                subtitle = state.error,
                icon = Icons.Rounded.Notifications
            )
            state.notifications.isEmpty() -> ObsidianEmptyState(
                message = "Không có thông báo nào",
                subtitle = "Thông báo sẽ xuất hiện khi có hoạt động",
                icon = Icons.Rounded.Notifications
            )
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(state.notifications, key = { it.id }) { notif ->
                    NotificationRow(
                        notification = notif,
                        onMarkRead = { viewModel.markRead(notif.id) }
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// NOTIFICATION ROW
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun NotificationRow(
    notification: NotificationResponse,
    onMarkRead: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !notification.isRead) { onMarkRead() },
        shape = ObsidianShape.md,
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // ── Unread indicator dot ───────────────────────────────────
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(8.dp)
                    .clip(ObsidianShape.full)
                    .background(
                        if (!notification.isRead)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
            Spacer(Modifier.width(12.dp))

            // ── Content ───────────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                notification.message?.let { msg ->
                    Text(
                        msg,
                        style = if (!notification.isRead)
                            ObsidianType.titleMedium
                        else ObsidianType.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    notification.type?.let { type ->
                        ObsidianBadge(
                            text = type,
                            color = when (type.uppercase()) {
                                "SYSTEM" -> MaterialTheme.colorScheme.primary
                                "ASSIGNMENT" -> ObsidianPalette.Amber500
                                "REVIEW" -> ObsidianPalette.Green500
                                "DEADLINE" -> ObsidianPalette.Red500
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    notification.createdAt?.let {
                        Text(
                            it,
                            style = ObsidianType.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Mark read button ───────────────────────────────────────
            if (!notification.isRead) {
                TextButton(
                    onClick = onMarkRead,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        "Đọc",
                        style = ObsidianType.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

package vn.edu.uit.devorbit.admin.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.core.designsystem.*
import vn.edu.uit.devorbit.admin.data.remote.dto.NotificationResponse
import vn.edu.uit.devorbit.admin.ui.theme.*

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Thông báo",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                if (state.unreadCount > 0) {
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.NotificationsActive,
                            contentDescription = null,
                            tint = UITBlue,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${state.unreadCount} chưa đọc",
                            style = MaterialTheme.typography.labelMedium,
                            color = UITBlue,
                        )
                    }
                }
            }
            if (state.unreadCount > 0) {
                AdminTextButton(
                    text = "Đọc tất cả",
                    onClick = { viewModel.markAllRead() },
                    icon = Icons.Rounded.DoneAll,
                )
            }
        }

        HorizontalDivider(color = Divider, thickness = 0.5.dp)

        when {
            state.isLoading -> InitialLoading()
            state.error != null -> ErrorState(
                title = "Không thể tải thông báo",
                subtitle = state.error,
                onRetry = { viewModel.retry() },
            )
            state.notifications.isEmpty() -> EmptyState(
                title = "Không có thông báo",
                subtitle = "Bạn sẽ nhận được thông báo khi có hoạt động cần xử lý.",
                icon = Icons.Rounded.NotificationsNone,
            )
            else -> LazyColumn(
                contentPadding = PaddingValues(vertical = 4.dp),
            ) {
                items(
                    items = state.notifications,
                    key = { it.id },
                ) { notification ->
                    NotificationRow(
                        notification = notification,
                        onMarkRead = { viewModel.markRead(notification.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(
    notification: NotificationResponse,
    onMarkRead: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !notification.isRead) { onMarkRead() }
            .background(if (notification.isRead) Surface else UITBlueSoft.copy(alpha = 0.3f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (notification.isRead) Divider else UITBlue),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (notification.type != null) {
                    StatusBadge(
                        label = notification.type,
                        type = StatusType.INFO,
                    )
                    Spacer(Modifier.width(8.dp))
                }
                if (notification.createdAt != null) {
                    Text(
                        text = notification.createdAt,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = notification.message ?: "(không có nội dung)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Medium,
                color = TextPrimary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
    HorizontalDivider(color = Divider, thickness = 0.5.dp)
}

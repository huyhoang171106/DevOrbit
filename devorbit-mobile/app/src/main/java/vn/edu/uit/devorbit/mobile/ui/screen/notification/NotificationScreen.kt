package vn.edu.uit.devorbit.mobile.ui.screen.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.data.remote.dto.StudentNotificationResponse
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.NotificationViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Thông báo",
                color = CosmicTheme.colors.textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            if (notifications.any { !it.isRead }) {
                TextButton(onClick = { viewModel.markAllAsRead() }) {
                    Icon(
                        Icons.Rounded.DoneAll,
                        contentDescription = "Đọc tất cả",
                        tint = CosmicTheme.colors.plasma,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Đọc tất cả",
                        color = CosmicTheme.colors.plasma,
                        fontSize = 13.sp
                    )
                }
            }
        }

        if (loading && notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = CosmicTheme.colors.plasma,
                    strokeWidth = 2.dp
                )
            }
        } else if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Chưa có thông báo nào",
                        color = CosmicTheme.colors.textSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = { viewModel.markAsRead(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: StudentNotificationResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead)
                CosmicTheme.colors.nebula.copy(alpha = 0.5f)
            else
                CosmicTheme.colors.nebula
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            if (!notification.isRead) {
                Icon(
                    Icons.Rounded.Circle,
                    contentDescription = null,
                    tint = CosmicTheme.colors.plasma,
                    modifier = Modifier
                        .size(8.dp)
                        .offset(y = 6.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    color = CosmicTheme.colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (notification.body.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notification.body,
                        color = CosmicTheme.colors.textSecondary,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formatTime(notification.createdAt),
                    color = CosmicTheme.colors.textTertiary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

private fun formatTime(isoString: String): String {
    return try {
        val instant = Instant.parse(isoString)
        val local = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val now = java.time.LocalDateTime.now()
        val diff = java.time.Duration.between(local, now)
        when {
            diff.toMinutes() < 1 -> "Vừa xong"
            diff.toMinutes() < 60 -> "${diff.toMinutes()} phút trước"
            diff.toHours() < 24 -> "${diff.toHours()} giờ trước"
            diff.toDays() < 7 -> "${diff.toDays()} ngày trước"
            else -> local.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("vi")))
        }
    } catch (_: Exception) {
        isoString
    }
}

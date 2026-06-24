package vn.edu.uit.devorbit.admin.core.designsystem

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.ui.theme.*

@Composable
fun EmptyState(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.Inbox,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = TextMuted,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
        )
        if (subtitle != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )
        }
        if (action != null) {
            Spacer(Modifier.height(24.dp))
            action()
        }
    }
}

@Composable
fun NoResultsState(
    query: String,
    modifier: Modifier = Modifier,
) {
    EmptyState(
        title = "Không tìm thấy kết quả",
        subtitle = "Không có dữ liệu phù hợp với \"$query\"",
        icon = Icons.Rounded.SearchOff,
        modifier = modifier,
    )
}

@Composable
fun ErrorState(
    title: String = "Có lỗi xảy ra",
    subtitle: String? = "Không thể tải dữ liệu. Vui lòng thử lại sau.",
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Rounded.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = Danger,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
        )
        if (subtitle != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )
        }
        if (onRetry != null) {
            Spacer(Modifier.height(24.dp))
            AdminPrimaryButton(
                text = "Thử lại",
                onClick = onRetry,
                icon = Icons.Rounded.Refresh,
            )
        }
    }
}

@Composable
fun OfflineBanner(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = WarningSoft,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Rounded.WifiOff,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Warning,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Không có kết nối mạng. Dữ liệu có thể không được cập nhật.",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
            )
        }
    }
}

@Composable
fun InitialLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = UITBlue,
            strokeWidth = 3.dp,
        )
    }
}

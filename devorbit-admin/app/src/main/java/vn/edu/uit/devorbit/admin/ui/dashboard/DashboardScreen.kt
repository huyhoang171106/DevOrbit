@file:OptIn(ExperimentalMaterial3Api::class)
package vn.edu.uit.devorbit.admin.ui.dashboard

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.core.designsystem.*
import vn.edu.uit.devorbit.admin.ui.theme.*

// ══════════════════════════════════════════════════════════════════════════════
// DASHBOARD SCREEN
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    when (val status = uiState.status) {
        is DashboardStatus.InitialLoading -> {
            InitialLoading()
        }

        is DashboardStatus.Error -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    HeaderRow(
                        adminName = uiState.adminName,
                        apiStatus = uiState.apiStatus,
                        lastSyncTime = uiState.lastSyncTime,
                        unreadCount = uiState.unreadCount,
                    )
                }
                item {
                    ErrorState(
                        title = "Không thể tải bảng điều khiển",
                        subtitle = status.message,
                        onRetry = { viewModel.refresh() },
                    )
                }
            }
        }

        is DashboardStatus.Loading -> {
            // Show skeleton while loading with cached data if available
            val cached = status.cachedData
            if (cached != null) {
                DashboardContent(
                    data = cached,
                    uiState = uiState,
                    isRefreshing = true,
                    onRefresh = viewModel::refresh,
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { HeaderRow(uiState.adminName, uiState.apiStatus, uiState.lastSyncTime, uiState.unreadCount) }
                    item { MetricSkeleton() }
                    item { SectionHeader(title = "Ưu tiên xử lý") }
                    item { ListSkeleton(itemCount = 3) }
                    item { SectionHeader(title = "Hoạt động gần đây") }
                    item { ListSkeleton(itemCount = 4) }
                }
            }
        }

        is DashboardStatus.Success -> {
            val data = status.data
            if (data.totalStudents == 0L && data.totalCourses == 0L && data.totalRepos == 0L &&
                data.priorityItems.isEmpty() && data.recentActivities.isEmpty()
            ) {
                // Empty state
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { HeaderRow(uiState.adminName, uiState.apiStatus, uiState.lastSyncTime, uiState.unreadCount) }
                    item {
                        EmptyState(
                            title = "Chưa có dữ liệu",
                            subtitle = "Hệ thống chưa có dữ liệu thống kê. Hãy quét kho lưu trữ để bắt đầu.",
                            icon = Icons.Rounded.SpaceDashboard,
                            action = {
                                AdminPrimaryButton(
                                    text = "Quét kho",
                                    onClick = { /* navigate to scan */ },
                                    icon = Icons.Rounded.Search,
                                )
                            },
                        )
                    }
                }
            } else {
                DashboardContent(
                    data = data,
                    uiState = uiState,
                    isRefreshing = false,
                    onRefresh = viewModel::refresh,
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// DASHBOARD CONTENT (full layout)
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun DashboardContent(
    data: DashboardData,
    uiState: DashboardUiState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        // ── Header ─────────────────────────────────────────────────────
        item {
            HeaderRow(
                adminName = uiState.adminName,
                apiStatus = uiState.apiStatus,
                lastSyncTime = uiState.lastSyncTime,
                unreadCount = uiState.unreadCount,
                onRefresh = onRefresh,
                isRefreshing = isRefreshing,
            )
        }

        // ── Overview Metric Cards ──────────────────────────────────────
        item { SectionHeader(title = "Tổng quan") }
        item {
            OverviewMetrics(
                totalStudents = data.totalStudents,
                totalCourses = data.totalCourses,
                totalRepos = data.totalRepos,
                pendingCount = data.pendingCandidatesCount,
            )
        }

        // ── Priority Section ───────────────────────────────────────────
        if (data.priorityItems.isNotEmpty()) {
            item { SectionHeader(title = "Ưu tiên xử lý") }
            items(
                items = data.priorityItems,
                key = { "${it.type}_${it.label}_${it.time}" },
            ) { item ->
                PriorityItem(
                    type = item.type,
                    label = item.label,
                    time = item.time,
                    entity = item.entity,
                    severity = item.severity,
                    onClick = { /* navigate to detail */ },
                )
            }
            item { Spacer(Modifier.height(4.dp)) }
        }

        // ── Recent Activity ────────────────────────────────────────────
        if (data.recentActivities.isNotEmpty()) {
            item { SectionHeader(title = "Hoạt động gần đây") }
            items(
                items = data.recentActivities,
                key = { "${it.title}_${it.subtitle}" },
            ) { activity ->
                ActivityTimelineItem(
                    title = activity.title,
                    subtitle = activity.subtitle,
                    time = activity.time,
                )
            }
            item { Spacer(Modifier.height(4.dp)) }
        }

        // ── Quick Actions ──────────────────────────────────────────────
        item { SectionHeader(title = "Thao tác nhanh") }
        item { QuickActionRow() }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// HEADER ROW
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HeaderRow(
    adminName: String,
    apiStatus: ApiStatus,
    lastSyncTime: String?,
    unreadCount: Int,
    onRefresh: (() -> Unit)? = null,
    isRefreshing: Boolean = false,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Surface,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(UITBlueSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = null,
                    tint = UITBlue,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.width(12.dp))

            // Admin name + status
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = adminName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ApiStatusDot(apiStatus)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = apiStatusLabel(apiStatus),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                    )
                    if (lastSyncTime != null) {
                        Text(
                            text = " · $lastSyncTime",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                        )
                    }
                }
            }

            // Notification bell
            if (unreadCount > 0) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = Danger,
                            contentColor = Color.White,
                        ) {
                            Text(
                                text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    },
                ) {
                    Icon(
                        Icons.Rounded.Notifications,
                        contentDescription = "Thông báo",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.width(8.dp))
            } else {
                Icon(
                    Icons.Rounded.NotificationsNone,
                    contentDescription = "Thông báo",
                    tint = TextMuted,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(Modifier.width(8.dp))
            }

            // Refresh button
            if (onRefresh != null) {
                IconButton(onClick = onRefresh, enabled = !isRefreshing) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = UITBlue,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = "Làm mới",
                            tint = UITBlue,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ApiStatusDot(status: ApiStatus) {
    val color = when (status) {
        ApiStatus.Connected -> Success
        ApiStatus.Disconnected -> Danger
        ApiStatus.Syncing -> Warning
    }
    Box(
        modifier = Modifier
            .size(7.dp)
            .clip(RoundedCornerShape(3.5.dp))
            .background(color),
    )
}

private fun apiStatusLabel(status: ApiStatus): String = when (status) {
    ApiStatus.Connected -> "Đã kết nối"
    ApiStatus.Disconnected -> "Mất kết nối"
    ApiStatus.Syncing -> "Đang đồng bộ"
}

// ══════════════════════════════════════════════════════════════════════════════
// OVERVIEW METRICS
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun OverviewMetrics(
    totalStudents: Long,
    totalCourses: Long,
    totalRepos: Long,
    pendingCount: Long,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MetricDisplay(
            value = formatNumber(totalStudents),
            label = "Tổng SV",
            valueColor = UITBlue,
            modifier = Modifier.weight(1f),
        )
        MetricDisplay(
            value = formatNumber(totalCourses),
            label = "Môn học",
            valueColor = Success,
            modifier = Modifier.weight(1f),
        )
        MetricDisplay(
            value = formatNumber(totalRepos),
            label = "Kho đã duyệt",
            valueColor = Info,
            modifier = Modifier.weight(1f),
        )
        MetricDisplay(
            value = formatNumber(pendingCount),
            label = "Chờ duyệt",
            valueColor = if (pendingCount > 0) Warning else TextMuted,
            modifier = Modifier.weight(1f),
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// QUICK ACTIONS
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuickActionRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        QuickAction(
            label = "Quét repo",
            onClick = { /* navigate to scan */ },
            modifier = Modifier.weight(1f),
        )
        QuickAction(
            label = "Thêm môn",
            onClick = { /* navigate to add course */ },
            modifier = Modifier.weight(1f),
        )
        QuickAction(
            label = "Xem ứng viên",
            onClick = { /* navigate to candidates */ },
            modifier = Modifier.weight(1f),
        )
        QuickAction(
            label = "Scan logs",
            onClick = { /* navigate to scan logs */ },
            modifier = Modifier.weight(1f),
        )
    }
    Spacer(Modifier.height(8.dp))
}

// ══════════════════════════════════════════════════════════════════════════════
// SECTION HEADER (reusable without old Obsidian dependency)
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

// ══════════════════════════════════════════════════════════════════════════════
// UTILITY
// ══════════════════════════════════════════════════════════════════════════════

private fun formatNumber(n: Long): String = when {
    n >= 1_000_000 -> "${n / 1_000_000}M"
    n >= 1_000 -> "${n / 1_000}K"
    else -> n.toString()
}

package vn.edu.uit.devorbit.admin.ui.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import vn.edu.uit.devorbit.admin.ui.theme.channelTypeLabel
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.*

@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tabs = listOf("Kênh", "Tin nhắn", "Hỗ trợ")
    var sessionDetail by remember { mutableStateOf<ChatSessionAdminResponse?>(null) }

    Column(Modifier.fillMaxSize()) {
        ObsidianPageHeader(
            title = "Cộng đồng",
            subtitle = "${state.channels.size} kênh · ${state.chatSessions.size} phiên hỗ trợ"
        )

        // ── Tab bar ───────────────────────────────────────────────────────
        TabRow(
            selectedTabIndex = state.selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                if (state.selectedTab < tabPositions.size) {
                    Box(
                        Modifier
                            .tabIndicatorOffset(tabPositions[state.selectedTab])
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    )
                }
            },
            divider = { ObsidianDivider() }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = state.selectedTab == index,
                    onClick = { viewModel.selectTab(index) },
                    text = {
                        Text(
                            title,
                            style = if (state.selectedTab == index)
                                ObsidianType.labelLarge
                            else ObsidianType.bodyMedium,
                            color = if (state.selectedTab == index)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        // ── Content ───────────────────────────────────────────────────────
        when {
            state.isLoading -> ObsidianLoadingBox()
            state.error != null -> ObsidianEmptyState(
                message = "Lỗi tải dữ liệu",
                subtitle = state.error,
                icon = Icons.Rounded.Chat
            )
            else -> when (state.selectedTab) {
                0 -> ChannelsTab(state.channels)
                1 -> MessagesTab(
                    messages = state.messages,
                    onDelete = viewModel::deleteMessage
                )
                2 -> ChatSessionsTab(
                    sessions = state.chatSessions,
                    onSelect = { session ->
                        sessionDetail = session
                        viewModel.loadChatMessages(session.id)
                    }
                )
            }
        }
    }

    // ── Chat session detail dialog ──────────────────────────────────────
    sessionDetail?.let { session ->
        ChatSessionDetailDialog(
            session = session,
            messages = state.chatMessages,
            onDismiss = { sessionDetail = null }
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// CHANNELS TAB
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ChannelsTab(channels: List<ChatChannel>) {
    if (channels.isEmpty()) {
        ObsidianEmptyState(
            message = "Không có kênh nào",
            subtitle = "Kênh sẽ xuất hiện khi được tạo từ hệ thống",
            icon = Icons.Rounded.Forum
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(channels, key = { it.id }) { channel ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ObsidianShape.md,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ObsidianAvatar(
                            name = channel.name,
                            size = 36,
                            icon = when (channel.type) {
                                "GENERAL" -> Icons.Rounded.Forum
                                "COURSE" -> Icons.Rounded.Chat
                                else -> Icons.Rounded.SupportAgent
                            }
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                channel.name,
                                style = ObsidianType.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(2.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ObsidianBadge(
                                    text = channelTypeLabel(channel.type),
                                    color = when (channel.type) {
                                        "GENERAL" -> MaterialTheme.colorScheme.primary
                                        "COURSE" -> ObsidianPalette.Green500
                                        "TECH_STACK" -> ObsidianPalette.Amber500
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                                if (channel.active) {
                                    ObsidianBadge(
                                        text = "Hoạt động",
                                        color = ObsidianPalette.Green500
                                    )
                                } else {
                                    ObsidianBadge(
                                        text = "Vô hiệu",
                                        color = ObsidianPalette.Red500
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// MESSAGES TAB
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun MessagesTab(
    messages: List<CommunityMessageAdminResponse>,
    onDelete: (Long) -> Unit
) {
    var deleteTarget by remember { mutableStateOf<Long?>(null) }

    if (messages.isEmpty()) {
        ObsidianEmptyState(
            message = "Không có tin nhắn nào",
            subtitle = "Tin nhắn sẽ xuất hiện khi sinh viên gửi",
            icon = Icons.Rounded.Chat
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ObsidianShape.md,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    msg.studentName,
                                    style = ObsidianType.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(2.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    msg.channelName?.let { name ->
                                        ObsidianBadge(
                                            text = name,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    msg.createdAt?.let { date ->
                                        Text(
                                            date,
                                            style = ObsidianType.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            IconButton(
                                onClick = { deleteTarget = msg.id },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.Delete,
                                    contentDescription = "Xoá",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        ObsidianDivider()
                        Spacer(Modifier.height(8.dp))
                        Text(
                            msg.content,
                            style = ObsidianType.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 3
                        )
                    }
                }
            }
        }
    }

    deleteTarget?.let { id ->
        ObsidianConfirmDialog(
            title = "Xoá tin nhắn",
            message = "Bạn có chắc muốn xoá tin nhắn này? Hành động không thể hoàn tác.",
            onConfirm = { onDelete(id); deleteTarget = null },
            onDismiss = { deleteTarget = null },
            isDestructive = true
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// CHAT SESSIONS (HỖ TRỢ) TAB
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ChatSessionsTab(
    sessions: List<ChatSessionAdminResponse>,
    onSelect: (ChatSessionAdminResponse) -> Unit
) {
    if (sessions.isEmpty()) {
        ObsidianEmptyState(
            message = "Không có phiên hỗ trợ nào",
            subtitle = "Phiên chat sẽ xuất hiện khi sinh viên bắt đầu",
            icon = Icons.Rounded.SupportAgent
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sessions, key = { it.id }) { session ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(session) },
                    shape = ObsidianShape.md,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ObsidianAvatar(
                            name = session.studentName,
                            size = 40
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                session.studentName,
                                style = ObsidianType.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            session.title?.let {
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    it,
                                    style = ObsidianType.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    "${session.messageCount} tin nhắn",
                                    style = ObsidianType.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                session.createdAt?.let {
                                    Text(
                                        it,
                                        style = ObsidianType.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        Icon(
                            Icons.Rounded.Chat,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// CHAT SESSION DETAIL DIALOG
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ChatSessionDetailDialog(
    session: ChatSessionAdminResponse,
    messages: List<ChatMessageAdminResponse>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ObsidianShape.lg,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column {
                Text(
                    session.title ?: "Phiên hỗ trợ",
                    style = ObsidianType.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${session.studentName} · ${session.messageCount} tin nhắn",
                    style = ObsidianType.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Đang tải tin nhắn...",
                        style = ObsidianType.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    messages.forEach { msg ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = ObsidianShape.sm,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                    alpha = 0.5f
                                )
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        msg.sender,
                                        style = ObsidianType.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    msg.createdAt?.let {
                                        Text(
                                            it,
                                            style = ObsidianType.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    msg.content,
                                    style = ObsidianType.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

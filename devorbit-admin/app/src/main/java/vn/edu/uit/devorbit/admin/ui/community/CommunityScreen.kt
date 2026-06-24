package vn.edu.uit.devorbit.admin.ui.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.*

@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tabs = listOf("Tin nhắn", "Phiên hỗ trợ")
    var sessionDetail by remember { mutableStateOf<ChatSessionAdminResponse?>(null) }
    var deleteTarget by remember { mutableStateOf<Long?>(null) }

    Column(Modifier.fillMaxSize()) {
        // ── Page Header ───────────────────────────────────────────────────
        ObsidianPageHeader(
            title = "Cộng đồng",
            subtitle = when {
                state.isLoading -> null
                else -> "${state.messages.size} tin nhắn · ${state.chatSessions.size} phiên"
            }
        )

        ObsidianDivider()

        // ── Segmented filter ──────────────────────────────────────────────
        SegmentedFilter(
            options = tabs,
            selectedIndex = state.selectedTab,
            onSelect = { viewModel.selectTab(it) }
        )

        // ── Content ───────────────────────────────────────────────────────
        when {
            state.isLoading -> ObsidianLoadingBox(Modifier.weight(1f))
            state.error != null -> ObsidianEmptyState(
                message = "Lỗi tải dữ liệu",
                subtitle = state.error,
                icon = Icons.Rounded.ErrorOutline,
                modifier = Modifier.weight(1f)
            )
            else -> when (state.selectedTab) {
                0 -> MessagesTab(
                    messages = state.messages,
                    onDelete = { deleteTarget = it }
                )
                1 -> ChatSessionsTab(
                    sessions = state.chatSessions,
                    onSelect = { session ->
                        sessionDetail = session
                        viewModel.loadChatMessages(session.id)
                    }
                )
            }
        }
    }

    // ── Delete confirmation ─────────────────────────────────────────────
    deleteTarget?.let { id ->
        ObsidianConfirmDialog(
            title = "Xoá tin nhắn",
            message = "Bạn có chắc muốn xoá tin nhắn này? Hành động không thể hoàn tác.",
            confirmLabel = "Xoá",
            isDestructive = true,
            onConfirm = { viewModel.deleteMessage(id); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }

    // ── Chat session detail dialog ──────────────────────────────────────
    sessionDetail?.let { session ->
        ChatSessionDetailDialog(
            sessionId = session.id,
            sessionTitle = session.title ?: "Phiên hỗ trợ",
            studentName = session.studentName,
            onDismiss = { sessionDetail = null },
            viewModel = viewModel
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// SEGMENTED FILTER
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SegmentedFilter(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, label ->
            FilterChip(
                selected = selectedIndex == index,
                onClick = { onSelect(index) },
                label = {
                    Text(
                        label,
                        style = ObsidianType.labelMedium,
                        fontWeight = if (selectedIndex == index) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// MESSAGES TAB
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ColumnScope.MessagesTab(
    messages: List<CommunityMessageAdminResponse>,
    onDelete: (Long) -> Unit
) {
    if (messages.isEmpty()) {
        ObsidianEmptyState(
            message = "Không có tin nhắn nào",
            subtitle = "Tin nhắn sẽ xuất hiện khi sinh viên gửi",
            icon = Icons.Rounded.Chat,
            modifier = Modifier.weight(1f)
        )
    } else {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageCard(msg = msg, onDelete = { onDelete(msg.id) })
            }
        }
    }
}

@Composable
private fun MessageCard(
    msg: CommunityMessageAdminResponse,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ObsidianShape.md,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ObsidianAvatar(name = msg.studentName, size = 36)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            msg.studentName,
                            style = ObsidianType.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.width(8.dp))
                        msg.channelName?.let { channel ->
                            ObsidianBadge(
                                text = channel,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    msg.createdAt?.let { date ->
                        Text(
                            date,
                            style = ObsidianType.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = "Xoá",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 0.5.dp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                msg.content,
                style = ObsidianType.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// CHAT SESSIONS TAB
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ColumnScope.ChatSessionsTab(
    sessions: List<ChatSessionAdminResponse>,
    onSelect: (ChatSessionAdminResponse) -> Unit
) {
    if (sessions.isEmpty()) {
        ObsidianEmptyState(
            message = "Không có phiên hỗ trợ nào",
            subtitle = "Phiên chat sẽ xuất hiện khi sinh viên bắt đầu",
            icon = Icons.Rounded.SupportAgent,
            modifier = Modifier.weight(1f)
        )
    } else {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sessions, key = { it.id }) { session ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(session) },
                    shape = ObsidianShape.md,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ObsidianAvatar(name = session.studentName, size = 40)
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
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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

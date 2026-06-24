package vn.edu.uit.devorbit.admin.ui.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.data.remote.dto.ChatMessageAdminResponse
import vn.edu.uit.devorbit.admin.ui.components.ObsidianAvatar
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianShape
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSessionDetailDialog(
    sessionId: String,
    sessionTitle: String,
    studentName: String,
    onDismiss: () -> Unit,
    viewModel: CommunityViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(sessionId) {
        viewModel.loadChatMessages(sessionId)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = ObsidianShape.lg,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(sessionTitle, style = ObsidianType.headlineSmall)
                    Text(
                        studentName,
                        style = ObsidianType.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Đóng")
                }
            }
        },
        text = {
            if (state.chatMessages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.chatMessages, key = { it.id }) { msg ->
                        ChatMessageBubble(msg)
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun ChatMessageBubble(msg: ChatMessageAdminResponse) {
    val isStudent = msg.sender == "Student"
    val parts = remember(msg.content) { parseMessageContent(msg.content) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isStudent) Arrangement.End else Arrangement.Start
    ) {
        if (!isStudent) {
            ObsidianAvatar(name = msg.sender, size = 28)
            Spacer(Modifier.width(8.dp))
        }
        Column(modifier = Modifier.widthIn(max = 280.dp)) {
            Text(
                msg.sender,
                style = ObsidianType.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(2.dp))
            Surface(
                shape = ObsidianShape.sm,
                color = if (isStudent)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    parts.forEach { part ->
                        when (part) {
                            is MessagePart.Text -> Text(
                                text = part.text,
                                style = ObsidianType.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            is MessagePart.CodeBlock -> Surface(
                                shape = ObsidianShape.xs,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                            ) {
                                Text(
                                    text = part.code,
                                    style = ObsidianType.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

// ── Message content parsing ────────────────────────────────────────────────────

private sealed class MessagePart {
    data class Text(val text: String) : MessagePart()
    data class CodeBlock(val code: String) : MessagePart()
}

private fun parseMessageContent(content: String): List<MessagePart> {
    val parts = mutableListOf<MessagePart>()
    val regex = Regex("```(?:\\w*)\\n?([\\s\\S]*?)```")
    var lastIndex = 0
    regex.findAll(content).forEach { match ->
        if (match.range.first > lastIndex) {
            val before = content.substring(lastIndex, match.range.first)
            if (before.isNotBlank()) parts.add(MessagePart.Text(before))
        }
        val code = match.groupValues[1].trimEnd()
        if (code.isNotBlank()) parts.add(MessagePart.CodeBlock(code))
        lastIndex = match.range.last + 1
    }
    if (lastIndex < content.length) {
        val remaining = content.substring(lastIndex)
        if (remaining.isNotBlank()) parts.add(MessagePart.Text(remaining))
    }
    return parts.ifEmpty { listOf(MessagePart.Text(content)) }
}

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.data.remote.dto.ChatMessageAdminResponse
import vn.edu.uit.devorbit.admin.ui.components.ObsidianAvatar
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianShape
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianType
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
                    Text(studentName, style = ObsidianType.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Đóng")
                }
            }
        },
        text = {
            if (state.chatMessages.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.chatMessages, key = { it.id }) { msg ->
                        ChatMessageRow(msg)
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun ChatMessageRow(msg: ChatMessageAdminResponse) {
    val isSystem = msg.sender != "Student"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSystem) Arrangement.Start else Arrangement.End
    ) {
        if (isSystem) {
            ObsidianAvatar(name = msg.sender, size = 28)
            Spacer(Modifier.width(8.dp))
        }
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .then(
                    if (isSystem) Modifier
                    else Modifier
                )
        ) {
            Text(
                msg.sender,
                style = ObsidianType.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(2.dp))
            Surface(
                shape = ObsidianShape.sm,
                color = if (isSystem) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Text(
                    msg.content,
                    style = ObsidianType.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

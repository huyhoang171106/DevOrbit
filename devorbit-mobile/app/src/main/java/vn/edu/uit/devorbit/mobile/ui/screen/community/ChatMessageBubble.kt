package vn.edu.uit.devorbit.mobile.ui.screen.community

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.ChatMessage
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    isMine: Boolean,
    showSender: Boolean,
    modifier: Modifier = Modifier
) {
    val alpha = if (message.isSending) 0.6f else 1f
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .alpha(alpha),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        if (showSender && !isMine) {
            Text(
                text = message.senderName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CosmicTheme.colors.plasma,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isMine) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!message.senderAvatar.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(message.senderAvatar)
                                .crossfade(true)
                                .build(),
                            contentDescription = message.senderName,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CosmicTheme.colors.plasma.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = message.senderName.take(1).uppercase(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTheme.colors.plasma
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            Column(
                horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
            ) {
                if (!message.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(message.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Image message",
                        modifier = Modifier
                            .widthIn(max = 260.dp)
                            .heightIn(max = 300.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(message.imageUrl))
                                context.startActivity(intent)
                            },
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .background(
                                if (isMine) CosmicTheme.colors.plasma.copy(alpha = 0.15f)
                                else CosmicTheme.colors.nebula,
                                RoundedCornerShape(
                                    topStart = 16.dp, topEnd = 16.dp,
                                    bottomStart = if (isMine) 16.dp else 4.dp,
                                    bottomEnd = if (isMine) 4.dp else 16.dp
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = if (message.deleted) "Tin nhắn đã bị xóa" else message.content,
                            fontSize = 14.sp,
                            color = if (message.deleted) CosmicTheme.colors.textTertiary else CosmicTheme.colors.textPrimary,
                            lineHeight = 20.sp
                        )
                    }
                }

                Text(
                    text = formatMessageTime(message.createdAt),
                    fontSize = 10.sp,
                    color = CosmicTheme.colors.textTertiary,
                    modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                )
            }
        }
    }
}

private fun formatMessageTime(isoString: String): String {
    if (isoString.isBlank()) return ""
    return try {
        val local = try {
            java.time.LocalDateTime.parse(isoString).atZone(ZoneId.systemDefault())
        } catch (_: Exception) {
            try {
                Instant.parse(isoString).atZone(ZoneId.systemDefault())
            } catch (_: Exception) {
                return ""
            }
        }
        val now = java.time.ZonedDateTime.now()
        val timeStr = local.format(DateTimeFormatter.ofPattern("HH:mm"))
        when {
            local.toLocalDate() == now.toLocalDate() -> timeStr
            local.toLocalDate() == now.toLocalDate().minusDays(1) -> "Hôm qua $timeStr"
            local.year == now.year -> local.format(DateTimeFormatter.ofPattern("dd 'thg' M, HH:mm"))
            else -> local.format(DateTimeFormatter.ofPattern("dd 'thg' M/yyyy, HH:mm"))
        }
    } catch (_: Exception) {
        ""
    }
}

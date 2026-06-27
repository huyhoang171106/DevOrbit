package vn.edu.uit.devorbit.mobile.ui.screen.community

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@Composable
fun ChatInputBar(
    channelName: String,
    onSend: (String) -> Unit,
    onSendImage: (Uri) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploading = true
            onSendImage(it)
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 4.dp,
        color = CosmicTheme.colors.nebula
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { imagePicker.launch("image/*") },
                enabled = enabled && !isUploading
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Gửi ảnh",
                    tint = CosmicTheme.colors.textTertiary
                )
            }

            BasicTextField(
                value = text,
                onValueChange = { if (it.length <= 1000) text = it },
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .background(
                        androidx.compose.material3.MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = CosmicTheme.colors.textPrimary
                ),
                cursorBrush = SolidColor(CosmicTheme.colors.plasma),
                decorationBox = { innerTextField ->
                    Box {
                        if (text.isEmpty() && !isUploading) {
                            Text(
                                text = "Gửi tin nhắn...",
                                fontSize = 14.sp,
                                color = CosmicTheme.colors.textTertiary
                            )
                        }
                        if (isUploading) {
                            Text(
                                text = "Đang tải ảnh...",
                                fontSize = 14.sp,
                                color = CosmicTheme.colors.plasma
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    val trimmed = text.trim()
                    if (trimmed.isNotBlank()) {
                        onSend(trimmed)
                        text = ""
                    }
                },
                enabled = enabled && text.isNotBlank() && !isUploading
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Gửi",
                    tint = if (text.isNotBlank() && !isUploading) CosmicTheme.colors.plasma
                           else CosmicTheme.colors.textTertiary
                )
            }
        }
    }
}

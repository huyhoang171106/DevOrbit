package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicGlowPurple

data class ChatMessage(val content: String, val isUser: Boolean)

@Composable
fun AiChatScreen(onBack: () -> Unit) {
    var messageText by remember { mutableStateOf("") }
    val chatMessages = remember { mutableStateListOf(
        ChatMessage("Hello! I'm your DevOrbit AI Tutor. How can I help you today?", false)
    ) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack) {
            Text("< Close AI Tutor", color = Color.White)
        }
        
        Text(
            "AI Learning Assistant",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatMessages) { msg ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    GlassCard(
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.content,
                            color = if (msg.isUser) CosmicGlowPurple else Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Ask me anything about UIT courses...", color = Color.White.copy(alpha = 0.5f)) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = CosmicGlowPurple,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                )
            )
            
            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        chatMessages.add(ChatMessage(messageText, true))
                        val userQuery = messageText
                        messageText = ""
                        // Simulating AI Response
                        chatMessages.add(ChatMessage("Processing your request about '$userQuery'...", false))
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(containerColor = CosmicGlowPurple)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

package vn.edu.uit.devorbit.mobile.ui.screen.plan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.SubjectQaMessage
import vn.edu.uit.devorbit.mobile.ui.viewmodel.SubjectQaProgressStep
import vn.edu.uit.devorbit.mobile.ui.viewmodel.SubjectQaViewModel

@Composable
fun SubjectQaScreen(
    viewModel: SubjectQaViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    val suggestedPrompts = listOf(
        "Môn Hệ điều hành có repo nào trên DevOrbit không?",
        "Môn IT007 có bao nhiêu tín chỉ?",
        "Mình nên học môn nào sau IT001?"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = CosmicTheme.colors.plasma,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Hỏi AI về môn học",
                style = CosmicTheme.typography.display.copy(fontSize = 22.sp),
                color = CosmicTheme.colors.textPrimary
            )
        }

        if (state.messages.isEmpty()) {
            // Premium Empty State
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = CosmicTheme.colors.plasma.copy(alpha = 0.2f),
                        modifier = Modifier.size(72.dp)
                    )
                    Text(
                        text = "Tôi có thể giúp gì cho bạn hôm nay?",
                        style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Medium),
                        color = CosmicTheme.colors.textSecondary
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        suggestedPrompts.forEach { prompt ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        input = prompt
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = CosmicTheme.colors.nebula,
                                border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                            ) {
                                Text(
                                    text = prompt,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    style = CosmicTheme.typography.label,
                                    color = CosmicTheme.colors.plasma
                                )
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(state.messages) { message ->
                    SubjectQaMessageCard(message)
                }
                if (state.loading) {
                    item {
                        SubjectQaStreamingCard(
                            steps = state.progressSteps,
                            partialAnswer = state.streamingText
                        )
                    }
                }
            }
        }

        state.error?.let { message ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CosmicTheme.colors.supernova.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, CosmicTheme.colors.supernova.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message,
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = viewModel::retryLastQuestion) {
                        Text("Thử lại", color = CosmicTheme.colors.plasma)
                    }
                    TextButton(onClick = viewModel::clearError) {
                        Text("Đóng", color = CosmicTheme.colors.textTertiary)
                    }
                }
            }
        }

        // Multiline Prompt Box
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Nhập câu hỏi...", color = CosmicTheme.colors.textTertiary) },
                maxLines = 4,
                minLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CosmicTheme.colors.plasma,
                    unfocusedBorderColor = CosmicTheme.colors.glassBorder,
                    cursorColor = CosmicTheme.colors.plasma,
                    focusedTextColor = CosmicTheme.colors.textPrimary,
                    unfocusedTextColor = CosmicTheme.colors.textPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            )
            IconButton(
                enabled = input.isNotBlank() && !state.loading,
                onClick = {
                    viewModel.ask(input)
                    input = ""
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (input.isNotBlank() && !state.loading) CosmicTheme.colors.plasma else CosmicTheme.colors.glassBorder,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Gửi",
                    tint = if (input.isNotBlank() && !state.loading) Color.White else CosmicTheme.colors.textTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SubjectQaMessageCard(message: SubjectQaMessage) {
    val isUser = message.role == "user"
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) MaterialTheme.colorScheme.primaryContainer else CosmicTheme.colors.nebula,
            border = if (isUser) null else BorderStroke(1.dp, CosmicTheme.colors.glassBorder),
            shadowElevation = if (isUser) 1.dp else 2.dp,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isUser) "Bạn" else "AI Tutor",
                    style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                    color = if (isUser) CosmicTheme.colors.plasma else CosmicTheme.colors.plasma
                )
                Spacer(Modifier.height(6.dp))

                if (!isUser && message.progressSteps.isNotEmpty()) {
                    SubjectQaProgressTimeline(steps = message.progressSteps, active = false)
                    Spacer(Modifier.height(10.dp))
                }

                FormattedMessageText(text = message.text)

                if (message.sources.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Nguồn tài liệu:",
                        style = CosmicTheme.typography.label.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = CosmicTheme.colors.textSecondary
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        message.sources.forEach { source ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                            ) {
                                Text(
                                    text = source,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = CosmicTheme.typography.label.copy(fontSize = 9.sp),
                                    color = CosmicTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectQaStreamingCard(
    steps: List<SubjectQaProgressStep>,
    partialAnswer: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder),
        modifier = Modifier.widthIn(max = 320.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "AI Tutor đang xử lý",
                style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                color = CosmicTheme.colors.plasma
            )
            if (steps.isEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = CosmicTheme.colors.plasma
                    )
                    Text(
                        text = "Đang kết nối với trợ lý AI...",
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textSecondary
                    )
                }
            } else {
                SubjectQaProgressTimeline(steps = steps, active = true)
            }
            if (partialAnswer.isNotBlank()) {
                HorizontalDivider(color = CosmicTheme.colors.glassBorder)
                FormattedMessageText(text = partialAnswer)
            }
        }
    }
}

@Composable
private fun SubjectQaProgressTimeline(
    steps: List<SubjectQaProgressStep>,
    active: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        steps.forEachIndexed { index, step ->
            val isCurrent = active && index == steps.lastIndex
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCurrent) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        strokeWidth = 2.dp,
                        color = CosmicTheme.colors.plasma
                    )
                } else {
                    Text(
                        text = "✓",
                        style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                        color = CosmicTheme.colors.aurora
                    )
                }
                Text(
                    text = step.message,
                    style = CosmicTheme.typography.label.copy(fontSize = 11.sp),
                    color = if (isCurrent) CosmicTheme.colors.textPrimary else CosmicTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun FormattedMessageText(text: String) {
    // Simple code block parser (splitting by ```)
    val parts = remember(text) { text.split("```") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        parts.forEachIndexed { index, part ->
            val isCode = index % 2 != 0
            if (isCode) {
                // Code Block Container
                val lines = part.trim().lines()
                val language = lines.firstOrNull()?.trim() ?: ""
                val codeContent = if (lines.size > 1) {
                    lines.drop(1).joinToString("\n")
                } else part

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        if (language.isNotBlank() && language.length < 15) {
                            Text(
                                text = language.uppercase(),
                                style = CosmicTheme.typography.label.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = CosmicTheme.colors.textTertiary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        Text(
                            text = codeContent,
                            style = CosmicTheme.typography.label.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            color = CosmicTheme.colors.textPrimary
                        )
                    }
                }
            } else {
                if (part.isNotBlank()) {
                    Text(
                        text = part,
                        style = CosmicTheme.typography.body,
                        color = CosmicTheme.colors.textPrimary
                    )
                }
            }
        }
    }
}

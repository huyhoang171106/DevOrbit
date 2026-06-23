package vn.edu.uit.devorbit.admin.screens.tutor

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.admin.components.EmptyStateScene
import vn.edu.uit.devorbit.admin.components.LoadingStructure
import vn.edu.uit.devorbit.admin.components.ResponseActions
import vn.edu.uit.devorbit.admin.components.ResponseSectionType
import vn.edu.uit.devorbit.admin.components.SignalButton
import vn.edu.uit.devorbit.admin.components.SourceData
import vn.edu.uit.devorbit.admin.components.SourceReference
import vn.edu.uit.devorbit.admin.components.TutorResponseSection
import vn.edu.uit.devorbit.admin.data.MessageRole
import vn.edu.uit.devorbit.admin.data.ResponseSection
import vn.edu.uit.devorbit.admin.data.TutorMessage
import vn.edu.uit.devorbit.admin.data.TutorMode
import vn.edu.uit.devorbit.admin.design.OrbitColors
import vn.edu.uit.devorbit.admin.design.OrbitShapes
import vn.edu.uit.devorbit.admin.design.OrbitSpacing

// ═══════════════════════════════════════════════════════════════════════════════
// LOCAL MODELS
// ═══════════════════════════════════════════════════════════════════════════════

/** A suggested follow-up question shown below an AI response. */
private data class FollowUpQuestion(
    val text: String,
    val mode: TutorMode = TutorMode.Explain,
)

// ═══════════════════════════════════════════════════════════════════════════════
// STATE
// ═══════════════════════════════════════════════════════════════════════════════

private sealed class TutorUiState {
    data object Loading : TutorUiState()
    data object Empty : TutorUiState()
    data class Error(val message: String) : TutorUiState()
    data class Active(
        val conversations: List<TutorMessage> = emptyList(),
        val currentMode: TutorMode = TutorMode.Explain,
        val isStreaming: Boolean = false,
        val streamingContent: String = "",
        val followUps: List<FollowUpQuestion> = emptyList(),
        val subjectName: String? = null,
    ) : TutorUiState()
}

// ═══════════════════════════════════════════════════════════════════════════════
// SCREEN
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * AI Tutor screen with contextual AI conversations.
 *
 * Features a mode selector (Explain / Practice / Review / Debug),
 * conversation display with typed response sections, source references,
 * follow-up suggestions, skeleton streaming state, and a fixed composer bar.
 *
 * @param modifier Optional [Modifier] applied to the root container.
 * @param subjectId Optional subject identifier. When set, the header shows
 *   the subject context; when null, a generic AI Tutor header is displayed.
 */
@Composable
fun TutorScreen(
    modifier: Modifier = Modifier,
    subjectId: String? = null,
) {
    // ── In production this would come from a ViewModel ────────────────────
    val state = rememberTutorState(subjectId)

    when (val s = state) {
        is TutorUiState.Loading -> LoadingStructure(modifier = modifier)
        is TutorUiState.Empty -> EmptyTutorState(
            subjectId = subjectId,
            modifier = modifier,
        )
        is TutorUiState.Error -> ErrorTutorState(
            message = s.message,
            onRetry = { /* ViewModel retry */ },
            modifier = modifier,
        )
        is TutorUiState.Active -> ActiveTutorContent(
            state = s,
            subjectId = subjectId,
            onSendQuery = { _, _ -> /* ViewModel send */ },
            onStopGeneration = { /* ViewModel stop */ },
            onChangeMode = { /* ViewModel change mode */ },
            modifier = modifier,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ACTIVE CONTENT
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ActiveTutorContent(
    state: TutorUiState.Active,
    subjectId: String?,
    onSendQuery: (String, TutorMode) -> Unit,
    onStopGeneration: () -> Unit,
    onChangeMode: (TutorMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    var queryText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current

    // Auto-scroll to bottom when new messages or streaming content arrive
    val itemCount = state.conversations.size + (if (state.isStreaming) 1 else 0)
    LaunchedEffect(itemCount) {
        if (itemCount > 0) {
            listState.animateScrollToItem(itemCount)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = OrbitSpacing.screenHorizontal),
    ) {
        // ── Context Header ─────────────────────────────────────────────
        ContextHeader(
            subjectName = state.subjectName,
            subjectId = subjectId,
        )

        // ── Mode Selector ──────────────────────────────────────────────
        ModeSelector(
            selectedMode = state.currentMode,
            onModeChange = onChangeMode,
        )

        // ── Conversation Area ──────────────────────────────────────────
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(OrbitSpacing.contentGap),
                contentPadding = PaddingValues(
                    top = OrbitSpacing.standard,
                    bottom = OrbitSpacing.hero,
                ),
            ) {
                // Existing messages
                items(state.conversations, key = { it.id }) { message ->
                    when (message.role) {
                        MessageRole.User -> UserQueryBubble(
                            content = message.content,
                            mode = message.mode,
                        )
                        MessageRole.AI -> AiResponseBlock(
                            message = message,
                            followUps = state.followUps,
                            onFollowUp = { q ->
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onSendQuery(q.text, q.mode)
                            },
                        )
                    }
                }

                // Streaming skeleton / partial content
                if (state.isStreaming) {
                    item(key = "streaming") {
                        StreamingSkeleton(content = state.streamingContent)
                    }
                }
            }
        }

        // ── Fixed Composer ─────────────────────────────────────────────
        ComposerBar(
            text = queryText,
            onTextChange = { queryText = it },
            onSend = {
                if (queryText.isNotBlank()) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSendQuery(queryText.trim(), state.currentMode)
                    queryText = ""
                }
            },
            onVoice = { /* ViewModel voice input */ },
            onAttachment = { /* ViewModel file attach */ },
            showStop = state.isStreaming,
            onStop = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onStopGeneration()
            },
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// CONTEXT HEADER
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ContextHeader(
    subjectName: String?,
    subjectId: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = OrbitSpacing.compact),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (subjectId != null) {
            // Subject-scoped context
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(OrbitColors.CyanSignal),
            )
            Spacer(Modifier.width(OrbitSpacing.compact))
            Column {
                Text(
                    text = "STUDYING",
                    style = MaterialTheme.typography.labelSmall,
                    color = OrbitColors.CyanSignal,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = subjectName ?: "Subject",
                    style = MaterialTheme.typography.titleSmall,
                    color = OrbitColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            // Generic context
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = OrbitColors.PrimaryElectricBlue,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(OrbitSpacing.compact))
            Text(
                text = "AI Tutor",
                style = MaterialTheme.typography.titleSmall,
                color = OrbitColors.TextPrimary,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// MODE SELECTOR
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ModeSelector(
    selectedMode: TutorMode,
    onModeChange: (TutorMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = OrbitSpacing.compact),
        horizontalArrangement = Arrangement.spacedBy(OrbitSpacing.compact),
    ) {
        TutorMode.entries.forEach { mode ->
            val isSelected = mode == selectedMode

            val chipBg by animateColorAsState(
                targetValue = if (isSelected) OrbitColors.PrimaryElectricBlue.copy(alpha = 0.15f)
                else Color.Transparent,
                label = "chipBg",
            )
            val chipBorder by animateColorAsState(
                targetValue = if (isSelected) OrbitColors.PrimaryElectricBlue
                else OrbitColors.BorderSubtle,
                label = "chipBorder",
            )

            FilterChip(
                selected = isSelected,
                onClick = { onModeChange(mode) },
                label = {
                    Text(
                        text = mode.name,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) OrbitColors.PrimaryElectricBlue
                        else OrbitColors.TextSecondary,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = chipBg,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = chipBorder,
                    selectedBorderColor = chipBorder,
                    enabled = true,
                    selected = isSelected,
                ),
                shape = OrbitShapes.compactControl,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// USER QUERY BUBBLE
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun UserQueryBubble(
    content: String,
    mode: TutorMode,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 320.dp),
            horizontalAlignment = Alignment.End,
        ) {
            // Mode indicator tag
            Text(
                text = mode.name.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = OrbitColors.TextMuted,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 4.dp, start = 8.dp),
            )
            // Message bubble — restrained query shape
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 4.dp,
                        ),
                    )
                    .background(OrbitColors.SurfaceInteractive)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OrbitColors.TextPrimary,
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// AI RESPONSE BLOCK
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun AiResponseBlock(
    message: TutorMessage,
    followUps: List<FollowUpQuestion>,
    onFollowUp: (FollowUpQuestion) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(OrbitSpacing.contentGap),
    ) {
        // ── Typed Response Sections ─────────────────────────────────────
        message.sections.forEach { section ->
            TutorResponseSection(
                type = section.type,
                title = section.title,
                content = section.content,
            )
        }

        // Fallback: plain content if no sections but content exists
        if (message.sections.isEmpty() && message.content.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(OrbitShapes.contentPanel)
                    .background(OrbitColors.SurfaceBase)
                    .padding(OrbitSpacing.standard),
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OrbitColors.TextSecondary,
                )
            }
        }

        // ── Source References ───────────────────────────────────────────
        if (message.sources.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(OrbitSpacing.compact),
            ) {
                Text(
                    text = "SOURCES",
                    style = MaterialTheme.typography.labelSmall,
                    color = OrbitColors.TextMuted,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 4.dp),
                )
                message.sources.forEach { source ->
                    SourceReference(
                        source = source,
                        onClick = { /* open source URL */ },
                    )
                }
            }
        }

        // ── Response Actions ────────────────────────────────────────────
        ResponseActions(
            onCopy = { /* copy response to clipboard */ },
            onSave = { /* save response */ },
            onListen = { /* text-to-speech */ },
        )

        // ── Follow-Up Questions ─────────────────────────────────────────
        if (followUps.isNotEmpty()) {
            FollowUpRow(
                questions = followUps,
                onQuestionClick = onFollowUp,
            )
        }

        // Bottom spacing between responses
        Spacer(Modifier.height(OrbitSpacing.section))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// FOLLOW-UP ROW
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun FollowUpRow(
    questions: List<FollowUpQuestion>,
    onQuestionClick: (FollowUpQuestion) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(OrbitSpacing.compact),
    ) {
        Text(
            text = "FOLLOW UP",
            style = MaterialTheme.typography.labelSmall,
            color = OrbitColors.TextMuted,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp),
        )

        questions.forEach { question ->
            val haptic = LocalHapticFeedback.current
            Box(
                modifier = Modifier
                    .clip(OrbitShapes.compactControl)
                    .background(OrbitColors.SurfaceRaised)
                    .border(
                        width = 0.5.dp,
                        color = OrbitColors.BorderSubtle.copy(alpha = 0.5f),
                        shape = OrbitShapes.compactControl,
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onQuestionClick(question)
                        },
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = question.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OrbitColors.TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "\u2192",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OrbitColors.PrimaryElectricBlue,
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// STREAMING SKELETON
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun StreamingSkeleton(
    content: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(OrbitSpacing.contentGap),
    ) {
        // Pulsing generating indicator
        Row(verticalAlignment = Alignment.CenterVertically) {
            val pulseAlpha by animateFloatAsState(
                targetValue = if (content.isEmpty()) 0.3f else 1f,
                animationSpec = tween(durationMillis = 800),
                label = "pulse",
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(OrbitColors.CyanSignal.copy(alpha = pulseAlpha)),
            )
            Spacer(Modifier.width(OrbitSpacing.compact))
            Text(
                text = "GENERATING",
                style = MaterialTheme.typography.labelSmall,
                color = OrbitColors.CyanSignal,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
            )
        }

        // Content placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(OrbitShapes.contentPanel)
                .background(OrbitColors.SurfaceBase)
                .padding(OrbitSpacing.standard),
        ) {
            if (content.isNotBlank()) {
                // Show partially streamed text with cursor
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OrbitColors.TextSecondary,
                )
            } else {
                // Structural skeleton blocks
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(OrbitColors.SurfaceRaised),
                    )
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(OrbitColors.SurfaceRaised),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(OrbitColors.SurfaceRaised),
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// FIXED COMPOSER BAR
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ComposerBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onVoice: () -> Unit,
    onAttachment: () -> Unit,
    showStop: Boolean,
    onStop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .background(OrbitColors.BackgroundNavy.copy(alpha = 0.95f))
            .padding(
                horizontal = OrbitSpacing.standard,
                vertical = OrbitSpacing.compact,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ── Attachment ──────────────────────────────────────────────
            IconButton(onClick = onAttachment) {
                Icon(
                    imageVector = Icons.Outlined.AttachFile,
                    contentDescription = "Attach",
                    tint = OrbitColors.TextMuted,
                    modifier = Modifier.size(22.dp),
                )
            }

            Spacer(Modifier.width(OrbitSpacing.compact))

            // ── Text Field ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(OrbitShapes.compactControl)
                    .background(OrbitColors.SurfaceBase)
                    .border(0.5.dp, OrbitColors.BorderSubtle, OrbitShapes.compactControl)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = if (showStop) "Generating..." else "Ask anything...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OrbitColors.TextMuted,
                    )
                }
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = OrbitColors.TextPrimary,
                    ),
                    cursorBrush = SolidColor(OrbitColors.PrimaryElectricBlue),
                    enabled = !showStop,
                )
            }

            Spacer(Modifier.width(OrbitSpacing.compact))

            // ── Action Button ───────────────────────────────────────────
            when {
                showStop -> {
                    // Stop generation
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(OrbitColors.Error.copy(alpha = 0.15f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onStop()
                                },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            tint = OrbitColors.Error,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                text.isNotBlank() -> {
                    // Send query
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(OrbitColors.PrimaryElectricBlue.copy(alpha = 0.15f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onSend()
                                },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = OrbitColors.PrimaryElectricBlue,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                else -> {
                    // Voice input
                    IconButton(onClick = onVoice) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice",
                            tint = OrbitColors.TextMuted,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// EMPTY STATE
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun EmptyTutorState(
    subjectId: String?,
    modifier: Modifier = Modifier,
) {
    val subtitle = if (subjectId != null) {
        "Ask questions about your subject. Choose a mode and start learning."
    } else {
        "Select a subject or ask anything. Choose a mode to focus your learning."
    }

    EmptyStateScene(
        title = "Start a Conversation",
        message = subtitle,
        modifier = modifier,
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// ERROR STATE
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ErrorTutorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(OrbitSpacing.major),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "!",
            style = MaterialTheme.typography.displaySmall,
            color = OrbitColors.Error.copy(alpha = 0.6f),
        )
        Spacer(Modifier.height(OrbitSpacing.standard))
        Text(
            text = "Failed to load tutor",
            style = MaterialTheme.typography.titleSmall,
            color = OrbitColors.TextPrimary,
        )
        Spacer(Modifier.height(OrbitSpacing.compact))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = OrbitColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(OrbitSpacing.standard))
        SignalButton(
            text = "Retry",
            onClick = onRetry,
            icon = Icons.Default.Refresh,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SAMPLE STATE — scaffold for development
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Returns a sample [TutorUiState] for development preview.
 * Replace with ViewModel-backed state in production.
 */
@Composable
private fun rememberTutorState(subjectId: String?): TutorUiState {
    return remember {
        when {
            subjectId != null -> TutorUiState.Active(
                conversations = listOf(
                    TutorMessage(
                        id = "1",
                        role = MessageRole.User,
                        mode = TutorMode.Explain,
                        content = "Can you explain the difference between a linked list and an array?",
                    ),
                    TutorMessage(
                        id = "2",
                        role = MessageRole.AI,
                        mode = TutorMode.Explain,
                        content = "",
                        sections = listOf(
                            ResponseSection(
                                type = ResponseSectionType.Explanation,
                                title = "Arrays vs Linked Lists",
                                content = "An array stores elements in contiguous memory blocks, " +
                                    "providing O(1) indexed access but O(n) insertion/deletion. " +
                                    "A linked list stores elements in non-contiguous nodes " +
                                    "connected by pointers, providing O(1) insertion/deletion " +
                                    "at known positions but O(n) access.",
                            ),
                            ResponseSection(
                                type = ResponseSectionType.KeyConcept,
                                title = "Memory Layout",
                                content = "Arrays: [0][1][2][3][4] \u2014 sequential in memory.\n" +
                                    "Linked List: [data|*] \u2192 [data|*] \u2192 [data|*] " +
                                    "\u2014 scattered, pointer-connected.",
                            ),
                            ResponseSection(
                                type = ResponseSectionType.Example,
                                title = "When to Use Each",
                                content = "Use arrays for random access patterns " +
                                    "(e.g., lookups by index). Use linked lists for frequent " +
                                    "insertions/removals, especially at the head or tail.",
                            ),
                        ),
                        sources = listOf(
                            SourceData(
                                title = "Introduction to Algorithms, CLRS",
                                type = "Book",
                                source = "MIT Press, 4th Ed.",
                                relevance = 0.95f,
                            ),
                            SourceData(
                                title = "Array vs Linked List: Time Complexity",
                                type = "Article",
                                source = "geeksforgeeks.org",
                                relevance = 0.82f,
                            ),
                        ),
                    ),
                ),
                currentMode = TutorMode.Explain,
                isStreaming = false,
                subjectName = "Data Structures",
                followUps = listOf(
                    FollowUpQuestion(
                        text = "What are the trade-offs with doubly linked lists?",
                    ),
                    FollowUpQuestion(
                        text = "Show me a code example of reversing a linked list",
                        mode = TutorMode.Practice,
                    ),
                    FollowUpQuestion(
                        text = "Compare ArrayList vs LinkedList in Java",
                        mode = TutorMode.Review,
                    ),
                ),
            )
            else -> TutorUiState.Empty
        }
    }
}

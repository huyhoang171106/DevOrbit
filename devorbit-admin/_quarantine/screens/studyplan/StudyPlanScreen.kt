package vn.edu.uit.devorbit.admin.screens.studyplan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.components.EmptyStateScene
import vn.edu.uit.devorbit.admin.components.LoadingStructure
import vn.edu.uit.devorbit.admin.components.SignalButton
import vn.edu.uit.devorbit.admin.components.StudyTimeline
import vn.edu.uit.devorbit.admin.components.TimelineDay
import vn.edu.uit.devorbit.admin.components.TimelineSession
import vn.edu.uit.devorbit.admin.design.OrbitColors
import vn.edu.uit.devorbit.admin.design.OrbitShapes
import vn.edu.uit.devorbit.admin.design.metricLabelStyle
import vn.edu.uit.devorbit.admin.design.metricTextStyle

// ═════════════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═════════════════════════════════════════════════════════════════════════════

/** Current phase of the study plan. */
private data class StudyPhase(
    val title: String,
    val description: String,
    val progress: Float,     // 0f..1f
    val completedSessions: Int,
    val totalSessions: Int,
    val color: Color = OrbitColors.PrimaryElectricBlue,
)

/** A single subject workload contribution. */
private data class WorkloadEntry(
    val subject: String,
    val percentage: Float,   // 0f..1f
    val color: Color,
)

/** An upcoming deadline. */
private data class DeadlineEntry(
    val title: String,
    val subject: String,
    val dueDays: Int,        // 0 = today
    val subjectColor: Color,
)

/** A past/future phase for the expansion section. */
private data class PhaseEntry(
    val name: String,
    val progress: Float,
    val sessionCount: Int,
    val isCurrent: Boolean = false,
    val isCompleted: Boolean = false,
    val description: String = "",
)

/** AI recommendation content. */
private data class AiRecommendation(
    val title: String,
    val summary: String,
    val reasoning: String,
)

// ═════════════════════════════════════════════════════════════════════════════
// STATE
// ═════════════════════════════════════════════════════════════════════════════

private sealed class StudyPlanState {
    data object Loading : StudyPlanState()
    data object Empty : StudyPlanState()
    data class Error(val message: String) : StudyPlanState()
    data class Content(
        val phase: StudyPhase,
        val timeline: List<TimelineDay>,
        val workload: List<WorkloadEntry>,
        val deadlines: List<DeadlineEntry>,
        val phases: List<PhaseEntry>,
        val recommendation: AiRecommendation,
    ) : StudyPlanState()
}

// ═════════════════════════════════════════════════════════════════════════════
// SCREEN
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Study Plan screen with phase tracking, weekly timeline, workload balance,
 * upcoming deadlines, AI insight, and phase roadmap.
 *
 * @param modifier Optional [Modifier] applied to the root container.
 */
@Composable
fun StudyPlanScreen(
    modifier: Modifier = Modifier,
) {
    // ── In a production build this would come from a ViewModel ────────────
    val state = rememberStudyPlanState()

    when (val s = state) {
        is StudyPlanState.Loading -> LoadingStructure(modifier = modifier)
        is StudyPlanState.Error -> ErrorState(
            message = s.message,
            onRetry = { /* ViewModel retry */ },
            modifier = modifier,
        )
        is StudyPlanState.Empty -> EmptyState(
            onGeneratePlan = { /* ViewModel generate */ },
            modifier = modifier,
        )
        is StudyPlanState.Content -> ContentState(
            state = s,
            modifier = modifier,
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// CONTENT
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun ContentState(
    state: StudyPlanState.Content,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
    ) {
        // ── Hero Section ────────────────────────────────────────────────
        item(key = "hero") { HeroSection(state.phase) }

        item(key = "heroSpacer") { Spacer(Modifier.height(28.dp)) }

        // ── Weekly Timeline ─────────────────────────────────────────────
        item(key = "timelineHeader") { SectionHeader("Weekly Timeline") }
        item(key = "timelineSpacer1") { Spacer(Modifier.height(12.dp)) }

        // Drag-like interaction hint — subtle grip dots
        item(key = "timelineDragHint") { DragHint() }
        item(key = "timelineSpacer2") { Spacer(Modifier.height(6.dp)) }

        item(key = "timeline") {
            StudyTimeline(
                days = state.timeline,
                modifier = Modifier.padding(horizontal = 2.dp),
                onSessionClick = { sessionId -> /* navigate */ },
            )
        }

        item(key = "timelineSpacer3") { Spacer(Modifier.height(28.dp)) }

        // ── Workload Balance ────────────────────────────────────────────
        item(key = "workloadHeader") { SectionHeader("Workload Balance") }
        item(key = "workloadSpacer1") { Spacer(Modifier.height(12.dp)) }
        item(key = "workload") { WorkloadBalance(state.workload) }

        item(key = "workloadSpacer2") { Spacer(Modifier.height(28.dp)) }

        // ── Upcoming Deadlines ──────────────────────────────────────────
        item(key = "deadlinesHeader") { SectionHeader("Upcoming Deadlines") }
        item(key = "deadlinesSpacer1") { Spacer(Modifier.height(12.dp)) }
        items(state.deadlines, key = { "deadline_${it.title}" }) { deadline ->
            DeadlineCard(deadline)
            Spacer(Modifier.height(8.dp))
        }

        if (state.deadlines.isNotEmpty()) {
            item(key = "deadlinesSpacer2") { Spacer(Modifier.height(28.dp)) }
        }

        // ── AI Recommendation ───────────────────────────────────────────
        item(key = "aiRecHeader") { SectionHeader("AI Recommendation") }
        item(key = "aiRecSpacer1") { Spacer(Modifier.height(12.dp)) }
        item(key = "aiRec") { AiRecommendationCard(state.recommendation) }

        item(key = "aiRecSpacer2") { Spacer(Modifier.height(28.dp)) }

        // ── Phase Expansion / Roadmap ───────────────────────────────────
        item(key = "roadmapHeader") { SectionHeader("Study Roadmap") }
        item(key = "roadmapSpacer1") { Spacer(Modifier.height(12.dp)) }
        items(state.phases, key = { "phase_${it.name}" }) { phase ->
            PhaseCard(phase)
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// HERO SECTION
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun HeroSection(phase: StudyPhase) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OrbitShapes.contentPanel)
            .background(OrbitColors.SurfaceBase)
            .border(
                width = 0.5.dp,
                color = OrbitColors.BorderSubtle.copy(alpha = 0.4f),
                shape = OrbitShapes.contentPanel,
            )
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // ── Progress Arc (left) ─────────────────────────────────────────
        Box(
            modifier = Modifier.size(72.dp),
            contentAlignment = Alignment.Center,
        ) {
            val animatedProgress by animateFloatAsState(
                targetValue = phase.progress,
                animationSpec = tween(800),
                label = "heroProgress",
            )
            Canvas(modifier = Modifier.size(72.dp)) {
                val strokeW = 5.dp.toPx()
                val arcSize = Size(size.width - strokeW, size.height - strokeW)
                val topLeft = Offset(strokeW / 2f, strokeW / 2f)
                val sweep = 270f * animatedProgress

                // Track
                drawArc(
                    color = OrbitColors.BorderSubtle.copy(alpha = 0.3f),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round),
                )
                // Progress fill
                drawArc(
                    color = phase.color,
                    startAngle = 135f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(phase.progress * 100).toInt()}%",
                    style = metricTextStyle,
                    color = OrbitColors.TextPrimary,
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        // ── Phase Info (right) ──────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = phase.title,
                style = MaterialTheme.typography.titleMedium,
                color = OrbitColors.PrimaryElectricBlue,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = phase.description,
                style = MaterialTheme.typography.bodyMedium,
                color = OrbitColors.TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))

            // Session count
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Sessions",
                    style = MaterialTheme.typography.labelSmall,
                    color = OrbitColors.TextMuted,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${phase.completedSessions}/${phase.totalSessions}",
                    style = metricLabelStyle,
                    color = OrbitColors.TextPrimary,
                )

                Spacer(Modifier.width(12.dp))

                // Rest day marker
                Text(
                    text = "R",
                    style = MaterialTheme.typography.labelSmall,
                    color = OrbitColors.ChartGreen,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Rest today",
                    style = MaterialTheme.typography.labelSmall,
                    color = OrbitColors.TextMuted,
                )
            }

            Spacer(Modifier.height(8.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(OrbitColors.BorderSubtle.copy(alpha = 0.4f)),
            ) {
                val barProgress by animateFloatAsState(
                    targetValue = phase.progress,
                    animationSpec = tween(800),
                    label = "heroBar",
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(barProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
                        .background(phase.color),
                )
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// DRAG HINT
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun DragHint() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(OrbitColors.TextMuted.copy(alpha = 0.25f)),
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// WORKLOAD BALANCE
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun WorkloadBalance(entries: List<WorkloadEntry>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OrbitShapes.contentPanel)
            .background(OrbitColors.SurfaceBase)
            .border(
                width = 0.5.dp,
                color = OrbitColors.BorderSubtle.copy(alpha = 0.4f),
                shape = OrbitShapes.contentPanel,
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        entries.forEach { entry ->
            WorkloadRow(entry)
        }
    }
}

@Composable
private fun WorkloadRow(entry: WorkloadEntry) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(entry.color),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = entry.subject,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OrbitColors.TextPrimary,
                )
            }
            Text(
                text = "${(entry.percentage * 100).toInt()}%",
                style = metricTextStyle,
                color = OrbitColors.TextSecondary,
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(OrbitColors.BorderSubtle.copy(alpha = 0.3f)),
        ) {
            val animatedWidth by animateFloatAsState(
                targetValue = entry.percentage,
                animationSpec = tween(600),
                label = "workloadBar",
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(entry.color),
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// DEADLINE CARD
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun DeadlineCard(deadline: DeadlineEntry) {
    val urgencyColor = when {
        deadline.dueDays <= 0 -> OrbitColors.Error
        deadline.dueDays <= 2 -> OrbitColors.Warning
        else -> OrbitColors.TextMuted
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OrbitShapes.compactControl)
            .background(OrbitColors.SurfaceBase)
            .border(
                width = 0.5.dp,
                color = OrbitColors.BorderSubtle.copy(alpha = 0.4f),
                shape = OrbitShapes.compactControl,
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Subject color indicator
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(deadline.subjectColor),
        )
        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = deadline.title,
                style = MaterialTheme.typography.bodyMedium,
                color = OrbitColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = deadline.subject,
                style = MaterialTheme.typography.labelSmall,
                color = OrbitColors.TextMuted,
            )
        }

        Spacer(Modifier.width(12.dp))

        // Due countdown
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = when {
                    deadline.dueDays <= 0 -> "Due"
                    deadline.dueDays == 1 -> "1d"
                    else -> "${deadline.dueDays}d"
                },
                style = metricTextStyle,
                color = urgencyColor,
            )
            Text(
                text = "left",
                style = MaterialTheme.typography.labelSmall,
                color = OrbitColors.TextMuted,
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// AI RECOMMENDATION CARD
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun AiRecommendationCard(recommendation: AiRecommendation) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OrbitShapes.contentPanel)
            .background(OrbitColors.SurfaceBase)
            .border(
                width = 0.5.dp,
                color = OrbitColors.PrimaryElectricBlue.copy(alpha = 0.2f),
                shape = OrbitShapes.contentPanel,
            )
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = OrbitColors.ChartYellow,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = recommendation.title,
                style = MaterialTheme.typography.titleSmall,
                color = OrbitColors.TextPrimary,
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = recommendation.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = OrbitColors.TextPrimary,
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(OrbitShapes.dataCard)
                .background(OrbitColors.SurfaceRaised)
                .padding(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = OrbitColors.PrimaryElectricBlue.copy(alpha = 0.6f),
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = recommendation.reasoning,
                style = MaterialTheme.typography.bodySmall,
                color = OrbitColors.TextSecondary,
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// PHASE EXPANSION CARD
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun PhaseCard(phase: PhaseEntry) {
    var expanded by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OrbitShapes.compactControl)
            .background(
                if (phase.isCurrent) OrbitColors.SurfaceInteractive.copy(alpha = 0.4f)
                else OrbitColors.SurfaceBase
            )
            .border(
                width = if (phase.isCurrent) 0.5.dp else 0.dp,
                color = if (phase.isCurrent) OrbitColors.PrimaryElectricBlue.copy(alpha = 0.25f)
                else OrbitColors.BorderSubtle.copy(alpha = 0.3f),
                shape = OrbitShapes.compactControl,
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    expanded = !expanded
                },
            )
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Completion dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        when {
                            phase.isCompleted -> OrbitColors.Success
                            phase.isCurrent -> OrbitColors.PrimaryElectricBlue
                            else -> OrbitColors.NodeLocked
                        }
                    ),
            )
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = phase.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OrbitColors.TextPrimary,
                    fontWeight = if (phase.isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                )
                if (expanded && phase.description.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = phase.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = OrbitColors.TextSecondary,
                    )
                }
            }

            // Session count
            Text(
                text = "${phase.sessionCount} sessions",
                style = MaterialTheme.typography.labelSmall,
                color = OrbitColors.TextMuted,
            )
            Spacer(Modifier.width(8.dp))

            // Expand chevron
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = OrbitColors.TextMuted,
                modifier = Modifier.size(18.dp),
            )
        }

        // Progress sub-bar when expanded
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(animationSpec = tween(200)) + slideInVertically(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(150)),
        ) {
            Column {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(OrbitColors.BorderSubtle.copy(alpha = 0.3f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(phase.progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                when {
                                    phase.isCompleted -> OrbitColors.Success
                                    phase.isCurrent -> OrbitColors.PrimaryElectricBlue
                                    else -> OrbitColors.ChartMuted
                                }
                            ),
                    )
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// ERROR STATE
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Flag,
            contentDescription = null,
            tint = OrbitColors.Error.copy(alpha = 0.6f),
            modifier = Modifier.size(40.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Failed to load plan",
            style = MaterialTheme.typography.titleSmall,
            color = OrbitColors.TextPrimary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = OrbitColors.TextSecondary,
        )
        Spacer(Modifier.height(16.dp))
        SignalButton(
            text = "Retry",
            onClick = onRetry,
            icon = Icons.Default.Refresh,
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// EMPTY STATE
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun EmptyState(
    onGeneratePlan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EmptyStateScene(
        title = "No Study Plan Yet",
        message = "Create a personalized study plan powered by AI. " +
            "It will optimise your weekly schedule, balance workload, " +
            "and adapt to your progress.",
        modifier = modifier,
        action = {
            SignalButton(
                text = "Generate Plan",
                onClick = onGeneratePlan,
                icon = Icons.Default.AutoAwesome,
            )
        },
    )
}

// ═════════════════════════════════════════════════════════════════════════════
// SECTION HEADER
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun SectionHeader(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(3.dp, 16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(OrbitColors.PrimaryElectricBlue.copy(alpha = 0.7f)),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = OrbitColors.TextPrimary,
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// SAMPLE STATE — scaffold for development
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Returns sample [StudyPlanState.Content] for development preview.
 * Replace with ViewModel-backed state in production.
 */
@Composable
private fun rememberStudyPlanState(): StudyPlanState {
    // Toggle this to preview different states:
    // return StudyPlanState.Loading
    // return StudyPlanState.Error("Network unavailable")
    // return StudyPlanState.Empty

    return StudyPlanState.Content(
        phase = StudyPhase(
            title = "Foundation Builder",
            description = "Core concepts & fundamentals — building a strong base for advanced topics.",
            progress = 0.62f,
            completedSessions = 18,
            totalSessions = 29,
        ),
        timeline = listOf(
            TimelineDay(
                dayOfWeek = "Mon",
                date = 23,
                isToday = true,
                sessions = listOf(
                    TimelineSession(
                        id = "s1", title = "Linear Algebra", duration = "90m",
                        completed = true, subject = "Math",
                        color = OrbitColors.ChartBlue,
                    ),
                    TimelineSession(
                        id = "s2", title = "Data Structures", duration = "60m",
                        completed = false, subject = "CS",
                        color = OrbitColors.ChartGreen,
                    ),
                    TimelineSession(
                        id = "s3", title = "OS Concepts", duration = "45m",
                        completed = false, subject = "CS",
                        color = OrbitColors.ChartOrange,
                    ),
                ),
            ),
            TimelineDay(
                dayOfWeek = "Tue", date = 24,
                sessions = listOf(
                    TimelineSession(
                        id = "s4", title = "Algorithms", duration = "90m",
                        completed = false, subject = "CS",
                        color = OrbitColors.ChartGreen,
                    ),
                    TimelineSession(
                        id = "s5", title = "Physics", duration = "60m",
                        completed = false, subject = "Science",
                        color = OrbitColors.ChartCyan,
                    ),
                ),
            ),
            TimelineDay(
                dayOfWeek = "Wed", date = 25,
                sessions = listOf(
                    TimelineSession(
                        id = "s6", title = "Linear Algebra", duration = "60m",
                        completed = false, subject = "Math",
                        color = OrbitColors.ChartBlue,
                    ),
                ),
            ),
            TimelineDay(
                dayOfWeek = "Thu", date = 26,
                sessions = emptyList(),
            ),
            TimelineDay(
                dayOfWeek = "Fri", date = 27,
                sessions = listOf(
                    TimelineSession(
                        id = "s7", title = "OS Review", duration = "90m",
                        completed = false, subject = "CS",
                        color = OrbitColors.ChartOrange,
                    ),
                    TimelineSession(
                        id = "s8", title = "Physics Lab", duration = "120m",
                        completed = false, subject = "Science",
                        color = OrbitColors.ChartCyan,
                    ),
                ),
            ),
            TimelineDay(
                dayOfWeek = "Sat", date = 28,
                sessions = listOf(
                    TimelineSession(
                        id = "s9", title = "Mock Test", duration = "180m",
                        completed = false, subject = "Exam",
                        color = OrbitColors.ChartRed,
                    ),
                ),
            ),
            TimelineDay(
                dayOfWeek = "Sun", date = 29,
                sessions = emptyList(),
            ),
        ),
        workload = listOf(
            WorkloadEntry("Mathematics", 0.35f, OrbitColors.ChartBlue),
            WorkloadEntry("Computer Science", 0.30f, OrbitColors.ChartGreen),
            WorkloadEntry("Physics", 0.20f, OrbitColors.ChartCyan),
            WorkloadEntry("Elective", 0.15f, OrbitColors.ChartOrange),
        ),
        deadlines = listOf(
            DeadlineEntry(
                title = "Assignment 3 — Sorting Networks",
                subject = "CS",
                dueDays = 0,
                subjectColor = OrbitColors.ChartGreen,
            ),
            DeadlineEntry(
                title = "Mid-term Exam Registration",
                subject = "Admin",
                dueDays = 2,
                subjectColor = OrbitColors.ChartYellow,
            ),
            DeadlineEntry(
                title = "Lab Report — Harmonic Oscillator",
                subject = "Physics",
                dueDays = 5,
                subjectColor = OrbitColors.ChartCyan,
            ),
        ),
        phases = listOf(
            PhaseEntry(
                name = "Foundation Builder",
                progress = 1f,
                sessionCount = 29,
                isCompleted = true,
                description = "Core concepts & fundamentals — complete.",
            ),
            PhaseEntry(
                name = "Deep Dive",
                progress = 0.62f,
                sessionCount = 34,
                isCurrent = true,
                description = "Advanced topics and applied problem-solving.",
            ),
            PhaseEntry(
                name = "Mastery & Review",
                progress = 0f,
                sessionCount = 22,
                description = "Comprehensive review and mock examinations.",
            ),
            PhaseEntry(
                name = "Final Preparation",
                progress = 0f,
                sessionCount = 16,
                description = "Targeted revision before assessments.",
            ),
        ),
        recommendation = AiRecommendation(
            title = "Weekly Focus: Algorithms Depth",
            summary = "Your mock test performance suggests spending extra time on " +
                "graph algorithms and dynamic programming this week.",
            reasoning = "Based on your session history, graph traversal exercises have " +
                "a 73% correlation with improved test scores. Prioritise LeetCode medium " +
                "problems on DFS/BFS and DP patterns.",
        ),
    )
}

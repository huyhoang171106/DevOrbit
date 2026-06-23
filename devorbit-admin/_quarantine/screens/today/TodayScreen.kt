package vn.edu.uit.devorbit.admin.screens.today

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.admin.components.EmptyStateScene
import vn.edu.uit.devorbit.admin.components.LoadingStructure
import vn.edu.uit.devorbit.admin.components.MetricDisplay
import vn.edu.uit.devorbit.admin.components.OrbitalLearningMap
import vn.edu.uit.devorbit.admin.components.OrbitalNode
import vn.edu.uit.devorbit.admin.components.SignalButton
import vn.edu.uit.devorbit.admin.components.TechnicalDivider
import vn.edu.uit.devorbit.admin.data.Subject
import vn.edu.uit.devorbit.admin.data.SubjectStatus
import vn.edu.uit.devorbit.admin.design.OrbitColors
import vn.edu.uit.devorbit.admin.design.OrbitShapes
import vn.edu.uit.devorbit.admin.design.metricTextStyle
import java.util.Calendar

// ═════════════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═════════════════════════════════════════════════════════════════════════════

/** A task item due today. */
private data class TodayTask(
    val id: String,
    val title: String,
    val subject: String,
    val subjectColor: Color = OrbitColors.PrimaryElectricBlue,
    val isUrgent: Boolean = false,
)

/** A learning recommendation item. */
private data class RecommendationItem(
    val id: String,
    val title: String,
    val summary: String,
    val icon: ImageVector = Icons.Default.AutoAwesome,
    val color: Color = OrbitColors.CyanSignal,
)

/** An upcoming study session with subject context. */
private data class UpcomingSession(
    val id: String,
    val title: String,
    val subject: String,
    val startTime: String,
    val duration: String,
    val subjectColor: Color = OrbitColors.ChartBlue,
)

// ═════════════════════════════════════════════════════════════════════════════
// STATE
// ═════════════════════════════════════════════════════════════════════════════

private sealed class TodayState {
    data object Loading : TodayState()
    data object Empty : TodayState()
    data class Error(val message: String) : TodayState()
    data class Content(
        val greeting: String,
        val subjects: List<Subject>,
        val streakDays: Int,
        val weeklyFocusHours: Float,
        val tasks: List<TodayTask>,
        val upcomingSession: UpcomingSession?,
        val recommendations: List<RecommendationItem>,
        val continueLearningSubject: String?,
    ) : TodayState()
}

// ═════════════════════════════════════════════════════════════════════════════
// SCREEN
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Today dashboard screen — the primary landing view of DevOrbit.
 *
 * Displays a time-based greeting, orbital learning map of active subjects,
 * key metrics (streak, weekly focus), tasks due today, the next upcoming
 * study session, a compact recommendation rail, and a "Continue Learning"
 * primary action.
 *
 * @param modifier Optional [Modifier] applied to the root container.
 * @param onNavigateToSubject Callback to navigate to the subjects overview.
 * @param onNavigateToTutor Callback to navigate to the AI tutor.
 * @param onNavigateToStudyPlan Callback to navigate to the study plan.
 */
@Composable
fun TodayScreen(
    modifier: Modifier = Modifier,
    onNavigateToSubject: () -> Unit = {},
    onNavigateToTutor: () -> Unit = {},
    onNavigateToStudyPlan: () -> Unit = {},
) {
    // ── In a production build this would come from a ViewModel ────────────
    val state = rememberTodayState()

    when (val s = state) {
        is TodayState.Loading -> LoadingStructure(modifier = modifier)
        is TodayState.Error -> ErrorState(
            message = s.message,
            onRetry = { /* ViewModel retry */ },
            modifier = modifier,
        )
        is TodayState.Empty -> EmptyState(
            onBrowseSubjects = onNavigateToSubject,
            modifier = modifier,
        )
        is TodayState.Content -> ContentSection(
            state = s,
            onSubjectClick = { subjectId ->
                onNavigateToSubject()
            },
            onNavigateToTutor = onNavigateToTutor,
            onNavigateToStudyPlan = onNavigateToStudyPlan,
            modifier = modifier,
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// CONTENT
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun ContentSection(
    state: TodayState.Content,
    onSubjectClick: (String) -> Unit,
    onNavigateToTutor: () -> Unit,
    onNavigateToStudyPlan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedNodeId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 16.dp,
            bottom = 96.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        // ── Greeting ────────────────────────────────────────────────────
        item(key = "greeting") { GreetingSection(text = state.greeting) }

        item(key = "greetingSpacer") { Spacer(Modifier.height(20.dp)) }

        // ── Orbital Learning Map ────────────────────────────────────────
        item(key = "orbitMap") {
            val orbitNodes = state.subjects.filter { s ->
                s.status == SubjectStatus.Active
            }.map { subject ->
                OrbitalNode(
                    id = subject.id,
                    label = subject.code,
                    progress = subject.progress,
                    color = subject.color,
                    isCurrent = subject.status == SubjectStatus.Active,
                    isCompleted = subject.status == SubjectStatus.Completed,
                    isLocked = subject.status == SubjectStatus.Locked,
                )
            }

            if (orbitNodes.isNotEmpty()) {
                OrbitalLearningMap(
                    nodes = orbitNodes,
                    selectedNodeId = selectedNodeId,
                    onNodeSelected = { id ->
                        selectedNodeId = if (selectedNodeId == id) null else id
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                )
            }
        }

        // ── Selected Node Expanded Card ─────────────────────────────────
        item(key = "selectedNodeCard") {
            val selectedSubject = state.subjects.find { it.id == selectedNodeId }
            if (selectedSubject != null) {
                Spacer(Modifier.height(12.dp))
                SelectedSubjectCard(
                    subject = selectedSubject,
                    onClick = { onSubjectClick(selectedSubject.id) },
                )
            }
        }

        item(key = "orbitSpacer") { Spacer(Modifier.height(24.dp)) }

        // ── Metrics Row ──────────────────────────────────────────────────
        item(key = "metricsRow") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricDisplay(
                    value = "${state.streakDays}",
                    label = "DAY STREAK",
                    valueColor = OrbitColors.ChartYellow,
                    modifier = Modifier.weight(1f),
                    large = true,
                )
                MetricDisplay(
                    value = "${state.weeklyFocusHours}h",
                    label = "THIS WEEK",
                    valueColor = OrbitColors.ChartCyan,
                    modifier = Modifier.weight(1f),
                    large = true,
                )
            }
        }

        item(key = "metricsSpacer") { Spacer(Modifier.height(20.dp)) }

        // ── Technical Divider ───────────────────────────────────────────
        item(key = "divider1") {
            TechnicalDivider(showAccent = true)
        }

        item(key = "dividerSpacer1") { Spacer(Modifier.height(20.dp)) }

        // ── Tasks Due Today ─────────────────────────────────────────────
        if (state.tasks.isNotEmpty()) {
            item(key = "tasksHeader") {
                SectionHeader(
                    title = "Tasks Due Today",
                    icon = Icons.Default.Flag,
                )
            }

            item(key = "tasksSpacer1") { Spacer(Modifier.height(10.dp)) }

            items(state.tasks, key = { "task_${it.id}" }) { task ->
                TaskRow(task = task)
                Spacer(Modifier.height(8.dp))
            }

            item(key = "tasksSpacer2") { Spacer(Modifier.height(20.dp)) }
            item(key = "tasksDivider") { TechnicalDivider(showAccent = false) }
            item(key = "tasksSpacer3") { Spacer(Modifier.height(20.dp)) }
        }

        // ── Upcoming Study Session ──────────────────────────────────────
        if (state.upcomingSession != null) {
            item(key = "upcomingHeader") {
                SectionHeader(
                    title = "Up Next",
                    icon = Icons.Default.Schedule,
                )
            }

            item(key = "upcomingSpacer1") { Spacer(Modifier.height(10.dp)) }
            item(key = "upcomingSession") {
                UpcomingSessionCard(session = state.upcomingSession!!)
            }

            item(key = "upcomingSpacer2") { Spacer(Modifier.height(24.dp)) }
        }

        // ── Recommendation Rail ─────────────────────────────────────────
        if (state.recommendations.isNotEmpty()) {
            item(key = "recsHeader") {
                SectionHeader(
                    title = "Recommendations",
                    icon = Icons.Default.AutoAwesome,
                )
            }

            item(key = "recsSpacer1") { Spacer(Modifier.height(10.dp)) }
            item(key = "recsRail") {
                RecommendationRail(
                    items = state.recommendations,
                    onItemClick = onNavigateToTutor,
                )
            }

            item(key = "recsSpacer2") { Spacer(Modifier.height(28.dp)) }
        }

        // ── Continue Learning ───────────────────────────────────────────
        item(key = "continueLearning") {
            val label = state.continueLearningSubject?.let {
                "Continue $it"
            } ?: "Continue Learning"

            SignalButton(
                text = label,
                onClick = { onNavigateToTutor() },
                icon = Icons.Default.Bolt,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// GREETING
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun GreetingSection(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = OrbitColors.TextPrimary,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
    )
}

// ═════════════════════════════════════════════════════════════════════════════
// SELECTED SUBJECT CARD
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun SelectedSubjectCard(
    subject: Subject,
    onClick: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val bgColor by animateColorAsState(
        targetValue = subject.color.copy(alpha = 0.08f),
        animationSpec = tween(300),
        label = "selectedCardBg",
    )
    val animatedProgress by animateFloatAsState(
        targetValue = subject.progress,
        animationSpec = tween(600),
        label = "selectedCardProgress",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OrbitShapes.contentPanel)
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
            )
            .padding(16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = OrbitColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subject.code,
                        style = MaterialTheme.typography.labelMedium,
                        color = OrbitColors.TextMuted,
                    )
                }
                Text(
                    text = "${(subject.progress * 100).toInt()}%",
                    style = metricTextStyle,
                    color = subject.color,
                )
            }

            Spacer(Modifier.height(12.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(OrbitColors.BorderSubtle),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .background(subject.color),
                )
            }

            if (subject.nextTask.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = OrbitColors.TextMuted,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = subject.nextTask,
                        style = MaterialTheme.typography.bodySmall,
                        color = OrbitColors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// TASK ROW
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun TaskRow(task: TodayTask) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OrbitShapes.technicalRow)
            .background(OrbitColors.SurfaceBase)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Urgency indicator
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(
                    if (task.isUrgent) OrbitColors.Error
                    else task.subjectColor.copy(alpha = 0.6f)
                ),
        )

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium,
                color = OrbitColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = task.subject,
                style = MaterialTheme.typography.labelSmall,
                color = OrbitColors.TextMuted,
            )
        }

        if (task.isUrgent) {
            Spacer(Modifier.width(6.dp))
            Text(
                text = "URGENT",
                style = MaterialTheme.typography.labelSmall,
                color = OrbitColors.Error,
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// UPCOMING SESSION CARD
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun UpcomingSessionCard(session: UpcomingSession) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OrbitShapes.contentPanel)
            .background(OrbitColors.SurfaceBase)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Time block
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(60.dp),
        ) {
            Text(
                text = session.startTime,
                style = MaterialTheme.typography.titleSmall,
                color = OrbitColors.TextPrimary,
            )
            Text(
                text = session.duration,
                style = MaterialTheme.typography.labelSmall,
                color = OrbitColors.TextMuted,
            )
        }

        Spacer(Modifier.width(12.dp))

        // Subject color line
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(session.subjectColor),
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = session.title,
                style = MaterialTheme.typography.titleSmall,
                color = OrbitColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = session.subject,
                style = MaterialTheme.typography.labelMedium,
                color = OrbitColors.TextMuted,
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// RECOMMENDATION RAIL
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun RecommendationRail(
    items: List<RecommendationItem>,
    onItemClick: () -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(items, key = { "rec_${it.id}" }) { item ->
            RecommendationCard(
                item = item,
                onClick = onItemClick,
            )
        }
    }
}

@Composable
private fun RecommendationCard(
    item: RecommendationItem,
    onClick: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .width(200.dp)
            .clip(OrbitShapes.contentPanel)
            .background(OrbitColors.SurfaceBase)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                },
            )
            .padding(14.dp),
    ) {
        Column {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.color,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = OrbitColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodySmall,
                color = OrbitColors.TextSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// SECTION HEADER
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OrbitColors.TextMuted,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = OrbitColors.TextMuted,
            letterSpacing = 0.5.sp,
        )
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
        Text(
            text = "Connection Lost",
            style = MaterialTheme.typography.titleSmall,
            color = OrbitColors.TextPrimary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = OrbitColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
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
    onBrowseSubjects: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EmptyStateScene(
        title = "No Subjects Yet",
        message = "Start by browsing available subjects to build your learning orbit.",
        modifier = modifier,
        action = {
            SignalButton(
                text = "Browse Subjects",
                onClick = onBrowseSubjects,
                icon = Icons.Default.TrendingUp,
            )
        },
    )
}

// ═════════════════════════════════════════════════════════════════════════════
// GREETING HELPER
// ═════════════════════════════════════════════════════════════════════════════

/** Returns a time-appropriate greeting string based on the current hour. */
private fun timeBasedGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..4 -> "Night Owl"
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// SAMPLE STATE — scaffold for development
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Returns sample [TodayState.Content] for development preview.
 * Replace with ViewModel-backed state in production.
 */
@Composable
private fun rememberTodayState(): TodayState {
    // Toggle this to preview different states:
    // return TodayState.Loading
    // return TodayState.Error("Unable to load dashboard data. Check your connection.")

    val sampleSubjects = listOf(
        Subject(
            id = "subj_1",
            code = "CS301",
            title = "Data Structures & Algorithms",
            description = "Core computer science fundamentals covering data organization and algorithmic problem-solving.",
            credits = 4,
            difficulty = "Advanced",
            progress = 0.72f,
            status = SubjectStatus.Active,
            color = OrbitColors.ChartGreen,
            nextTask = "Complete Graph Traversal module",
            estimatedTime = "45 min",
        ),
        Subject(
            id = "subj_2",
            code = "MATH201",
            title = "Linear Algebra",
            description = "Vector spaces, matrices, and linear transformations.",
            credits = 3,
            difficulty = "Intermediate",
            progress = 0.45f,
            status = SubjectStatus.Active,
            color = OrbitColors.ChartBlue,
            nextTask = "Review Eigenvalues",
            estimatedTime = "30 min",
        ),
        Subject(
            id = "subj_3",
            code = "PHY101",
            title = "Physics: Mechanics",
            description = "Classical mechanics from Newton's laws to conservation principles.",
            credits = 3,
            difficulty = "Beginner",
            progress = 0.88f,
            status = SubjectStatus.Active,
            color = OrbitColors.ChartCyan,
            nextTask = "Practice problems — Work-Energy Theorem",
            estimatedTime = "60 min",
        ),
        Subject(
            id = "subj_4",
            code = "CS205",
            title = "Operating Systems",
            description = "Process management, memory, file systems, and concurrency.",
            credits = 4,
            difficulty = "Advanced",
            progress = 0.12f,
            status = SubjectStatus.Active,
            color = OrbitColors.ChartOrange,
            nextTask = "Introduction to Scheduling",
            estimatedTime = "20 min",
        ),
    )

    val sampleTasks = listOf(
        TodayTask(
            id = "task_1",
            title = "Assignment 3 — Sorting Networks",
            subject = "CS301",
            subjectColor = OrbitColors.ChartGreen,
            isUrgent = true,
        ),
        TodayTask(
            id = "task_2",
            title = "Eigenvalue Problem Set",
            subject = "MATH201",
            subjectColor = OrbitColors.ChartBlue,
        ),
        TodayTask(
            id = "task_3",
            title = "Lab Report — Harmonic Oscillator",
            subject = "PHY101",
            subjectColor = OrbitColors.ChartCyan,
        ),
    )

    val sampleRecommendations = listOf(
        RecommendationItem(
            id = "rec_1",
            title = "Graph Traversal Review",
            summary = "Your quiz performance suggests reviewing DFS and BFS edge cases.",
            color = OrbitColors.CyanSignal,
        ),
        RecommendationItem(
            id = "rec_2",
            title = "Linear Algebra Deep Dive",
            summary = "Eigenvalues are a recurring topic — strengthen your foundation.",
            color = OrbitColors.ChartBlue,
        ),
        RecommendationItem(
            id = "rec_3",
            title = "Physics Practice",
            summary = "Work-Energy problems are next in your syllabus.",
            color = OrbitColors.ChartCyan,
        ),
        RecommendationItem(
            id = "rec_4",
            title = "OS Scheduling Quiz",
            summary = "Quick 5-min quiz to reinforce scheduling algorithms.",
            color = OrbitColors.ChartOrange,
        ),
    )

    return TodayState.Content(
        greeting = timeBasedGreeting(),
        subjects = sampleSubjects,
        streakDays = 14,
        weeklyFocusHours = 8.5f,
        tasks = sampleTasks,
        upcomingSession = UpcomingSession(
            id = "session_1",
            title = "Graph Traversal Lab",
            subject = "Data Structures & Algorithms",
            startTime = "14:30",
            duration = "60 min",
            subjectColor = OrbitColors.ChartGreen,
        ),
        recommendations = sampleRecommendations,
        continueLearningSubject = "CS301",
    )
}

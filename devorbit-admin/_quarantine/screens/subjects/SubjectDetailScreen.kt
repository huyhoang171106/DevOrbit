package vn.edu.uit.devorbit.admin.screens.subjects

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LibraryBooks
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material.icons.rounded.Warning
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.admin.components.EmptyStateScene
import vn.edu.uit.devorbit.admin.components.LearningPath
import vn.edu.uit.devorbit.admin.components.LoadingStructure
import vn.edu.uit.devorbit.admin.components.PathNode
import vn.edu.uit.devorbit.admin.components.PathNodeState
import vn.edu.uit.devorbit.admin.components.ProgressArc
import vn.edu.uit.devorbit.admin.components.SignalButton
import vn.edu.uit.devorbit.admin.components.SignalOutlineButton
import vn.edu.uit.devorbit.admin.components.SourceData
import vn.edu.uit.devorbit.admin.components.SourceReference
import vn.edu.uit.devorbit.admin.components.TechnicalDivider
import vn.edu.uit.devorbit.admin.design.OrbitColors
import vn.edu.uit.devorbit.admin.design.OrbitShapes
import vn.edu.uit.devorbit.admin.design.OrbitTypography

// ═════════════════════════════════════════════════════════════════════════════
// LOCAL DATA MODELS (bridge until ViewModel is wired)
// ═════════════════════════════════════════════════════════════════════════════

data class SubjectDetail(
    val id: String,
    val code: String,
    val title: String,
    val subtitle: String = "",
    val description: String = "",
    val credits: Int = 0,
    val semester: Int? = null,
    val progress: Float = 0f,
    val currentPhase: String = "",
    val nextLesson: RecommendedLesson? = null,
    val learningObjectives: List<String> = emptyList(),
    val modules: List<PathNode> = emptyList(),
    val resources: List<SourceData> = emptyList(),
    val aiSummary: String = "",
    val recentActivity: List<ActivityItem> = emptyList(),
)

data class RecommendedLesson(
    val title: String,
    val moduleName: String,
    val estimatedMinutes: Int = 0,
)

data class ActivityItem(
    val id: String,
    val description: String,
    val timestamp: String,
    val type: String = "",
)

// ═════════════════════════════════════════════════════════════════════════════
// UI STATE
// ═════════════════════════════════════════════════════════════════════════════

private sealed interface SubjectUiState {
    data object Loading : SubjectUiState
    data class Content(val subject: SubjectDetail) : SubjectUiState
    data object Empty : SubjectUiState
    data class Error(val message: String) : SubjectUiState
}

// ═════════════════════════════════════════════════════════════════════════════
// MAIN COMPOSABLE
// ═════════════════════════════════════════════════════════════════════════════

/**
 * Detail screen for a single subject.
 *
 * Shows the subject's hero visual, progress, modules, resources,
 * AI summary, recent activity, and a primary CTA.
 *
 * @param subjectId  Route parameter identifying the subject.
 * @param onBack     Called when the user presses the back button.
 * @param modifier   Optional [Modifier] applied to the root scaffold.
 */
@Composable
fun SubjectDetailScreen(
    subjectId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var uiState by remember { mutableStateOf<SubjectUiState>(SubjectUiState.Loading) }

    LaunchedEffect(subjectId) {
        uiState = SubjectUiState.Loading
        try {
            // Simulated load — replace with ViewModel call
            kotlinx.coroutines.delay(400L)
            val data = sampleSubject()
            uiState = if (data.id.isNotBlank()) {
                SubjectUiState.Content(data)
            } else {
                SubjectUiState.Empty
            }
        } catch (e: Exception) {
            uiState = SubjectUiState.Error(e.message ?: "Unknown error")
        }
    }

    // ── Edge-to-edge Box ───────────────────────────────────────────────────
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OrbitColors.BackgroundDeep),
    ) {
        when (val state = uiState) {
            is SubjectUiState.Loading -> {
                LoadingContent()
            }
            is SubjectUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = {
                        // Re-trigger via fresh LaunchedEffect key
                    },
                )
            }
            is SubjectUiState.Empty -> {
                EmptyContent()
            }
            is SubjectUiState.Content -> {
                SubjectContent(
                    subject = state.subject,
                    onBack = onBack,
                )
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// STATE COMPOSABLES
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LoadingStructure(lines = 6)
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                tint = OrbitColors.Error,
                modifier = Modifier.size(48.dp),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Failed to load subject",
                style = OrbitTypography.titleMedium,
                color = OrbitColors.TextPrimary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                style = OrbitTypography.bodyMedium,
                color = OrbitColors.TextSecondary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            SignalOutlineButton(
                text = "Retry",
                onClick = onRetry,
                icon = Icons.Rounded.Refresh,
            )
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        EmptyStateScene(
            title = "No subject data",
            message = "This subject does not exist or has no content yet.",
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// CONTENT LAYOUT
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun SubjectContent(
    subject: SubjectDetail,
    onBack: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            // ── Hero ────────────────────────────────────────────────────────
            HeroArea(
                code = subject.code,
                title = subject.title,
                subtitle = subject.subtitle,
                onBack = onBack,
            )

            // ── Metadata chips ──────────────────────────────────────────────
            MetadataRow(subject = subject)

            Spacer(Modifier.height(20.dp))

            // ── Progress & Phase ────────────────────────────────────────────
            ProgressPhaseCard(subject = subject)
            Spacer(Modifier.height(20.dp))

            // ── Next lesson ─────────────────────────────────────────────────
            subject.nextLesson?.let { lesson ->
                NextLessonCard(lesson = lesson)
                Spacer(Modifier.height(20.dp))
            }

            // ── Learning objectives ─────────────────────────────────────────
            if (subject.learningObjectives.isNotEmpty()) {
                ObjectiveList(objectives = subject.learningObjectives)
                Spacer(Modifier.height(20.dp))
            }

            // ── Module / Learning Path ──────────────────────────────────────
            if (subject.modules.isNotEmpty()) {
                ModulePath(modules = subject.modules)
                Spacer(Modifier.height(20.dp))
            }

            // ── Resources ───────────────────────────────────────────────────
            if (subject.resources.isNotEmpty()) {
                ResourceList(resources = subject.resources)
                Spacer(Modifier.height(20.dp))
            }

            // ── AI summary ──────────────────────────────────────────────────
            if (subject.aiSummary.isNotBlank()) {
                AiSummary(summary = subject.aiSummary)
                Spacer(Modifier.height(20.dp))
            }

            // ── Recent activity ─────────────────────────────────────────────
            if (subject.recentActivity.isNotEmpty()) {
                ActivityFeed(activities = subject.recentActivity)
                Spacer(Modifier.height(24.dp))
            }

            // ── Continue button ─────────────────────────────────────────────
            ContinueCta(subject = subject)
            Spacer(Modifier.height(32.dp))
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// HERO
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun HeroArea(
    code: String,
    title: String,
    subtitle: String,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
    ) {
        // Orbital background
        OrbitalHeroCanvas(modifier = Modifier.fillMaxSize())

        // Overlaid content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
        ) {
            // Back
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(OrbitColors.SurfaceBase.copy(alpha = 0.7f)),
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = OrbitColors.TextPrimary,
                )
            }

            Spacer(Modifier.weight(1f))

            // Code badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(OrbitColors.PrimaryElectricBlue.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            ) {
                Text(
                    text = code,
                    style = OrbitTypography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = OrbitColors.PrimaryBright,
                    letterSpacing = 1.sp,
                )
            }

            Spacer(Modifier.height(12.dp))

            // Title
            Text(
                text = title,
                style = OrbitTypography.headlineMedium,
                color = OrbitColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            // Subtitle
            if (subtitle.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = OrbitTypography.bodyLarge,
                    color = OrbitColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OrbitalHeroCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2
        val cy = h / 2
        val r = minOf(w, h) * 0.42f

        // Gradient base
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    OrbitColors.BackgroundNavy,
                    OrbitColors.SurfaceBase,
                ),
            ),
            size = size,
        )

        // Central glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    OrbitColors.PrimaryElectricBlue.copy(alpha = 0.07f),
                    Color.Transparent,
                ),
            ),
            radius = r,
            center = Offset(cx, cy),
        )

        // Orbital ellipses
        val ellipseAlphas = floatArrayOf(0.15f, 0.10f, 0.06f)
        val ellipseRadii = floatArrayOf(r * 0.85f, r * 1.05f, r * 1.25f)
        for (i in ellipseRadii.indices) {
            drawOval(
                color = OrbitColors.PrimaryElectricBlue.copy(alpha = ellipseAlphas[i]),
                topLeft = Offset(
                    cx - ellipseRadii[i],
                    cy - ellipseRadii[i] * 0.40f,
                ),
                size = Size(
                    ellipseRadii[i] * 2,
                    ellipseRadii[i] * 0.80f,
                ),
                style = Stroke(width = 1.2f),
            )
        }

        // Orbital dots
        val dots = listOf(
            0.12f to 0.28f,
            0.88f to 0.45f,
            0.18f to 0.72f,
            0.75f to 0.22f,
            0.55f to 0.78f,
        )
        dots.forEach { (fx, fy) ->
            drawCircle(
                color = OrbitColors.CyanSignal.copy(alpha = 0.25f),
                radius = 2.5f,
                center = Offset(w * fx, h * fy),
            )
        }

        // Faint constellation lines
        val constellation = listOf(
            Offset(w * 0.12f, h * 0.28f),
            Offset(w * 0.25f, h * 0.35f),
            Offset(w * 0.55f, h * 0.22f),
            Offset(w * 0.75f, h * 0.30f),
            Offset(w * 0.88f, h * 0.45f),
        )
        for (i in 0 until constellation.size - 1) {
            val a = constellation[i]
            val b = constellation[i + 1]
            drawLine(
                color = OrbitColors.PrimaryElectricBlue.copy(alpha = 0.04f),
                start = a,
                end = b,
                strokeWidth = 0.5f,
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// METADATA
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun MetadataRow(subject: SubjectDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MetaChip(
            icon = Icons.Rounded.AutoStories,
            label = "${subject.credits} cr",
            visible = subject.credits > 0,
        )
        subject.semester?.let { sem ->
            MetaChip(
                icon = Icons.Rounded.DateRange,
                label = "Sem $sem",
            )
        }
        if (subject.description.isNotBlank()) {
            MetaChip(
                icon = Icons.Rounded.Description,
                label = subject.description.take(36) +
                    if (subject.description.length > 36) "..." else "",
            )
        }
    }
}

@Composable
private fun MetaChip(
    icon: ImageVector,
    label: String,
    visible: Boolean = true,
) {
    if (!visible) return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(OrbitShapes.compactControl)
            .background(OrbitColors.SurfaceRaised)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OrbitColors.PrimaryElectricBlue,
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = OrbitTypography.labelSmall,
            color = OrbitColors.TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// PROGRESS & PHASE
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun ProgressPhaseCard(subject: SubjectDetail) {
    val animatedProgress by animateFloatAsState(
        targetValue = subject.progress,
        animationSpec = tween(800),
        label = "subjectProgress",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Arc
        Box(
            modifier = Modifier.size(76.dp),
            contentAlignment = Alignment.Center,
        ) {
            ProgressArc(
                progress = animatedProgress,
                size = 76.dp,
                strokeWidth = 4.dp,
                progressColor = OrbitColors.ChartCyan,
                trackColor = OrbitColors.BorderSubtle,
            )
            Text(
                text = "${(subject.progress * 100).toInt()}%",
                style = OrbitTypography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = OrbitColors.TextPrimary,
            )
        }

        Spacer(Modifier.width(16.dp))

        // Labels
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Overall Progress",
                style = OrbitTypography.labelLarge,
                color = OrbitColors.TextSecondary,
            )
            Spacer(Modifier.height(6.dp))
            TechnicalDivider(showAccent = true)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(OrbitShapes.statusChip)
                        .background(OrbitColors.ChartGreen),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Phase: ${subject.currentPhase}",
                    style = OrbitTypography.bodyMedium,
                    color = OrbitColors.TextPrimary,
                )
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// NEXT LESSON
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun NextLessonCard(lesson: RecommendedLesson) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(OrbitShapes.contentPanel)
            .background(OrbitColors.SurfaceRaised)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { /* navigate to lesson */ },
            )
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Rounded.PlayCircle,
                contentDescription = null,
                tint = OrbitColors.PrimaryElectricBlue,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Next up",
                    style = OrbitTypography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                    ),
                    color = OrbitColors.TextMuted,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = lesson.title,
                    style = OrbitTypography.bodyMedium,
                    color = OrbitColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = lesson.moduleName,
                        style = OrbitTypography.labelSmall,
                        color = OrbitColors.TextMuted,
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(OrbitShapes.statusChip)
                            .background(OrbitColors.TextMuted),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${lesson.estimatedMinutes} min",
                        style = OrbitTypography.labelSmall,
                        color = OrbitColors.TextMuted,
                    )
                }
            }
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Start",
                tint = OrbitColors.TextMuted,
                modifier = Modifier
                    .size(20.dp)
                    .rotate(180f),
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// LEARNING OBJECTIVES
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun ObjectiveList(objectives: List<String>) {
    Section(
        icon = Icons.Rounded.TrackChanges,
        title = "Learning Objectives",
        paddingHorizontal = 20.dp,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            objectives.forEach { objective ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(OrbitColors.PrimaryElectricBlue.copy(alpha = 0.55f)),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = objective,
                        style = OrbitTypography.bodyMedium,
                        color = OrbitColors.TextSecondary,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// MODULE PATH
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun ModulePath(modules: List<PathNode>) {
    Section(
        icon = Icons.Rounded.MenuBook,
        title = "Modules",
        paddingHorizontal = 20.dp,
    ) {
        LearningPath(
            nodes = modules,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// RESOURCES
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun ResourceList(resources: List<SourceData>) {
    Section(
        icon = Icons.Rounded.LibraryBooks,
        title = "Resources",
        paddingHorizontal = 20.dp,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            resources.forEach { source ->
                SourceReference(source = source)
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// AI SUMMARY
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun AiSummary(summary: String) {
    Section(
        icon = Icons.Rounded.Psychology,
        title = "AI Summary",
        paddingHorizontal = 20.dp,
        trailing = {
            Box(
                modifier = Modifier
                    .clip(OrbitShapes.dataCard)
                    .background(OrbitColors.CyanSignal.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    text = "GENERATED",
                    style = OrbitTypography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = OrbitColors.CyanSignal,
                    letterSpacing = 1.sp,
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(OrbitShapes.contentPanel)
                .background(OrbitColors.SurfaceRaised)
                .padding(16.dp),
        ) {
            Text(
                text = summary,
                style = OrbitTypography.bodyMedium,
                color = OrbitColors.TextSecondary,
                lineHeight = 22.sp,
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// ACTIVITY FEED
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun ActivityFeed(activities: List<ActivityItem>) {
    Section(
        icon = Icons.Rounded.History,
        title = "Recent Activity",
        paddingHorizontal = 20.dp,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            activities.forEach { activity ->
                ActivityRow(activity = activity)
            }
        }
    }
}

@Composable
private fun ActivityRow(activity: ActivityItem) {
    val dotColor = when (activity.type) {
        "completed" -> OrbitColors.ChartGreen
        "started" -> OrbitColors.ChartBlue
        "submitted" -> OrbitColors.ChartYellow
        else -> OrbitColors.TextMuted
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Timeline dot
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(8.dp)
                .clip(OrbitShapes.statusChip)
                .background(dotColor),
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.description,
                style = OrbitTypography.bodyMedium,
                color = OrbitColors.TextPrimary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = activity.timestamp,
                style = OrbitTypography.labelSmall,
                color = OrbitColors.TextMuted,
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// CONTINUE CTA
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun ContinueCta(subject: SubjectDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        SignalButton(
            text = "Continue Learning",
            onClick = { /* navigate to session */ },
            icon = Icons.Rounded.PlayArrow,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Resume from where you left off in ${subject.currentPhase}.",
            style = OrbitTypography.bodySmall,
            color = OrbitColors.TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// SECTION WRAPPER
// ═════════════════════════════════════════════════════════════════════════════

@Composable
private fun Section(
    icon: ImageVector,
    title: String,
    paddingHorizontal: Dp = 0.dp,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingHorizontal),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OrbitColors.PrimaryElectricBlue,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = title,
                style = OrbitTypography.titleSmall,
                color = OrbitColors.TextPrimary,
                modifier = Modifier.weight(1f),
            )
            trailing?.invoke()
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}
// ═════════════════════════════════════════════════════════════════════════════
// SAMPLE DATA  (remove when ViewModel is connected)
// ═════════════════════════════════════════════════════════════════════════════

private fun sampleSubject() = SubjectDetail(
    id = "cs201",
    code = "CS201",
    title = "Data Structures & Algorithms",
    subtitle = "Foundations of efficient computation",
    description = "Core computer science course",
    credits = 4,
    semester = 3,
    progress = 0.62f,
    currentPhase = "Graph Algorithms",
    nextLesson = RecommendedLesson(
        title = "Dijkstra's Shortest Path",
        moduleName = "Graph Algorithms",
        estimatedMinutes = 35,
    ),
    learningObjectives = listOf(
        "Analyze time and space complexity using Big-O notation",
        "Implement and compare sorting, searching, and hashing algorithms",
        "Design and apply tree and graph data structures to real-world problems",
        "Apply dynamic programming and greedy strategies to optimization problems",
        "Evaluate algorithm correctness through invariants and proof techniques",
    ),
    modules = listOf(
        PathNode("m1", "Intro & Complexity", PathNodeState.Completed, "Big-O, Omega, Theta"),
        PathNode("m2", "Sorting & Searching", PathNodeState.Completed, "QuickSort, MergeSort, Binary Search"),
        PathNode("m3", "Hash Tables", PathNodeState.Completed, "Collision resolution, load factor"),
        PathNode("m4", "Trees & Balanced BSTs", PathNodeState.Current, "AVL, Red-Black, B-Trees"),
        PathNode("m5", "Graph Algorithms", PathNodeState.Available, "BFS, DFS, Dijkstra, Topological Sort"),
        PathNode("m6", "Dynamic Programming", PathNodeState.Locked, "Memoization, tabulation"),
        PathNode("m7", "Advanced Topics", PathNodeState.Locked, "NP-Completeness, approximation"),
    ),
    resources = listOf(
        SourceData("Intro to Algorithms (CLRS)", "Textbook", "MIT Press", 0.95f),
        SourceData("Algorithm Design Manual", "Textbook", "Springer", 0.88f),
        SourceData("Visualgo — Algorithm Visualizations", "Interactive", "visualgo.net", 0.82f),
        SourceData("LeetCode Algorithm Problems", "Practice", "leetcode.com", 0.79f),
    ),
    aiSummary = "Student is progressing steadily through CS201. " +
        "Completed complexity analysis and sorting fundamentals with strong performance. " +
        "Currently working on balanced tree structures. " +
        "Recommended focus: practice AVL rotations and prepare for the upcoming graph algorithms module.",
    recentActivity = listOf(
        ActivityItem("a1", "Completed AVL Tree Insertion", "2 hours ago", "completed"),
        ActivityItem("a2", "Started Red-Black Tree module", "Yesterday at 3:45 PM", "started"),
        ActivityItem("a3", "Submitted homework: Balanced BST", "2 days ago", "submitted"),
        ActivityItem("a4", "Reviewed Lecture 7: Tree rotations", "3 days ago", "started"),
    ),
)

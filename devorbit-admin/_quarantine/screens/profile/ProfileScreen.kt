package vn.edu.uit.devorbit.admin.screens.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.edu.uit.devorbit.admin.components.EmptyStateScene
import vn.edu.uit.devorbit.admin.components.LoadingStructure
import vn.edu.uit.devorbit.admin.components.MetricDisplay
import vn.edu.uit.devorbit.admin.components.StatusChip
import vn.edu.uit.devorbit.admin.components.TechnicalDivider
import vn.edu.uit.devorbit.admin.components.TechnicalVerticalDivider
import vn.edu.uit.devorbit.admin.design.OrbitColors
import vn.edu.uit.devorbit.admin.design.OrbitSpacing
import vn.edu.uit.devorbit.admin.design.OrbitShapes

// ── Data Models ─────────────────────────────────────────────────────────────────

private data class Achievement(
    val title: String,
    val description: String,
    val emoji: String,
    val unlocked: Boolean,
)

private data class SubjectHourData(
    val subject: String,
    val hours: Float,
    val color: Color,
)

private data class WeekDayStat(
    val day: String,
    val hours: Float,
)

private data class ProfileData(
    val name: String,
    val email: String,
    val level: Int,
    val xp: Int,
    val xpToNextLevel: Int,
    val avatarInitials: String,
    val goals: List<String>,
    val streak: Int,
    val focusedHours: Double,
    val subjectsCount: Int,
    val achievements: List<Achievement>,
    val subjectHours: List<SubjectHourData>,
    val weeklyStats: List<WeekDayStat>,
)

// ── UI State ────────────────────────────────────────────────────────────────────

private sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data object Empty : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    data class Content(
        val data: ProfileData,
        val darkTheme: Boolean,
        val haptic: Boolean,
        val notifications: Boolean,
        val privacy: Boolean,
    ) : ProfileUiState()
}

// ── Sample Data ─────────────────────────────────────────────────────────────────

private fun sampleProfileData() = ProfileData(
    name = "Alex Chen",
    email = "alex.chen@devorbit.io",
    level = 27,
    xp = 2840,
    xpToNextLevel = 3200,
    avatarInitials = "AC",
    goals = listOf(
        "Complete System Design capstone",
        "Reach Level 30 before end of month",
        "Maintain 7-day streak",
    ),
    streak = 12,
    focusedHours = 47.5,
    subjectsCount = 6,
    achievements = listOf(
        Achievement("Early Bird", "Complete 5 morning sessions", "\uD83C\uDF05", true),
        Achievement("Streak Master", "20-day streak", "\uD83D\uDD25", false),
        Achievement("Polyglot", "Study 3 languages", "\uD83D\uDDE3\uFE0F", true),
        Achievement("Night Owl", "10 late-night sessions", "\uD83E\uDD89", true),
        Achievement("Speed Runner", "Complete module in 2 days", "\u26A1", false),
        Achievement("Contributor", "10 repo contributions", "\uD83C\uDF1F", true),
    ),
    subjectHours = listOf(
        SubjectHourData("Data Structures", 18.5f, OrbitColors.ChartBlue),
        SubjectHourData("Algorithms", 12.0f, OrbitColors.ChartCyan),
        SubjectHourData("System Design", 8.5f, OrbitColors.ChartGreen),
        SubjectHourData("Databases", 5.0f, OrbitColors.ChartYellow),
        SubjectHourData("Networking", 3.5f, OrbitColors.ChartOrange),
    ),
    weeklyStats = listOf(
        WeekDayStat("Mon", 3.5f),
        WeekDayStat("Tue", 4.0f),
        WeekDayStat("Wed", 2.5f),
        WeekDayStat("Thu", 5.0f),
        WeekDayStat("Fri", 3.0f),
        WeekDayStat("Sat", 6.5f),
        WeekDayStat("Sun", 4.5f),
    ),
)

// ── Main Screen ─────────────────────────────────────────────────────────────────

/**
 * Profile and Analytics screen.
 *
 * Sections: identity card, goals, stats row, achievement grid, subject
 * distribution bar chart, weekly activity chart, and settings toggles.
 *
 * All chart visuals are drawn with [Canvas]; no third-party charting library.
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
) {
    val profileData = remember { sampleProfileData() }

    val state: ProfileUiState = remember {
        ProfileUiState.Content(
            data = profileData,
            darkTheme = true,
            haptic = true,
            notifications = true,
            privacy = false,
        )
    }

    when (val s = state) {
        is ProfileUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LoadingStructure()
            }
        }

        is ProfileUiState.Empty -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                EmptyStateScene(
                    title = "No Profile Data",
                    message = "Your profile information will appear here once available.",
                )
            }
        }

        is ProfileUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                EmptyStateScene(
                    title = "Could Not Load Profile",
                    message = s.message,
                )
            }
        }

        is ProfileUiState.Content -> {
            ProfileContent(
                data = s.data,
                darkTheme = s.darkTheme,
                haptic = s.haptic,
                notifications = s.notifications,
                privacy = s.privacy,
                modifier = modifier,
            )
        }
    }
}

// ── Content Layout (scrollable) ─────────────────────────────────────────────────

@Composable
private fun ProfileContent(
    data: ProfileData,
    darkTheme: Boolean,
    haptic: Boolean,
    notifications: Boolean,
    privacy: Boolean,
    modifier: Modifier = Modifier,
) {
    var darkThemeEnabled by remember { mutableStateOf(darkTheme) }
    var hapticEnabled by remember { mutableStateOf(haptic) }
    var notificationsEnabled by remember { mutableStateOf(notifications) }
    var privacyEnabled by remember { mutableStateOf(privacy) }

    val spacing = OrbitSpacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.screenHorizontal),
    ) {
        Spacer(Modifier.height(spacing.section))

        // ── Identity ─────────────────────────────────────────────────
        ProfileIdentitySection(
            name = data.name,
            email = data.email,
            level = data.level,
            xp = data.xp,
            xpToNextLevel = data.xpToNextLevel,
            avatarInitials = data.avatarInitials,
        )

        Spacer(Modifier.height(spacing.standard))

        // ── Goals ────────────────────────────────────────────────────
        ProfileGoalsSection(goals = data.goals)

        Spacer(Modifier.height(spacing.section))

        // ── Stats Row ────────────────────────────────────────────────
        ProfileStatsRow(
            streak = data.streak,
            focusedHours = data.focusedHours,
            subjectsCount = data.subjectsCount,
        )

        Spacer(Modifier.height(spacing.section))

        // ── Achievements ─────────────────────────────────────────────
        SectionHeader(title = "Achievements")
        Spacer(Modifier.height(spacing.compact))
        AchievementsGrid(achievements = data.achievements)

        Spacer(Modifier.height(spacing.section))

        // ── Subject Distribution ─────────────────────────────────────
        SectionHeader(title = "Subject Distribution")
        Spacer(Modifier.height(spacing.compact))
        SubjectDistributionChart(
            subjects = data.subjectHours,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        )

        Spacer(Modifier.height(spacing.section))

        // ── Weekly Activity ──────────────────────────────────────────
        SectionHeader(title = "Weekly Activity")
        Spacer(Modifier.height(spacing.compact))
        WeeklyStatsChart(
            stats = data.weeklyStats,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
        )

        Spacer(Modifier.height(spacing.section))

        TechnicalDivider()
        Spacer(Modifier.height(spacing.standard))

        // ── Settings ─────────────────────────────────────────────────
        SectionHeader(title = "Settings")
        Spacer(Modifier.height(spacing.compact))
        SettingsSection(
            darkThemeEnabled = darkThemeEnabled,
            onDarkThemeChange = { darkThemeEnabled = it },
            hapticEnabled = hapticEnabled,
            onHapticChange = { hapticEnabled = it },
            notificationsEnabled = notificationsEnabled,
            onNotificationsChange = { notificationsEnabled = it },
            privacyEnabled = privacyEnabled,
            onPrivacyChange = { privacyEnabled = it },
        )

        // Bottom spacer so content clears the CommandDock overlay.
        Spacer(Modifier.height(spacing.hero))
    }
}

// ── Section: Identity ───────────────────────────────────────────────────────────

@Composable
private fun ProfileIdentitySection(
    name: String,
    email: String,
    level: Int,
    xp: Int,
    xpToNextLevel: Int,
    avatarInitials: String,
) {
    val spacing = OrbitSpacing

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = OrbitColors.SurfaceRaised,
                shape = OrbitShapes.contentPanel,
            )
            .border(
                width = 0.5.dp,
                color = OrbitColors.BorderSubtle.copy(alpha = 0.5f),
                shape = OrbitShapes.contentPanel,
            )
            .padding(spacing.standard),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileAvatar(initials = avatarInitials)
            Spacer(Modifier.width(spacing.standard))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    color = OrbitColors.TextPrimary,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = OrbitColors.TextMuted,
                )
                Spacer(Modifier.height(spacing.compact))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(
                        text = "Lv. $level",
                        color = OrbitColors.CyanSignal,
                        background = OrbitColors.CyanSignal.copy(alpha = 0.12f),
                    )
                    Spacer(Modifier.width(spacing.compact))
                    Text(
                        text = "$xp / $xpToNextLevel XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = OrbitColors.TextSecondary,
                    )
                }
            }
        }

        Spacer(Modifier.height(spacing.compact))

        XpProgressBar(
            progress = xp.toFloat() / xpToNextLevel.toFloat().coerceAtLeast(1f),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ── Avatar ──────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(OrbitColors.PrimaryElectricBlue.copy(alpha = 0.18f))
            .border(1.dp, OrbitColors.PrimaryElectricBlue.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            color = OrbitColors.PrimaryElectricBlue,
            fontWeight = FontWeight.Medium,
        )
    }
}

// ── XP Progress Bar ─────────────────────────────────────────────────────────────

@Composable
private fun XpProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "xpProgress",
    )

    Box(
        modifier = modifier
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(OrbitColors.SurfaceInteractive),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(3.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            OrbitColors.PrimaryElectricBlue,
                            OrbitColors.CyanSignal,
                        ),
                    ),
                ),
        )
    }
}

// ── Section: Goals ──────────────────────────────────────────────────────────────

@Composable
private fun ProfileGoalsSection(
    goals: List<String>,
) {
    val spacing = OrbitSpacing

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = OrbitColors.SurfaceBase,
                shape = OrbitShapes.contentPanel,
            )
            .border(
                width = 0.5.dp,
                color = OrbitColors.BorderSubtle.copy(alpha = 0.4f),
                shape = OrbitShapes.contentPanel,
            )
            .padding(spacing.standard),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.Flag,
                contentDescription = null,
                tint = OrbitColors.PrimaryElectricBlue,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(spacing.compact))
            Text(
                text = "Current Goals",
                style = MaterialTheme.typography.titleSmall,
                color = OrbitColors.TextPrimary,
            )
        }

        Spacer(Modifier.height(spacing.compact))

        goals.forEachIndexed { index, goal ->
            Row(
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "\u2022",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OrbitColors.CyanSignal,
                )
                Spacer(Modifier.width(spacing.compact))
                Text(
                    text = goal,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OrbitColors.TextSecondary,
                    modifier = Modifier.weight(1f),
                )
            }
            if (index < goals.lastIndex) {
                Spacer(Modifier.height(spacing.listItemGap))
            }
        }
    }
}

// ── Section: Stats Row ──────────────────────────────────────────────────────────

@Composable
private fun ProfileStatsRow(
    streak: Int,
    focusedHours: Double,
    subjectsCount: Int,
) {
    val spacing = OrbitSpacing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = OrbitColors.SurfaceRaised,
                shape = OrbitShapes.contentPanel,
            )
            .border(
                width = 0.5.dp,
                color = OrbitColors.BorderSubtle.copy(alpha = 0.5f),
                shape = OrbitShapes.contentPanel,
            )
            .padding(vertical = spacing.standard),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MetricDisplay(
            value = "${streak}d",
            label = "STREAK",
            valueColor = OrbitColors.ChartOrange,
        )
        TechnicalVerticalDivider(height = 32.dp)
        MetricDisplay(
            value = String.format("%.1fh", focusedHours),
            label = "FOCUSED",
            valueColor = OrbitColors.PrimaryElectricBlue,
        )
        TechnicalVerticalDivider(height = 32.dp)
        MetricDisplay(
            value = "$subjectsCount",
            label = "SUBJECTS",
            valueColor = OrbitColors.ChartGreen,
        )
    }
}

// ── Section: Achievements Grid ──────────────────────────────────────────────────

@Composable
private fun AchievementsGrid(
    achievements: List<Achievement>,
) {
    val spacing = OrbitSpacing
    val chunked = achievements.chunked(2)

    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.compact),
    ) {
        chunked.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.compact),
            ) {
                rowItems.forEach { achievement ->
                    AchievementCard(
                        achievement = achievement,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowItems.size < 2) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitSpacing
    val bgColor by animateColorAsState(
        targetValue = if (achievement.unlocked)
            OrbitColors.PrimaryElectricBlue.copy(alpha = 0.08f)
        else
            OrbitColors.SurfaceInteractive,
        animationSpec = tween(300),
        label = "achievementBg",
    )

    Column(
        modifier = modifier
            .background(bgColor, OrbitShapes.compactControl)
            .border(
                width = 0.5.dp,
                color = if (achievement.unlocked)
                    OrbitColors.PrimaryElectricBlue.copy(alpha = 0.2f)
                else
                    OrbitColors.BorderSubtle.copy(alpha = 0.3f),
                shape = OrbitShapes.compactControl,
            )
            .padding(spacing.compact),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = achievement.emoji,
                fontSize = 20.sp,
            )
            Spacer(Modifier.width(spacing.compact))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (achievement.unlocked)
                        OrbitColors.TextPrimary
                    else
                        OrbitColors.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = OrbitColors.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// ── Section: Subject Distribution (Bar Chart) ───────────────────────────────────

@Composable
private fun SubjectDistributionChart(
    subjects: List<SubjectHourData>,
    modifier: Modifier = Modifier,
) {
    val maxHours = subjects.maxOfOrNull { it.hours } ?: 1f

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800),
        label = "subjectChartReveal",
    )

    Canvas(modifier = modifier) {
        val chartLeft = 100.dp.toPx()
        val chartRight = size.width - 16.dp.toPx()
        val chartWidth = chartRight - chartLeft
        val barHeight = 18.dp.toPx()
        val gap = 12.dp.toPx()
        val totalHeight = subjects.size * (barHeight + gap) - gap
        val startY = (size.height - totalHeight) / 2f

        // Vertical reference line
        drawLine(
            color = OrbitColors.BorderSubtle.copy(alpha = 0.3f),
            start = Offset(chartLeft - 6.dp.toPx(), startY),
            end = Offset(chartLeft - 6.dp.toPx(), startY + totalHeight),
            strokeWidth = 1f,
        )

        subjects.forEachIndexed { index, subject ->
            val y = startY + index * (barHeight + gap)
            val barWidth = (subject.hours / maxHours) * chartWidth * animatedProgress

            // Subject label (right-aligned text before bar)
            val labelPaint = android.graphics.Paint().apply {
                color = OrbitColors.TextSecondary.toArgb()
                textSize = 10.sp.toPx()
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.RIGHT
            }
            drawContext.canvas.nativeCanvas.drawText(
                subject.subject,
                chartLeft - 8.dp.toPx(),
                y + barHeight * 0.75f,
                labelPaint,
            )

            // Bar
            drawRoundRect(
                color = subject.color,
                topLeft = Offset(chartLeft, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx()),
            )

            // Hour value (left-aligned text after bar)
            val valuePaint = android.graphics.Paint().apply {
                color = OrbitColors.TextMuted.toArgb()
                textSize = 10.sp.toPx()
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.LEFT
            }
            drawContext.canvas.nativeCanvas.drawText(
                String.format("%.1fh", subject.hours),
                chartLeft + barWidth + 6.dp.toPx(),
                y + barHeight * 0.75f,
                valuePaint,
            )
        }
    }
}

// ── Section: Weekly Stats (Column Chart) ────────────────────────────────────────

@Composable
private fun WeeklyStatsChart(
    stats: List<WeekDayStat>,
    modifier: Modifier = Modifier,
) {
    val maxHours = stats.maxOfOrNull { it.hours } ?: 1f

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800),
        label = "weeklyChartReveal",
    )

    Canvas(modifier = modifier) {
        val chartBottom = size.height - 24.dp.toPx()
        val chartTop = 8.dp.toPx()
        val chartHeight = chartBottom - chartTop
        val barCount = stats.size
        val totalGap = 12.dp.toPx() * (barCount - 1)
        val barWidth = (size.width - totalGap - 32.dp.toPx()) / barCount
        val gap = 12.dp.toPx()
        val startX = 16.dp.toPx()

        // Horizontal reference lines
        val referenceFractions = listOf(0.25f, 0.5f, 0.75f)
        referenceFractions.forEach { fraction ->
            val y = chartBottom - chartHeight * fraction
            drawLine(
                color = OrbitColors.BorderSubtle.copy(alpha = 0.15f),
                start = Offset(startX, y),
                end = Offset(size.width - 16.dp.toPx(), y),
                strokeWidth = 1f,
            )
        }

        stats.forEachIndexed { index, stat ->
            val x = startX + index * (barWidth + gap)
            val barHeight = (stat.hours / maxHours) * chartHeight * animatedProgress
            val y = chartBottom - barHeight

            // Column bar
            drawRoundRect(
                color = OrbitColors.PrimaryElectricBlue.copy(alpha = 0.85f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(3.dp.toPx()),
            )

            // Day label
            val labelPaint = android.graphics.Paint().apply {
                color = OrbitColors.TextMuted.toArgb()
                textSize = 10.sp.toPx()
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
            }
            drawContext.canvas.nativeCanvas.drawText(
                stat.day,
                x + barWidth / 2f,
                chartBottom + 16.dp.toPx(),
                labelPaint,
            )

            // Hour value above bar
            val valuePaint = android.graphics.Paint().apply {
                color = OrbitColors.TextSecondary.toArgb()
                textSize = 10.sp.toPx()
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
            }
            drawContext.canvas.nativeCanvas.drawText(
                String.format("%.1f", stat.hours),
                x + barWidth / 2f,
                y - 4.dp.toPx(),
                valuePaint,
            )
        }
    }
}

// ── Section: Settings ───────────────────────────────────────────────────────────

@Composable
private fun SettingsSection(
    darkThemeEnabled: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    hapticEnabled: Boolean,
    onHapticChange: (Boolean) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    privacyEnabled: Boolean,
    onPrivacyChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = OrbitColors.SurfaceRaised,
                shape = OrbitShapes.contentPanel,
            )
            .border(
                width = 0.5.dp,
                color = OrbitColors.BorderSubtle.copy(alpha = 0.5f),
                shape = OrbitShapes.contentPanel,
            )
            .padding(horizontal = OrbitSpacing.standard),
    ) {
        SettingsToggleRow(
            icon = Icons.Rounded.DarkMode,
            title = "Dark Theme",
            subtitle = "Use dark color scheme",
            checked = darkThemeEnabled,
            onCheckedChange = onDarkThemeChange,
        )
        TechnicalDivider(showAccent = false, thickness = 0.5.dp)
        SettingsToggleRow(
            icon = Icons.Rounded.Vibration,
            title = "Haptic Feedback",
            subtitle = "Vibration on interactions",
            checked = hapticEnabled,
            onCheckedChange = onHapticChange,
        )
        TechnicalDivider(showAccent = false, thickness = 0.5.dp)
        SettingsToggleRow(
            icon = Icons.Rounded.Notifications,
            title = "Notifications",
            subtitle = "Push and in-app alerts",
            checked = notificationsEnabled,
            onCheckedChange = onNotificationsChange,
        )
        TechnicalDivider(showAccent = false, thickness = 0.5.dp)
        SettingsToggleRow(
            icon = Icons.Rounded.Lock,
            title = "Privacy Mode",
            subtitle = "Hide sensitive data on screen",
            checked = privacyEnabled,
            onCheckedChange = onPrivacyChange,
        )
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val spacing = OrbitSpacing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = spacing.compact),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (checked) OrbitColors.PrimaryElectricBlue else OrbitColors.TextMuted,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(spacing.standard))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = OrbitColors.TextPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = OrbitColors.TextMuted,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = OrbitColors.PrimaryElectricBlue,
                checkedTrackColor = OrbitColors.PrimaryElectricBlue.copy(alpha = 0.3f),
                uncheckedThumbColor = OrbitColors.TextMuted,
                uncheckedTrackColor = OrbitColors.SurfaceInteractive,
            ),
        )
    }
}

// ── Shared helpers ──────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(
    title: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = OrbitColors.TextPrimary,
    )
}

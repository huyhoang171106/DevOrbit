package vn.edu.uit.devorbit.admin.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.data.remote.dto.ReviewSummary
import vn.edu.uit.devorbit.admin.data.remote.dto.StudentSummary
import vn.edu.uit.devorbit.admin.data.remote.dto.SubmissionSummary
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianPalette
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianShape
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianType
import vn.edu.uit.devorbit.admin.ui.theme.submissionStatusLabel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // ── Page Header ───────────────────────────────────────────────
        item {
            ObsidianPageHeader(
                title = "Tổng quan",
                subtitle = "Bảng điều khiển quản trị",
                actions = {
                    IconButton(onClick = viewModel::loadStats) {
                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = "Làm mới",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }

        item { ObsidianDivider() }

        // ── Loading state ─────────────────────────────────────────────
        if (state.isLoading && state.stats == null) {
            item { ObsidianLoadingBox(modifier = Modifier.height(200.dp)) }
        }

        // ── Error state ───────────────────────────────────────────────
        if (state.error != null && state.stats == null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Rounded.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = state.error.orEmpty(),
                        style = ObsidianType.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = viewModel::loadStats) {
                        Text("Thử lại", style = ObsidianType.labelMedium)
                    }
                }
            }
        }

        // ── KPI Cards ─────────────────────────────────────────────────
        state.stats?.let { stats ->
            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ObsidianStatCard(
                        label = "Sinh viên",
                        value = stats.totalStudents.toString(),
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.People
                    )
                    ObsidianStatCard(
                        label = "Môn học",
                        value = stats.totalCourses.toString(),
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.School,
                        accentColor = ObsidianPalette.Amber500
                    )
                }
            }
            item {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ObsidianStatCard(
                        label = "Kho lưu trữ",
                        value = stats.totalRepos.toString(),
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.Inventory2,
                        accentColor = ObsidianPalette.Green500
                    )
                    ObsidianStatCard(
                        label = "Chờ duyệt",
                        value = stats.pendingCandidates.toString(),
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.HourglassTop,
                        accentColor = if (stats.pendingCandidates > 0)
                            ObsidianPalette.Red500 else ObsidianPalette.Green500
                    )
                }
            }

            // ── Recent Students ───────────────────────────────────────
            if (stats.recentStudents.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    ObsidianSectionHeader(title = "Sinh viên mới nhất")
                }
                items(stats.recentStudents.take(5), key = { "student_${it.id}" }) { student ->
                    StudentRow(student)
                }
            }

            // ── Recent Reviews ─────────────────────────────────────────
            if (stats.recentCourseReviews.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    ObsidianSectionHeader(title = "Đánh giá gần đây")
                }
                items(stats.recentCourseReviews.take(5), key = { "review_${it.id}" }) { review ->
                    ReviewRow(review)
                }
            }

            // ── Recent Submissions ─────────────────────────────────────
            if (stats.recentSubmissions.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    ObsidianSectionHeader(title = "Bài nộp gần đây")
                }
                items(stats.recentSubmissions.take(5), key = { "submission_${it.id}" }) { submission ->
                    SubmissionRow(submission)
                }
            }
        }

        // ── Bottom spacing ────────────────────────────────────────────
        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// PRIVATE ITEM COMPOSABLES
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun StudentRow(student: StudentSummary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ObsidianAvatar(name = student.fullName, size = 36)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = student.fullName,
                style = ObsidianType.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = student.studentCode,
                style = ObsidianType.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ReviewRow(review: ReviewSummary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        ObsidianAvatar(name = review.studentName, size = 36)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = review.studentName,
                    style = ObsidianType.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                ObsidianBadge(
                    text = "${review.rating}/5",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = review.courseName,
                style = ObsidianType.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!review.comment.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = review.comment,
                    style = ObsidianType.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SubmissionRow(submission: SubmissionSummary) {
    val statusColor = when (submission.status.lowercase()) {
        "approved", "accepted", "pass" -> ObsidianPalette.Green500
        "rejected", "failed", "declined" -> ObsidianPalette.Red500
        "pending", "reviewing" -> ObsidianPalette.Amber500
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(ObsidianShape.sm)
                .background(statusColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.Upload,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = submission.courseName,
                style = ObsidianType.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = submission.githubUrl,
                style = ObsidianType.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(8.dp))
        ObsidianBadge(
            text = submissionStatusLabel(submission.status),
            color = statusColor
        )
    }
}

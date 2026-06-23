package vn.edu.uit.devorbit.admin.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import vn.edu.uit.devorbit.admin.ui.components.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToStudents: (() -> Unit)? = null,
    onNavigateToCourses: (() -> Unit)? = null,
    onNavigateToCandidates: (() -> Unit)? = null
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { PageTitle("Tổng quan") }

        if (state.isLoading && state.stats == null) {
            item { LoadingBox() }
        }

        state.stats?.let { stats ->
            // Top row
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Sinh viên", stats.totalStudents.toString(),
                        Modifier.weight(1f), Color(0xFF5B9BD5))
                    StatCard("Môn học", stats.totalCourses.toString(),
                        Modifier.weight(1f), Color(0xFFFFB347))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Kho", stats.totalRepos.toString(),
                        Modifier.weight(1f), Color(0xFF7BC67E))
                    StatCard("Chờ duyệt", stats.pendingCandidates.toString(),
                        Modifier.weight(1f),
                        if (stats.pendingCandidates > 0) Color(0xFFE57373) else Color(0xFF7BC67E))
                }
            }

            // Top repos
            if (stats.topFavoritedRepos.isNotEmpty()) {
                item { SectionHeader("Yêu thích nhất") }
                items(stats.topFavoritedRepos.take(3)) { repo ->
                    RepoStatItem(repo = repo, label = "${repo.count} bookmarks")
                }
            }
            if (stats.topViewedRepos.isNotEmpty()) {
                item { SectionHeader("Xem nhiều nhất") }
                items(stats.topViewedRepos.take(3)) { repo ->
                    RepoStatItem(repo = repo, label = "${repo.count} views")
                }
            }

            // Recent students
            if (stats.recentStudents.isNotEmpty()) {
                item { SectionHeader("Sinh viên mới") }
                items(stats.recentStudents.take(5)) { s ->
                    CompactStudentCard(s, onClick = onNavigateToStudents)
                }
            }

            // Recent reviews
            if (stats.recentCourseReviews.isNotEmpty()) {
                item { SectionHeader("Đánh giá gần đây") }
                items(stats.recentCourseReviews.take(5)) { r ->
                    CompactReviewCard(r)
                }
            }

            // Recent submissions
            if (stats.recentSubmissions.isNotEmpty()) {
                item { SectionHeader("Submission gần đây") }
                items(stats.recentSubmissions.take(5)) { sub ->
                    SubmissionCard(sub)
                }
            }
        }

        if (state.error != null) {
            item {
                Text("Lỗi: ${state.error}", color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
private fun RepoStatItem(repo: RepoStatsEntry, label: String) {
    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.small) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(repo.repoName, fontWeight = FontWeight.Medium)
                repo.description?.let { Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 1) }
            }
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun CompactStudentCard(s: StudentSummary, onClick: (() -> Unit)?) {
    ElevatedCard(modifier = Modifier
        .fillMaxWidth().padding(horizontal = 16.dp)
        .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = MaterialTheme.shapes.small) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(s.fullName, fontWeight = FontWeight.Medium)
                Text(s.studentCode, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!s.active) Text("Vô hiệu", color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun CompactReviewCard(r: ReviewSummary) {
    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.small) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text(r.studentName, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Text("${r.rating}/5", color = MaterialTheme.colorScheme.primary)
            }
            r.comment?.let { Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 2) }
        }
    }
}

@Composable
private fun SubmissionCard(sub: SubmissionSummary) {
    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.small) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(sub.repoName, fontWeight = FontWeight.Medium)
                Text("SV: ${sub.studentName}", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

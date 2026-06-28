package vn.edu.uit.devorbit.mobile.ui.screen.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.domain.model.GraphNode
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.ui.CourseDetailScreen
import vn.edu.uit.devorbit.mobile.ui.RepoDetailScreen
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseHubScreen(
    viewModel: CourseViewModel = hiltViewModel(),
    onCreatePlan: () -> Unit = {},
    pendingCourseId: Long? = null,
    pendingRepoId: Long? = null,
    onPendingCleared: () -> Unit = {}
) {
    var selectedTutorial by remember { mutableStateOf<vn.edu.uit.devorbit.mobile.data.remote.dto.CourseTutorial?>(null) }
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val selectedCourse by viewModel.selectedCourse.collectAsStateWithLifecycle()
    val selectedRepo by viewModel.selectedRepo.collectAsStateWithLifecycle()
    val repos by viewModel.detailRepos.collectAsStateWithLifecycle()
    val tutorials by viewModel.detailTutorials.collectAsStateWithLifecycle()
    val videos by viewModel.detailVideos.collectAsStateWithLifecycle()
    val articles by viewModel.detailArticles.collectAsStateWithLifecycle()
    val detailLoading by viewModel.detailLoading.collectAsStateWithLifecycle()
    val detailError by viewModel.detailError.collectAsStateWithLifecycle()
    val bookmarkedCourseIds by viewModel.bookmarkedCourseIds.collectAsStateWithLifecycle()
    val bookmarkedRepoIds by viewModel.bookmarkedRepoIds.collectAsStateWithLifecycle()
    val repoSummary by viewModel.repoSummary.collectAsStateWithLifecycle()
    val repoAdvice by viewModel.repoAdvice.collectAsStateWithLifecycle()
    val aiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()
    val repoSocialInfo by viewModel.repoSocialInfo.collectAsStateWithLifecycle()
    val userVote by viewModel.userVote.collectAsStateWithLifecycle()
    val socialLoading by viewModel.socialLoading.collectAsStateWithLifecycle()

    // Handle deep-linking from bookmarks
    var processedCourseId by remember { mutableStateOf<Long?>(null) }
    var processedRepoId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(pendingCourseId) {
        if (pendingCourseId != null && pendingCourseId != processedCourseId) {
            processedCourseId = pendingCourseId
            val course = courses.find { it.id == pendingCourseId }
            if (course != null) viewModel.openCourse(course)
            onPendingCleared()
        }
    }

    LaunchedEffect(pendingRepoId) {
        if (pendingRepoId != null && pendingRepoId != processedRepoId) {
            processedRepoId = pendingRepoId
            // Try to find repo in currently loaded repos
            val existing = viewModel.detailRepos.value.find { it.id == pendingRepoId }
            if (existing != null && selectedCourse != null) {
                viewModel.openRepo(existing)
                onPendingCleared()
            } else {
                // Fetch the repo to get its courseId
                viewModel.navigateToRepoFromBookmark(pendingRepoId)
            }
        }
    }

    when {
        selectedRepo != null -> RepoDetailScreen(
            repo = selectedRepo!!,
            aiSummary = repoSummary,
            aiAdvice = repoAdvice,
            aiLoading = aiLoading,
            socialInfo = repoSocialInfo,
            userVote = userVote,
            socialLoading = socialLoading,
            isBookmarked = selectedRepo!!.id in bookmarkedRepoIds,
            onVote = { viewModel.voteRepo(selectedRepo!!.id, it) },
            onBookmark = { viewModel.toggleRepoBookmark(selectedRepo!!) },
            onSubmitReview = { rating, comment -> viewModel.submitReview(selectedRepo!!.id, rating, comment) },
            onBack = { viewModel.backFromRepo() }
        )

        selectedCourse != null -> {
            if (detailLoading) {
                CourseDetailLoading(
                    courseName = selectedCourse!!.tenMH,
                    onBack = { viewModel.closeCourseDetail() }
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    detailError?.let { error ->
                        Text(
                            text = error,
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.supernova,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    CourseDetailScreen(
                        course = selectedCourse!!,
                        repos = repos,
                        bookmarked = selectedCourse!!.id in bookmarkedCourseIds,
                        bookmarkedRepoIds = bookmarkedRepoIds,
                        onBack = { viewModel.closeCourseDetail() },
                        onBookmarkClick = { viewModel.toggleCourseBookmark(selectedCourse!!) },
                        onRepoClick = { viewModel.openRepo(it) },
                        onCreatePlan = onCreatePlan
                    )
                }
            }
        }

        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)) {
                    Text(
                        text = "Môn học",
                        style = CosmicTheme.typography.display,
                        color = CosmicTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                CourseListScreen(viewModel = viewModel, onCourseClick = { viewModel.openCourse(it) })
            }
        }
    }
}

@Composable
private fun CourseDetailLoading(
    courseName: String,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp)) {
            Text("← Môn học", color = CosmicTheme.colors.textSecondary)
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = CosmicTheme.colors.plasma,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = courseName,
                    style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = CosmicTheme.colors.textPrimary
                )
                Text(
                    text = "Đang tải tài nguyên",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textTertiary
                )
            }
        }
    }
}

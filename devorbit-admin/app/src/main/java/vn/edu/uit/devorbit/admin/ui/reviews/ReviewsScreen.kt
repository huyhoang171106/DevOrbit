package vn.edu.uit.devorbit.admin.ui.reviews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.*

@Composable
fun ReviewsScreen(
    viewModel: ReviewsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tabs = listOf("Môn học", "Kho")
    var deleteReviewId by remember { mutableStateOf<Pair<Long, Boolean>?>(null) } // (id, isCourse)
    var searchQuery by remember { mutableStateOf("") }
    val filteredCourseReviews = state.courseReviews.filter {
        searchQuery.isBlank() ||
        it.studentName.contains(searchQuery, ignoreCase = true) ||
        it.courseName.contains(searchQuery, ignoreCase = true)
    }
    val filteredRepoReviews = state.repoReviews.filter {
        searchQuery.isBlank() ||
        it.studentName.contains(searchQuery, ignoreCase = true) ||
        it.repoName.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ObsidianPageHeader(
            title = "Đánh giá",
            subtitle = "${state.courseReviews.size + state.repoReviews.size} đánh giá"
        )

        ObsidianDivider()

        // Tabs
        TabRow(
            selectedTabIndex = state.selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                if (state.selectedTab < tabPositions.size) {
                    Box(
                        Modifier
                            .tabIndicatorOffset(tabPositions[state.selectedTab])
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    )
                }
            },
            divider = {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = state.selectedTab == index,
                    onClick = { viewModel.selectTab(index) },
                    text = {
                        Text(
                            text = title,
                            style = ObsidianType.labelMedium,
                            fontWeight = if (state.selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                )
            }
        }
        ObsidianSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = "Tìm theo tên sinh viên, môn học hoặc kho..."
        )

        if (state.isLoading) {
            ObsidianLoadingBox()
        } else if (state.error != null) {
            ObsidianEmptyState(
                message = "Lỗi tải dữ liệu",
                subtitle = state.error,
                icon = Icons.Rounded.ErrorOutline
            )
        } else {
            when (state.selectedTab) {
                0 -> CourseReviewsList(
                    reviews = filteredCourseReviews,
                    onDelete = { id -> deleteReviewId = id to true }
                )
                1 -> RepoReviewsList(
                    reviews = filteredRepoReviews,
                    onDelete = { id -> deleteReviewId = id to false }
                )
            }
        }
    }

    // Delete confirm dialog
    deleteReviewId?.let { (id, isCourse) ->
        ObsidianConfirmDialog(
            title = "Xoá đánh giá",
            message = "Bạn có chắc muốn xoá đánh giá này? Hành động này không thể hoàn tác.",
            confirmLabel = "Xoá",
            isDestructive = true,
            onConfirm = {
                if (isCourse) viewModel.deleteCourseReview(id)
                else viewModel.deleteRepoReview(id)
                deleteReviewId = null
            },
            onDismiss = { deleteReviewId = null }
        )
    }
}

// ── Course Reviews List ────────────────────────────────────────────────────
@Composable
private fun CourseReviewsList(
    reviews: List<CourseReviewAdminResponse>,
    onDelete: (Long) -> Unit
) {
    if (reviews.isEmpty()) {
        ObsidianEmptyState(
            message = "Không có đánh giá môn học",
            icon = Icons.Rounded.RateReview
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reviews, key = { it.id }) { review ->
                ReviewCard(
                    studentName = review.studentName,
                    targetName = review.courseName,
                    targetLabel = "Môn học",
                    rating = review.rating,
                    comment = review.comment,
                    createdAt = review.createdAt,
                    onDelete = { onDelete(review.id) }
                )
            }
        }
    }
}

// ── Repo Reviews List ─────────────────────────────────────────────────────
@Composable
private fun RepoReviewsList(
    reviews: List<RepoReviewAdminResponse>,
    onDelete: (Long) -> Unit
) {
    if (reviews.isEmpty()) {
        ObsidianEmptyState(
            message = "Không có đánh giá kho lưu trữ",
            icon = Icons.Rounded.RateReview
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reviews, key = { it.id }) { review ->
                ReviewCard(
                    studentName = review.studentName,
                    targetName = review.repoName,
                    targetLabel = "Kho lưu trữ",
                    rating = review.rating,
                    comment = review.comment,
                    createdAt = review.createdAt,
                    onDelete = { onDelete(review.id) }
                )
            }
        }
    }
}

// ── Review Card ────────────────────────────────────────────────────────────
@Composable
private fun ReviewCard(
    studentName: String,
    targetName: String,
    targetLabel: String,
    rating: Int?,
    comment: String?,
    createdAt: String?,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ObsidianShape.md,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header: student avatar + name + rating badge + date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ObsidianAvatar(name = studentName)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = studentName,
                        style = ObsidianType.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$targetLabel: $targetName",
                        style = ObsidianType.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                rating?.let { r ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < r) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                                contentDescription = null,
                                tint = if (index < r) ObsidianPalette.Amber500 else ObsidianPalette.Gray300,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "$r/5",
                            style = ObsidianType.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                createdAt?.let {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = it.take(10),
                        style = ObsidianType.labelSmall,
                        color = ObsidianPalette.Gray500
                    )
                }
            }
            // Comment
            comment?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it,
                    style = ObsidianType.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(6.dp))
            ObsidianDivider()

            // Delete action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = ObsidianPalette.Red500
                    )
                ) {
                    Icon(Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    ObsidianButtonText("Xoá", style = ObsidianType.labelMedium)
                }
            }
        }
    }
}

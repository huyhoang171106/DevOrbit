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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.*

@Composable
fun ReviewsScreen(
    viewModel: ReviewsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tabs = listOf("Đánh giá môn học", "Đánh giá kho")
    var deleteTarget by remember { mutableStateOf<Pair<Long, Boolean>?>(null) } // (id, isCourse)

    Column(modifier = Modifier.fillMaxSize()) {
        // ── Page Header ───────────────────────────────────────────────────
        ObsidianPageHeader(
            title = "Đánh giá",
            subtitle = when {
                state.isLoading -> null
                else -> "${state.courseReviews.size + state.repoReviews.size} đánh giá"
            }
        )

        ObsidianDivider()

        // ── Segmented filter ──────────────────────────────────────────────
        SegmentedFilter(
            options = tabs,
            selectedIndex = state.selectedTab,
            onSelect = { viewModel.selectTab(it) }
        )

        // ── Content ───────────────────────────────────────────────────────
        when {
            state.isLoading -> ObsidianLoadingBox(Modifier.weight(1f))
            state.error != null -> ObsidianEmptyState(
                message = "Lỗi tải dữ liệu",
                subtitle = state.error,
                icon = Icons.Rounded.ErrorOutline,
                modifier = Modifier.weight(1f)
            )
            else -> when (state.selectedTab) {
                0 -> CourseReviewsList(
                    reviews = state.courseReviews,
                    onDelete = { id -> deleteTarget = id to true }
                )
                1 -> RepoReviewsList(
                    reviews = state.repoReviews,
                    onDelete = { id -> deleteTarget = id to false }
                )
            }
        }
    }

    // ── Delete confirmation ─────────────────────────────────────────────
    deleteTarget?.let { (id, isCourse) ->
        ObsidianConfirmDialog(
            title = "Xoá đánh giá",
            message = "Bạn có chắc muốn xoá đánh giá này? Hành động này không thể hoàn tác.",
            confirmLabel = "Xoá",
            isDestructive = true,
            onConfirm = {
                if (isCourse) viewModel.deleteCourseReview(id)
                else viewModel.deleteRepoReview(id)
                deleteTarget = null
            },
            onDismiss = { deleteTarget = null }
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// SEGMENTED FILTER
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SegmentedFilter(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, label ->
            FilterChip(
                selected = selectedIndex == index,
                onClick = { onSelect(index) },
                label = {
                    Text(
                        label,
                        style = ObsidianType.labelMedium,
                        fontWeight = if (selectedIndex == index) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// COURSE REVIEWS LIST
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ColumnScope.CourseReviewsList(
    reviews: List<CourseReviewAdminResponse>,
    onDelete: (Long) -> Unit
) {
    if (reviews.isEmpty()) {
        ObsidianEmptyState(
            message = "Không có đánh giá môn học",
            icon = Icons.Rounded.RateReview,
            modifier = Modifier.weight(1f)
        )
    } else {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reviews, key = { it.id }) { review ->
                ReviewItemCard(
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

// ══════════════════════════════════════════════════════════════════════════════
// REPO REVIEWS LIST
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ColumnScope.RepoReviewsList(
    reviews: List<RepoReviewAdminResponse>,
    onDelete: (Long) -> Unit
) {
    if (reviews.isEmpty()) {
        ObsidianEmptyState(
            message = "Không có đánh giá kho lưu trữ",
            icon = Icons.Rounded.RateReview,
            modifier = Modifier.weight(1f)
        )
    } else {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reviews, key = { it.id }) { review ->
                ReviewItemCard(
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

// ══════════════════════════════════════════════════════════════════════════════
// REVIEW ITEM CARD
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ReviewItemCard(
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
        Column(modifier = Modifier.padding(16.dp)) {
            // ── Header: avatar + name + entity + timestamp ──────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ObsidianAvatar(name = studentName, size = 36)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = studentName,
                        style = ObsidianType.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ObsidianBadge(
                            text = targetLabel,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = targetName,
                            style = ObsidianType.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                createdAt?.let {
                    Text(
                        text = it.take(10),
                        style = ObsidianType.labelSmall,
                        color = ObsidianPalette.Gray500
                    )
                }
            }

            // ── Rating stars ────────────────────────────────────────────
            rating?.let { r ->
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < r) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                            contentDescription = null,
                            tint = if (index < r) ObsidianPalette.Amber500 else ObsidianPalette.Gray300,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "$r/5",
                        style = ObsidianType.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Comment preview ─────────────────────────────────────────
            comment?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    style = ObsidianType.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // ── Delete action ───────────────────────────────────────────
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 0.5.dp
            )
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
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Xoá", style = ObsidianType.labelMedium)
                }
            }
        }
    }
}

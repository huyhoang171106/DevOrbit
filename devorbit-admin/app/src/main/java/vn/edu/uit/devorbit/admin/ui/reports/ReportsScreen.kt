package vn.edu.uit.devorbit.admin.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
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
import vn.edu.uit.devorbit.admin.core.designsystem.*
import vn.edu.uit.devorbit.admin.data.remote.dto.RepoStatsEntry
import vn.edu.uit.devorbit.admin.ui.theme.*

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Yêu thích nhất", "Xem nhiều nhất")

    Column(Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    "Báo cáo",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Text(
                    "Kho lưu trữ nổi bật",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            IconButton(onClick = { viewModel.loadStats() }) {
                Icon(
                    Icons.Rounded.Refresh,
                    contentDescription = "Làm mới",
                    tint = TextSecondary,
                )
            }
        }

        // Segmented tab
        SegmentedFilter(
            options = tabs.mapIndexed { i, label -> FilterOption(i.toString(), label) },
            selectedId = selectedTab.toString(),
            onSelected = { selectedTab = it.toInt() },
        )
        Spacer(Modifier.height(8.dp))

        when {
            state.isLoading -> ListSkeleton(itemCount = 5)
            state.error != null -> ErrorState(
                title = "Không thể tải báo cáo",
                subtitle = state.error,
                onRetry = { viewModel.loadStats() },
            )
            else -> {
                val repos = if (selectedTab == 0) state.stats?.topFavoritedRepos.orEmpty() else state.stats?.topViewedRepos.orEmpty()
                if (repos.isEmpty()) {
                    EmptyState(
                        title = "Chưa có dữ liệu",
                        subtitle = "Chưa có kho lưu trữ nào được đánh giá.",
                        icon = Icons.Rounded.BarChart,
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(repos, key = { it.repoId }) { repo ->
                            RepoStatsCard(
                                repo = repo,
                                rank = repos.indexOf(repo) + 1,
                                metricLabel = if (selectedTab == 0) "Lượt yêu thích" else "Lượt xem",
                                metricValue = if (selectedTab == 0) repo.bookmarkCount.toString() else repo.viewCount.toString(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RepoStatsCard(
    repo: RepoStatsEntry,
    rank: Int,
    metricLabel: String,
    metricValue: String,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = Surface,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (rank <= 3) UITBlueSoft else SurfaceTertiary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (rank <= 3) UITBlue else TextSecondary,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = repo.repoName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (repo.courseName != null) {
                        Text(
                            text = repo.courseName,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    if (repo.primaryLanguage != null) {
                        StatusBadge(
                            label = repo.primaryLanguage,
                            type = StatusType.NEUTRAL,
                        )
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = metricValue,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = UITBlue,
                )
                Text(
                    text = metricLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                )
            }
        }
    }
}

package vn.edu.uit.devorbit.admin.ui.reports

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
import vn.edu.uit.devorbit.admin.data.remote.dto.RepoStatsEntry
import vn.edu.uit.devorbit.admin.ui.components.*
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianPalette
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianShape
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianType

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        ObsidianPageHeader(
            title = "Báo cáo",
            subtitle = "Top kho theo lượt yêu thích và lượt xem",
            actions = {
                IconButton(onClick = { viewModel.loadStats() }) {
                    Icon(
                        Icons.Rounded.Refresh,
                        contentDescription = "Làm mới",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        when {
            state.isLoading -> ObsidianLoadingBox(modifier = Modifier.weight(1f))
            state.error != null -> ObsidianEmptyState(
                message = state.error!!,
                icon = Icons.Rounded.Warning,
                modifier = Modifier.weight(1f)
            )
            state.stats != null -> {
                val stats = state.stats!!
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ── Top Favorited ───────────────────────────────────
                    item {
                        Text(
                            "Top yêu thích",
                            style = ObsidianType.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (stats.topFavoritedRepos.isEmpty()) {
                        item { ObsidianEmptyState(message = "Chưa có dữ liệu", icon = Icons.Rounded.FavoriteBorder) }
                    } else {
                        items(stats.topFavoritedRepos, key = { it.repoId }) { repo ->
                            RepoStatsCard(repo, rank = stats.topFavoritedRepos.indexOf(repo) + 1, metricLabel = "Yêu thích", metricValue = "${repo.bookmarkCount}")
                        }
                    }

                    // ── Top Viewed ──────────────────────────────────────
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Top lượt xem",
                            style = ObsidianType.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (stats.topViewedRepos.isEmpty()) {
                        item { ObsidianEmptyState(message = "Chưa có dữ liệu", icon = Icons.Rounded.Visibility) }
                    } else {
                        items(stats.topViewedRepos, key = { it.repoId }) { repo ->
                            RepoStatsCard(repo, rank = stats.topViewedRepos.indexOf(repo) + 1, metricLabel = "Lượt xem", metricValue = "${repo.viewCount}")
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
    metricValue: String
) {
    Card(
        shape = ObsidianShape.md,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge
            Surface(
                shape = ObsidianShape.sm,
                color = if (rank <= 3) ObsidianPalette.Amber500.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "#$rank",
                        style = ObsidianType.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (rank <= 3) ObsidianPalette.Amber500
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    repo.repoName,
                    style = ObsidianType.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repo.courseName?.let {
                        Text(it, style = ObsidianType.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    repo.primaryLanguage?.let {
                        ObsidianBadge(text = it)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(metricValue, style = ObsidianType.titleSmall, fontWeight = FontWeight.Bold, color = ObsidianPalette.Amber500)
                Text(metricLabel, style = ObsidianType.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

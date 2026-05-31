package vn.edu.uit.devorbit.mobile.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.data.remote.dto.*
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@Composable
fun CourseDetailScreen(
    courseName: String,
    repos: List<RepoSummary>,
    tutorials: List<CourseTutorial>,
    videos: List<CourseYoutubePlaylist>,
    articles: List<CourseArticle>,
    onBack: () -> Unit,
    onRepoClick: (RepoSummary) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Repos (${repos.size})", "Tutorials", "Videos", "Articles")
    val context = LocalContext.current
    var selectedTechStack by remember(repos) { mutableStateOf<String?>(null) }
    val repoFilterState = remember(repos, selectedTechStack) {
        RepoFilterState(repos = repos, selectedTechStack = selectedTechStack)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp)) {
            Text("← Môn học", color = CosmicTheme.colors.textSecondary)
        }

        Text(
            text = courseName,
            style = CosmicTheme.typography.display,
            color = CosmicTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(12.dp))

        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = CosmicTheme.colors.void,
            contentColor = CosmicTheme.colors.plasma,
            edgePadding = 16.dp,
            divider = { Divider(color = CosmicTheme.colors.glassBorder, thickness = 1.dp) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            title,
                            style = CosmicTheme.typography.label.copy(fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium),
                            color = if (selectedTabIndex == index) CosmicTheme.colors.plasma else CosmicTheme.colors.textTertiary
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> {
                    RepoListSection(
                        repos = repoFilterState.filteredRepos,
                        resultCount = repoFilterState.filteredRepos.size,
                        totalCount = repos.size,
                        availableTechStacks = repoFilterState.availableTechStacks,
                        selectedTechStack = repoFilterState.selectedTechStack,
                        onTechStackSelected = { stack ->
                            selectedTechStack = repoFilterState.selectTechStack(stack).selectedTechStack
                        },
                        onRepoClick = onRepoClick
                    )
                }
                1 -> KnowledgeList(tutorials) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
                }
                2 -> KnowledgeList(videos) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.playlistUrl)))
                }
                3 -> KnowledgeList(articles) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
                }
            }
        }
    }
}

@Composable
fun <T> KnowledgeList(items: List<T>, onClick: (T) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(item) },
                shape = RoundedCornerShape(14.dp),
                color = CosmicTheme.colors.nebula,
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val title = when (item) {
                        is CourseTutorial -> item.title
                        is CourseYoutubePlaylist -> item.title
                        is CourseArticle -> item.title
                        else -> ""
                    }
                    val subtitle = when (item) {
                        is CourseTutorial -> item.description ?: "Bài viết"
                        is CourseYoutubePlaylist -> "YouTube Playlist"
                        is CourseArticle -> "bởi ${item.author ?: "Không rõ"}"
                        else -> ""
                    }

                    Text(title, style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold), color = CosmicTheme.colors.textPrimary)
                    if (subtitle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(subtitle, style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                    }
                }
            }
        }
    }
}

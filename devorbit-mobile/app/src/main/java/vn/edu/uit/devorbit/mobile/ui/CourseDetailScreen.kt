package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import vn.edu.uit.devorbit.mobile.data.remote.dto.*
import vn.edu.uit.devorbit.mobile.ui.components.GlassCard

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
        TextButton(onClick = onBack) {
            Text("< Courses", color = Color.White)
        }

        Text(
            text = courseName,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(Modifier.height(16.dp))

        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = vn.edu.uit.devorbit.mobile.ui.theme.CosmicGlowPurple,
            edgePadding = 16.dp,
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, color = if (selectedTabIndex == index) vn.edu.uit.devorbit.mobile.ui.theme.CosmicGlowPurple else Color.White.copy(alpha = 0.6f)) }
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
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
                    context.startActivity(intent)
                }
                2 -> KnowledgeList(videos) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.playlistUrl))
                    context.startActivity(intent)
                }
                3 -> KnowledgeList(articles) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun <T> KnowledgeList(items: List<T>, onClick: (T) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(item) }
            ) {
                val title = when (item) {
                    is CourseTutorial -> item.title
                    is CourseYoutubePlaylist -> item.title
                    is CourseArticle -> item.title
                    else -> ""
                }
                val subtitle = when (item) {
                    is CourseTutorial -> item.description ?: "Article"
                    is CourseYoutubePlaylist -> "YouTube Playlist"
                    is CourseArticle -> "by ${item.author ?: "Unknown"}"
                    else -> ""
                }
                
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                }
            }
        }
    }
}

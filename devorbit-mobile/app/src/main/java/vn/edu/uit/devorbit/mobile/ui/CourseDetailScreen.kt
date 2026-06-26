package vn.edu.uit.devorbit.mobile.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.data.remote.dto.CourseArticle
import vn.edu.uit.devorbit.mobile.data.remote.dto.CourseTutorial
import vn.edu.uit.devorbit.mobile.data.remote.dto.CourseYoutubePlaylist
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@Composable
fun CourseDetailScreen(
    course: CourseEntity,
    repos: List<RepoSummary>,
    tutorials: List<CourseTutorial>,
    videos: List<CourseYoutubePlaylist>,
    articles: List<CourseArticle>,
    bookmarked: Boolean,
    bookmarkedRepoIds: Set<Long> = emptySet(),
    onBack: () -> Unit,
    onBookmarkClick: () -> Unit,
    onRepoClick: (RepoSummary) -> Unit,
    onCreatePlan: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var descExpanded by remember { mutableStateOf(false) }
    val tabs = listOf("Repos (${repos.size})", "Tutorials", "Videos", "Articles")
    val context = LocalContext.current
    var selectedTechStack by remember(repos) { mutableStateOf<String?>(null) }
    val repoFilterState = remember(repos, selectedTechStack) {
        RepoFilterState(repos = repos, selectedTechStack = selectedTechStack)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Fixed header
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("< Mon hoc", color = CosmicTheme.colors.textSecondary) }
            IconButton(onClick = onBookmarkClick) {
                Icon(Icons.Rounded.Star, contentDescription = "Luu mon hoc",
                    tint = if (bookmarked) CosmicTheme.colors.plasma else CosmicTheme.colors.textTertiary)
            }
        }

        // Course name
        Text(course.tenMH, style = CosmicTheme.typography.display, color = CosmicTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 16.dp))

        // Description (chỉ hiện ở tab Repos, nằm dưới tên môn, scroll riêng với tab content)
        if (selectedTabIndex == 0 && !course.description.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = CosmicTheme.colors.nebula,
                border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = CosmicTheme.colors.plasma, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Tổng quan", style = CosmicTheme.typography.label.copy(fontWeight = FontWeight.Bold), color = CosmicTheme.colors.textPrimary)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = course.description.cleanFormatting(),
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textSecondary,
                        maxLines = if (descExpanded) Int.MAX_VALUE else 3,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    if (!descExpanded) {
                        Spacer(Modifier.height(2.dp))
                        TextButton(onClick = { descExpanded = true }, contentPadding = PaddingValues(0.dp)) {
                            Text("Xem thêm", style = CosmicTheme.typography.label, color = CosmicTheme.colors.plasma)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Tabs
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
                    text = { Text(title, style = CosmicTheme.typography.label.copy(
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium),
                        color = if (selectedTabIndex == index) CosmicTheme.colors.plasma else CosmicTheme.colors.textTertiary) }
                )
            }
        }

        // Tab content (scrollable independently)
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (selectedTabIndex) {
                0 -> {
                    val filtered = repoFilterState.filteredRepos
                    if (filtered.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Không có repository nào", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filtered, key = { it.id }) { repo ->
                                RepoListItem(repo = repo, bookmarked = repo.id in bookmarkedRepoIds, onClick = { onRepoClick(repo) })
                            }
                        }
                    }
                }
                1 -> {
                    if (tutorials.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Chưa có hướng dẫn nào", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(tutorials, key = { it.id }) { tut ->
                                TutorialListItem(tutorial = tut, onClick = {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(tut.url)))
                                })
                            }
                        }
                    }
                }
                2 -> {
                    if (videos.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Chưa có video nào", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(videos, key = { it.id }) { vid ->
                                KnowledgeItem(title = vid.title, subtitle = "YouTube Playlist", onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(vid.playlistUrl))) })
                            }
                        }
                    }
                }
                3 -> {
                    if (articles.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Chưa có bài viết nào", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(articles, key = { it.id }) { art ->
                                KnowledgeItem(title = art.title, subtitle = "boi ${art.author ?: "Khong ro"}", onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(art.url))) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RepoListItem(repo: RepoSummary, bookmarked: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(repo.displayName.orEmpty(), style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = CosmicTheme.colors.textPrimary, modifier = Modifier.weight(1f))
                if (bookmarked) Icon(Icons.Rounded.Star, contentDescription = "Da luu", tint = CosmicTheme.colors.plasma, modifier = Modifier.size(16.dp))
            }
            if (!repo.description.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(repo.description, style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary, maxLines = 2)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 8.dp)) {
                if (!repo.primaryLanguage.isNullOrBlank()) {
                    Surface(shape = RoundedCornerShape(6.dp), color = CosmicTheme.colors.plasma.copy(alpha = 0.12f)) {
                        Text(repo.primaryLanguage, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = CosmicTheme.typography.label, color = CosmicTheme.colors.plasma)
                    }
                }
                repo.techStacks.filter { it.name != repo.primaryLanguage }.take(2).forEach { stack ->
                    if (!stack.name.isNullOrBlank()) {
                        Surface(shape = RoundedCornerShape(6.dp), color = CosmicTheme.colors.nebula, border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)) {
                            Text(stack.name, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary)
                        }
                    }
                }
                val avg = repo.averageRating
                val rCount = repo.reviewCount
                if (avg != null && avg > 0) {
                    Surface(shape = RoundedCornerShape(6.dp), color = CosmicTheme.colors.supernova.copy(alpha = 0.12f)) {
                        Text("\u2605 ${"%.1f".format(avg)}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = CosmicTheme.typography.label, color = CosmicTheme.colors.supernova)
                    }
                }
                if (rCount != null && rCount > 0) {
                    Surface(shape = RoundedCornerShape(6.dp), color = CosmicTheme.colors.plasma.copy(alpha = 0.12f)) {
                        Text("$rCount reviews", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = CosmicTheme.typography.label, color = CosmicTheme.colors.plasma)
                    }
                }
            }
        }
    }
}

@Composable
private fun TutorialListItem(tutorial: CourseTutorial, onClick: () -> Unit) {
    val typeLabel = when (tutorial.type) {
        "video" -> "Video"
        "lab" -> "Lab"
        "guide" -> "Hướng dẫn"
        "interactive" -> "Tương tác"
        else -> "Bài viết"
    }
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(tutorial.title, style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold), color = CosmicTheme.colors.textPrimary)
                if (!tutorial.description.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(tutorial.description, style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary, maxLines = 2)
                }
                Spacer(Modifier.height(6.dp))
                Surface(shape = RoundedCornerShape(6.dp), color = CosmicTheme.colors.plasma.copy(alpha = 0.12f)) {
                    Text(typeLabel, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = CosmicTheme.typography.label, color = CosmicTheme.colors.plasma)
                }
            }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Filled.OpenInNew, contentDescription = "Mở link", tint = CosmicTheme.colors.textTertiary, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun KnowledgeItem(title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold), color = CosmicTheme.colors.textPrimary)
            if (subtitle.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(subtitle, style = CosmicTheme.typography.label, color = CosmicTheme.colors.textTertiary)
            }
        }
    }
}

fun String.cleanFormatting(): String {
    return replace("\r\n", "\n")
        .replace(Regex("\n{3,}"), "\n\n")
        .split("\n\n")
        .joinToString("\n\n") { para ->
            para.replace("\n", " ")
                .replace(Regex("\\s+"), " ")
                .trim()
        }
}

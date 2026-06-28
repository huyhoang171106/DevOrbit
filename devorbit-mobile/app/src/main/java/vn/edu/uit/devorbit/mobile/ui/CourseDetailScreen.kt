package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@Composable
fun CourseDetailScreen(
    course: CourseEntity,
    repos: List<RepoSummary>,
    bookmarked: Boolean,
    bookmarkedRepoIds: Set<Long> = emptySet(),
    onBack: () -> Unit,
    onBookmarkClick: () -> Unit,
    onRepoClick: (RepoSummary) -> Unit,
    onCreatePlan: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var descExpanded by remember { mutableStateOf(false) }
    val tabs = listOf("Repos (${repos.size})")
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

@Composable
private fun RepoListItem(repo: RepoSummary, bookmarked: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = CosmicTheme.colors.nebula,
        border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (bookmarked) CosmicTheme.colors.plasma else CosmicTheme.colors.textTertiary.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = repo.displayName,
                        style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.Medium),
                        color = CosmicTheme.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (!repo.description.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = repo.description.cleanFormatting(),
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!repo.primaryLanguage.isNullOrBlank()) {
                        Surface(shape = RoundedCornerShape(6.dp), color = CosmicTheme.colors.plasma.copy(alpha = 0.08f)) {
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
                }
            }
            Icon(
                Icons.Filled.OpenInNew,
                contentDescription = null,
                tint = CosmicTheme.colors.textTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.model.RepoSummary

@Composable
fun CourseDetailScreen(
    courseName: String,
    repoCount: Int,
    repos: List<RepoSummary>,
    onBack: () -> Unit,
    onRepoClick: (RepoSummary) -> Unit
) {
    val allTechStacks = repos.flatMap { it.techStacks }.distinct().sorted()
    var selectedTechStack by remember { mutableStateOf<String?>(null) }

    val filteredRepos = if (selectedTechStack != null) {
        repos.filter { it.techStacks.contains(selectedTechStack) }
    } else repos

    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = onBack) {
            Text("< Courses")
        }

        Text(
            text = courseName,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = "$repoCount repositories",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp)
        )

        RepoFilterSheet(
            techStacks = allTechStacks,
            selectedTechStack = selectedTechStack,
            onTechStackSelected = { selectedTechStack = it }
        )

        RepoListSection(
            repos = filteredRepos,
            onRepoClick = onRepoClick
        )
    }
}

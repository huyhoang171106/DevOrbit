package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.model.RepoSummary

@Composable
fun RepoListSection(
    repos: List<RepoSummary>,
    onRepoClick: (RepoSummary) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(repos) { repo ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRepoClick(repo) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = repo.displayName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (repo.description.isNotBlank()) {
                        Text(
                            text = repo.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        if (repo.primaryLanguage.isNotBlank()) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text(repo.primaryLanguage) }
                            )
                        }
                        repo.techStacks.forEach { stack ->
                            AssistChip(
                                onClick = {},
                                label = { Text(stack) }
                            )
                        }
                    }
                }
            }
        }
    }
}

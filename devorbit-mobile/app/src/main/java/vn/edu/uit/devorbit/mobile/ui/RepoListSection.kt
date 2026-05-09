package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import vn.edu.uit.devorbit.mobile.model.*

import vn.edu.uit.devorbit.mobile.ui.components.GlassCard

@Composable
fun RepoListSection(
    repos: List<RepoSummary>,
    onRepoClick: (RepoSummary) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(repos) { repo ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRepoClick(repo) }
            ) {
                Column {
                    Text(
                        text = repo.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    if (repo.description.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = repo.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 2
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        if (repo.primaryLanguage.isNotBlank()) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text(repo.primaryLanguage) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    labelColor = Color.White
                                )
                            )
                        }
                        repo.techStacks.take(2).forEach { stack ->
                            AssistChip(
                                onClick = {},
                                label = { Text(stack.name) },
                                colors = AssistChipDefaults.assistChipColors(
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

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
import vn.edu.uit.devorbit.mobile.data.remote.dto.*

import vn.edu.uit.devorbit.mobile.ui.components.GlassCard
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@Composable
fun RepoListSection(
    repos: List<RepoSummary>,
    resultCount: Int = repos.size,
    totalCount: Int = repos.size,
    availableTechStacks: List<String> = emptyList(),
    selectedTechStack: String? = null,
    onTechStackSelected: (String?) -> Unit = {},
    onRepoClick: (RepoSummary) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            RepoFilterHeader(
                resultCount = resultCount,
                totalCount = totalCount,
                availableTechStacks = availableTechStacks,
                selectedTechStack = selectedTechStack,
                onTechStackSelected = onTechStackSelected
            )
        }

        if (repos.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text(
                            text = "No repositories found",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Try another tech stack filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }
                }
            }
        }

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

@Composable
private fun RepoFilterHeader(
    resultCount: Int,
    totalCount: Int,
    availableTechStacks: List<String>,
    selectedTechStack: String?,
    onTechStackSelected: (String?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Repositories",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = "$resultCount/$totalCount",
                style = MaterialTheme.typography.labelLarge,
                color = CosmicTheme.colors.textSecondary
            )
        }

        RepoFilterSheet(
            techStacks = availableTechStacks,
            selectedTechStack = selectedTechStack,
            onTechStackSelected = onTechStackSelected
        )
    }
}

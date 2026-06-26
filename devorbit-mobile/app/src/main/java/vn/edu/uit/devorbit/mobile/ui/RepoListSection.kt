package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.data.remote.dto.*
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
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = CosmicTheme.colors.nebula,
                    border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Không tìm thấy repository",
                            style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                            color = CosmicTheme.colors.textPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Thử bộ lọc tech stack khác.",
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.textTertiary
                        )
                    }
                }
            }
        }

        items(repos) { repo ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRepoClick(repo) },
                shape = RoundedCornerShape(14.dp),
                color = CosmicTheme.colors.nebula,
                border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = repo.displayName.orEmpty(),
                        style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = CosmicTheme.colors.textPrimary
                    )
                    if (!repo.description.isNullOrBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = repo.description,
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.textSecondary,
                            maxLines = 2
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        if (!repo.primaryLanguage.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CosmicTheme.colors.plasma.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = repo.primaryLanguage,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = CosmicTheme.typography.label,
                                    color = CosmicTheme.colors.plasma
                                )
                            }
                        }
                        repo.techStacks.filter { it.name != repo.primaryLanguage }.take(2).forEach { stack ->
                            if (!stack.name.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CosmicTheme.colors.nebula,
                                    border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                                ) {
                                    Text(
                                        text = stack.name,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = CosmicTheme.typography.label,
                                        color = CosmicTheme.colors.textSecondary
                                    )
                                }
                            }
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
                style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                color = CosmicTheme.colors.textPrimary
            )
            Text(
                text = "$resultCount/$totalCount",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textTertiary
            )
        }

        RepoFilterSheet(
            techStacks = availableTechStacks,
            selectedTechStack = selectedTechStack,
            onTechStackSelected = onTechStackSelected
        )
    }
}

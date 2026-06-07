package vn.edu.uit.devorbit.mobile.ui.screen.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.domain.repository.RecentRepo
import vn.edu.uit.devorbit.mobile.domain.repository.TechStackInfo
import vn.edu.uit.devorbit.mobile.domain.repository.TopStack
import vn.edu.uit.devorbit.mobile.ui.RepoDetailScreen
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.ExploreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(viewModel: ExploreViewModel = hiltViewModel()) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    uiState.selectedRepo?.let { repo ->
        RepoDetailScreen(repo = repo, onBack = { viewModel.closeRepo() })
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 96.dp)
    ) {
        item {
            Text(
                text = "Kham pha",
                style = CosmicTheme.typography.display,
                color = CosmicTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Kho tai nguyen hoc thuat tu sinh vien UIT",
                style = CosmicTheme.typography.body,
                color = CosmicTheme.colors.textSecondary
            )
        }

        item {
            OutlinedTextField(
                value = uiState.filterState.query,
                onValueChange = { viewModel.updateSearch(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Tim repo theo ten hoac mo ta...", color = CosmicTheme.colors.textTertiary) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = CosmicTheme.colors.textTertiary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CosmicTheme.colors.plasma,
                    unfocusedBorderColor = CosmicTheme.colors.glassBorder,
                    cursorColor = CosmicTheme.colors.plasma,
                    focusedTextColor = CosmicTheme.colors.textPrimary,
                    unfocusedTextColor = CosmicTheme.colors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        uiState.error?.let { message ->
            item {
                ErrorBanner(message = message, onRetry = { viewModel.load() })
            }
        }

        item {
            SectionTitle("Cong nghe pho bien")
        }
        item {
            if (uiState.topStacks.isNotEmpty()) {
                FlowRowCompat(
                    topStacks = uiState.topStacks.take(6),
                    selectedTechStack = uiState.filterState.selectedTechStack,
                    onTechStackClick = { viewModel.selectTechStack(it) },
                    accent = true
                )
            } else {
                LoadingLabel()
            }
        }

        item {
            SectionTitle("Tat ca tech stack")
        }
        item {
            if (uiState.techStacks.isNotEmpty()) {
                FlowRowCompat(
                    techStacks = uiState.techStacks.take(10),
                    selectedTechStack = uiState.filterState.selectedTechStack,
                    onTechStackClick = { viewModel.selectTechStack(it) },
                    accent = false
                )
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            SectionTitle("Repo moi cap nhat")
        }

        when {
            uiState.loading -> item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CosmicTheme.colors.plasma, strokeWidth = 2.dp)
                }
            }

            uiState.visibleRepos.isEmpty() -> item {
                Text(
                    "Khong co repo phu hop",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textTertiary
                )
            }

            else -> items(uiState.visibleRepos, key = { it.id }) { repo ->
                ExploreRepoCard(repo = repo, onClick = { viewModel.openRepo(repo) })
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = CosmicTheme.typography.command,
        color = CosmicTheme.colors.textTertiary
    )
}

@Composable
private fun LoadingLabel() {
    Text(
        "Dang tai du lieu...",
        style = CosmicTheme.typography.label,
        color = CosmicTheme.colors.textTertiary
    )
}

@Composable
private fun ErrorBanner(message: String, onRetry: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CosmicTheme.colors.supernova.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.supernova.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onRetry) {
                Text("Thu lai", color = CosmicTheme.colors.plasma)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExploreRepoCard(repo: RecentRepo, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = CosmicTheme.colors.nebula,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                repo.name,
                style = CosmicTheme.typography.body.copy(fontWeight = FontWeight.SemiBold),
                color = CosmicTheme.colors.textPrimary
            )
            if (repo.courseName != null) {
                Text(
                    repo.courseName,
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.plasma
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                repo.description,
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textSecondary,
                maxLines = 2
            )
            Spacer(Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (repo.language.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CosmicTheme.colors.plasma.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = repo.language,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.plasma
                        )
                    }
                }
                Text(
                    "${repo.stars} stars",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textTertiary
                )
            }
            if (repo.techStacks.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repo.techStacks.take(4).forEach { stack ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CosmicTheme.colors.glass,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                        ) {
                            Text(
                                text = stack,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowCompat(
    topStacks: List<TopStack> = emptyList(),
    techStacks: List<TechStackInfo> = emptyList(),
    selectedTechStack: String?,
    onTechStackClick: (String?) -> Unit,
    accent: Boolean
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (accent) {
            topStacks.forEach { stack ->
                SelectableStackChip(
                    label = "${stack.name} (${stack.count})",
                    selected = selectedTechStack == stack.name,
                    accent = true,
                    onClick = { onTechStackClick(stack.name) }
                )
            }
        } else {
            SelectableStackChip(
                label = "Tat ca",
                selected = selectedTechStack == null,
                accent = false,
                onClick = { onTechStackClick(null) }
            )
            techStacks.forEach { stack ->
                SelectableStackChip(
                    label = stack.name,
                    selected = selectedTechStack == stack.name,
                    accent = false,
                    onClick = { onTechStackClick(stack.name) }
                )
            }
        }
    }
}

@Composable
private fun SelectableStackChip(
    label: String,
    selected: Boolean,
    accent: Boolean,
    onClick: () -> Unit
) {
    val selectedColor = CosmicTheme.colors.plasma
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = when {
            selected -> selectedColor.copy(alpha = 0.16f)
            accent -> selectedColor.copy(alpha = 0.12f)
            else -> CosmicTheme.colors.nebula
        },
        border = if (selected || !accent) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                if (selected) selectedColor else CosmicTheme.colors.glassBorder
            )
        } else {
            null
        }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = CosmicTheme.typography.label,
            color = if (selected || accent) selectedColor else CosmicTheme.colors.textSecondary
        )
    }
}

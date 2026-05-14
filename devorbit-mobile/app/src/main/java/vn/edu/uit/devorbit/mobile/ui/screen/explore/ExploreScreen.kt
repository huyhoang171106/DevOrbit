package vn.edu.uit.devorbit.mobile.ui.screen.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.ExploreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(viewModel: ExploreViewModel = hiltViewModel()) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        item {
            Text("Khám phá", style = CosmicTheme.typography.display, color = CosmicTheme.colors.plasma)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Kho tài nguyên học thuật từ sinh viên UIT",
                style = CosmicTheme.typography.label,
                color = CosmicTheme.colors.textSecondary
            )
        }

        // Top Tech Stacks
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Công nghệ phổ biến", style = CosmicTheme.typography.command, color = CosmicTheme.colors.textPrimary)
        }
        item {
            if (uiState.topStacks.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.topStacks.take(4).forEach { stack ->
                        AssistChip(
                            onClick = {},
                            label = { Text("${stack.name} (${stack.count})") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = CosmicTheme.colors.glass,
                                labelColor = CosmicTheme.colors.plasma
                            )
                        )
                    }
                }
            } else {
                Text("Đang tải...", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary)
            }
        }

        // Tech Stacks Browser
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tất cả tech stack", style = CosmicTheme.typography.command, color = CosmicTheme.colors.textPrimary)
        }
        item {
            if (uiState.techStacks.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.techStacks.take(8).forEach { stack ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(stack.name, style = CosmicTheme.typography.label) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = CosmicTheme.colors.glass,
                                labelColor = CosmicTheme.colors.textPrimary
                            )
                        )
                    }
                }
            }
        }

        // Recent Discovery Repos
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Repo mới cập nhật", style = CosmicTheme.typography.command, color = CosmicTheme.colors.textPrimary)
        }
        items(uiState.recentRepos) { repo ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CosmicTheme.colors.nebula)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(repo.name, style = CosmicTheme.typography.body, color = CosmicTheme.colors.textPrimary)
                    if (repo.courseName != null) {
                        Text(repo.courseName, style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(repo.description, style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary, maxLines = 2)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(repo.language, style = CosmicTheme.typography.label) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = CosmicTheme.colors.glass,
                                labelColor = CosmicTheme.colors.plasma
                            )
                        )
                        Text("⭐ ${repo.stars}", style = CosmicTheme.typography.label, color = CosmicTheme.colors.textSecondary, modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
            }
        }
    }
}

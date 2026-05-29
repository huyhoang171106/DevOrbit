package vn.edu.uit.devorbit.mobile.ui.screen.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.domain.repository.TopStack
import vn.edu.uit.devorbit.mobile.domain.repository.TechStackInfo
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.ExploreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(viewModel: ExploreViewModel = hiltViewModel()) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 96.dp)
    ) {
        // Header
        item {
            Text(
                text = "Khám phá",
                style = CosmicTheme.typography.display,
                color = CosmicTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Kho tài nguyên học thuật từ sinh viên UIT",
                style = CosmicTheme.typography.body,
                color = CosmicTheme.colors.textSecondary
            )
        }

        // Top tech stacks
        item {
            Text(
                "Công nghệ phổ biến",
                style = CosmicTheme.typography.command,
                color = CosmicTheme.colors.textTertiary
            )
        }
        item {
            if (uiState.topStacks.isNotEmpty()) {
                FlowRowCompat(
                    topStacks = uiState.topStacks.take(6),
                    accent = true
                )
            } else {
                Text(
                    "Đang tải dữ liệu...",
                    style = CosmicTheme.typography.label,
                    color = CosmicTheme.colors.textTertiary
                )
            }
        }

        // All tech stacks
        item {
            Text(
                "Tất cả tech stack",
                style = CosmicTheme.typography.command,
                color = CosmicTheme.colors.textTertiary
            )
        }
        item {
            if (uiState.techStacks.isNotEmpty()) {
                FlowRowCompat(
                    techStacks = uiState.techStacks.take(10),
                    accent = false
                )
            }
        }

        // Recent repos
        item {
            Spacer(Modifier.height(4.dp))
            Text(
                "Repo mới cập nhật",
                style = CosmicTheme.typography.command,
                color = CosmicTheme.colors.textTertiary
            )
        }
        items(uiState.recentRepos) { repo ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
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
                        Text(
                            "${repo.stars} ★",
                            style = CosmicTheme.typography.label,
                            color = CosmicTheme.colors.textTertiary
                        )
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
    accent: Boolean
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (accent) {
            topStacks.forEach { stack ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CosmicTheme.colors.plasma.copy(alpha = 0.12f),
                    border = null
                ) {
                    Text(
                        text = "${stack.name} (${stack.count})",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.plasma
                    )
                }
            }
        } else {
            techStacks.forEach { stack ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CosmicTheme.colors.nebula,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
                ) {
                    Text(
                        text = stack.name,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = CosmicTheme.typography.label,
                        color = CosmicTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}

package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoFilterSheet(
    techStacks: List<String>,
    selectedTechStack: String?,
    onTechStackSelected: (String?) -> Unit
) {
    if (techStacks.isEmpty()) return

    Text(
        text = "Lọc theo tech stack",
        style = CosmicTheme.typography.label,
        color = CosmicTheme.colors.textTertiary,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        FilterChip(
            selected = selectedTechStack == null,
            onClick = { onTechStackSelected(null) },
            label = { Text("Tất cả", style = CosmicTheme.typography.label, fontWeight = FontWeight.SemiBold) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                selectedLabelColor = CosmicTheme.colors.plasma,
                containerColor = CosmicTheme.colors.nebula,
                labelColor = CosmicTheme.colors.textSecondary
            )
        )
        techStacks.take(5).forEach { stack ->
            FilterChip(
                selected = selectedTechStack == stack,
                onClick = { onTechStackSelected(stack) },
                label = { Text(stack, style = CosmicTheme.typography.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CosmicTheme.colors.plasma.copy(alpha = 0.15f),
                    selectedLabelColor = CosmicTheme.colors.plasma,
                    containerColor = CosmicTheme.colors.nebula,
                    labelColor = CosmicTheme.colors.textSecondary
                )
            )
        }
    }
}

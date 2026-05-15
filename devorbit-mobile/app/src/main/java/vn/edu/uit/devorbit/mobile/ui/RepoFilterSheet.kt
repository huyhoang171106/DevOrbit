package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoFilterSheet(
    techStacks: List<String>,
    selectedTechStack: String?,
    onTechStackSelected: (String?) -> Unit
) {
    if (techStacks.isEmpty()) return

    Text(
        text = "Filter by Tech Stack",
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        FilterChip(
            selected = selectedTechStack == null,
            onClick = { onTechStackSelected(null) },
            label = { Text("All") }
        )
        techStacks.take(5).forEach { stack ->
            FilterChip(
                selected = selectedTechStack == stack,
                onClick = { onTechStackSelected(stack) },
                label = { Text(stack) }
            )
        }
    }
}

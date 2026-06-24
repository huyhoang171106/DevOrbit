package vn.edu.uit.devorbit.admin.core.designsystem

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.admin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipGroup(
    options: List<FilterOption>,
    selectedId: String?,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            val selected = option.id == selectedId
            FilterChip(
                selected = selected,
                onClick = { onSelected(option.id) },
                label = {
                    Text(
                        option.label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = UITBlueSoft,
                    selectedLabelColor = UITBlue,
                    containerColor = Surface,
                    labelColor = TextSecondary,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Border,
                    selectedBorderColor = UITBlueBorder,
                    enabled = true,
                    selected = selected,
                ),
            )
        }
    }
}

data class FilterOption(
    val id: String,
    val label: String,
)

@Composable
fun SegmentedFilter(
    options: List<FilterOption>,
    selectedId: String?,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        options.forEachIndexed { index, option ->
            val selected = option.id == selectedId
            val isFirst = index == 0
            val isLast = index == options.lastIndex

            FilterChip(
                selected = selected,
                onClick = { onSelected(option.id) },
                label = {
                    Text(
                        option.label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = UITBlue,
                    selectedLabelColor = Surface,
                    containerColor = Surface,
                    labelColor = TextSecondary,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Border,
                    selectedBorderColor = UITBlue,
                    enabled = true,
                    selected = selected,
                ),
            )
        }
    }
}

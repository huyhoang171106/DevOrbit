package vn.edu.uit.devorbit.mobile.ui.screen.courses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    viewModel: CourseViewModel,
    onCourseClick: (CourseEntity) -> Unit
) {
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val filteredCourses = courses.filter {
        it.tenMH.contains(searchQuery, ignoreCase = true) || 
        it.maMH.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(CosmicTheme.spacing.medium),
            placeholder = { Text("Search course code or name...", color = CosmicTheme.colors.textTertiary) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = CosmicTheme.colors.plasma) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CosmicTheme.colors.plasma,
                unfocusedBorderColor = CosmicTheme.colors.glassBorder,
                cursorColor = CosmicTheme.colors.plasma,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(CosmicTheme.spacing.small)
        ) {
            items(filteredCourses, key = { it.id }) { course ->
                CourseListItem(course = course, onClick = { onCourseClick(course) })
            }
        }
    }
}

@Composable
fun CourseListItem(course: CourseEntity, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = CosmicTheme.spacing.medium),
        onClick = onClick,
        color = CosmicTheme.colors.nebula.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicTheme.colors.glassBorder)
    ) {
        Row(
            modifier = Modifier.padding(CosmicTheme.spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = course.maMH, style = CosmicTheme.typography.label, color = CosmicTheme.colors.plasma)
                Text(text = course.tenMH, style = CosmicTheme.typography.body, color = Color.White)
            }
            Text(
                text = "${course.credits} TC",
                style = CosmicTheme.typography.command,
                color = CosmicTheme.colors.textSecondary
            )
        }
    }
}

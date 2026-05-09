package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import vn.edu.uit.devorbit.mobile.model.*
import vn.edu.uit.devorbit.mobile.repository.DevOrbitRepository
import kotlinx.coroutines.launch

import vn.edu.uit.devorbit.mobile.ui.components.GlassCard

@Composable
fun CourseListScreen(
    courses: List<CourseSummary>,
    onCourseClick: (CourseSummary) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(courses) { course ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCourseClick(course) }
            ) {
                Column {
                    Text(
                        text = course.code,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = course.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

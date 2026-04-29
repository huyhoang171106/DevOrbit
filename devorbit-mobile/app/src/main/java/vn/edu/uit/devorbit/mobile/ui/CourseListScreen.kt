package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.model.CourseSummary
import vn.edu.uit.devorbit.mobile.repository.DevOrbitRepository
import kotlinx.coroutines.launch

@Composable
fun CourseListScreen(onCourseClick: (CourseSummary) -> Unit) {
    val repository = remember { DevOrbitRepository() }
    val scope = rememberCoroutineScope()
    var courses by remember { mutableStateOf<List<CourseSummary>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        repository.getCourses().onSuccess {
            courses = it
            isLoading = false
        }.onFailure {
            error = it.message
            isLoading = false
        }
    }

    when {
        isLoading -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator() }
        error != null -> Text("Error: $error")
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(courses) { course ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCourseClick(course) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = course.code,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = course.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

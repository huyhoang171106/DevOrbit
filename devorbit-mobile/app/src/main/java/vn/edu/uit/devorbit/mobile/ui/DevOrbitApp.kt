package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.model.CourseSummary
import vn.edu.uit.devorbit.mobile.model.RepoSummary

enum class Screen { COURSES, DETAIL, REPO_DETAIL }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevOrbitApp() {
    val courseViewModel: CourseViewModel = hiltViewModel()

    var currentScreen by remember { mutableStateOf(Screen.COURSES) }
    var selectedCourse by remember { mutableStateOf<CourseSummary?>(null) }
    var selectedRepo by remember { mutableStateOf<RepoSummary?>(null) }

    LaunchedEffect(currentScreen) {
        if (currentScreen == Screen.COURSES) {
            courseViewModel.loadCourses()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("DevOrbit", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (currentScreen) {
                Screen.COURSES -> CourseListScreen(
                    courses = courseViewModel.courses.value,
                    onCourseClick = { course ->
                        selectedCourse = course
                        courseViewModel.loadRepos(course.id)
                        currentScreen = Screen.DETAIL
                    }
                )
                Screen.DETAIL -> CourseDetailScreen(
                    courseName = selectedCourse?.name ?: "",
                    repos = courseViewModel.repos.value,
                    tutorials = courseViewModel.tutorials.value,
                    videos = courseViewModel.videos.value,
                    articles = courseViewModel.articles.value,
                    onBack = { currentScreen = Screen.COURSES },
                    onRepoClick = { repo ->
                        selectedRepo = repo
                        currentScreen = Screen.REPO_DETAIL
                    }
                )
                Screen.REPO_DETAIL -> selectedRepo?.let { repo ->
                    RepoDetailScreen(
                        repo = repo,
                        onBack = { currentScreen = Screen.DETAIL },
                    )
                }
            }

            if (courseViewModel.isLoading.value) {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    tonalElevation = 6.dp,
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Text("Loading data...")
                    }
                }
            }
        }
    }
}

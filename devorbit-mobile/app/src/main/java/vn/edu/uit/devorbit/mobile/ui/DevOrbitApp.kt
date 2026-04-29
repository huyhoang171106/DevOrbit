package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.runtime.*
import vn.edu.uit.devorbit.mobile.model.CourseSummary
import vn.edu.uit.devorbit.mobile.model.RepoSummary
import vn.edu.uit.devorbit.mobile.repository.DevOrbitRepository
import kotlinx.coroutines.launch

enum class Screen {
    COURSES, DETAIL, REPO_DETAIL
}

@Composable
fun DevOrbitApp() {
    val repository = remember { DevOrbitRepository() }
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf(Screen.COURSES) }
    var selectedCourse by remember { mutableStateOf<CourseSummary?>(null) }
    var selectedRepo by remember { mutableStateOf<RepoSummary?>(null) }
    var repos by remember { mutableStateOf<List<RepoSummary>>(emptyList()) }
    var isLoadingRepos by remember { mutableStateOf(false) }

    when (currentScreen) {
        Screen.COURSES -> CourseListScreen(
            onCourseClick = { course ->
                selectedCourse = course
                isLoadingRepos = true
                scope.launch {
                    repository.getRepos(course.id).onSuccess {
                        repos = it
                        currentScreen = Screen.DETAIL
                    }
                    isLoadingRepos = false
                }
            }
        )
        Screen.DETAIL -> CourseDetailScreen(
            courseName = selectedCourse?.name ?: "",
            repoCount = repos.size,
            repos = repos,
            onBack = {
                currentScreen = Screen.COURSES
                selectedCourse = null
                repos = emptyList()
            },
            onRepoClick = { repo ->
                selectedRepo = repo
                currentScreen = Screen.REPO_DETAIL
            }
        )
        Screen.REPO_DETAIL -> selectedRepo?.let { repo ->
            RepoDetailScreen(
                repo = repo,
                onBack = { currentScreen = Screen.DETAIL }
            )
        }
    }
}

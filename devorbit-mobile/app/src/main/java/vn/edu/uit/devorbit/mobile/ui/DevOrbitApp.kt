package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import vn.edu.uit.devorbit.mobile.model.CourseSummary
import vn.edu.uit.devorbit.mobile.model.RepoSummary
import vn.edu.uit.devorbit.mobile.model.BookmarkTargetType
import vn.edu.uit.devorbit.mobile.repository.AuthSessionStore
import vn.edu.uit.devorbit.mobile.repository.DevOrbitRepository
import kotlinx.coroutines.launch

enum class Screen { LOGIN, COURSES, DETAIL, REPO_DETAIL, BOOKMARKS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevOrbitApp() {
    val context = LocalContext.current
    val repository = remember { DevOrbitRepository(context) }
    val sessionStore = remember { AuthSessionStore(context) }
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf(if (sessionStore.getToken() == null) Screen.LOGIN else Screen.COURSES) }
    var selectedCourse by remember { mutableStateOf<CourseSummary?>(null) }
    var selectedRepo by remember { mutableStateOf<RepoSummary?>(null) }
    var repos by remember { mutableStateOf<List<RepoSummary>>(emptyList()) }
    var isLoadingRepos by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(sessionStore.getDisplayName() ?: "DevOrbit") },
            actions = {
                if (currentScreen != Screen.LOGIN) TextButton(onClick = { currentScreen = Screen.BOOKMARKS }) { Text("Bookmarks") }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                Screen.LOGIN -> LoginScreen(onLoginSuccess = { currentScreen = Screen.COURSES })
                Screen.COURSES -> CourseListScreen(onCourseClick = { course ->
                    selectedCourse = course
                    isLoadingRepos = true
                    scope.launch {
                        repository.getRepos(course.id).onSuccess {
                            repos = it
                            currentScreen = Screen.DETAIL
                        }
                        isLoadingRepos = false
                    }
                })
                Screen.DETAIL -> CourseDetailScreen(
                    courseName = selectedCourse?.name ?: "",
                    repoCount = repos.size,
                    repos = repos,
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
                        onBookmark = {
                            scope.launch { repository.saveBookmark(BookmarkTargetType.REPO, repo.id) }
                        }
                    )
                }
                Screen.BOOKMARKS -> BookmarksScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.COURSES }
                )
            }

            if (isLoadingRepos) {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    tonalElevation = 6.dp,
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Text("Loading repositories...")
                    }
                }
            }
        }
    }
}

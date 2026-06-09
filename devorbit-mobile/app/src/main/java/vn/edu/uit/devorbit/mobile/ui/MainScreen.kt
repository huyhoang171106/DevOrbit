package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.domain.model.*
import vn.edu.uit.devorbit.mobile.ui.components.CosmicBackground
import vn.edu.uit.devorbit.mobile.ui.navigation.Screen
import vn.edu.uit.devorbit.mobile.ui.screen.dashboard.DashboardScreen
import vn.edu.uit.devorbit.mobile.ui.screen.knowledge.KnowledgeGraphScreen
import vn.edu.uit.devorbit.mobile.ui.screen.explore.ExploreScreen
import vn.edu.uit.devorbit.mobile.ui.screen.plan.StudyPlannerScreen
import vn.edu.uit.devorbit.mobile.ui.screen.profile.ProfileScreen
import vn.edu.uit.devorbit.mobile.ui.screen.repos.ReposByCourseScreen
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AcademicViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.CourseViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.StudyPlanViewModel

@Composable
fun MainScreen(
    academicVm: AcademicViewModel = hiltViewModel()
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

    val health by academicVm.academicHealth.collectAsStateWithLifecycle()
    val recommendations by academicVm.recommendations.collectAsStateWithLifecycle()
    val todayTasks by academicVm.todayTasks.collectAsStateWithLifecycle()
    val nextAction by academicVm.nextAction.collectAsStateWithLifecycle()

    val navItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = CosmicTheme.colors.plasma,
            unselectedIconColor = CosmicTheme.colors.textTertiary,
            selectedTextColor = CosmicTheme.colors.plasma,
            unselectedTextColor = CosmicTheme.colors.textTertiary,
            indicatorColor = CosmicTheme.colors.plasma.copy(alpha = 0.1f)
        )
    )

    CosmicBackground {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                val items = Screen.navItems
                items.forEach { screen ->
                    item(
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        colors = navItemColors
                    )
                }
            },
            containerColor = Color.Transparent
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)).togetherWith(
                            fadeOut(animationSpec = tween(150))
                        )
                    },
                    label = "ScreenTransition"
                ) { screen ->
                    when (screen) {
                        Screen.Dashboard -> DashboardScreen(
                            academicHealth = health,
                            nextAction = nextAction,
                            recommendations = recommendations,
                            todayTasks = todayTasks,
                            focusTask = academicVm.focusTask.value,
                            onStartFocus = { academicVm.setFocusTask(it) },
                            onCompleteTask = { academicVm.toggleTaskComplete(it.id) },
                            onBreakdownGoal = { currentScreen = Screen.Plan }
                        )
                        Screen.Repos -> ReposByCourseScreen()
                        Screen.Explore -> ExploreScreen()
                        Screen.Plan -> PlanTabView()
                        Screen.Gpa -> GpaPlaceholderScreen()
                        Screen.Profile -> ProfileScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun GpaPlaceholderScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "GPA",
            style = CosmicTheme.typography.display,
            color = CosmicTheme.colors.textPrimary
        )
    }
}

@Composable
private fun KnowledgeTabView(courseViewModel: CourseViewModel = hiltViewModel()) {
    val nodes by courseViewModel.graphNodes.collectAsState()
    val links by courseViewModel.graphLinks.collectAsState()
    val loading by courseViewModel.graphLoading.collectAsState()

    var selectedNode by remember { mutableStateOf<GraphNode?>(null) }

    LaunchedEffect(Unit) { courseViewModel.loadGraph() }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = CosmicTheme.colors.plasma,
                strokeWidth = 2.dp
            )
        }
    } else {
        KnowledgeGraphScreen(
            nodes = nodes,
            links = links,
            learningPath = emptyList(),
            selectedNode = selectedNode,
            onNodeClick = { selectedNode = it }
        )
    }
}

@Composable
private fun PlanTabView(viewModel: StudyPlanViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    StudyPlannerScreen(
        studyPlan = state.plan,
        loading = state.loading,
        error = state.error,
        onGeneratePlan = { learningGoals, careerPath ->
            viewModel.generateRoadmap(learningGoals, careerPath)
        },
        onToggleItem = { viewModel.toggleItem(it) },
        onBreakdownTask = { }
    )
}

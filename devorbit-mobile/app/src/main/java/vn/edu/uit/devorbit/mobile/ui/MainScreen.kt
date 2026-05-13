package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.ui.components.CosmicBackground
import vn.edu.uit.devorbit.mobile.ui.navigation.Screen
import vn.edu.uit.devorbit.mobile.ui.screen.dashboard.DashboardScreen
import vn.edu.uit.devorbit.mobile.ui.screen.courses.CourseHubScreen
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AcademicViewModel

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
            unselectedIconColor = CosmicTheme.colors.textSecondary,
            selectedTextColor = CosmicTheme.colors.plasma,
            unselectedTextColor = CosmicTheme.colors.textSecondary,
            indicatorColor = Color.Transparent
        )
    )

    CosmicBackground {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                val items = listOf(
                    Screen.Dashboard,
                    Screen.Courses,
                    Screen.Progress,
                    Screen.Execution,
                    Screen.Copilot
                )
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
                    transitionSpec = { fadeIn().togetherWith(fadeOut()) },
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
                            onBreakdownGoal = { /* Navigate to breakdown */ }
                        )
                        Screen.Courses -> CourseHubScreen()
                        else -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                                Text(
                                    text = "${screen.label} Coming Soon",
                                    color = Color.White,
                                    style = CosmicTheme.typography.display
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

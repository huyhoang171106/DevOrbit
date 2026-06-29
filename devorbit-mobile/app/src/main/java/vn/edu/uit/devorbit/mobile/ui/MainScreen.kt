package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navArgument
import vn.edu.uit.devorbit.mobile.ui.components.CosmicBackground
import vn.edu.uit.devorbit.mobile.ui.screen.courses.CourseHubScreen
import vn.edu.uit.devorbit.mobile.ui.screen.dashboard.DashboardScreen
import vn.edu.uit.devorbit.mobile.ui.screen.dashboard.TaskManagementScreen
import vn.edu.uit.devorbit.mobile.ui.screen.explore.ExploreScreen
import vn.edu.uit.devorbit.mobile.ui.screen.knowledge.KnowledgeTabView
import vn.edu.uit.devorbit.mobile.ui.screen.notification.NotificationScreen
import vn.edu.uit.devorbit.mobile.ui.screen.plan.GroupPlanDetailScreen
import vn.edu.uit.devorbit.mobile.ui.screen.plan.GroupPlanListScreen
import vn.edu.uit.devorbit.mobile.ui.screen.plan.SubjectQaScreen
import vn.edu.uit.devorbit.mobile.ui.screen.community.CommunityScreen
import vn.edu.uit.devorbit.mobile.ui.screen.profile.ProfileScreen
import vn.edu.uit.devorbit.mobile.ui.screen.profile.ProfileDetailScreen
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AcademicViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.NotificationViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.ProfileViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.StudyPlanViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.SubjectQaViewModel

@Composable
fun MainScreen(
    academicVm: AcademicViewModel = hiltViewModel(),
    notificationVm: NotificationViewModel = hiltViewModel(),
    profileVm: ProfileViewModel = hiltViewModel(),
    studyPlanVm: StudyPlanViewModel = hiltViewModel(),
    subjectQaVm: SubjectQaViewModel = hiltViewModel(),
    showRegistrationOnboarding: Boolean = false,
    onRegistrationOnboardingCompleted: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showProfileDetail by remember { mutableStateOf(false) }
    var pendingCourseId by remember { mutableStateOf<Long?>(null) }
    var pendingRepoId by remember { mutableStateOf<Long?>(null) }
    var pendingGalaxy by remember { mutableStateOf(false) }

    val unreadCount by notificationVm.unreadCount.collectAsStateWithLifecycle()
    val profileState by profileVm.state.collectAsStateWithLifecycle()

    val navigateToTopLevel: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = false
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    val showBottomBar = currentRoute in listOf(
        "dashboard", "subjects", "tutor", "plan", "community", "profile"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        CosmicBackground {
            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = CosmicTheme.colors.nebula, // WarmWhite
                                border = BorderStroke(1.dp, CosmicTheme.colors.glassBorder),
                                shadowElevation = 6.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FloatingNavItem(
                                        selected = currentRoute == "dashboard",
                                        onClick = { navigateToTopLevel("dashboard") },
                                        icon = Icons.Rounded.Home,
                                        label = "Hôm nay"
                                    )
                                    FloatingNavItem(
                                        selected = currentRoute == "subjects",
                                        onClick = { navigateToTopLevel("subjects") },
                                        icon = Icons.Rounded.Book,
                                        label = "Môn học"
                                    )
                                    FloatingNavItem(
                                        selected = currentRoute == "tutor",
                                        onClick = { navigateToTopLevel("tutor") },
                                        icon = Icons.Rounded.AutoAwesome,
                                        label = "AI Tutor"
                                    )
                                    FloatingNavItem(
                                        selected = currentRoute == "plan",
                                        onClick = { navigateToTopLevel("plan") },
                                        icon = Icons.Rounded.DateRange,
                                        label = "Kế hoạch"
                                    )
                                     FloatingNavItem(
                                        selected = currentRoute == "community",
                                        onClick = { navigateToTopLevel("community") },
                                        icon = Icons.AutoMirrored.Rounded.Chat,
                                        label = "Cộng đồng"
                                     )
                                }
                            }
                        }
                    }
                },
                containerColor = Color.Transparent
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (showBottomBar) 88.dp else 0.dp)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.fillMaxSize(),
                        enterTransition = { fadeIn(animationSpec = tween(220)) },
                        exitTransition = { fadeOut(animationSpec = tween(180)) },
                        popEnterTransition = { fadeIn(animationSpec = tween(220)) },
                        popExitTransition = { fadeOut(animationSpec = tween(180)) }
                    ) {
                        composable("dashboard") {
                            DashboardScreen(
                                onNavigateToCourse = { courseId ->
                                    pendingCourseId = courseId
                                    navController.navigate("subjects")
                                },
                                onNavigateToPlanner = {
                                    pendingGalaxy = true
                                    navController.navigate("subjects")
                                },
                                onNavigateToCreateTask = {
                                    navigateToTopLevel("plan")
                                },
                                onNavigateToTaskManagement = {
                                    navigateToTopLevel("plan")
                                },
                                onNavigateToGroupPlan = { planId ->
                                    navController.navigate("group_plan_detail/$planId")
                                },
                                onNavigateToNotifications = {
                                    navController.navigate("notifications")
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile")
                                },
                                unreadCount = unreadCount,
                                avatarUrl = profileState.avatar,
                                showRegistrationOnboarding = showRegistrationOnboarding,
                                onRegistrationOnboardingCompleted = onRegistrationOnboardingCompleted
                            )
                        }
                        composable("subjects") {
                            CourseHubScreen(
                                onCreatePlan = {
                                    navController.navigate("plan")
                                },
                                pendingCourseId = pendingCourseId,
                                pendingRepoId = pendingRepoId,
                                pendingGalaxy = pendingGalaxy,
                                onPendingCleared = {
                                    pendingCourseId = null
                                    pendingRepoId = null
                                    pendingGalaxy = false
                                }
                            )
                        }
                        composable("tutor") {
                            SubjectQaScreen(viewModel = subjectQaVm)
                        }
                        composable("plan") {
                            BackHandler {
                                navigateToTopLevel("dashboard")
                            }
                            TaskManagementScreen(
                                onNavigateBack = {
                                    navigateToTopLevel("dashboard")
                                },
                                onNavigateToGroupPlan = { planId ->
                                    navController.navigate("group_plan_detail/$planId")
                                }
                            )
                        }
                        composable("community") {
                            CommunityScreen()
                        }
                        composable("notifications") {
                            NotificationScreen(
                                viewModel = notificationVm,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToGroupPlan = { planId ->
                                    navController.navigate("group_plan_detail/$planId")
                                }
                            )
                        }
                        composable("profile") {
                            ProfileScreen(
                                viewModel = profileVm,
                                onNavigateToDetail = {
                                    showProfileDetail = true
                                },
                                onBookmarkClick = { bookmark ->
                                    if (bookmark.targetType == "COURSE") {
                                        pendingCourseId = bookmark.targetId
                                    } else if (bookmark.targetType == "REPO") {
                                        pendingRepoId = bookmark.targetId
                                    }
                                    navController.navigate("subjects")
                                },
                                onLogout = onLogout
                            )
                        }
                        composable("group_plan_list") {
                            GroupPlanListScreen(
                                onNavigateToPlan = { planId ->
                                    navController.navigate("group_plan_detail/$planId")
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(
                            route = "group_plan_detail/{planId}",
                            arguments = listOf(navArgument("planId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val planId = backStackEntry.arguments?.getLong("planId") ?: 0L
                            GroupPlanDetailScreen(
                                planId = planId,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onNavigateLeave = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }

        // Profile detail overlay
        if (showProfileDetail) {
            ProfileDetailScreen(
                viewModel = profileVm,
                onBack = { showProfileDetail = false }
            )
        }
    }
}

@Composable
private fun RowScope.FloatingNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    val activeColor = CosmicTheme.colors.plasma // UIT Blue
    val inactiveColor = CosmicTheme.colors.textTertiary

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) activeColor else inactiveColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) activeColor else inactiveColor
            )
        }
    }
}

package vn.edu.uit.devorbit.admin.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import vn.edu.uit.devorbit.admin.ui.candidates.CandidatesScreen
import vn.edu.uit.devorbit.admin.ui.community.CommunityScreen
import vn.edu.uit.devorbit.admin.ui.courses.CourseDetailScreen
import vn.edu.uit.devorbit.admin.ui.courses.CourseRelationshipsScreen
import vn.edu.uit.devorbit.admin.ui.courses.CoursesScreen
import vn.edu.uit.devorbit.admin.ui.dashboard.DashboardScreen
import vn.edu.uit.devorbit.admin.ui.github.GithubScreen
import vn.edu.uit.devorbit.admin.ui.login.AdminLoginScreen
import vn.edu.uit.devorbit.admin.ui.login.AdminLoginViewModel
import vn.edu.uit.devorbit.admin.ui.navigation.AdminRoutes
import vn.edu.uit.devorbit.admin.ui.navigation.AdminScreen
import vn.edu.uit.devorbit.admin.ui.navigation.PrimaryTab
import vn.edu.uit.devorbit.admin.ui.notifications.NotificationsScreen
import vn.edu.uit.devorbit.admin.ui.notifications.NotificationsViewModel
import vn.edu.uit.devorbit.admin.ui.repos.ReposScreen
import vn.edu.uit.devorbit.admin.ui.reviews.ReviewsScreen
import vn.edu.uit.devorbit.admin.ui.students.StudentsScreen
import vn.edu.uit.devorbit.admin.ui.techstack.TechStackScreen
import vn.edu.uit.devorbit.admin.ui.reports.ReportsScreen
import vn.edu.uit.devorbit.admin.ui.theme.TextPrimary
import vn.edu.uit.devorbit.admin.ui.theme.UITBlue
import vn.edu.uit.devorbit.admin.ui.theme.UITBlueSoft

/** Top-level composable wired into MainActivity. */
@Composable
fun AdminRoot() {
    val loginVm: AdminLoginViewModel = hiltViewModel()
    val loginState by loginVm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { loginVm.checkSession() }

    // Splash while session is being restored — avoids login flash on cold start
    if (!loginState.sessionChecked) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator(color = UITBlue)
        }
        return
    }

    if (loginState.isLoggedIn) {
        AdminShell(onLogout = { loginVm.logout() })
    } else {
        AdminLoginScreen(
            viewModel = loginVm,
            onLoggedIn = { /* state drives recomposition */ }
        )
    }
}

// ── Command Menu (secondary screens overflow) ────────────────────────────────

@Composable
private fun CommandMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        AdminScreen.secondaryScreens.forEach { screen ->
            DropdownMenuItem(
                text = { Text(screen.label) },
                leadingIcon = { Icon(screen.icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
                onClick = { onDismiss(); onNavigate(screen.route) }
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        DropdownMenuItem(
            text = { Text("Đăng xuất") },
            leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, modifier = Modifier.size(20.dp)) },
            onClick = { onDismiss(); onLogout() }
        )
    }
}

// ── Routes classification ───────────────────
private val selfBarRoutes = setOf(
    AdminRoutes.COURSE_DETAIL,
    AdminRoutes.COURSE_RELATIONSHIPS
)

private val primaryTabRoutes = setOf(
    AdminRoutes.DASHBOARD,
    AdminRoutes.STUDENTS,
    AdminRoutes.COURSES,
    AdminRoutes.REPOS
)

// ── Shell with bottom nav + NavHost ──────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminShell(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentDestination = navBackStackEntry?.destination
    var commandMenuExpanded by remember { mutableStateOf(false) }
    val notifVm: NotificationsViewModel = hiltViewModel()
    val notifState by notifVm.state.collectAsStateWithLifecycle()
    val unreadCount = notifState.unreadCount

    val safeRoute = currentRoute ?: AdminRoutes.DASHBOARD
    val showBottomBar = safeRoute in primaryTabRoutes
    val showShellBar = safeRoute !in selfBarRoutes
    val isSecondaryScreen = safeRoute !in primaryTabRoutes && safeRoute !in selfBarRoutes

    Scaffold(
        topBar = {
            if (showShellBar) {
                CenterAlignedTopAppBar(
                    title = {
                        Text("DevOrbit", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                    },
                    navigationIcon = {
                        if (isSecondaryScreen) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Quay lại")
                            }
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { commandMenuExpanded = true }) {
                                Icon(Icons.Rounded.Menu, contentDescription = "Menu")
                            }
                            CommandMenu(
                                expanded = commandMenuExpanded,
                                onDismiss = { commandMenuExpanded = false },
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                    commandMenuExpanded = false
                                },
                                onLogout = onLogout
                            )
                        }
                        // Notification bell with unread count badge
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            if (unreadCount > 99) "99+" else "$unreadCount",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            IconButton(onClick = {
                                navController.navigate(AdminRoutes.NOTIFICATIONS) { launchSingleTop = true }
                            }) {
                                Icon(Icons.Rounded.Notifications, contentDescription = "Thông báo")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                    PrimaryTab.items.forEach { tab ->
                        val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label, style = MaterialTheme.typography.labelSmall, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = UITBlue, selectedTextColor = UITBlue, indicatorColor = UITBlueSoft,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant, unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AdminRoutes.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Primary tabs
            composable(AdminRoutes.DASHBOARD) { DashboardScreen() }
            composable(AdminRoutes.STUDENTS) { StudentsScreen() }
            composable(AdminRoutes.COURSES) {
                CoursesScreen(
                    onCourseClick = { id -> navController.navigate(AdminRoutes.courseDetail(id)) },
                    onRelationshipsClick = { navController.navigate(AdminRoutes.COURSE_RELATIONSHIPS) }
                )
            }
            composable(AdminRoutes.REPOS) { ReposScreen() }

            // Detail screens (own TopAppBar)
            composable(
                route = AdminRoutes.COURSE_DETAIL,
                arguments = listOf(navArgument("courseId") { type = NavType.LongType })
            ) { back ->
                val courseId = back.arguments?.getLong("courseId") ?: return@composable
                CourseDetailScreen(courseId = courseId, onBack = { navController.popBackStack() })
            }
            composable(AdminRoutes.COURSE_RELATIONSHIPS) {
                CourseRelationshipsScreen(onBack = { navController.popBackStack() })
            }

            // Secondary screens
            composable(AdminRoutes.CANDIDATES) { CandidatesScreen() }
            composable(AdminRoutes.REVIEWS) { ReviewsScreen() }
            composable(AdminRoutes.GITHUB) { GithubScreen() }
            composable(AdminRoutes.COMMUNITY) { CommunityScreen() }
            composable(AdminRoutes.TECHSTACK) { TechStackScreen() }
            composable(AdminRoutes.REPORTS) { ReportsScreen() }
            composable(AdminRoutes.NOTIFICATIONS) { NotificationsScreen() }
        }
    }
}

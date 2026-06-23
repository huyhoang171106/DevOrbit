package vn.edu.uit.devorbit.admin.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import vn.edu.uit.devorbit.admin.ui.notes.NotesScreen
import vn.edu.uit.devorbit.admin.ui.notifications.NotificationsScreen
import vn.edu.uit.devorbit.admin.ui.repos.ReposScreen
import vn.edu.uit.devorbit.admin.ui.reviews.ReviewsScreen
import vn.edu.uit.devorbit.admin.ui.students.StudentsScreen
import vn.edu.uit.devorbit.admin.ui.techstack.TechStackScreen
import vn.edu.uit.devorbit.admin.ui.theme.AdminTheme
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianPalette
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianShape
import vn.edu.uit.devorbit.admin.ui.theme.ObsidianType

@Composable
fun DevOrbitAdminApp() {
    val loginVm: AdminLoginViewModel = hiltViewModel()
    val loginState by loginVm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { loginVm.checkSession() }

    AdminTheme {
        if (loginState.isLoggedIn) {
            AdminScaffold(onLogout = { loginVm.logout() })
        } else {
            AdminLoginScreen(
                viewModel = loginVm,
                onLoggedIn = { /* state handled by VM */ }
            )
        }
    }
}

@Composable
private fun AdminScaffold(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AdminRoutes.DASHBOARD

    // Hide bottom nav on detail screens
    val isDetailScreen = currentRoute.startsWith("courses/") || currentRoute == AdminRoutes.COURSE_RELATIONSHIPS
    var showCommandHub by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (!isDetailScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    PrimaryTab.items.forEach { tab ->
                        val selected = currentRoute == tab.route
                        NavigationBarItem(
                            icon = {
                                Icon(tab.icon, contentDescription = tab.label)
                            },
                            label = {
                                Text(tab.label, style = ObsidianType.labelSmall)
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            )
                        )
                    }
                    // Command hub button
                    NavigationBarItem(
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.Apps,
                                    contentDescription = "Command Hub",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        label = { Text("Thêm", style = ObsidianType.labelSmall) },
                        selected = false,
                        onClick = { showCommandHub = true },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AdminScreen.Dashboard.route,
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn(tween(200)) + slideInHorizontally(tween(200)) { it / 20 } },
            exitTransition = { fadeOut(tween(150)) },
            popEnterTransition = { fadeIn(tween(200)) },
            popExitTransition = { fadeOut(tween(150)) }
        ) {
            composable(AdminRoutes.DASHBOARD) { DashboardScreen() }
            composable(AdminRoutes.STUDENTS) { StudentsScreen() }
            composable(AdminRoutes.COURSES) {
                CoursesScreen(
                    onCourseClick = { id -> navController.navigate(AdminRoutes.courseDetail(id)) },
                    onRelationshipsClick = { navController.navigate(AdminRoutes.COURSE_RELATIONSHIPS) }
                )
            }
            composable(
                route = AdminRoutes.COURSE_DETAIL,
                arguments = listOf(navArgument("courseId") { type = NavType.LongType })
            ) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getLong("courseId") ?: 0L
                CourseDetailScreen(courseId = courseId, onBack = { navController.popBackStack() })
            }
            composable(AdminRoutes.COURSE_RELATIONSHIPS) {
                CourseRelationshipsScreen(onBack = { navController.popBackStack() })
            }
            composable(AdminRoutes.REPOS) { ReposScreen() }
            composable(AdminRoutes.CANDIDATES) { CandidatesScreen() }
            composable(AdminRoutes.REVIEWS) { ReviewsScreen() }
            composable(AdminRoutes.GITHUB) { GithubScreen() }
            composable(AdminRoutes.COMMUNITY) { CommunityScreen() }
            composable(AdminRoutes.TECHSTACK) { TechStackScreen() }
            composable(AdminRoutes.NOTES) { NotesScreen() }
            composable(AdminRoutes.NOTIFICATIONS) { NotificationsScreen() }
        }
    }

    // ── Command Hub Dialog ────────────────────────────────────────────────────
    if (showCommandHub) {
        CommandHubDialog(
            onDismiss = { showCommandHub = false },
            onNavigate = { route ->
                showCommandHub = false
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onLogout = {
                showCommandHub = false
                onLogout()
            }
        )
    }
}

@Composable
private fun CommandHubDialog(
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = ObsidianShape.lg,
        title = {
            Text("Command Hub", style = ObsidianType.headlineMedium)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    placeholder = { Text("Tìm module...", style = ObsidianType.bodyMedium) },
                    leadingIcon = { Icon(Icons.Rounded.Search, null, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    shape = ObsidianShape.sm,
                    textStyle = ObsidianType.bodyMedium
                )
                Spacer(Modifier.height(12.dp))

                val filtered = AdminScreen.secondaryScreens.filter {
                    searchQuery.isBlank() || it.label.contains(searchQuery, ignoreCase = true)
                }
                val grouped = filtered.groupBy { it.category }

                grouped.forEach { (category, screens) ->
                    Text(
                        category.uppercase(),
                        style = ObsidianType.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                    screens.forEach { screen ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(ObsidianShape.sm)
                                .background(Color.Transparent)
                                .clickable { onNavigate(screen.route) }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(screen.icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(screen.label, style = ObsidianType.bodyMedium, modifier = Modifier.weight(1f))
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ObsidianShape.sm)
                        .clickable { onLogout() }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Logout, null, tint = ObsidianPalette.Red500, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Đăng xuất", style = ObsidianType.bodyMedium, color = ObsidianPalette.Red500)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Đóng") }
        }
    )
}

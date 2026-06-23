package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.edu.uit.devorbit.mobile.ui.components.CosmicBackground
import vn.edu.uit.devorbit.mobile.ui.navigation.Screen
import vn.edu.uit.devorbit.mobile.ui.screen.courses.CourseHubScreen
import vn.edu.uit.devorbit.mobile.ui.screen.dashboard.DashboardScreen
import vn.edu.uit.devorbit.mobile.ui.screen.explore.ExploreScreen
import vn.edu.uit.devorbit.mobile.ui.screen.knowledge.KnowledgeGraphScreen
import vn.edu.uit.devorbit.mobile.ui.screen.notification.NotificationScreen
import vn.edu.uit.devorbit.mobile.ui.screen.community.CommunityScreen
import vn.edu.uit.devorbit.mobile.ui.screen.plan.StudyPlannerScreen
import vn.edu.uit.devorbit.mobile.ui.screen.profile.ProfileScreen
import vn.edu.uit.devorbit.mobile.ui.theme.CosmicTheme
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AcademicViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.NotificationViewModel
import vn.edu.uit.devorbit.mobile.domain.model.GraphNode
import vn.edu.uit.devorbit.mobile.ui.viewmodel.CourseViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.StudyPlanViewModel

@Composable
fun MainScreen(
    academicVm: AcademicViewModel = hiltViewModel(),
    notificationVm: NotificationViewModel = hiltViewModel()
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
    var showPopup by remember { mutableStateOf(false) }
    val unreadCount by notificationVm.unreadCount.collectAsStateWithLifecycle()

    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = CosmicTheme.colors.plasma,
        unselectedIconColor = CosmicTheme.colors.textTertiary,
        indicatorColor = CosmicTheme.colors.plasma.copy(alpha = 0.1f)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        CosmicBackground {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(56.dp)
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == Screen.Dashboard && !showPopup,
                            onClick = { currentScreen = Screen.Dashboard; showPopup = false },
                            icon = { Icon(Icons.Rounded.Home, contentDescription = "Tổng quan") },
                            colors = navItemColors
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.Community && !showPopup,
                            onClick = { currentScreen = Screen.Community; showPopup = false },
                            icon = { Icon(Icons.Rounded.Chat, contentDescription = "Cộng đồng") },
                            colors = navItemColors
                        )
                        NavigationBarItem(
                            selected = showPopup || currentScreen in listOf(
                                Screen.Courses, Screen.Explore, Screen.Plan, Screen.Knowledge
                            ),
                            onClick = { showPopup = !showPopup },
                            icon = {
                                Icon(
                                    Icons.Rounded.Star,
                                    contentDescription = "Menu",
                                    tint = if (showPopup) CosmicTheme.colors.plasma
                                           else CosmicTheme.colors.textTertiary
                                )
                            },
                            colors = navItemColors
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.Notifications && !showPopup,
                            onClick = { currentScreen = Screen.Notifications; showPopup = false },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (unreadCount > 0) {
                                            Badge(
                                                containerColor = CosmicTheme.colors.plasma,
                                                contentColor = Color.Black
                                            ) {
                                                Text(
                                                    text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Rounded.Notifications, contentDescription = "Thông báo")
                                }
                            },
                            colors = navItemColors
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.Profile && !showPopup,
                            onClick = { currentScreen = Screen.Profile; showPopup = false },
                            icon = { Icon(Icons.Rounded.Person, contentDescription = "Cá nhân") },
                            colors = navItemColors
                        )
                    }
                },
                containerColor = Color.Transparent
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
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
                                onNavigateToCourse = { courseId ->
                                    currentScreen = Screen.Courses
                                    showPopup = false
                                },
                                onNavigateToPlan = {
                                    currentScreen = Screen.Plan
                                    showPopup = false
                                }
                            )
                            Screen.Courses -> CourseHubScreen()
                            Screen.Knowledge -> KnowledgeTabView()
                            Screen.Explore -> ExploreScreen()
                            Screen.Notifications -> NotificationScreen(viewModel = notificationVm)
                            Screen.Plan -> PlanTabView()
                            Screen.Profile -> ProfileScreen()
                            Screen.Community -> CommunityScreen()
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showPopup,
            enter = scaleIn(
                animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
                initialScale = 0.85f
            ) + fadeIn(animationSpec = tween(200)),
            exit = scaleOut(
                animationSpec = tween(150),
                targetScale = 0.85f
            ) + fadeOut(animationSpec = tween(150)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showPopup = false }
            ) {
                val screenWidthDp = LocalConfiguration.current.screenWidthDp
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(x = (-0.125f * screenWidthDp).dp)
                        .padding(bottom = 88.dp)
                        .widthIn(min = 200.dp, max = 260.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CosmicTheme.colors.nebula
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        PopupText("Môn học") {
                            currentScreen = Screen.Courses
                            showPopup = false
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = CosmicTheme.colors.glassBorder.copy(alpha = 0.3f)
                        )
                        PopupText("Khám phá") {
                            currentScreen = Screen.Explore
                            showPopup = false
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = CosmicTheme.colors.glassBorder.copy(alpha = 0.3f)
                        )
                        PopupText("Kế hoạch") {
                            currentScreen = Screen.Plan
                            showPopup = false
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = CosmicTheme.colors.glassBorder.copy(alpha = 0.3f)
                        )
                        PopupText("Kiến thức") {
                            currentScreen = Screen.Knowledge
                            showPopup = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PopupText(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        color = CosmicTheme.colors.textPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
    )
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

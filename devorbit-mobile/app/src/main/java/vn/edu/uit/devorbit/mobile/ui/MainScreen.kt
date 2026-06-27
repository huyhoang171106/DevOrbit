package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import vn.edu.uit.devorbit.mobile.ui.components.CosmicBackground
import vn.edu.uit.devorbit.mobile.ui.navigation.Screen
import vn.edu.uit.devorbit.mobile.ui.screen.courses.CourseHubScreen
import vn.edu.uit.devorbit.mobile.ui.screen.dashboard.DashboardScreen
import vn.edu.uit.devorbit.mobile.ui.screen.dashboard.TaskManagementScreen
import vn.edu.uit.devorbit.mobile.ui.screen.explore.ExploreScreen
import vn.edu.uit.devorbit.mobile.ui.screen.knowledge.KnowledgeTabView
import vn.edu.uit.devorbit.mobile.ui.screen.notification.NotificationScreen
import vn.edu.uit.devorbit.mobile.ui.screen.plan.GroupPlanDetailScreen
import vn.edu.uit.devorbit.mobile.ui.screen.plan.GroupPlanListScreen
import vn.edu.uit.devorbit.mobile.ui.screen.community.CommunityScreen
import vn.edu.uit.devorbit.mobile.ui.screen.plan.StudyPlannerScreen
import vn.edu.uit.devorbit.mobile.ui.screen.plan.SubjectQaScreen
import vn.edu.uit.devorbit.mobile.domain.repository.Bookmark
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
    subjectQaVm: SubjectQaViewModel = hiltViewModel()
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
    var showPopup by remember { mutableStateOf(false) }
    var showProfileDetail by remember { mutableStateOf(false) }
    var pendingCourseId by remember { mutableStateOf<Long?>(null) }
    var pendingRepoId by remember { mutableStateOf<Long?>(null) }
    val unreadCount by notificationVm.unreadCount.collectAsStateWithLifecycle()
    val profileState by profileVm.state.collectAsStateWithLifecycle()

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
                        modifier = Modifier.height(80.dp)
                    ) {
                        // 1. Dashboard (Hôm nay)
                        NavigationBarItem(
                            selected = currentScreen == Screen.Dashboard,
                            onClick = { currentScreen = Screen.Dashboard; showPopup = false },
                            icon = { Icon(Icons.Rounded.Home, contentDescription = "Hôm nay") },
                            label = { Text("Hôm nay", fontSize = 10.sp) },
                            colors = navItemColors
                        )
                        // 2. Subjects (Môn học)
                        NavigationBarItem(
                            selected = currentScreen == Screen.Subjects,
                            onClick = { currentScreen = Screen.Subjects; showPopup = false },
                            icon = { Icon(Icons.Rounded.Book, contentDescription = "Môn học") },
                            label = { Text("Môn học", fontSize = 10.sp) },
                            colors = navItemColors
                        )
                        // 3. Tutor (AI Tutor)
                        NavigationBarItem(
                            selected = currentScreen == Screen.Tutor,
                            onClick = { currentScreen = Screen.Tutor; showPopup = false },
                            icon = { Icon(Icons.Rounded.AutoAwesome, contentDescription = "AI Tutor") },
                            label = { Text("AI Tutor", fontSize = 10.sp) },
                            colors = navItemColors
                        )
                        // 4. Plan (Kế hoạch)
                        NavigationBarItem(
                            selected = currentScreen == Screen.Plan,
                            onClick = { currentScreen = Screen.Plan; showPopup = false },
                            icon = { Icon(Icons.Rounded.DateRange, contentDescription = "Kế hoạch") },
                            label = { Text("Kế hoạch", fontSize = 10.sp) },
                            colors = navItemColors
                        )
                        // 5. Community (Cộng đồng)
                        NavigationBarItem(
                            selected = currentScreen == Screen.Community,
                            onClick = { currentScreen = Screen.Community; showPopup = false },
                            icon = { Icon(Icons.Rounded.Chat, contentDescription = "Cộng đồng") },
                            label = { Text("Cộng đồng", fontSize = 10.sp) },
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
                                    currentScreen = Screen.Subjects
                                    showPopup = false
                                },
                                onNavigateToCreateTask = {
                                    currentScreen = Screen.Plan
                                    showPopup = false
                                },
                                onNavigateToTaskManagement = {
                                    currentScreen = Screen.Plan
                                    showPopup = false
                                },
                                onNavigateToGroupPlan = { planId ->
                                    currentScreen = Screen.GroupPlanDetail(planId)
                                    showPopup = false
                                },
                                onNavigateToNotifications = {
                                    currentScreen = Screen.Notifications
                                    showPopup = false
                                },
                                onNavigateToProfile = {
                                    currentScreen = Screen.Profile
                                    showPopup = false
                                },
                                unreadCount = unreadCount,
                                avatarUrl = profileState.avatar
                            )
                            Screen.TaskManagement -> TaskManagementScreen(
                                onNavigateBack = {
                                    currentScreen = Screen.Dashboard
                                },
                                onNavigateToGroupPlan = { planId ->
                                    currentScreen = Screen.GroupPlanDetail(planId)
                                }
                            )
                            is Screen.GroupPlanDetail -> GroupPlanDetailScreen(
                                planId = screen.planId,
                                onNavigateBack = {
                                    currentScreen = Screen.Plan
                                },
                                onNavigateLeave = {
                                    currentScreen = Screen.Plan
                                }
                            )
                            Screen.GroupPlanList -> GroupPlanListScreen(
                                onNavigateToPlan = { planId ->
                                    currentScreen = Screen.GroupPlanDetail(planId)
                                },
                                onNavigateBack = {
                                    currentScreen = Screen.Dashboard
                                }
                            )
                            Screen.Subjects -> CourseHubScreen(
                                onCreatePlan = {
                                    currentScreen = Screen.Plan
                                    showPopup = false
                                },
                                pendingCourseId = pendingCourseId,
                                pendingRepoId = pendingRepoId,
                                onPendingCleared = {
                                    pendingCourseId = null
                                    pendingRepoId = null
                                }
                            )
                            Screen.Tutor -> SubjectQaScreen(viewModel = subjectQaVm)
                            Screen.Plan -> TaskManagementScreen(
                                onNavigateBack = {
                                    currentScreen = Screen.Dashboard
                                },
                                onNavigateToGroupPlan = { planId ->
                                    currentScreen = Screen.GroupPlanDetail(planId)
                                }
                            )
                            Screen.Knowledge -> KnowledgeTabView()
                            Screen.Explore -> ExploreScreen()
                            Screen.Notifications -> NotificationScreen(
                                viewModel = notificationVm,
                                onNavigateToGroupPlan = { planId ->
                                    currentScreen = Screen.GroupPlanDetail(planId)
                                }
                            )
                            Screen.Profile -> ProfileScreen(
                                onNavigateToDetail = { showProfileDetail = true },
                                onBookmarkClick = { bookmark ->
                                    currentScreen = Screen.Subjects
                                    showPopup = false
                                    if (bookmark.targetType == "COURSE") {
                                        pendingCourseId = bookmark.targetId
                                    } else if (bookmark.targetType == "REPO") {
                                        pendingRepoId = bookmark.targetId
                                    }
                                }
                            )
                            Screen.Community -> CommunityScreen()
                            else -> {}
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
                            currentScreen = Screen.Subjects
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
                        PopupText("Kế hoạch nhóm") {
                            currentScreen = Screen.GroupPlanList
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

        // Profile detail overlay
        if (showProfileDetail) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
            ) {
                ProfileDetailScreen(
                    onBack = { showProfileDetail = false }
                )
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



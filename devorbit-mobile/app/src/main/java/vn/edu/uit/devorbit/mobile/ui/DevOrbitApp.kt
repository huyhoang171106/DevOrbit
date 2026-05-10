package vn.edu.uit.devorbit.mobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import vn.edu.uit.devorbit.mobile.model.*
import vn.edu.uit.devorbit.mobile.model.domain.*
import vn.edu.uit.devorbit.mobile.ui.screen.dashboard.*
import vn.edu.uit.devorbit.mobile.ui.screen.knowledge.*
import vn.edu.uit.devorbit.mobile.ui.screen.plan.*
import vn.edu.uit.devorbit.mobile.ui.screen.analytics.*
import vn.edu.uit.devorbit.mobile.ui.screen.risk.BurnoutScreen
import vn.edu.uit.devorbit.mobile.ui.screen.risk.DigitalTwinScreen
import vn.edu.uit.devorbit.mobile.ui.screen.risk.RecommendationsScreen
import vn.edu.uit.devorbit.mobile.ui.screen.risk.RiskCenterScreen
import vn.edu.uit.devorbit.mobile.ui.screen.risk.SimulationScreen
import vn.edu.uit.devorbit.mobile.ui.screen.analytics.WorkloadScreen
import vn.edu.uit.devorbit.mobile.ui.viewmodel.AcademicViewModel
import vn.edu.uit.devorbit.mobile.ui.viewmodel.CourseViewModel
import vn.edu.uit.devorbit.mobile.ui.theme.*

enum class Screen { COURSES, DETAIL, REPO_DETAIL, FOCUS_MODE, BREAKDOWN, SIMULATE, TIMELINE, GPA, BURNOUT, TWIN, RECOMMENDATIONS, SYLLABUS, KNOWLEDGE_DETAIL, WORKLOAD }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevOrbitApp() {
    val courseViewModel: CourseViewModel = hiltViewModel()
    val academicVm: AcademicViewModel = hiltViewModel()

    var currentScreen by remember { mutableStateOf(Screen.COURSES) }
    var selectedCourse by remember { mutableStateOf<CourseSummary?>(null) }
    var selectedRepo by remember { mutableStateOf<RepoSummary?>(null) }
    var selectedTab by remember { mutableStateOf(0) }
    var breakdownGoal by remember { mutableStateOf("") }
    var breakdownDifficulty by remember { mutableStateOf("medium") }
    var syllabusText by remember { mutableStateOf("") }
    var parsedSyllabus by remember { mutableStateOf<ParsedSyllabus?>(null) }

    LaunchedEffect(Unit) {
        courseViewModel.loadCourses()
        academicVm.loadInitialData()
        academicVm.loadKnowledgeGraph()
        academicVm.loadAnalytics()
        academicVm.loadSemesterTimeline()
    }

    val showBottomBar = currentScreen == Screen.COURSES

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            if (!showBottomBar) {
                TopAppBar(
                    title = {
                        val title = when (currentScreen) {
                            Screen.FOCUS_MODE -> "Chế độ Focus"
                            Screen.BREAKDOWN -> "Phân tích công việc"
                            Screen.WORKLOAD -> "Khối lượng công việc"
                            Screen.SIMULATE -> "Mô phỏng rủi ro"
                            Screen.TIMELINE -> "Dòng thời gian"
                            Screen.GPA -> "GPA Impact"
                            Screen.BURNOUT -> "Kiệt sức"
                            Screen.TWIN -> "Digital Twin"
                            Screen.RECOMMENDATIONS -> "Gợi ý"
                            Screen.SYLLABUS -> "Import Syllabus"
                            Screen.KNOWLEDGE_DETAIL -> "Chi tiết kiến thức"
                            Screen.DETAIL -> selectedCourse?.name ?: ""
                            Screen.REPO_DETAIL -> selectedRepo?.displayName ?: ""
                            else -> "DevOrbit"
                        }
                        Text(title, color = Color.White)
                    },
                    navigationIcon = {
                        TextButton(onClick = { currentScreen = Screen.COURSES }) {
                            Text("< Back", color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color(0x1A1A1A2E)) {
                    val items = listOf(
                        Triple(Icons.Default.Home, "Tổng quan"),
                        Triple(Icons.Default.Info, "Kiến thức"),
                        Triple(Icons.Default.List, "Kế hoạch"),
                        Triple(Icons.Default.BarChart, "Phân tích"),
                        Triple(Icons.Default.Warning, "Rủi ro"),
                    )
                    items.forEachIndexed { index, (icon, label) ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = { Icon(icon, contentDescription = label, tint = if (selectedTab == index) CosmicGlowPurple else Color.White.copy(alpha = 0.6f)) },
                            label = { Text(label, color = if (selectedTab == index) CosmicGlowPurple else Color.White.copy(alpha = 0.6f)) },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                !showBottomBar -> {
                    when (currentScreen) {
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
                            RepoDetailScreen(repo = repo, onBack = { currentScreen = Screen.DETAIL })
                        }
                        Screen.FOCUS_MODE -> academicVm.focusTask.value?.let { task ->
                            FocusModeScreen(
                                focusTask = task,
                                onComplete = {
                                    academicVm.toggleTaskComplete(task.id)
                                    currentScreen = Screen.COURSES
                                },
                                onBack = { currentScreen = Screen.COURSES }
                            )
                        }
                        Screen.BREAKDOWN -> TaskBreakdownScreen(
                            breakdown = academicVm.breakdown.value,
                            goal = breakdownGoal,
                            difficulty = breakdownDifficulty,
                            onGoalChange = { breakdownGoal = it },
                            onDifficultyChange = { breakdownDifficulty = it },
                            onBreakdown = { academicVm.breakdownGoal(breakdownGoal, breakdownDifficulty) },
                            onCreateTasks = {
                                academicVm.breakdown.value?.steps?.forEach { step ->
                                    academicVm.addTask(LearningTask(
                                        title = step.title,
                                        estimatedMinutes = step.estimatedMinutes,
                                        taskType = "breakdown_step"
                                    ))
                                }
                                currentScreen = Screen.COURSES
                            }
                        )
                        Screen.WORKLOAD -> WorkloadScreen(workload = academicVm.workload.value)
                        Screen.SIMULATE -> SimulationScreen(
                            courses = academicVm.courses.value.map { vn.edu.uit.devorbit.mobile.ui.screen.risk.CourseSummary(it.id, it.name) },
                            simulationResult = academicVm.simulationResult.value,
                            onSimulate = { id -> academicVm.simulateFailure(id) }
                        )
                        Screen.TIMELINE -> SemesterTimelineScreen(timeline = academicVm.semesterTimeline.value)
                        Screen.GPA -> GpaScreen(gpaImpact = academicVm.gpaImpact.value)
                        Screen.BURNOUT -> BurnoutScreen(burnout = academicVm.burnout.value)
                        Screen.TWIN -> DigitalTwinScreen(twin = academicVm.academicTwin.value)
                        Screen.RECOMMENDATIONS -> RecommendationsScreen(recommendations = academicVm.recommendations.value)
                        Screen.SYLLABUS -> SyllabusParserScreen(
                            text = syllabusText,
                            onTextChange = { syllabusText = it },
                            onParse = {
                                parsedSyllabus = ParsedSyllabus(
                                    courseName = "Đã parse",
                                    deadlines = listOf("Giữa kỳ", "Cuối kỳ"),
                                    topics = listOf("Chương 1", "Chương 2"),
                                    assignments = listOf("Bài tập 1")
                                )
                            },
                            parsedResult = parsedSyllabus,
                            onCreateTasks = {
                                parsedSyllabus?.let { s ->
                                    s.assignments.forEach { a ->
                                        academicVm.addTask(LearningTask(title = a, taskType = "assignment"))
                                    }
                                    currentScreen = Screen.COURSES
                                }
                            }
                        )
                        Screen.KNOWLEDGE_DETAIL -> {
                            val node = academicVm.selectedNode.value
                            if (node != null) {
                                KnowledgeDetailScreen(
                                    node = node,
                                    learningPath = academicVm.learningPath.value,
                                    onSimulate = {
                                        academicVm.simulateFailure(node.id)
                                        currentScreen = Screen.SIMULATE
                                    },
                                    onBack = { currentScreen = Screen.COURSES }
                                )
                            }
                        }
                        else -> {}
                    }
                }
                // Bottom nav tabs
                selectedTab == 0 -> DashboardScreen(
                    academicHealth = academicVm.academicHealth.value,
                    nextAction = academicVm.nextAction.value,
                    recommendations = academicVm.recommendations.value.take(3),
                    todayTasks = academicVm.todayTasks.value,
                    focusTask = academicVm.focusTask.value,
                    onStartFocus = { task ->
                        academicVm.setFocusTask(task)
                        currentScreen = Screen.FOCUS_MODE
                    },
                    onCompleteTask = { task -> academicVm.toggleTaskComplete(task.id) },
                    onBreakdownGoal = { currentScreen = Screen.BREAKDOWN }
                )
                selectedTab == 1 -> KnowledgeGraphScreen(
                    nodes = academicVm.graphNodes.value,
                    links = academicVm.graphLinks.value,
                    learningPath = academicVm.learningPath.value,
                    selectedNode = academicVm.selectedNode.value,
                    onNodeClick = { node ->
                        academicVm.selectGraphNode(node)
                        currentScreen = Screen.KNOWLEDGE_DETAIL
                    }
                )
                selectedTab == 2 -> StudyPlannerScreen(
                    studyPlan = academicVm.studyPlan.value,
                    onGeneratePlan = { academicVm.generatePlan() },
                    onToggleItem = {},
                    onBreakdownTask = { currentScreen = Screen.BREAKDOWN }
                )
                selectedTab == 3 -> AnalyticsScreen(
                    analytics = academicVm.analytics.value,
                    onRefresh = { academicVm.loadAnalytics() },
                    onNavigateToGpa = { currentScreen = Screen.GPA },
                    onNavigateToTimeline = { currentScreen = Screen.TIMELINE },
                    onNavigateToWorkload = { currentScreen = Screen.WORKLOAD }
                )
                selectedTab == 4 -> RiskCenterScreen(
                    riskProfile = academicVm.riskProfile.value,
                    onNavigateToBurnout = { currentScreen = Screen.BURNOUT },
                    onNavigateToTwin = { currentScreen = Screen.TWIN },
                    onNavigateToRecommendations = { currentScreen = Screen.RECOMMENDATIONS },
                    onNavigateToSimulation = { currentScreen = Screen.SIMULATE }
                )
            }

            if (academicVm.isLoading.value || courseViewModel.isLoading.value) {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    tonalElevation = 6.dp,
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Text("Đang tải...")
                    }
                }
            }
        }
    }
}

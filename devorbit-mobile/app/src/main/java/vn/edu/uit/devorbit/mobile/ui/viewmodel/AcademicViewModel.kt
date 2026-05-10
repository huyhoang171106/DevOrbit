package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import vn.edu.uit.devorbit.mobile.engine.KnowledgeGraphEngine
import vn.edu.uit.devorbit.mobile.model.*
import vn.edu.uit.devorbit.mobile.model.domain.*
import vn.edu.uit.devorbit.mobile.repository.AcademicRepository
import vn.edu.uit.devorbit.mobile.repository.DevOrbitRepository
import javax.inject.Inject

@HiltViewModel
class AcademicViewModel @Inject constructor(
    private val academicRepo: AcademicRepository,
    private val courseRepo: DevOrbitRepository,
) : ViewModel() {

    // ─── Shared state ───
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // ─── Courses ───
    private val _courses = mutableStateOf<List<CourseSummary>>(emptyList())
    val courses: State<List<CourseSummary>> = _courses

    // ─── Dashboard ───
    private val _todayTasks = mutableStateOf<List<LearningTask>>(emptyList())
    val todayTasks: State<List<LearningTask>> = _todayTasks

    private val _nextAction = mutableStateOf<BreakdownStep?>(null)
    val nextAction: State<BreakdownStep?> = _nextAction

    private val _academicHealth = mutableStateOf(AcademicHealth(0.0, emptyList()))
    val academicHealth: State<AcademicHealth> = _academicHealth

    private val _recommendations = mutableStateOf<List<StudyRecommendation>>(emptyList())
    val recommendations: State<List<StudyRecommendation>> = _recommendations

    // ─── Knowledge Graph ───
    private val _graphNodes = mutableStateOf<List<GraphNode>>(emptyList())
    val graphNodes: State<List<GraphNode>> = _graphNodes

    private val _graphLinks = mutableStateOf<List<GraphLink>>(emptyList())
    val graphLinks: State<List<GraphLink>> = _graphLinks

    private val _selectedNode = mutableStateOf<GraphNode?>(null)
    val selectedNode: State<GraphNode?> = _selectedNode

    private val _learningPath = mutableStateOf<List<GraphNode>>(emptyList())
    val learningPath: State<List<GraphNode>> = _learningPath

    // ─── Tasks ───
    private val _allTasks = mutableStateOf<List<LearningTask>>(emptyList())
    val allTasks: State<List<LearningTask>> = _allTasks

    // ─── Study Plan ───
    private val _studyPlan = mutableStateOf<StudyPlan?>(null)
    val studyPlan: State<StudyPlan?> = _studyPlan

    // ─── Risk ───
    private val _riskProfile = mutableStateOf(RiskProfile(subjectRisks = emptyList()))
    val riskProfile: State<RiskProfile> = _riskProfile

    // ─── Workload ───
    private val _workload = mutableStateOf<WorkloadProfile?>(null)
    val workload: State<WorkloadProfile?> = _workload

    // ─── Burnout ───
    private val _burnout = mutableStateOf(BurnoutStatus(BurnoutRisk.NONE, emptyList()))
    val burnout: State<BurnoutStatus> = _burnout

    // ─── GPA ───
    private val _gpaImpact = mutableStateOf(GpaImpact(bySubject = emptyList()))
    val gpaImpact: State<GpaImpact> = _gpaImpact

    // ─── Simulation ───
    private val _simulationResult = mutableStateOf<SimulationResult?>(null)
    val simulationResult: State<SimulationResult?> = _simulationResult

    // ─── Task Breakdown ───
    private val _breakdown = mutableStateOf<TaskBreakdown?>(null)
    val breakdown: State<TaskBreakdown?> = _breakdown

    // ─── Focus Mode ───
    private val _focusTask = mutableStateOf<LearningTask?>(null)
    val focusTask: State<LearningTask?> = _focusTask

    // ─── Academic Twin ───
    private val _academicTwin = mutableStateOf<AcademicTwin?>(null)
    val academicTwin: State<AcademicTwin?> = _academicTwin

    // ─── Semester Timeline ───
    private val _semesterTimeline = mutableStateOf<SemesterTimeline?>(null)
    val semesterTimeline: State<SemesterTimeline?> = _semesterTimeline

    // ─── Analytics ───
    private val _analytics = mutableStateOf(StudyAnalytics(0.0, emptyList(), emptyList(), ProcrastinationPattern(0.0, 0.0), FocusStats(0, 0), studyEfficiency = 0.0))
    val analytics: State<StudyAnalytics> = _analytics

    // ══════════════════════════════════════════════
    //  Actions
    // ══════════════════════════════════════════════

    fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            courseRepo.getCourses().onSuccess { _courses.value = it }
            val tasks = academicRepo.getAllTasks()
            _allTasks.value = tasks
            _todayTasks.value = tasks.filter { !it.completed }
            refreshAllAnalytics(tasks)
            _isLoading.value = false
        }
    }

    private suspend fun refreshAllAnalytics(tasks: List<LearningTask>) {
        val courses = _courses.value
        _riskProfile.value = academicRepo.assessRisk(tasks, courses)
        _workload.value = academicRepo.analyzeWorkload(tasks)
        _burnout.value = academicRepo.detectBurnout(tasks)
        _recommendations.value = academicRepo.getRecommendations(
            tasks, _riskProfile.value, _workload.value ?: return, _burnout.value
        )
        computeHealthScore()
    }

    private fun computeHealthScore() {
        val r = _riskProfile.value.overallRisk
        val b = _burnout.value.riskLevel
        val w = _workload.value?.overallStatus ?: WorkloadStatus.UNDERLOADED
        val riskScore = when (r) { RiskLevel.LOW -> 1.0; RiskLevel.MEDIUM -> 0.6; RiskLevel.HIGH -> 0.3; RiskLevel.CRITICAL -> 0.0 }
        val burnoutScore = when (b) { BurnoutRisk.NONE -> 1.0; BurnoutRisk.LOW -> 0.7; BurnoutRisk.MODERATE -> 0.4; BurnoutRisk.HIGH -> 0.0 }
        val workloadScore = when (w) { WorkloadStatus.UNDERLOADED -> 0.7; WorkloadStatus.BALANCED -> 1.0; WorkloadStatus.HEAVY -> 0.5; WorkloadStatus.CRITICAL -> 0.0 }
        val avg = (riskScore + burnoutScore + workloadScore) / 3.0
        _academicHealth.value = AcademicHealth(avg, listOf(
            HealthCategory("Risk", riskScore, 1.0),
            HealthCategory("Burnout", burnoutScore, 1.0),
            HealthCategory("Workload", workloadScore, 1.0),
        ))
    }

    // ─── Knowledge Graph ───

    fun loadKnowledgeGraph() {
        viewModelScope.launch {
            val graph = academicRepo.getKnowledgeGraph()
            val computed = academicRepo.computeGraphMetadata(graph.nodes, graph.links)
            _graphNodes.value = computed.nodes
            _graphLinks.value = computed.links
        }
    }

    fun selectGraphNode(node: GraphNode) {
        _selectedNode.value = node
        _learningPath.value = KnowledgeGraphEngine.generateLearningPath(
            node.id, _graphNodes.value, _graphLinks.value
        )
    }

    // ─── Tasks ───

    fun addTask(task: LearningTask) {
        viewModelScope.launch {
            academicRepo.saveTask(task)
            _allTasks.value = academicRepo.getAllTasks()
            refreshAllAnalytics(_allTasks.value)
        }
    }

    fun toggleTaskComplete(taskId: Long) {
        viewModelScope.launch {
            academicRepo.completeTask(taskId)
            _allTasks.value = academicRepo.getAllTasks()
            refreshAllAnalytics(_allTasks.value)
        }
    }

    // ─── Study Planner ───

    fun generatePlan(hoursPerDay: Double = 4.0) {
        _studyPlan.value = academicRepo.generatePlan(_allTasks.value, hoursPerDay)
    }

    // ─── Task Breakdown ───

    fun breakdownGoal(goal: String, difficulty: String = "medium") {
        _breakdown.value = academicRepo.breakdownGoal(goal, difficulty)
        _nextAction.value = TaskBreakdownEngine.generateNextAction(_breakdown.value!!)
    }

    // ─── Simulation ───

    fun simulateFailure(failedId: Long) {
        _simulationResult.value = academicRepo.simulateFailure(
            failedId, _graphNodes.value, _graphLinks.value
        )
    }

    // ─── Focus Mode ───

    fun setFocusTask(task: LearningTask) {
        _focusTask.value = task
    }

    fun clearFocus() { _focusTask.value = null }

    // ─── Semester Timeline ───

    fun loadSemesterTimeline(semesterStartMs: Long = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(8 * 7L)) {
        val weekMs = TimeUnit.DAYS.toMillis(7L)
        val weeks = (0 until 16).map { w ->
            val weekStart = semesterStartMs + w * weekMs
            val weekEnd = weekStart + weekMs
            val weekTasks = _allTasks.value.filter { t ->
                t.deadline?.let { d -> d in weekStart until weekEnd } ?: false
            }
            TimelineWeek(w + 1, "Tuần ${w + 1}", weekTasks.map { t ->
                TimelineEvent(t.title, t.deadline ?: 0, EventType.DEADLINE)
            })
        }
        _semesterTimeline.value = SemesterTimeline(
            "Học kỳ hiện tại", 0, 0, weeks
        )
    }

    // ─── GPA ───

    fun updateGpaForecast(currentGpa: Double, subjects: List<SubjectGpaImpact>) {
        _gpaImpact.value = academicRepo.forecastGpa(currentGpa, subjects)
    }

    // ─── Analytics ───

    fun loadAnalytics() {
        val tasks = _allTasks.value
        val completed = tasks.filter { it.completed }
        val consistency = if (tasks.isEmpty()) 1.0
            else completed.size.toDouble() / tasks.size
        _analytics.value = StudyAnalytics(
            consistencyScore = consistency,
            strongestSubjects = emptyList(),
            weakestSubjects = emptyList(),
            procrastinationPattern = ProcrastinationPattern(
                score = 1.0 - consistency,
                peakDelayHours = 0.0
            ),
            focusDuration = FocusStats(
                averageMinutes = completed.filter { it.achievedMinutes > 0 }
                    .let { if (it.isEmpty()) 0 else it.sumOf { a -> a.achievedMinutes } / it.size },
                bestSessionMinutes = completed.maxOfOrNull { it.achievedMinutes } ?: 0
            ),
            studyEfficiency = consistency
        )
    }

    // ─── Academic Twin ───

    fun computeAcademicTwin() {
        val health = _academicHealth.value.score
        val gpa = _gpaImpact.value.forecastGpa
        val risk = _riskProfile.value.overallRisk
        val overloadProb = when (_workload.value?.overallStatus) {
            WorkloadStatus.CRITICAL -> 0.9
            WorkloadStatus.HEAVY -> 0.6
            WorkloadStatus.BALANCED -> 0.2
            else -> 0.05
        }
        _academicTwin.value = AcademicTwin(
            projectedGpa = if (gpa > 0) gpa else 3.0,
            graduationYear = 2028,
            onTrack = risk != RiskLevel.HIGH && risk != RiskLevel.CRITICAL,
            overloadProbability = overloadProb,
            academicHealthScore = health * 10,
            insights = buildList {
                if (health < 0.4) add("Cần cải thiện điểm sức khỏe học tập khẩn cấp")
                if (overloadProb > 0.5) add("Nguy cơ quá tải cao — cần giảm khối lượng")
                if (gpa < 2.5 && gpa > 0) add("GPA đang thấp — ưu tiên các môn có trọng số cao")
                if (risk == RiskLevel.LOW) add("Đang đi đúng hướng, duy trì nhịp độ hiện tại")
            }
        )
    }
}

package vn.edu.uit.devorbit.mobile.repository

import vn.edu.uit.devorbit.mobile.engine.*
import vn.edu.uit.devorbit.mobile.model.*
import vn.edu.uit.devorbit.mobile.model.domain.*
import vn.edu.uit.devorbit.mobile.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AcademicRepository @Inject constructor(
    private val api: ApiService,
    private val taskDao: LearningTaskDao,
    private val courseRepo: DevOrbitRepository,
) {
    // ─── Learning Tasks ───

    suspend fun getAllTasks() = taskDao.getAll()

    suspend fun getIncompleteTasks() = taskDao.getIncomplete()

    suspend fun saveTask(task: LearningTask) = taskDao.insert(task)

    suspend fun completeTask(id: Long) = taskDao.setCompleted(id, true)

    suspend fun deleteTask(task: LearningTask) = taskDao.delete(task)

    // ─── Knowledge Graph (from API) ───

    suspend fun getKnowledgeGraph(): KnowledgeGraph {
        val response = api.getKnowledgeGraph()
        val nodes = response.nodes.map { dto ->
            GraphNode(
                id = dto.id,
                name = dto.name,
                code = dto.code,
                level = dto.level ?: 0,
                impactScore = dto.impactScore ?: 0.0,
                semester = dto.semester ?: courseSemesterFallback(dto.code),
                description = dto.description
            )
        }
        val links = response.links.map { dto ->
            GraphLink(sourceId = dto.source, targetId = dto.target, type = dto.type)
        }
        return KnowledgeGraph(nodes, links)
    }

    private fun courseSemesterFallback(code: String): Int? {
        // Heuristic fallback from course code prefix
        val num = code.filter { it.isDigit() }.take(2).toIntOrNull() ?: return null
        return when {
            num in 1..9 -> 1
            num in 10..19 -> 2
            num in 20..29 -> 3
            num in 30..39 -> 4
            num in 40..49 -> 5
            else -> null
        }
    }

    fun computeGraphMetadata(nodes: List<GraphNode>, links: List<GraphLink>): KnowledgeGraph {
        val levels = KnowledgeGraphEngine.computeLevels(nodes, links)
        val scores = KnowledgeGraphEngine.computeImpactScores(nodes, links)
        val enrichedNodes = nodes.map { n ->
            n.copy(level = levels[n.id] ?: 0, impactScore = scores[n.id] ?: 0.0)
        }
        return KnowledgeGraph(enrichedNodes, links)
    }

    // ─── Study Planner ───

    fun generatePlan(
        tasks: List<LearningTask>,
        hoursPerDay: Double = 4.0,
    ): StudyPlan = StudyPlannerEngine.generatePlan(
        tasks = tasks,
        availableHoursPerDay = hoursPerDay,
    )

    // ─── Risk ───

    suspend fun assessRisk(tasks: List<LearningTask>, courses: List<CourseSummary>): RiskProfile {
        val subjects: List<Pair<Long, String>> = courses.map { it.id to it.name }
        val overdueTasks: Map<Long, Int> = tasks
            .groupBy { it.subjectId ?: 0L }
            .mapValues { (_, ts) -> ts.count { !it.completed && it.deadline != null && it.deadline < System.currentTimeMillis() } }
        val consistencyScores: Map<Long, Double> = tasks
            .groupBy { it.subjectId ?: 0L }
            .mapValues { (_, ts) ->
                if (ts.isEmpty()) 1.0
                else ts.count { it.completed }.toDouble() / ts.size
            }
        val weakPrereqs: Map<Long, List<String>> = emptyMap()
        return RiskEngine.assessRisk(subjects, overdueTasks, consistencyScores, weakPrereqs)
    }

    // ─── Simulation ───

    fun simulateFailure(
        failedId: Long,
        nodes: List<GraphNode>,
        links: List<GraphLink>,
    ) = SimulationEngine.simulateFailure(failedId, nodes, links)

    // ─── Task Breakdown ───

    fun breakdownGoal(goal: String, difficulty: String = "medium") =
        TaskBreakdownEngine.breakdown(goal, difficulty)

    // ─── Workload ───

    fun analyzeWorkload(tasks: List<LearningTask>, weeks: Int = 16) =
        WorkloadEngine.analyzeWorkload(tasks, weeks)

    // ─── Burnout ───

    fun detectBurnout(tasks: List<LearningTask>) =
        BurnoutEngine.analyzeBurnout(tasks)

    // ─── GPA ───

    fun forecastGpa(currentGpa: Double, subjects: List<SubjectGpaImpact>) =
        GpaEngine.forecastGpa(currentGpa, subjects)

    // ─── Recommendations ───

    fun getRecommendations(
        tasks: List<LearningTask>,
        riskProfile: RiskProfile,
        workloadProfile: WorkloadProfile,
        burnoutStatus: BurnoutStatus? = null,
    ) = RecommendationEngine.generate(tasks, riskProfile, workloadProfile, burnoutStatus)
}

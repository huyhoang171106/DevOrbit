package vn.edu.uit.devorbit.mobile.domain.engine

import org.junit.Assert.*
import org.junit.Test
import vn.edu.uit.devorbit.mobile.domain.model.*

class RecommendationEngineTest {

    @Test
    fun `generates PREREQUISITE_REVIEW when weak prerequisites exist`() {
        val risk = RiskProfile(
            subjectRisks = listOf(
                SubjectRisk(subjectId = 1L, subjectName = "OOP", riskLevel = RiskLevel.LOW,
                    weakPrerequisites = listOf("C basics"), overdueTasks = 0, consistency = 0.9)
            ),
            overallRisk = RiskLevel.LOW,
            riskFactors = emptyList()
        )
        val result = RecommendationEngine.generate(
            tasks = emptyList(),
            riskProfile = risk,
            workloadProfile = WorkloadProfile(emptyList(), emptyList(), WorkloadStatus.UNDERLOADED)
        )
        assertTrue(result.any { it.category == RecommendationCategory.PREREQUISITE_REVIEW })
    }

    @Test
    fun `generates FOCUS_SUBJECT when subject risk is HIGH`() {
        val risk = RiskProfile(
            subjectRisks = listOf(
                SubjectRisk(subjectId = 1L, subjectName = "Data Structures", riskLevel = RiskLevel.HIGH,
                    weakPrerequisites = emptyList(), overdueTasks = 5, consistency = 0.2)
            ),
            overallRisk = RiskLevel.HIGH,
            riskFactors = emptyList()
        )
        val result = RecommendationEngine.generate(
            tasks = emptyList(),
            riskProfile = risk,
            workloadProfile = WorkloadProfile(emptyList(), emptyList(), WorkloadStatus.UNDERLOADED)
        )
        assertTrue(result.any { it.category == RecommendationCategory.FOCUS_SUBJECT && it.relatedSubjectId == 1L })
    }

    @Test
    fun `generates FOCUS_SUBJECT when subject risk is CRITICAL`() {
        val risk = RiskProfile(
            subjectRisks = listOf(
                SubjectRisk(subjectId = 2L, subjectName = "AI", riskLevel = RiskLevel.CRITICAL,
                    weakPrerequisites = emptyList(), overdueTasks = 10, consistency = 0.1)
            ),
            overallRisk = RiskLevel.CRITICAL,
            riskFactors = emptyList()
        )
        val result = RecommendationEngine.generate(
            tasks = emptyList(),
            riskProfile = risk,
            workloadProfile = WorkloadProfile(emptyList(), emptyList(), WorkloadStatus.UNDERLOADED)
        )
        assertTrue(result.any { it.category == RecommendationCategory.FOCUS_SUBJECT && it.relatedSubjectId == 2L })
    }

    @Test
    fun `generates WORKLOAD_REDUCTION when workload is CRITICAL`() {
        val result = RecommendationEngine.generate(
            tasks = emptyList(),
            riskProfile = RiskProfile(),
            workloadProfile = WorkloadProfile(emptyList(), emptyList(), WorkloadStatus.CRITICAL)
        )
        assertTrue(result.any { it.category == RecommendationCategory.WORKLOAD_REDUCTION })
    }

    @Test
    fun `does not generate WORKLOAD_REDUCTION when workload is not CRITICAL`() {
        val result = RecommendationEngine.generate(
            tasks = emptyList(),
            riskProfile = RiskProfile(),
            workloadProfile = WorkloadProfile(emptyList(), emptyList(), WorkloadStatus.BALANCED)
        )
        assertTrue(result.none { it.category == RecommendationCategory.WORKLOAD_REDUCTION })
    }

    @Test
    fun `generates BREAK_SUGGESTION when burnout is MODERATE`() {
        val burnout = BurnoutStatus(BurnoutRisk.MODERATE, emptyList())
        val result = RecommendationEngine.generate(
            tasks = emptyList(),
            riskProfile = RiskProfile(),
            workloadProfile = WorkloadProfile(emptyList(), emptyList(), WorkloadStatus.UNDERLOADED),
            burnoutStatus = burnout
        )
        assertTrue(result.any { it.category == RecommendationCategory.BREAK_SUGGESTION })
    }

    @Test
    fun `generates BREAK_SUGGESTION when burnout is HIGH`() {
        val burnout = BurnoutStatus(BurnoutRisk.HIGH, emptyList())
        val result = RecommendationEngine.generate(
            tasks = emptyList(),
            riskProfile = RiskProfile(),
            workloadProfile = WorkloadProfile(emptyList(), emptyList(), WorkloadStatus.UNDERLOADED),
            burnoutStatus = burnout
        )
        assertTrue(result.any { it.category == RecommendationCategory.BREAK_SUGGESTION })
    }

    @Test
    fun `does not generate BREAK_SUGGESTION when burnout is NONE`() {
        val burnout = BurnoutStatus(BurnoutRisk.NONE, emptyList())
        val result = RecommendationEngine.generate(
            tasks = emptyList(),
            riskProfile = RiskProfile(),
            workloadProfile = WorkloadProfile(emptyList(), emptyList(), WorkloadStatus.UNDERLOADED),
            burnoutStatus = burnout
        )
        assertTrue(result.none { it.category == RecommendationCategory.BREAK_SUGGESTION })
    }

    @Test
    fun `generates CONSISTENCY_IMPROVEMENT when any subject consistency below 0 dot 3`() {
        val risk = RiskProfile(
            subjectRisks = listOf(
                SubjectRisk(subjectId = 1L, subjectName = "Physics", riskLevel = RiskLevel.MEDIUM,
                    weakPrerequisites = emptyList(), overdueTasks = 2, consistency = 0.25)
            ),
            overallRisk = RiskLevel.MEDIUM,
            riskFactors = emptyList()
        )
        val result = RecommendationEngine.generate(
            tasks = emptyList(),
            riskProfile = risk,
            workloadProfile = WorkloadProfile(emptyList(), emptyList(), WorkloadStatus.UNDERLOADED)
        )
        assertTrue(result.any { it.category == RecommendationCategory.CONSISTENCY_IMPROVEMENT })
    }

    @Test
    fun `generates TIME_MANAGEMENT when 3 or more deadlines in same week`() {
        val now = System.currentTimeMillis()
        // Find the end of current ISO week so we can place 3 deadlines in the same week
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 12)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val monday = cal.timeInMillis
        val tasks = (1..3).map { i ->
            LearningTask(id = i.toLong(), title = "Deadline $i", deadline = monday + i * 86400000L,
                completed = false, createdAt = now - 86400000L, estimatedMinutes = 60, priority = 5)
        }
        val result = RecommendationEngine.generate(
            tasks = tasks,
            riskProfile = RiskProfile(),
            workloadProfile = WorkloadProfile(emptyList(), emptyList(), WorkloadStatus.UNDERLOADED)
        )
        assertTrue(result.any { it.category == RecommendationCategory.TIME_MANAGEMENT })
    }

    @Test
    fun `does not generate TIME_MANAGEMENT when less than 3 deadlines in same week`() {
        val now = System.currentTimeMillis()
        val tasks = listOf(
            LearningTask(id = 1L, title = "Single deadline", deadline = now + 86400000L,
                completed = false, createdAt = now - 3600000, estimatedMinutes = 30, priority = 3)
        )
        val result = RecommendationEngine.generate(
            tasks = tasks,
            riskProfile = RiskProfile(),
            workloadProfile = WorkloadProfile(emptyList(), emptyList(), WorkloadStatus.UNDERLOADED)
        )
        assertTrue(result.none { it.category == RecommendationCategory.TIME_MANAGEMENT })
    }

    @Test
    fun `returns empty list when no risk or workload issues`() {
        val result = RecommendationEngine.generate(
            tasks = emptyList(),
            riskProfile = RiskProfile(),
            workloadProfile = WorkloadProfile(emptyList(), emptyList(), WorkloadStatus.UNDERLOADED)
        )
        assertTrue(result.isEmpty())
    }

    @Test
    fun `recommendations are sorted by priority ascending`() {
        val risk = RiskProfile(
            subjectRisks = listOf(
                SubjectRisk(subjectId = 1L, subjectName = "OOP", riskLevel = RiskLevel.LOW,
                    weakPrerequisites = listOf("C"), overdueTasks = 0, consistency = 0.9)
            ),
            overallRisk = RiskLevel.LOW,
            riskFactors = emptyList()
        )
        val burnout = BurnoutStatus(BurnoutRisk.HIGH, emptyList())
        val result = RecommendationEngine.generate(
            tasks = emptyList(),
            riskProfile = risk,
            workloadProfile = WorkloadProfile(emptyList(), emptyList(), WorkloadStatus.CRITICAL),
            burnoutStatus = burnout
        )
        assertTrue(result.isNotEmpty())
        for (i in 0 until result.size - 1) {
            assertTrue("Priority should be ascending: ${result[i].priority} <= ${result[i+1].priority}",
                result[i].priority <= result[i+1].priority)
        }
    }
}

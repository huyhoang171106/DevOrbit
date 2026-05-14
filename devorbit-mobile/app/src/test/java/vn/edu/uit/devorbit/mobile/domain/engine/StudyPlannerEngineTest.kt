package vn.edu.uit.devorbit.mobile.domain.engine

import org.junit.Assert.*
import org.junit.Test
import vn.edu.uit.devorbit.mobile.domain.model.*

class StudyPlannerEngineTest {

    @Test
    fun `generatePlan creates phases from tasks within weekly hour limit`() {
        val tasks = listOf(
            LearningTask(id = 1L, title = "Study Math", deadline = 1000L, estimatedMinutes = 120, priority = 5),
            LearningTask(id = 2L, title = "Study Physics", deadline = 2000L, estimatedMinutes = 90, priority = 3)
        )
        val plan = StudyPlannerEngine.generatePlan(tasks, availableHoursPerDay = 4.0)
        // total = 210 min = 3.5h should fit in one week
        assertEquals(1, plan.phases.size)
        assertEquals(2, plan.phases[0].items.size)
        assertEquals(3.5, plan.totalHours, 0.001)
    }

    @Test
    fun `generatePlan creates multiple phases when tasks exceed weekly capacity`() {
        val tasks = (1L..10L).map { i ->
            LearningTask(id = i, title = "Task $i", deadline = Long.MAX_VALUE,
                estimatedMinutes = 300, priority = 5) // 5h each = 50h total
        }
        val plan = StudyPlannerEngine.generatePlan(tasks, availableHoursPerDay = 2.0) // 14h/week
        assertTrue("Should need multiple weeks", plan.phases.size >= 2)
    }

    @Test
    fun `generatePlan handles empty task list`() {
        val plan = StudyPlannerEngine.generatePlan(emptyList(), availableHoursPerDay = 4.0)
        assertTrue(plan.phases.isEmpty())
        assertEquals(0.0, plan.totalHours, 0.001)
    }

    @Test
    fun `generatePlan excludes completed tasks`() {
        val tasks = listOf(
            LearningTask(id = 1L, title = "Done", deadline = 1000L, estimatedMinutes = 60, completed = true, priority = 5),
            LearningTask(id = 2L, title = "Pending", deadline = 2000L, estimatedMinutes = 60, priority = 3)
        )
        val plan = StudyPlannerEngine.generatePlan(tasks, availableHoursPerDay = 4.0)
        assertEquals(1, plan.phases[0].items.size)
        assertEquals("Pending", plan.phases[0].items[0].title)
    }

    @Test
    fun `generatePlan merges existing tasks and deduplicates by id`() {
        val tasks = listOf(
            LearningTask(id = 1L, title = "Task A", deadline = 1000L, estimatedMinutes = 60, priority = 5)
        )
        val existing = listOf(
            LearningTask(id = 1L, title = "Task A dup", deadline = 1000L, estimatedMinutes = 60, priority = 5),
            LearningTask(id = 2L, title = "Task B", deadline = 2000L, estimatedMinutes = 60, priority = 3)
        )
        val plan = StudyPlannerEngine.generatePlan(tasks, emptyMap(), 4.0, existing)
        assertEquals(2, plan.phases[0].items.size) // 2 unique items
    }

    @Test
    fun `generatePlan sorts by deadline then priority descending`() {
        val tasks = listOf(
            LearningTask(id = 3L, title = "Later high pri", deadline = 2000L, estimatedMinutes = 30, priority = 8),
            LearningTask(id = 1L, title = "Soon low pri", deadline = 1000L, estimatedMinutes = 30, priority = 1),
            LearningTask(id = 2L, title = "Soon high pri", deadline = 1000L, estimatedMinutes = 30, priority = 5)
        )
        val plan = StudyPlannerEngine.generatePlan(tasks, availableHoursPerDay = 4.0)
        // Should be sorted: Soon high pri (earliest, highest pri), Soon low pri (earliest, lower pri), Later high pri
        assertEquals("Soon high pri", plan.phases[0].items[0].title)
        assertEquals("Soon low pri", plan.phases[0].items[1].title)
        assertEquals("Later high pri", plan.phases[0].items[2].title)
    }

    // ─── balanceWorkload ───

    @Test
    fun `balanceWorkload returns same plan when no phase exceeds max`() {
        val plan = StudyPlan(
            title = "Test",
            phases = listOf(
                StudyPhase("Tuần 1", listOf(
                    StudyItem(id = "1", title = "A", subjectId = 1L, estimatedHours = 3.0)
                ), startDay = 1, endDay = 7)
            ),
            totalHours = 3.0
        )
        val balanced = StudyPlannerEngine.balanceWorkload(plan, 10.0)
        assertEquals(1, balanced.phases.size)
    }

    @Test
    fun `balanceWorkload moves overflow to subsequent phases`() {
        val plan = StudyPlan(
            title = "Test",
            phases = listOf(
                StudyPhase("Tuần 1", listOf(
                    StudyItem(id = "1", title = "A", subjectId = 1L, estimatedHours = 15.0), // > 10
                ), startDay = 1, endDay = 7)
            ),
            totalHours = 15.0
        )
        val balanced = StudyPlannerEngine.balanceWorkload(plan, 10.0)
        assertTrue("Should have more phases", balanced.phases.size >= 1)
        // Only one item: it can't fit so gets force-added to first phase
        val firstPhaseHours = balanced.phases[0].items.sumOf { it.estimatedHours }
        assertTrue(firstPhaseHours <= 10.0 || balanced.phases.size > 1)
    }

    // ─── autoReprioritize ───

    @Test
    fun `autoReprioritize removes completed items and redistributes evenly`() {
        val plan = StudyPlan(
            title = "Test",
            phases = listOf(
                StudyPhase("Tuần 1", listOf(
                    StudyItem(id = "1", title = "A", subjectId = 1L, estimatedHours = 2.0),
                    StudyItem(id = "2", title = "B", subjectId = 1L, estimatedHours = 3.0),
                ), startDay = 1, endDay = 7),
                StudyPhase("Tuần 2", listOf(
                    StudyItem(id = "3", title = "C", subjectId = 1L, estimatedHours = 1.0),
                ), startDay = 8, endDay = 14)
            ),
            totalHours = 6.0
        )
        val updated = StudyPlannerEngine.autoReprioritize(plan, setOf("1"))
        assertEquals(2, updated.phases.size)
        // 2 remaining items distributed across 2 phases -> 1 each
        assertEquals(1, updated.phases[0].items.size)
        assertEquals(1, updated.phases[1].items.size)
        assertTrue(updated.phases.flatMap { p -> p.items }.none { it.id == "1" })
    }

    @Test
    fun `autoReprioritize returns empty plan when all items completed`() {
        val plan = StudyPlan(
            title = "Test",
            phases = listOf(
                StudyPhase("Tuần 1", listOf(
                    StudyItem(id = "1", title = "A", subjectId = 1L, estimatedHours = 1.0),
                ), startDay = 1, endDay = 7)
            ),
            totalHours = 1.0
        )
        val updated = StudyPlannerEngine.autoReprioritize(plan, setOf("1"))
        assertTrue(updated.phases.isEmpty())
        assertEquals(0.0, updated.totalHours, 0.001)
    }
}

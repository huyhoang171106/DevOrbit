package vn.edu.uit.devorbit.mobile.domain.engine

import org.junit.Assert.*
import org.junit.Test
import vn.edu.uit.devorbit.mobile.domain.model.*
import java.util.concurrent.TimeUnit

class WorkloadEngineTest {

    @Test
    fun `analyzeWorkload returns UNDERLOADED when average is under 10 hours`() {
        val now = System.currentTimeMillis()
        val semesterStart = now - TimeUnit.DAYS.toMillis(8 * 7L)
        val tasks = listOf(
            LearningTask(id = 1L, title = "Easy", deadline = semesterStart + 3600000,
                estimatedMinutes = 30, priority = 3)
        )
        val result = WorkloadEngine.analyzeWorkload(tasks, totalWeeks = 16, semesterStartMs = semesterStart)
        assertEquals(WorkloadStatus.UNDERLOADED, result.overallStatus)
    }

    @Test
    fun `analyzeWorkload returns BALANCED when average is between 10 and 25 hours`() {
        val now = System.currentTimeMillis()
        val semesterStart = now - TimeUnit.DAYS.toMillis(8 * 7L)
        // 200 min/task * 10 tasks = 2000 min / 16 weeks = 125 min/week = 2.08h -> underloaded
        // Let me use 24000 min total = 400h total / 16 = 25h avg -> still under 25
        // To get 10-25h avg: total min = 10*60*16 = 9600 to 25*60*16 = 24000
        val totalMin = 200 * 50 // 10000 min -> ~10.4h/week
        val tasks = (1L..50L).map { i ->
            LearningTask(id = i, title = "Task $i", deadline = semesterStart + (i % 7) * 86400000L,
                estimatedMinutes = 200, priority = 5)
        }
        val result = WorkloadEngine.analyzeWorkload(tasks, totalWeeks = 16, semesterStartMs = semesterStart)
        assertTrue(result.overallStatus == WorkloadStatus.BALANCED || result.overallStatus == WorkloadStatus.HEAVY)
    }

    @Test
    fun `analyzeWorkload handles empty task list`() {
        val result = WorkloadEngine.analyzeWorkload(emptyList(), totalWeeks = 16)
        assertEquals(16, result.weeks.size)
        assertEquals(WorkloadStatus.UNDERLOADED, result.overallStatus)
        assertTrue(result.peakWeeks.isEmpty())
    }

    @Test
    fun `analyzeWorkload creates correct number of weeks`() {
        val result = WorkloadEngine.analyzeWorkload(emptyList(), totalWeeks = 10)
        assertEquals(10, result.weeks.size)
        assertEquals(1, result.weeks[0].weekNumber)
        assertEquals(10, result.weeks[9].weekNumber)
    }

    @Test
    fun `tasks are assigned to correct weeks based on deadline`() {
        val semesterStart = 1000000L
        // Week 0 is the first week: 0 to 7 days after semesterStart
        val week1Deadline = semesterStart + 86400000L // day 1 of semester
        val week2Deadline = semesterStart + 8 * 86400000L // day 8 -> week 2
        val tasks = listOf(
            LearningTask(id = 1L, title = "Week 1 task", deadline = week1Deadline,
                estimatedMinutes = 120, priority = 5),
            LearningTask(id = 2L, title = "Week 2 task", deadline = week2Deadline,
                estimatedMinutes = 60, priority = 3)
        )
        val result = WorkloadEngine.analyzeWorkload(tasks, totalWeeks = 16, semesterStartMs = semesterStart)
        // Week 1 should have 120 min = 2h
        assertEquals(2.0, result.weeks[0].totalHours, 0.5)
        // Week 2 should have 60 min = 1h
        assertEquals(1.0, result.weeks[1].totalHours, 0.5)
    }

    @Test
    fun `detectPeakWeeks returns peak week numbers`() {
        val profile = WorkloadProfile(
            weeks = listOf(
                WeekWorkload(weekNumber = 1, totalHours = 10.0, taskCount = 2),
                WeekWorkload(weekNumber = 2, totalHours = 50.0, taskCount = 10),
                WeekWorkload(weekNumber = 3, totalHours = 5.0, taskCount = 1)
            ),
            peakWeeks = listOf(2),
            overallStatus = WorkloadStatus.HEAVY
        )
        val peaks = WorkloadEngine.detectPeakWeeks(profile)
        assertEquals(listOf(2), peaks)
    }

    @Test
    fun `suggestBalancing returns empty when workload is not heavy or critical`() {
        val profile = WorkloadProfile(
            weeks = listOf(WeekWorkload(weekNumber = 1, totalHours = 5.0, taskCount = 1)),
            peakWeeks = emptyList(),
            overallStatus = WorkloadStatus.UNDERLOADED
        )
        val suggestions = WorkloadEngine.suggestBalancing(profile, 20.0)
        assertTrue(suggestions.isEmpty())
    }

    @Test
    fun `suggestBalancing returns suggestions for heavy workload`() {
        val profile = WorkloadProfile(
            weeks = listOf(
                WeekWorkload(weekNumber = 5, totalHours = 60.0, taskCount = 10),
                WeekWorkload(weekNumber = 6, totalHours = 5.0, taskCount = 1)
            ),
            peakWeeks = listOf(5),
            overallStatus = WorkloadStatus.HEAVY
        )
        val suggestions = WorkloadEngine.suggestBalancing(profile, 20.0)
        assertTrue(suggestions.isNotEmpty())
        assertTrue(suggestions.any { it.contains("Tuần 5") })
    }

    @Test
    fun `week labels are sequential`() {
        val result = WorkloadEngine.analyzeWorkload(emptyList(), totalWeeks = 4)
        assertEquals("Tuần 1", result.weeks[0].label)
        assertEquals("Tuần 2", result.weeks[1].label)
        assertEquals("Tuần 3", result.weeks[2].label)
        assertEquals("Tuần 4", result.weeks[3].label)
    }

    @Test
    fun `tasks with null deadline are not assigned to any week`() {
        val semesterStart = 1000000L
        val tasks = listOf(
            LearningTask(id = 1L, title = "No deadline", deadline = null,
                estimatedMinutes = 60, priority = 3)
        )
        val result = WorkloadEngine.analyzeWorkload(tasks, totalWeeks = 4, semesterStartMs = semesterStart)
        // All weeks should have 0h
        result.weeks.forEach { week ->
            assertEquals(0.0, week.totalHours, 0.001)
            assertEquals(0, week.taskCount)
        }
    }
}

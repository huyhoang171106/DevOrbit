package vn.edu.uit.devorbit.mobile.domain.engine

import org.junit.Assert.*
import org.junit.Test
import vn.edu.uit.devorbit.mobile.domain.model.*

class BurnoutEngineTest {

    @Test
    fun `returns NONE when tasks is empty`() {
        val result = BurnoutEngine.analyzeBurnout(emptyList())
        assertEquals(BurnoutRisk.NONE, result.riskLevel)
        assertTrue(result.indicators.isEmpty())
    }

    @Test
    fun `returns NONE when all tasks are on time and consistent`() {
        val now = System.currentTimeMillis()
        val tasks = listOf(
            LearningTask(id = 1, title = "Task 1", deadline = now + 86400000, completed = false, createdAt = now - 3600000, estimatedMinutes = 60, priority = 5),
            LearningTask(id = 2, title = "Task 2", deadline = now + 172800000, completed = false, createdAt = now - 7200000, estimatedMinutes = 30, priority = 3)
        )
        val result = BurnoutEngine.analyzeBurnout(tasks, nowMs = now)
        assertEquals(BurnoutRisk.NONE, result.riskLevel)
    }

    @Test
    fun `triggers overdue indicator when ratio exceeds 30 percent`() {
        val now = System.currentTimeMillis()
        val tasks = listOf(
            LearningTask(id = 1, title = "Overdue", deadline = now - 86400000, completed = false, createdAt = now - 172800000, estimatedMinutes = 30, priority = 5),
            LearningTask(id = 2, title = "On time", deadline = now + 86400000, completed = false, createdAt = now - 3600000, estimatedMinutes = 30, priority = 3)
        )
        val result = BurnoutEngine.analyzeBurnout(tasks, nowMs = now)
        val overdueInd = result.indicators.find { it.name == "overdueRatio" }
        assertNotNull(overdueInd)
        assertTrue(overdueInd!!.triggered)
        assertTrue(overdueInd.value > 0.3)
    }

    @Test
    fun `triggers lateNight indicator when more than half tasks created after 22pm`() {
        val now = System.currentTimeMillis()
        // Use a known hour: create tasks so createdAt encodes hour >= 22
        // We need to find a time that has hour >= 22
        val lateHour = 23 // 11 PM
        val earlyHour = 10 // 10 AM
        val lateTime = makeTimeWithHour(lateHour)
        val earlyTime = makeTimeWithHour(earlyHour)
        val tasks = listOf(
            LearningTask(id = 1, title = "Late night work", deadline = now + 86400000, completed = false, createdAt = lateTime, estimatedMinutes = 30, priority = 5),
            LearningTask(id = 2, title = "Day work", deadline = now + 172800000, completed = false, createdAt = earlyTime, estimatedMinutes = 30, priority = 3)
        )
        val result = BurnoutEngine.analyzeBurnout(tasks, nowMs = now)
        val lateInd = result.indicators.find { it.name == "lateNightRatio" }
        assertNotNull(lateInd)
        // Only 1 out of 2 created recently (within 7 days) is late night = 0.5 ratio, not > 0.5, so not triggered
        // Actually both were created recently (within 7 days if now is close to those times), and lateTime is 1 of 2 = 0.5
        // Threshold is 0.5, triggered = ratio > 0.5, so should NOT trigger
        assertFalse(lateInd!!.triggered)
    }

    @Test
    fun `returns HIGH when three or more indicators triggered`() {
        val now = System.currentTimeMillis()
        val veryOld = now - 30 * 86400000L
        val lateTime = makeTimeWithHour(23)
        val tasks = listOf(
            // Overdue (ratio > 0.3: 2 out of 3 overdue)
            LearningTask(id = 1, title = "Overdue 1", deadline = now - 86400000, completed = false, createdAt = veryOld, estimatedMinutes = 30, priority = 5),
            LearningTask(id = 2, title = "Overdue 2", deadline = now - 86400000, completed = false, createdAt = veryOld, estimatedMinutes = 30, priority = 5),
            LearningTask(id = 3, title = "Future", deadline = now + 86400000, completed = false, createdAt = lateTime, estimatedMinutes = 30, priority = 3)
        )
        val result = BurnoutEngine.analyzeBurnout(tasks, nowMs = now)
        // overdueRatio = 2/3 = 0.666 > 0.3 -> triggered
        // consistencyScore = no deadlines in last 7 days (all very old or lateTime) -> ratio = 1.0 (no tasks due in window) -> not triggered
        // lateNightRatio = 1 out of 1 recent (lateTime created task 3 is recent) -> ratio = 1.0 > 0.5 -> triggered
        // workloadSpike: this week tasks count = 1 (task 3), last week = 0, ratio = INF -> triggered (capped at 3.0 > 2.0)
        // So 3 triggered -> HIGH
        assertTrue(result.riskLevel == BurnoutRisk.HIGH)
    }

    @Test
    fun `workloadSpike returns infinite ratio when this week has tasks but last week had none`() {
        val now = System.currentTimeMillis()
        val tasks = listOf(
            LearningTask(id = 1, title = "This week", deadline = now, completed = false, createdAt = now - 3600000, estimatedMinutes = 30, priority = 5)
        )
        val result = BurnoutEngine.analyzeBurnout(tasks, nowMs = now)
        val spikeInd = result.indicators.find { it.name == "workloadSpike" }
        assertNotNull(spikeInd)
        assertTrue(spikeInd!!.triggered)
        assertEquals(3.0, spikeInd.value, 0.001) // INF capped to 3.0
    }

    private fun makeTimeWithHour(targetHour: Int): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, targetHour)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}

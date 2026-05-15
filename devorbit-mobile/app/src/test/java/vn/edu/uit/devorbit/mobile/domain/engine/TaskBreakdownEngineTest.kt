package vn.edu.uit.devorbit.mobile.domain.engine

import org.junit.Assert.*
import org.junit.Test
import vn.edu.uit.devorbit.mobile.domain.model.*

class TaskBreakdownEngineTest {

    @Test
    fun `breakdown generates steps for study goal with medium difficulty default`() {
        val result = TaskBreakdownEngine.breakdown("study math")
        assertEquals("study math", result.goal)
        assertTrue(result.steps.isNotEmpty())
        assertEquals(5, result.steps.size)
        assertTrue(result.totalEstimatedMinutes > 0)
    }

    @Test
    fun `breakdown first step is marked as next action`() {
        val result = TaskBreakdownEngine.breakdown("Learn Kotlin")
        assertTrue(result.steps[0].isNextAction)
        assertFalse(result.steps[1].isNextAction)
    }

    @Test
    fun `breakdown uses project template when goal mentions project`() {
        val result = TaskBreakdownEngine.breakdown("do a project")
        assertEquals(5, result.steps.size)
        // project template: "Phân tích yêu cầu", "Thiết kế kiến trúc", etc.
        assertTrue(result.steps.any { it.title.contains("Phân tích") })
    }

    @Test
    fun `breakdown uses review template when goal mentions review`() {
        val result = TaskBreakdownEngine.breakdown("review for exam")
        assertTrue(result.steps.any { it.title.contains("Ôn") })
    }

    @Test
    fun `breakdown uses practice template when goal mentions luyện`() {
        val result = TaskBreakdownEngine.breakdown("luyện tập coding")
        assertTrue(result.steps.any { it.title.contains("Khởi động") })
    }

    @Test
    fun `breakdown uses read template when goal mentions đọc`() {
        val result = TaskBreakdownEngine.breakdown("đọc tài liệu")
        assertTrue(result.steps.any { it.title.contains("Đọc lướt") })
    }

    @Test
    fun `breakdown uses study template as default for unknown goals`() {
        val result = TaskBreakdownEngine.breakdown("xyz random goal")
        assertTrue(result.steps.any { it.title.contains("Tìm hiểu") })
    }

    @Test
    fun `breakdown adjusts base minutes by difficulty`() {
        val easy = TaskBreakdownEngine.breakdown("study", "easy")
        val hard = TaskBreakdownEngine.breakdown("study", "hard")
        val medium = TaskBreakdownEngine.breakdown("study", "medium")
        // easy -> 15 min base, medium -> 25, hard -> 40
        val totalEasy = easy.steps.sumOf { it.estimatedMinutes }
        val totalHard = hard.steps.sumOf { it.estimatedMinutes }
        assertTrue("Hard should take longer than easy", totalHard > totalEasy)
    }

    @Test
    fun `generateNextAction returns first step with isNextAction true`() {
        val breakdown = TaskBreakdownEngine.breakdown("study")
        val next = TaskBreakdownEngine.generateNextAction(breakdown)
        assertNotNull(next)
        assertTrue(next!!.isNextAction)
        assertEquals(breakdown.steps[0].id, next.id)
    }

    @Test
    fun `markStepDone moves next action to subsequent step`() {
        val breakdown = TaskBreakdownEngine.breakdown("study")
        val step1 = breakdown.steps[0]
        val updated = TaskBreakdownEngine.markStepDone(breakdown, step1.id)
        assertFalse(updated.steps[0].isNextAction)
        assertTrue(updated.steps[1].isNextAction)
    }

    @Test
    fun `markStepDone does nothing when step id not found`() {
        val breakdown = TaskBreakdownEngine.breakdown("study")
        val updated = TaskBreakdownEngine.markStepDone(breakdown, "nonexistent")
        assertEquals(breakdown.steps.size, updated.steps.size)
        assertTrue(updated.steps[0].isNextAction) // unchanged
    }
}

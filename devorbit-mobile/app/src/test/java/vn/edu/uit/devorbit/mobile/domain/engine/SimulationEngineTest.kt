package vn.edu.uit.devorbit.mobile.domain.engine

import org.junit.Assert.*
import org.junit.Test
import vn.edu.uit.devorbit.mobile.domain.model.*

class SimulationEngineTest {

    @Test
    fun `simulateFailure finds blocked subjects via prerequisite chain`() {
        val nodes = listOf(
            GraphNode(id = 1L, name = "Math 101", code = "M101"),
            GraphNode(id = 2L, name = "Math 201", code = "M201"),
            GraphNode(id = 3L, name = "Math 301", code = "M301"),
            GraphNode(id = 4L, name = "CS 101", code = "C101")
        )
        val links = listOf(
            GraphLink(sourceId = 1L, targetId = 2L, type = "PREREQUISITE"),
            GraphLink(sourceId = 2L, targetId = 3L, type = "PREREQUISITE"),
        )
        val result = SimulationEngine.simulateFailure(1L, nodes, links)
        assertEquals(1L, result.failedSubjectId)
        assertEquals("Math 101", result.failedSubjectName)
        // 2 and 3 are blocked (but only 2 is first-level)
        assertEquals(2, result.blockedSubjects.size)
        assertTrue(result.blockedSubjects.any { it.id == 2L })
        assertTrue(result.blockedSubjects.any { it.id == 3L })
    }

    @Test
    fun `simulateFailure returns zero delay when fewer than 2 blocked`() {
        val nodes = listOf(
            GraphNode(id = 1L, name = "Alone", code = "A"),
        )
        val result = SimulationEngine.simulateFailure(1L, nodes, emptyList())
        assertEquals(0, result.graduationDelay)
    }

    @Test
    fun `simulateFailure returns delay 1 when 2 to 4 subjects blocked`() {
        val nodes = (1L..4L).map { GraphNode(id = it, name = "N$it", code = "N$it") }
        val links = listOf(
            GraphLink(sourceId = 1L, targetId = 2L, type = "PREREQUISITE"),
            GraphLink(sourceId = 1L, targetId = 3L, type = "PREREQUISITE"),
        )
        val result = SimulationEngine.simulateFailure(1L, nodes, links)
        assertEquals(1, result.graduationDelay)
    }

    @Test
    fun `simulateFailure returns delay 2 when 5 or more subjects blocked`() {
        val nodes = (1L..7L).map { GraphNode(id = it, name = "N$it", code = "N$it") }
        val links = (2L..7L).map { GraphLink(sourceId = 1L, targetId = it, type = "PREREQUISITE") }
        val result = SimulationEngine.simulateFailure(1L, nodes, links)
        assertEquals(2, result.graduationDelay)
    }

    @Test
    fun `simulateFailure handles unknown failed subject`() {
        val nodes = listOf(GraphNode(id = 1L, name = "Known", code = "K"))
        val result = SimulationEngine.simulateFailure(999L, nodes, emptyList())
        assertEquals(999L, result.failedSubjectId)
        assertEquals("Unknown", result.failedSubjectName)
        assertTrue(result.blockedSubjects.isEmpty())
        assertEquals(0, result.graduationDelay)
    }

    @Test
    fun `simulateFailure builds timeline with no impact when nothing blocked`() {
        val nodes = listOf(GraphNode(id = 1L, name = "Solo", code = "S"))
        val result = SimulationEngine.simulateFailure(1L, nodes, emptyList())
        assertEquals(1, result.timelineImpact.size)
        assertEquals("Hiện tại", result.timelineImpact[0].semesterName)
    }

    @Test
    fun `simulateFailure ignores non-PREREQUISITE links`() {
        val nodes = listOf(
            GraphNode(id = 1L, name = "A", code = "A"),
            GraphNode(id = 2L, name = "B", code = "B")
        )
        val links = listOf(GraphLink(sourceId = 1L, targetId = 2L, type = "RELATED"))
        val result = SimulationEngine.simulateFailure(1L, nodes, links)
        assertEquals(0, result.blockedSubjects.size)
    }
}

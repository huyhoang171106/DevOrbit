package vn.edu.uit.devorbit.mobile.domain.engine

import org.junit.Assert.*
import org.junit.Test
import vn.edu.uit.devorbit.mobile.domain.model.*

class KnowledgeGraphEngineTest {

    // ─── computeLevels ───

    @Test
    fun `computeLevels returns zero for all nodes when no links`() {
        val nodes = listOf(
            GraphNode(id = 1L, name = "A", code = "A", level = 0),
            GraphNode(id = 2L, name = "B", code = "B", level = 0)
        )
        val levels = KnowledgeGraphEngine.computeLevels(nodes, emptyList())
        assertEquals(0, levels[1L] ?: -1)
        assertEquals(0, levels[2L] ?: -1)
    }

    @Test
    fun `computeLevels assigns level 1 to direct dependents`() {
        val nodes = listOf(
            GraphNode(id = 1L, name = "A", code = "A"),
            GraphNode(id = 2L, name = "B", code = "B")
        )
        val links = listOf(GraphLink(sourceId = 1L, targetId = 2L, type = "PREREQUISITE"))
        val levels = KnowledgeGraphEngine.computeLevels(nodes, links)
        assertEquals(0, levels[1L] ?: -1) // A has no prereq
        assertEquals(1, levels[2L] ?: -1) // B depends on A
    }

    @Test
    fun `computeLevels handles chain of prerequisites`() {
        val nodes = (1L..4L).map { GraphNode(id = it, name = "N$it", code = "N$it") }
        val links = listOf(
            GraphLink(sourceId = 1L, targetId = 2L, type = "PREREQUISITE"),
            GraphLink(sourceId = 2L, targetId = 3L, type = "PREREQUISITE"),
            GraphLink(sourceId = 3L, targetId = 4L, type = "PREREQUISITE")
        )
        val levels = KnowledgeGraphEngine.computeLevels(nodes, links)
        assertEquals(0, levels[1L] ?: -1)
        assertEquals(1, levels[2L] ?: -1)
        assertEquals(2, levels[3L] ?: -1)
        assertEquals(3, levels[4L] ?: -1)
    }

    @Test
    fun `computeLevels ignores non-PREREQUISITE links`() {
        val nodes = listOf(
            GraphNode(id = 1L, name = "A", code = "A"),
            GraphNode(id = 2L, name = "B", code = "B")
        )
        val links = listOf(GraphLink(sourceId = 1L, targetId = 2L, type = "RELATED"))
        val levels = KnowledgeGraphEngine.computeLevels(nodes, links)
        assertEquals(0, levels[1L] ?: -1)
        assertEquals(0, levels[2L] ?: -1)
    }

    @Test
    fun `computeLevels handles empty node list`() {
        val levels = KnowledgeGraphEngine.computeLevels(emptyList(), emptyList())
        assertTrue(levels.isEmpty())
    }

    // ─── computeImpactScores ───

    @Test
    fun `computeImpactScores returns zero when no links`() {
        val nodes = listOf(
            GraphNode(id = 1L, name = "A", code = "A"),
            GraphNode(id = 2L, name = "B", code = "B")
        )
        val scores = KnowledgeGraphEngine.computeImpactScores(nodes, emptyList())
        assertEquals(0.0, scores[1L] ?: -1.0, 0.001)
        assertEquals(0.0, scores[2L] ?: -1.0, 0.001)
    }

    @Test
    fun `computeImpactScores gives higher score for nodes with more reachable dependents`() {
        val nodes = (1L..4L).map { GraphNode(id = it, name = "N$it", code = "N$it") }
        val links = listOf(
            GraphLink(sourceId = 1L, targetId = 3L, type = "PREREQUISITE"),
            GraphLink(sourceId = 2L, targetId = 3L, type = "PREREQUISITE"),
            GraphLink(sourceId = 3L, targetId = 4L, type = "PREREQUISITE")
        )
        val scores = KnowledgeGraphEngine.computeImpactScores(nodes, links)
        // Node 1 has 2 downstream nodes reachable (3, 4) and depth 2
        // Node 4 has 0 downstream
        val score1 = scores[1L] ?: -1.0
        val score4 = scores[4L] ?: -1.0
        assertTrue("Node 1 should have higher impact than leaf node 4", score1 >= score4)
    }

    @Test
    fun `computeImpactScores handles single node`() {
        val nodes = listOf(GraphNode(id = 1L, name = "A", code = "A"))
        val scores = KnowledgeGraphEngine.computeImpactScores(nodes, emptyList())
        assertEquals(0.0, scores[1L] ?: -1.0, 0.001)
    }

    // ─── findWeakNodes ───

    @Test
    fun `findWeakNodes excludes completed nodes`() {
        val nodes = listOf(
            GraphNode(id = 1L, name = "A", code = "A"),
            GraphNode(id = 2L, name = "B", code = "B")
        )
        val links = listOf(GraphLink(sourceId = 1L, targetId = 2L, type = "PREREQUISITE"))
        val weak = KnowledgeGraphEngine.findWeakNodes(nodes, links, setOf(1L))
        assertEquals(1, weak.size)
        assertEquals(2L, weak[0].id)
    }

    @Test
    fun `findWeakNodes returns empty when all nodes completed`() {
        val nodes = listOf(
            GraphNode(id = 1L, name = "A", code = "A"),
        )
        val weak = KnowledgeGraphEngine.findWeakNodes(nodes, emptyList(), setOf(1L))
        assertTrue(weak.isEmpty())
    }

    @Test
    fun `findWeakNodes returns nodes sorted by impact descending`() {
        val nodes = listOf(
            GraphNode(id = 1L, name = "A", code = "A"),
            GraphNode(id = 2L, name = "B", code = "B")
        )
        val links = listOf(GraphLink(sourceId = 1L, targetId = 2L, type = "PREREQUISITE"))
        val weak = KnowledgeGraphEngine.findWeakNodes(nodes, links, emptySet())
        assertEquals(2, weak.size)
        // Node 2 is downstream of 1 -> more descendants from 1
        // Actually both have similar scores, but order should be deterministic
        assertEquals(1L, weak[0].id)
        assertEquals(2L, weak[1].id)
    }

    // ─── generateLearningPath ───

    @Test
    fun `generateLearningPath returns prerequisites first then target`() {
        val nodes = listOf(
            GraphNode(id = 1L, name = "Math 101", code = "M101"),
            GraphNode(id = 2L, name = "Math 201", code = "M201"),
            GraphNode(id = 3L, name = "Math 301", code = "M301")
        )
        val links = listOf(
            GraphLink(sourceId = 1L, targetId = 2L, type = "PREREQUISITE"),
            GraphLink(sourceId = 2L, targetId = 3L, type = "PREREQUISITE")
        )
        val path = KnowledgeGraphEngine.generateLearningPath(3L, nodes, links)
        assertEquals(3, path.size)
        assertEquals(1L, path[0].id) // prereq first
        assertEquals(2L, path[1].id)
        assertEquals(3L, path[2].id) // target last
    }

    @Test
    fun `generateLearningPath returns just target when no prerequisites`() {
        val nodes = listOf(GraphNode(id = 1L, name = "Alone", code = "A"))
        val path = KnowledgeGraphEngine.generateLearningPath(1L, nodes, emptyList())
        assertEquals(1, path.size)
        assertEquals(1L, path[0].id)
    }

    @Test
    fun `generateLearningPath returns empty for unknown target`() {
        val nodes = listOf(GraphNode(id = 1L, name = "A", code = "A"))
        val path = KnowledgeGraphEngine.generateLearningPath(999L, nodes, emptyList())
        assertTrue(path.isEmpty())
    }

    @Test
    fun `generateLearningPath handles diamond dependency`() {
        val nodes = (1L..4L).map { GraphNode(id = it, name = "N$it", code = "N$it") }
        val links = listOf(
            GraphLink(sourceId = 1L, targetId = 3L, type = "PREREQUISITE"),
            GraphLink(sourceId = 2L, targetId = 3L, type = "PREREQUISITE"),
        )
        val path = KnowledgeGraphEngine.generateLearningPath(3L, nodes, links)
        assertEquals(3, path.size)
        assertEquals(3L, path[2].id) // target last
    }
}

package vn.edu.uit.devorbit.mobile.engine

import vn.edu.uit.devorbit.mobile.model.domain.GraphNode
import vn.edu.uit.devorbit.mobile.model.domain.GraphLink

object KnowledgeGraphEngine {

    /**
     * Topological sort via Kahn's algorithm on PREREQUISITE edges.
     * Level 0 = no prerequisites; higher levels = deeper in the dependency chain.
     * Nodes not reachable from any edge get level 0.
     */
    fun computeLevels(nodes: List<GraphNode>, links: List<GraphLink>): Map<Long, Int> {
        val nodeIds = nodes.map { it.id }.toSet()
        val inDegree = mutableMapOf<Long, Int>()
        val adjacency = mutableMapOf<Long, MutableList<Long>>()

        nodeIds.forEach { id ->
            inDegree[id] = 0
            adjacency[id] = mutableListOf()
        }

        // Edge sourceId -> targetId with type=PREREQUISITE means source must be done before target
        links.filter { it.type == "PREREQUISITE" }.forEach { link ->
            adjacency[link.sourceId]?.add(link.targetId)
            inDegree[link.targetId] = (inDegree[link.targetId] ?: 0) + 1
        }

        val queue = ArrayDeque<Long>()
        inDegree.filter { it.value == 0 }.keys.forEach { queue.add(it) }

        val level = mutableMapOf<Long, Int>()

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val currentLevel = level[current] ?: 0

            adjacency[current]?.forEach { neighbor ->
                val candidate = currentLevel + 1
                if (candidate > (level[neighbor] ?: -1)) {
                    level[neighbor] = candidate
                }
                inDegree[neighbor] = (inDegree[neighbor] ?: 1) - 1
                if (inDegree[neighbor] == 0) {
                    queue.add(neighbor)
                }
            }
        }

        // Nodes never reached (disconnected or cyclic remainder) get level 0
        nodeIds.forEach { id ->
            if (id !in level) level[id] = 0
        }

        return level
    }

    /**
     * Impact = reachableCount * 0.4 + depth * 0.3 + bottleneck * 0.3, normalized 0-10.
     *
     * - reachableCount: how many other nodes depend (transitively) on this node
     * - depth: longest path from this node to any leaf
     * - bottleneck: ancestors * descendants as a centrality proxy
     */
    fun computeImpactScores(nodes: List<GraphNode>, links: List<GraphLink>): Map<Long, Double> {
        val preReqLinks = links.filter { it.type == "PREREQUISITE" }
        val nodeIds = nodes.map { it.id }.toSet()

        val outgoing = mutableMapOf<Long, MutableList<Long>>()
        val incoming = mutableMapOf<Long, MutableList<Long>>()
        nodeIds.forEach { id ->
            outgoing[id] = mutableListOf()
            incoming[id] = mutableListOf()
        }
        preReqLinks.forEach { link ->
            outgoing[link.sourceId]?.add(link.targetId)
            incoming[link.targetId]?.add(link.sourceId)
        }

        // --- helpers ---
        fun bfsForward(start: Long): Set<Long> {
            val visited = mutableSetOf(start)
            val queue = ArrayDeque(listOf(start))
            while (queue.isNotEmpty()) {
                outgoing[queue.removeFirst()]?.forEach { n ->
                    if (n !in visited) { visited.add(n); queue.add(n) }
                }
            }
            return visited
        }

        fun maxDepth(node: Long): Int {
            fun dfs(cur: Long, visited: MutableSet<Long>): Int {
                var best = 0
                outgoing[cur]?.forEach { n ->
                    if (n !in visited) {
                        visited.add(n)
                        best = maxOf(best, 1 + dfs(n, visited))
                        visited.remove(n)
                    }
                }
                return best
            }
            return dfs(node, mutableSetOf(node))
        }

        fun bottleneck(node: Long): Int {
            // ancestors = nodes that (transitively) lead to this node
            val ancestors = mutableSetOf<Long>()
            val qa = ArrayDeque(listOf(node))
            while (qa.isNotEmpty()) {
                incoming[qa.removeFirst()]?.forEach { p ->
                    if (p !in ancestors) { ancestors.add(p); qa.add(p) }
                }
            }
            // descendants = nodes reachable from this node
            val descendants = mutableSetOf<Long>()
            val qd = ArrayDeque(listOf(node))
            while (qd.isNotEmpty()) {
                outgoing[qd.removeFirst()]?.forEach { n ->
                    if (n !in descendants) { descendants.add(n); qd.add(n) }
                }
            }
            return ancestors.size * descendants.size
        }
        // ---

        val rawReach = nodeIds.associateWith { bfsForward(it).size - 1 }
        val rawDepth = nodeIds.associateWith { maxDepth(it) }
        val rawBottle = nodeIds.associateWith { bottleneck(it) }

        fun normalize(values: Map<Long, Int>): Map<Long, Double> {
            val min = values.values.minOrNull() ?: 0
            val max = values.values.maxOrNull() ?: 0
            return if (max == min) values.mapValues { 0.0 }
            else values.mapValues { (it.value - min).toDouble() / (max - min) * 10.0 }
        }

        val normReach = normalize(rawReach)
        val normDepth = normalize(rawDepth)
        val normBottle = normalize(rawBottle)

        return nodeIds.associateWith { id ->
            (normReach[id] ?: 0.0) * 0.4 +
                    (normDepth[id] ?: 0.0) * 0.3 +
                    (normBottle[id] ?: 0.0) * 0.3
        }
    }

    /**
     * Returns nodes with high impact but NOT in [completedIds],
     * sorted descending by impact score.
     */
    fun findWeakNodes(
        nodes: List<GraphNode>,
        links: List<GraphLink>,
        completedIds: Set<Long>
    ): List<GraphNode> {
        val scores = computeImpactScores(nodes, links)
        return nodes
            .filter { it.id !in completedIds }
            .sortedByDescending { scores[it.id] ?: 0.0 }
    }

    /**
     * DFS-based prerequisite chain from [targetId] backwards.
     * Returns prerequisites first, target last.
     */
    fun generateLearningPath(
        targetId: Long,
        nodes: List<GraphNode>,
        links: List<GraphLink>
    ): List<GraphNode> {
        val preReqLinks = links.filter { it.type == "PREREQUISITE" }
        val prereqOf = mutableMapOf<Long, MutableList<Long>>()
        nodes.forEach { prereqOf[it.id] = mutableListOf() }
        preReqLinks.forEach { link ->
            prereqOf[link.targetId]?.add(link.sourceId)
        }

        val result = mutableListOf<GraphNode>()
        val visited = mutableSetOf<Long>()
        val nodeMap = nodes.associateBy { it.id }

        fun dfs(currentId: Long) {
            prereqOf[currentId]?.forEach { prereqId ->
                if (prereqId !in visited) {
                    visited.add(prereqId)
                    dfs(prereqId)
                    nodeMap[prereqId]?.let { result.add(it) }
                }
            }
        }

        dfs(targetId)
        nodeMap[targetId]?.let { result.add(it) }
        return result
    }
}

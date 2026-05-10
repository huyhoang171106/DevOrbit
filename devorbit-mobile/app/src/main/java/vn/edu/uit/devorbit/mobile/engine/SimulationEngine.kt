package vn.edu.uit.devorbit.mobile.engine

import vn.edu.uit.devorbit.mobile.model.domain.*

object SimulationEngine {

    fun simulateFailure(
        failedSubjectId: Long,
        allNodes: List<GraphNode>,
        links: List<GraphLink>,
    ): SimulationResult {
        val failedNode = allNodes.find { it.id == failedSubjectId }
        val blocked = findBlockedSubjects(failedSubjectId, allNodes, links)
        val delay = estimateDelay(blocked.size)
        val timeline = buildImpactTimeline(failedNode?.name ?: "Unknown", blocked)
        return SimulationResult(
            failedSubjectId = failedSubjectId,
            failedSubjectName = failedNode?.name ?: "Unknown",
            blockedSubjects = blocked,
            graduationDelay = delay,
            timelineImpact = timeline
        )
    }

    private fun findBlockedSubjects(
        failedId: Long,
        nodes: List<GraphNode>,
        links: List<GraphLink>,
    ): List<BlockedSubject> {
        val adj = mutableMapOf<Long, MutableList<Long>>()
        nodes.forEach { adj[it.id] = mutableListOf() }
        links.forEach { link ->
            if (link.type == "PREREQUISITE") {
                adj[link.sourceId]?.add(link.targetId)
            }
        }

        val visited = mutableSetOf<Long>()
        val queue = ArrayDeque<Long>()
        queue.add(failedId)
        visited.add(failedId)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            adj[current]?.forEach { next ->
                if (visited.add(next)) queue.add(next)
            }
        }

        val blockedIds = visited - failedId
        val nodeMap = nodes.associateBy { it.id }

        // Only first-level blocked get explicit reasons
        val firstLevel = adj[failedId]?.filter { blockedIds.contains(it) }?.toSet() ?: emptySet()
        val downstream = blockedIds - firstLevel

        return blockedIds.map { id ->
            val node = nodeMap[id]
            val reason = when {
                id in firstLevel -> "$failedId là tiên quyết của môn này"
                else -> "Bị ảnh hưởng gián tiếp qua chuỗi tiên quyết"
            }
            BlockedSubject(id, node?.name ?: "Môn #$id", reason)
        }
    }

    private fun estimateDelay(blockedCount: Int): Int {
        return when {
            blockedCount >= 5 -> 2
            blockedCount >= 2 -> 1
            else -> 0
        }
    }

    private fun buildImpactTimeline(
        failedName: String,
        blocked: List<BlockedSubject>,
    ): List<TimelineEntry> {
        if (blocked.isEmpty()) return listOf(
            TimelineEntry("Hiện tại", listOf("Rớt $failedName"), "Không ảnh hưởng môn khác")
        )
        val chunked = blocked.chunked((blocked.size + 1) / 2)
        return listOf(
            TimelineEntry("Học kỳ này", listOf("❌ $failedName"), "Cần học lại vào hè"),
            TimelineEntry("Học kỳ sau", chunked.getOrElse(0) { emptyList() }.map { "⛔ ${it.name}" }, "Bị chặn bởi tiên quyết"),
            TimelineEntry("Học kỳ tiếp", chunked.getOrElse(1) { emptyList() }.map { "⚠️ ${it.name}" }, "Có thể bị trễ nếu không điều chỉnh"),
        )
    }
}

package vn.edu.uit.devorbit.mobile.model.domain

data class GraphNode(
    val id: Long,
    val name: String,
    val code: String,
    val level: Int = 0,
    val impactScore: Double = 0.0,
    val semester: Int? = null,
    val description: String? = null,
)

data class GraphLink(
    val sourceId: Long,
    val targetId: Long,
    val type: String
)

data class KnowledgeGraph(
    val nodes: List<GraphNode>,
    val links: List<GraphLink>
)

data class Concept(
    val id: String,
    val name: String,
    val subjectId: Long,
    val prerequisites: List<String>,
    val mastered: Boolean = false
)

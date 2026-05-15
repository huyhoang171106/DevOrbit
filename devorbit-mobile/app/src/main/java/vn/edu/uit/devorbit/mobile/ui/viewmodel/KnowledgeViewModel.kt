package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.repository.AcademicRepository
import vn.edu.uit.devorbit.mobile.domain.engine.KnowledgeGraphEngine
import vn.edu.uit.devorbit.mobile.domain.model.GraphLink
import vn.edu.uit.devorbit.mobile.domain.model.GraphNode
import javax.inject.Inject

data class KnowledgeUiState(
    val nodes: List<GraphNode> = emptyList(),
    val links: List<GraphLink> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val selectedNode: GraphNode? = null,
    val learningPath: List<GraphNode> = emptyList(),
    val searchQuery: String = "",
    val selectedSemester: Int? = null,
    val completedNodeIds: Set<Long> = emptySet(),
    val simulationMode: Boolean = false,
    val simulationFailedNodeIds: Set<Long> = emptySet(),
    val detailNode: GraphNode? = null
) {
    /** Returns filtered nodes based on search query, semester filter, and simulation state. */
    fun getFilteredNodes(): List<GraphNode> {
        var result = nodes

        // Apply semester filter
        if (selectedSemester != null) {
            result = result.filter { it.semester == selectedSemester }
        }

        // Apply search query
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.lowercase()
            result = result.filter {
                it.name.lowercase().contains(q) || it.code.lowercase().contains(q)
            }
        }

        // In simulation mode, mark failed nodes but still show all
        return result
    }
}

@HiltViewModel
class KnowledgeViewModel @Inject constructor(
    private val repository: AcademicRepository
) : ViewModel() {

    private val _state = MutableStateFlow(KnowledgeUiState())
    val state: StateFlow<KnowledgeUiState> = _state.asStateFlow()

    private val engine = KnowledgeGraphEngine

    private var allNodes: List<GraphNode> = emptyList()
    private var allLinks: List<GraphLink> = emptyList()

    // ─── Load ───

    fun loadGraph() {
        val s = _state.value
        if (s.loading || s.nodes.isNotEmpty()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val kg = repository.getCourseGraph()
                val levels = engine.computeLevels(kg.nodes, kg.links)
                val impactScores = engine.computeImpactScores(kg.nodes, kg.links)

                val enhancedNodes = kg.nodes.map { node ->
                    node.copy(
                        level = levels[node.id] ?: node.level,
                        impactScore = impactScores[node.id] ?: node.impactScore
                    )
                }
                allNodes = enhancedNodes
                allLinks = kg.links

                _state.value = _state.value.copy(
                    nodes = enhancedNodes,
                    links = kg.links,
                    loading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Failed to load knowledge graph"
                )
            }
        }
    }

    // ─── Selection ───

    fun selectNode(node: GraphNode) {
        val path = engine.generateLearningPath(node.id, allNodes, allLinks)
        _state.value = _state.value.copy(
            selectedNode = node,
            learningPath = path
        )
    }

    fun clearSelection() {
        _state.value = _state.value.copy(
            selectedNode = null,
            learningPath = emptyList()
        )
    }

    // ─── Navigation ───

    fun openDetail(node: GraphNode) {
        val path = engine.generateLearningPath(node.id, allNodes, allLinks)
        _state.value = _state.value.copy(
            detailNode = node,
            learningPath = path
        )
    }

    fun closeDetail() {
        _state.value = _state.value.copy(
            detailNode = null,
            selectedNode = null,
            learningPath = emptyList()
        )
    }

    // ─── Search ───

    fun search(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    // ─── Semester Filter ───

    fun setSemesterFilter(semester: Int?) {
        val current = _state.value.selectedSemester
        _state.value = _state.value.copy(
            selectedSemester = if (current == semester) null else semester
        )
    }

    // ─── Completion ───

    fun toggleComplete(nodeId: Long) {
        val current = _state.value.completedNodeIds
        val updated = if (nodeId in current) current - nodeId else current + nodeId
        _state.value = _state.value.copy(completedNodeIds = updated)
    }

    // ─── Simulation ───

    fun toggleSimulationMode() {
        _state.value = _state.value.copy(
            simulationMode = !_state.value.simulationMode,
            simulationFailedNodeIds = emptySet()
        )
    }

    fun simulateFail(nodeId: Long) {
        // Cascade simulation: find all downstream nodes affected by this failure
        val preReqLinks = allLinks.filter { it.type == "PREREQUISITE" }
        val downstream = mutableSetOf<Long>()
        val queue = ArrayDeque(listOf(nodeId))

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            preReqLinks.filter { it.sourceId == current }.forEach { link ->
                if (link.targetId !in downstream) {
                    downstream.add(link.targetId)
                    queue.add(link.targetId)
                }
            }
        }

        _state.value = _state.value.copy(
            simulationMode = true,
            simulationFailedNodeIds = downstream + nodeId
        )
    }
}

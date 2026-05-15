package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.data.repository.AcademicRepository
import vn.edu.uit.devorbit.mobile.domain.model.GraphNode
import vn.edu.uit.devorbit.mobile.domain.model.GraphLink
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val repository: AcademicRepository
) : ViewModel() {

    val courses: StateFlow<List<CourseEntity>> = repository.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _graphNodes = MutableStateFlow<List<GraphNode>>(emptyList())
    val graphNodes: StateFlow<List<GraphNode>> = _graphNodes.asStateFlow()

    private val _graphLinks = MutableStateFlow<List<GraphLink>>(emptyList())
    val graphLinks: StateFlow<List<GraphLink>> = _graphLinks.asStateFlow()

    private val _graphLoading = MutableStateFlow(false)
    val graphLoading: StateFlow<Boolean> = _graphLoading.asStateFlow()

    private val _graphError = MutableStateFlow<String?>(null)
    val graphError: StateFlow<String?> = _graphError.asStateFlow()

    init {
        refreshCourses()
        loadGraph()
    }

    fun refreshCourses() {
        viewModelScope.launch {
            repository.refreshCourses()
        }
    }

    fun loadGraph() {
        viewModelScope.launch {
            _graphLoading.value = true
            _graphError.value = null
            try {
                val kg = repository.getCourseGraph()
                _graphNodes.value = kg.nodes
                _graphLinks.value = kg.links
            } catch (e: Exception) {
                _graphError.value = e.message ?: "Failed to load graph"
            } finally {
                _graphLoading.value = false
            }
        }
    }

    fun getNodesGroupedBySemester(): Map<Int, List<GraphNode>> {
        return _graphNodes.value
            .filter { it.semester != null && it.semester in 1..8 }
            .groupBy { it.semester!! }
            .toSortedMap()
    }
}

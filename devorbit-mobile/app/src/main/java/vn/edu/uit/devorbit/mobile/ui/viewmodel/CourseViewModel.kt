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
import vn.edu.uit.devorbit.mobile.data.remote.dto.CourseArticle
import vn.edu.uit.devorbit.mobile.data.remote.dto.CourseTutorial
import vn.edu.uit.devorbit.mobile.data.remote.dto.CourseYoutubePlaylist
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.domain.model.GraphNode
import vn.edu.uit.devorbit.mobile.domain.model.GraphLink
import vn.edu.uit.devorbit.mobile.ui.screen.courses.CourseHubNavigationState
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

    private val _courseHubNavigationState = MutableStateFlow(CourseHubNavigationState())
    val courseHubNavigationState: StateFlow<CourseHubNavigationState> = _courseHubNavigationState.asStateFlow()

    private val _selectedCourse = MutableStateFlow<CourseEntity?>(null)
    val selectedCourse: StateFlow<CourseEntity?> = _selectedCourse.asStateFlow()

    private val _selectedRepo = MutableStateFlow<RepoSummary?>(null)
    val selectedRepo: StateFlow<RepoSummary?> = _selectedRepo.asStateFlow()

    private val _detailRepos = MutableStateFlow<List<RepoSummary>>(emptyList())
    val detailRepos: StateFlow<List<RepoSummary>> = _detailRepos.asStateFlow()

    private val _detailTutorials = MutableStateFlow<List<CourseTutorial>>(emptyList())
    val detailTutorials: StateFlow<List<CourseTutorial>> = _detailTutorials.asStateFlow()

    private val _detailVideos = MutableStateFlow<List<CourseYoutubePlaylist>>(emptyList())
    val detailVideos: StateFlow<List<CourseYoutubePlaylist>> = _detailVideos.asStateFlow()

    private val _detailArticles = MutableStateFlow<List<CourseArticle>>(emptyList())
    val detailArticles: StateFlow<List<CourseArticle>> = _detailArticles.asStateFlow()

    private val _detailLoading = MutableStateFlow(false)
    val detailLoading: StateFlow<Boolean> = _detailLoading.asStateFlow()

    private val _detailError = MutableStateFlow<String?>(null)
    val detailError: StateFlow<String?> = _detailError.asStateFlow()

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

    fun openCourse(course: CourseEntity) {
        _selectedCourse.value = course
        _selectedRepo.value = null
        _courseHubNavigationState.value = _courseHubNavigationState.value.openCourse(course.id)
        loadCourseDetail(course.id)
    }

    fun openRepo(repo: RepoSummary) {
        _selectedRepo.value = repo
        _courseHubNavigationState.value = _courseHubNavigationState.value.openRepo(repo.id)
    }

    fun backFromRepo() {
        _selectedRepo.value = null
        _courseHubNavigationState.value = _courseHubNavigationState.value.back()
    }

    fun closeCourseDetail() {
        _selectedCourse.value = null
        _selectedRepo.value = null
        _courseHubNavigationState.value = CourseHubNavigationState()
        _detailRepos.value = emptyList()
        _detailTutorials.value = emptyList()
        _detailVideos.value = emptyList()
        _detailArticles.value = emptyList()
        _detailError.value = null
    }

    private fun loadCourseDetail(courseId: Long) {
        viewModelScope.launch {
            _detailLoading.value = true
            _detailError.value = null
            try {
                val detail = repository.loadCourseDetail(courseId)
                _detailRepos.value = detail.repos
                _detailTutorials.value = detail.tutorials
                _detailVideos.value = detail.videos
                _detailArticles.value = detail.articles
            } catch (e: Exception) {
                _detailError.value = e.message ?: "Failed to load course detail"
                _detailRepos.value = emptyList()
                _detailTutorials.value = emptyList()
                _detailVideos.value = emptyList()
                _detailArticles.value = emptyList()
            } finally {
                _detailLoading.value = false
            }
        }
    }
}

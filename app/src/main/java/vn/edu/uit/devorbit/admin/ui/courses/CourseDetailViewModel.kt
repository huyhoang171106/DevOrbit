package vn.edu.uit.devorbit.admin.ui.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

data class CourseDetailUiState(
    val courseId: Long = 0,
    val course: CourseDetailResponse? = null,
    val tutorials: List<TutorialItem> = emptyList(),
    val videos: List<VideoItem> = emptyList(),
    val articles: List<ArticleItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTab: Int = 0,
    val snackbarMessage: String? = null
)

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CourseDetailUiState())
    val state: StateFlow<CourseDetailUiState> = _state.asStateFlow()

    fun loadCourse(courseId: Long) {
        if (courseId == _state.value.courseId && _state.value.course != null) return
        _state.value = _state.value.copy(courseId = courseId, isLoading = true)
        viewModelScope.launch {
            // Fetch course detail using getAllCourses and filter
            adminRepository.getAllCourses().fold(
                onSuccess = { courses ->
                    val summary = courses.find { it.id == courseId }
                    if (summary != null) {
                        _state.value = _state.value.copy(
                            course = CourseDetailResponse(
                                id = summary.id, code = summary.code,
                                tenMH = summary.tenMH, loaiMonHoc = summary.loaiMonHoc,
                                semester = summary.semester, tinChi = null,
                                managementUnit = summary.managementUnit, active = summary.active
                            ),
                            isLoading = false
                        )
                    }
                    loadResources(courseId)
                },
                onFailure = { _state.value = _state.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun loadResources(courseId: Long) {
        viewModelScope.launch {
            adminRepository.getTutorials(courseId).fold(
                onSuccess = { _state.value = _state.value.copy(tutorials = it) },
                onFailure = {}
            )
            adminRepository.getVideos(courseId).fold(
                onSuccess = { _state.value = _state.value.copy(videos = it) },
                onFailure = {}
            )
            adminRepository.getArticles(courseId).fold(
                onSuccess = { _state.value = _state.value.copy(articles = it) },
                onFailure = {}
            )
        }
    }

    fun selectTab(index: Int) {
        _state.value = _state.value.copy(selectedTab = index)
    }

    fun addTutorial(title: String, url: String) {
        viewModelScope.launch {
            adminRepository.createTutorial(_state.value.courseId, TutorialRequest(title, url.ifBlank { null }, null)).fold(
                onSuccess = {
                    _state.value = _state.value.copy(snackbarMessage = "Đã thêm tutorial")
                    loadResources(_state.value.courseId)
                },
                onFailure = { _state.value = _state.value.copy(snackbarMessage = "Lỗi: ${it.message}") }
            )
        }
    }

    fun deleteTutorial(id: Long) {
        viewModelScope.launch {
            adminRepository.deleteTutorial(_state.value.courseId, id).fold(
                onSuccess = {
                    _state.value = _state.value.copy(snackbarMessage = "Đã xoá tutorial")
                    loadResources(_state.value.courseId)
                },
                onFailure = { _state.value = _state.value.copy(snackbarMessage = "Lỗi: ${it.message}") }
            )
        }
    }

    fun addVideo(title: String, url: String) {
        viewModelScope.launch {
            adminRepository.createVideo(_state.value.courseId, VideoRequest(title, url)).fold(
                onSuccess = {
                    _state.value = _state.value.copy(snackbarMessage = "Đã thêm video")
                    loadResources(_state.value.courseId)
                },
                onFailure = { _state.value = _state.value.copy(snackbarMessage = "Lỗi: ${it.message}") }
            )
        }
    }

    fun deleteVideo(id: Long) {
        viewModelScope.launch {
            adminRepository.deleteVideo(_state.value.courseId, id).fold(
                onSuccess = {
                    _state.value = _state.value.copy(snackbarMessage = "Đã xoá video")
                    loadResources(_state.value.courseId)
                },
                onFailure = { _state.value = _state.value.copy(snackbarMessage = "Lỗi: ${it.message}") }
            )
        }
    }

    fun addArticle(title: String, url: String) {
        viewModelScope.launch {
            adminRepository.createArticle(_state.value.courseId, ArticleRequest(title, url)).fold(
                onSuccess = {
                    _state.value = _state.value.copy(snackbarMessage = "Đã thêm bài viết")
                    loadResources(_state.value.courseId)
                },
                onFailure = { _state.value = _state.value.copy(snackbarMessage = "Lỗi: ${it.message}") }
            )
        }
    }

    fun deleteArticle(id: Long) {
        viewModelScope.launch {
            adminRepository.deleteArticle(_state.value.courseId, id).fold(
                onSuccess = {
                    _state.value = _state.value.copy(snackbarMessage = "Đã xoá bài viết")
                    loadResources(_state.value.courseId)
                },
                onFailure = { _state.value = _state.value.copy(snackbarMessage = "Lỗi: ${it.message}") }
            )
        }
    }

    fun updateCourse(request: AdminCourseUpsertRequest) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            adminRepository.updateCourse(_state.value.courseId, request).fold(
                onSuccess = {
                    _state.value = _state.value.copy(isLoading = false, snackbarMessage = "Đã cập nhật môn học")
                },
                onFailure = {
                    _state.value = _state.value.copy(isLoading = false, snackbarMessage = "Lỗi: ${it.message}")
                }
            )
        }
    }

    fun clearSnackbar() {
        _state.value = _state.value.copy(snackbarMessage = null)
    }
}

package vn.edu.uit.devorbit.admin.ui.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.CourseRelationshipResponse
import vn.edu.uit.devorbit.admin.data.remote.dto.CourseRelationshipRequest
import vn.edu.uit.devorbit.admin.data.remote.dto.CourseSummaryResponse
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

data class CourseRelationshipsUiState(
    val relationships: List<CourseRelationshipResponse> = emptyList(),
    val courses: List<CourseSummaryResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CourseRelationshipsViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CourseRelationshipsUiState())
    val state: StateFlow<CourseRelationshipsUiState> = _state.asStateFlow()

    init {
        loadRelationships()
        loadCourses()
    }

    fun loadRelationships() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            adminRepository.getAllRelationships().fold(
                onSuccess = { _state.value = _state.value.copy(relationships = it, isLoading = false) },
                onFailure = { _state.value = _state.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    private fun loadCourses() {
        viewModelScope.launch {
            adminRepository.getAllCourses().onSuccess { _state.value = _state.value.copy(courses = it) }
        }
    }

    fun createRelationship(courseId: Long, relatedCourseId: Long, relationType: String) {
        viewModelScope.launch {
            adminRepository.createRelationship(
                CourseRelationshipRequest(courseId = courseId, relatedCourseId = relatedCourseId, relationType = relationType)
            ).fold(
                onSuccess = { loadRelationships() },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun deleteRelationship(id: Long) {
        viewModelScope.launch {
            adminRepository.deleteRelationship(id).fold(
                onSuccess = { loadRelationships() },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }
}

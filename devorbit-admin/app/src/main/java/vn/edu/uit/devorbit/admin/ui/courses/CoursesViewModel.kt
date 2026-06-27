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

data class CoursesUiState(
    val courses: List<CourseSummaryResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {
    @Volatile
    private var submitting = false


    private val _state = MutableStateFlow(CoursesUiState())
    val state: StateFlow<CoursesUiState> = _state.asStateFlow()

    init { loadCourses() }

    fun loadCourses() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            adminRepository.getAllCourses().fold(
                onSuccess = { _state.value = _state.value.copy(courses = it, isLoading = false) },
                onFailure = { _state.value = _state.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun deleteCourse(id: Long) {
        viewModelScope.launch {
            adminRepository.deleteCourse(id).fold(
                onSuccess = { loadCourses() },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun createCourse(request: AdminCourseUpsertRequest) {
        if (submitting) return
        submitting = true
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                adminRepository.createCourse(request).fold(
                    onSuccess = { _state.value = _state.value.copy(isLoading = false); loadCourses() },
                    onFailure = { _state.value = _state.value.copy(isLoading = false, error = it.message) }
                )
            } finally {
                submitting = false
            }
        }
    }
}

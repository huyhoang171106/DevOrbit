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

data class RelationshipUiState(
    val relationships: List<CourseRelationshipResponse> = emptyList(),
    val courses: List<CourseSummaryResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val snackbarMessage: String? = null
)

@HiltViewModel
class CourseRelationshipsViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RelationshipUiState())
    val state: StateFlow<RelationshipUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            adminRepository.getAllRelationships().fold(
                onSuccess = { _state.value = _state.value.copy(relationships = it) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
            adminRepository.getAllCourses().fold(
                onSuccess = { _state.value = _state.value.copy(courses = it) },
                onFailure = {}
            )
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun createRelationship(fromId: Long, toId: Long, type: String) {
        viewModelScope.launch {
            adminRepository.createRelationship(CourseRelationshipRequest(fromId, toId, type)).fold(
                onSuccess = {
                    _state.value = _state.value.copy(snackbarMessage = "Đã thêm quan hệ")
                    load()
                },
                onFailure = { _state.value = _state.value.copy(snackbarMessage = "Lỗi: ${it.message}") }
            )
        }
    }

    fun deleteRelationship(id: Long) {
        viewModelScope.launch {
            adminRepository.deleteRelationship(id).fold(
                onSuccess = {
                    _state.value = _state.value.copy(snackbarMessage = "Đã xoá quan hệ")
                    load()
                },
                onFailure = { _state.value = _state.value.copy(snackbarMessage = "Lỗi: ${it.message}") }
            )
        }
    }

    fun clearSnackbar() {
        _state.value = _state.value.copy(snackbarMessage = null)
    }
}

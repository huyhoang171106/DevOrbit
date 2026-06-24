package vn.edu.uit.devorbit.admin.ui.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

data class CoursesUiState(
    /** Raw list from API, unfiltered */
    val allCourses: List<CourseSummaryResponse> = emptyList(),
    /** True during initial load */
    val isLoading: Boolean = false,
    /** Load/delete error message */
    val error: String? = null,
    // ── Search & Filters ──────────────────────────────────────────────────────
    val searchQuery: String = "",
    /** null = all types, otherwise backend code like "DAI_CUONG" */
    val selectedTypeFilter: String? = null,
    // ── Create / Update ────────────────────────────────────────────────────────
    val showEditor: Boolean = false,
    /** null = create mode; non-null = edit mode, pre-fill from this detail */
    val editingDetail: CourseDetailResponse? = null,
    val isLoadingDetail: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    // ── Delete ─────────────────────────────────────────────────────────────────
    val deleteTarget: CourseSummaryResponse? = null,
    val isDeleting: Boolean = false,
    val deleteResult: CourseDeleteResult? = null,
) {
    /** Courses filtered by search query + type filter. */
    val filteredCourses: List<CourseSummaryResponse>
        get() {
            var result = allCourses
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.trim().lowercase()
                result = result.filter { c ->
                    (c.code?.lowercase()?.contains(q) == true)
                        || (c.name?.lowercase()?.contains(q) == true)
                }
            }
            if (selectedTypeFilter != null) {
                result = result.filter { it.loaiMonHoc == selectedTypeFilter }
            }
            return result
        }

    val isFirstLoading: Boolean get() = isLoading && allCourses.isEmpty()
}

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CoursesUiState())
    val state: StateFlow<CoursesUiState> = _state.asStateFlow()

    init { loadCourses() }

    // ── Load ───────────────────────────────────────────────────────────────────

    fun loadCourses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            adminRepository.getAllCourses().fold(
                onSuccess = { list ->
                    _state.update { it.copy(allCourses = list, isLoading = false) }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isLoading = false, error = e.message ?: "Không thể tải danh sách môn học")
                    }
                },
            )
        }
    }

    // ── Search & Filter ────────────────────────────────────────────────────────

    fun setSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun setTypeFilter(type: String?) {
        _state.update { it.copy(selectedTypeFilter = type) }
    }

    // ── Editor (Create / Update) ───────────────────────────────────────────────

    /** Open full-screen editor. Pass null for create, course id for edit. */
    fun openEditor(courseId: Long? = null) {
        if (courseId == null) {
            // Create mode – no detail needed
            _state.update { it.copy(showEditor = true, editingDetail = null, submitError = null) }
        } else {
            // Edit mode – load detail for pre-fill
            _state.update { it.copy(showEditor = true, isLoadingDetail = true, submitError = null) }
            viewModelScope.launch {
                adminRepository.getCourseDetail(courseId).fold(
                    onSuccess = { detail ->
                        _state.update { it.copy(editingDetail = detail, isLoadingDetail = false) }
                    },
                    onFailure = { e ->
                        _state.update {
                            it.copy(
                                showEditor = false,
                                isLoadingDetail = false,
                                error = e.message ?: "Không thể tải thông tin môn học",
                            )
                        }
                    },
                )
            }
        }
    }

    fun closeEditor() {
        _state.update {
            it.copy(showEditor = false, editingDetail = null, submitError = null)
        }
    }

    fun createCourse(request: AdminCourseUpsertRequest) {
        if (_state.value.isSubmitting) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, submitError = null) }
            adminRepository.createCourse(request).fold(
                onSuccess = {
                    _state.update { it.copy(isSubmitting = false, showEditor = false) }
                    loadCourses()
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isSubmitting = false, submitError = e.message ?: "Không thể tạo môn học")
                    }
                },
            )
        }
    }

    fun updateCourse(id: Long, request: AdminCourseUpsertRequest) {
        if (_state.value.isSubmitting) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, submitError = null) }
            adminRepository.updateCourse(id, request).fold(
                onSuccess = {
                    _state.update { it.copy(isSubmitting = false, showEditor = false) }
                    loadCourses()
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isSubmitting = false, submitError = e.message ?: "Không thể cập nhật môn học")
                    }
                },
            )
        }
    }

    // ── Delete ─────────────────────────────────────────────────────────────────

    fun requestDelete(course: CourseSummaryResponse) {
        _state.update { it.copy(deleteTarget = course) }
    }

    fun cancelDelete() {
        _state.update { it.copy(deleteTarget = null) }
    }

    fun confirmDelete() {
        val id = _state.value.deleteTarget?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            adminRepository.deleteCourse(id).fold(
                onSuccess = {
                    _state.update { state ->
                        state.copy(
                            allCourses = state.allCourses.filter { c -> c.id != id },
                            deleteTarget = null,
                            isDeleting = false,
                            deleteResult = CourseDeleteResult(),
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isDeleting = false, error = e.message ?: "Không thể xoá môn học")
                    }
                },
            )
        }
    }

    fun clearDeleteResult() {
        _state.update { it.copy(deleteResult = null) }
    }

    fun clearError() {
        _state.update { it.copy(error = null, submitError = null) }
    }
}

package vn.edu.uit.devorbit.admin.ui.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.ApprovedRepoUpdateRequest
import vn.edu.uit.devorbit.admin.data.remote.dto.CourseSummaryResponse
import vn.edu.uit.devorbit.admin.data.remote.dto.RepoSummaryResponse
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

sealed class ReposUiState {
    data object Loading : ReposUiState()
    data class Content(
        val repos: List<RepoSummaryResponse> = emptyList(),
        val allCourses: List<CourseSummaryResponse> = emptyList(),
        val searchQuery: String = "",
        val selectedCourseId: Long? = null,
    ) : ReposUiState() {
        val filteredRepos: List<RepoSummaryResponse>
            get() {
                var result = repos
                if (searchQuery.isNotBlank()) {
                    val q = searchQuery.trim().lowercase()
                    result = result.filter {
                        it.displayName.lowercase().contains(q) ||
                            it.description?.lowercase()?.contains(q) == true ||
                            it.primaryLanguage?.lowercase()?.contains(q) == true ||
                            it.courseName?.lowercase()?.contains(q) == true ||
                            it.courseCode?.lowercase()?.contains(q) == true
                    }
                }
                if (selectedCourseId != null) {
                    result = result.filter { it.courseId == selectedCourseId }
                }
                return result
            }
    }
    data class Error(val message: String) : ReposUiState()
}

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ReposUiState>(ReposUiState.Loading)
    val state: StateFlow<ReposUiState> = _state.asStateFlow()

    @Volatile private var submitting = false

    init { loadRepos() }

    fun loadRepos() {
        viewModelScope.launch {
            _state.value = ReposUiState.Loading
            val reposResult = adminRepository.getAllRepos()
            val coursesResult = adminRepository.getAllCourses()

            reposResult.fold(
                onSuccess = { repos ->
                    val courses = coursesResult.getOrNull() ?: emptyList()
                    _state.value = ReposUiState.Content(
                        repos = repos,
                        allCourses = courses,
                    )
                },
                onFailure = { e ->
                    _state.value = ReposUiState.Error(
                        e.message ?: "Không thể tải danh sách kho lưu trữ"
                    )
                }
            )
        }
    }

    fun setSearchQuery(query: String) {
        _state.update { current ->
            if (current is ReposUiState.Content) current.copy(searchQuery = query) else current
        }
    }

    fun setCourseFilter(courseId: Long?) {
        _state.update { current ->
            if (current is ReposUiState.Content) current.copy(selectedCourseId = courseId) else current
        }
    }

    fun syncRepo(repoId: Long) {
        if (submitting) return
        submitting = true
        viewModelScope.launch {
            try {
                adminRepository.syncRepo(repoId).fold(
                    onSuccess = { loadRepos() },
                    onFailure = { /* handled by loadRepos */ }
                )
            } finally {
                submitting = false
            }
        }
    }

    fun updateRepo(repoId: Long, request: ApprovedRepoUpdateRequest) {
        if (submitting) return
        submitting = true
        viewModelScope.launch {
            try {
                adminRepository.updateRepo(repoId, request).fold(
                    onSuccess = { loadRepos() },
                    onFailure = { /* handled by loadRepos */ }
                )
            } finally {
                submitting = false
            }
        }
    }

    fun deleteRepo(repoId: Long) {
        if (submitting) return
        submitting = true
        viewModelScope.launch {
            try {
                adminRepository.deleteRepo(repoId).fold(
                    onSuccess = { loadRepos() },
                    onFailure = { /* handled by loadRepos */ }
                )
            } finally {
                submitting = false
            }
        }
    }

    fun evaluateAll() {
        if (submitting) return
        submitting = true
        viewModelScope.launch {
            try {
                adminRepository.evaluateAllRepos().fold(
                    onSuccess = { loadRepos() },
                    onFailure = { /* handled by loadRepos */ }
                )
            } finally {
                submitting = false
            }
        }
    }
}

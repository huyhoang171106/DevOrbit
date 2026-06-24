package vn.edu.uit.devorbit.admin.ui.candidates

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

enum class SortBy {
    RECENT, OLDEST, STARS_DESC, STARS_ASC
}

sealed class CandidatesUiState {
    data object Loading : CandidatesUiState()
    data class Content(
        val candidates: List<RepoCandidateResponse> = emptyList(),
        val allCourses: List<CourseSummaryResponse> = emptyList(),
        val reviewerStats: List<ReviewerStatsResponse> = emptyList(),
        val searchQuery: String = "",
        val selectedCourseId: Long? = null,
        val selectedReviewer: String? = null,
        val sortBy: SortBy = SortBy.RECENT,
    ) : CandidatesUiState() {
        val filteredCandidates: List<RepoCandidateResponse>
            get() {
                var result = candidates
                if (searchQuery.isNotBlank()) {
                    val q = searchQuery.trim().lowercase()
                    result = result.filter {
                        it.githubName?.lowercase()?.contains(q) == true ||
                            it.githubOwner?.lowercase()?.contains(q) == true ||
                            it.primaryLanguage?.lowercase()?.contains(q) == true ||
                            it.courseName?.lowercase()?.contains(q) == true
                    }
                }
                if (selectedCourseId != null) {
                    result = result.filter { it.courseId == selectedCourseId }
                }
                result = when (sortBy) {
                    SortBy.RECENT -> result.sortedByDescending { it.lastPushedAt ?: "" }
                    SortBy.OLDEST -> result.sortedBy { it.lastPushedAt ?: "" }
                    SortBy.STARS_DESC -> result.sortedByDescending { it.stars }
                    SortBy.STARS_ASC -> result.sortedBy { it.stars }
                }
                return result
            }
    }
    data class Error(val message: String) : CandidatesUiState()
}

@HiltViewModel
class CandidatesViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CandidatesUiState>(CandidatesUiState.Loading)
    val state: StateFlow<CandidatesUiState> = _state.asStateFlow()

    @Volatile private var submitting = false

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.value = CandidatesUiState.Loading
            val candidatesResult = adminRepository.getPendingCandidates()
            val coursesResult = adminRepository.getAllCourses()
            val statsResult = adminRepository.getReviewerStats()

            candidatesResult.fold(
                onSuccess = { candidates ->
                    val courses = coursesResult.getOrNull() ?: emptyList()
                    val stats = statsResult.getOrNull() ?: emptyList()
                    _state.value = CandidatesUiState.Content(
                        candidates = candidates,
                        allCourses = courses,
                        reviewerStats = stats,
                    )
                },
                onFailure = { e ->
                    _state.value = CandidatesUiState.Error(
                        e.message ?: "Không thể tải danh sách ứng viên"
                    )
                }
            )
        }
    }

    fun setSearchQuery(query: String) {
        _state.update { current ->
            if (current is CandidatesUiState.Content) current.copy(searchQuery = query) else current
        }
    }

    fun setCourseFilter(courseId: Long?) {
        _state.update { current ->
            if (current is CandidatesUiState.Content) current.copy(selectedCourseId = courseId) else current
        }
    }

    fun setReviewerFilter(reviewer: String?) {
        _state.update { current ->
            if (current is CandidatesUiState.Content) current.copy(selectedReviewer = reviewer) else current
        }
        // Reviewer filter requires backend reload
        loadCandidates()
    }

    fun setSortBy(sortBy: SortBy) {
        _state.update { current ->
            if (current is CandidatesUiState.Content) current.copy(sortBy = sortBy) else current
        }
    }

    private fun loadCandidates() {
        val reviewer = when (val s = _state.value) {
            is CandidatesUiState.Content -> s.selectedReviewer
            else -> return
        }
        viewModelScope.launch {
            adminRepository.getPendingCandidates(reviewer ?: "all").fold(
                onSuccess = { candidates ->
                    _state.update { current ->
                        if (current is CandidatesUiState.Content) current.copy(candidates = candidates)
                        else current
                    }
                },
                onFailure = { e ->
                    _state.update { current ->
                        if (current is CandidatesUiState.Content) current
                        else CandidatesUiState.Error(e.message ?: "Lỗi tải dữ liệu")
                    }
                }
            )
        }
    }

    fun approve(
        candidateId: Long,
        description: String?,
        techStacks: List<String>?,
        reviewNote: String?,
    ) {
        if (submitting) return
        submitting = true
        viewModelScope.launch {
            try {
                adminRepository.approveCandidate(
                    candidateId,
                    CandidateReviewRequest(description, techStacks, reviewNote)
                ).fold(
                    onSuccess = { loadCandidates() },
                    onFailure = { /* error handled implicitly */ }
                )
            } finally {
                submitting = false
            }
        }
    }

    fun reject(candidateId: Long) {
        if (submitting) return
        submitting = true
        viewModelScope.launch {
            try {
                adminRepository.rejectCandidate(candidateId).fold(
                    onSuccess = { loadCandidates() },
                    onFailure = { /* error handled implicitly */ }
                )
            } finally {
                submitting = false
            }
        }
    }
}

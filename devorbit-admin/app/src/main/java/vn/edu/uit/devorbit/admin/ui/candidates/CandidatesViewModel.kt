package vn.edu.uit.devorbit.admin.ui.candidates

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

data class CandidatesUiState(
    val candidates: List<RepoCandidateResponse> = emptyList(),
    val allCourses: List<CourseSummaryResponse> = emptyList(),
    val reviewerStats: List<ReviewerStatsResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CandidatesViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CandidatesUiState())
    val state: StateFlow<CandidatesUiState> = _state.asStateFlow()
    @Volatile
    private var submitting = false


    init {
        loadCandidates()
        loadCourses()
        loadReviewerStats()
    }

    fun loadCandidates() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            adminRepository.getPendingCandidates().fold(
                onSuccess = { _state.value = _state.value.copy(candidates = it, isLoading = false) },
                onFailure = { _state.value = _state.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    private fun loadCourses() {
        viewModelScope.launch {
            adminRepository.getAllCourses().onSuccess { _state.value = _state.value.copy(allCourses = it) }
        }
    }

    private fun loadReviewerStats() {
        viewModelScope.launch {
            adminRepository.getReviewerStats().onSuccess { _state.value = _state.value.copy(reviewerStats = it) }
        }
    }

    fun approve(candidateId: Long, description: String?, techStacks: List<String>?, reviewNote: String?) {
        if (submitting) return
        submitting = true
        viewModelScope.launch {
            try {
                adminRepository.approveCandidate(candidateId, CandidateReviewRequest(description, techStacks, reviewNote)).fold(
                    onSuccess = { loadCandidates() },
                    onFailure = { _state.value = _state.value.copy(error = it.message) }
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
                    onFailure = { _state.value = _state.value.copy(error = it.message) }
                )
            } finally {
                submitting = false
            }
        }
    }
}

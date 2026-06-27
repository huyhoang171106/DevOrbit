package vn.edu.uit.devorbit.admin.ui.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.ApprovedRepoUpdateRequest
import vn.edu.uit.devorbit.admin.data.remote.dto.RepoSummaryResponse
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

data class ReposUiState(
    val repos: List<RepoSummaryResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {
    @Volatile private var submitting = false


    private val _state = MutableStateFlow(ReposUiState())
    val state: StateFlow<ReposUiState> = _state.asStateFlow()

    init { loadRepos() }

    fun loadRepos() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            adminRepository.getAllRepos().fold(
                onSuccess = { _state.value = ReposUiState(repos = it, isLoading = false) },
                onFailure = { _state.value = _state.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun syncRepo(repoId: Long) {
        if (submitting) return
        submitting = true
        viewModelScope.launch {
            try {
                adminRepository.syncRepo(repoId).fold(
                    onSuccess = { loadRepos() },
                    onFailure = { _state.value = _state.value.copy(error = it.message) }
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
                    onFailure = { _state.value = _state.value.copy(error = it.message) }
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
                    onFailure = { _state.value = _state.value.copy(error = it.message) }
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
                    onFailure = { _state.value = _state.value.copy(error = it.message) }
                )
            } finally {
                submitting = false
            }
        }
    }
}

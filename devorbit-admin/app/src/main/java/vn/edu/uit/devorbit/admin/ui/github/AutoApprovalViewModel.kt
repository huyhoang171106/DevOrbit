package vn.edu.uit.devorbit.admin.ui.github

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.AutoApprovalRun
import vn.edu.uit.devorbit.admin.data.remote.dto.GithubAutomationStatus
import vn.edu.uit.devorbit.admin.data.remote.dto.RepoCandidateResponse
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

data class AutoApprovalUiState(
    val status: GithubAutomationStatus? = null,
    val repos: List<RepoCandidateResponse> = emptyList(),
    val lastRun: AutoApprovalRun? = null,
    val loading: Boolean = true,
    val running: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AutoApprovalViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AutoApprovalUiState())
    val state: StateFlow<AutoApprovalUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        val status = repository.getGithubAutomationStatus()
        val repos = repository.getAutoApprovedRepos()
        _state.value = _state.value.copy(
            status = status.getOrNull(),
            repos = repos.getOrDefault(emptyList()),
            loading = false,
            error = status.exceptionOrNull()?.message ?: repos.exceptionOrNull()?.message
        )
    }

    fun runNow() = viewModelScope.launch {
        _state.value = _state.value.copy(running = true, error = null)
        repository.runAutoApproval().fold(
            onSuccess = { result ->
                _state.value = _state.value.copy(running = false, lastRun = result)
                refresh()
            },
            onFailure = { _state.value = _state.value.copy(running = false, error = it.message) }
        )
    }
}

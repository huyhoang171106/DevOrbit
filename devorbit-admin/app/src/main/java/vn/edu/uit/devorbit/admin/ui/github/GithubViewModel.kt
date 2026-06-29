package vn.edu.uit.devorbit.admin.ui.github

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.CourseSummaryResponse
import vn.edu.uit.devorbit.admin.data.remote.dto.GithubScanRequest
import vn.edu.uit.devorbit.admin.data.remote.dto.RepoCandidateResponse
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

data class GithubUiState(
    val scanLogs: List<String> = emptyList(),
    val scanResult: List<RepoCandidateResponse>? = null,
    val courses: List<CourseSummaryResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isScanning: Boolean = false,
    val error: String? = null,
    val scanMessage: String? = null
)

@HiltViewModel
class GithubViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GithubUiState())
    val state: StateFlow<GithubUiState> = _state.asStateFlow()

    init {
        loadLogs()
        loadCourses()
    }
    private fun loadCourses() {
        viewModelScope.launch {
            adminRepository.getAllCourses().onSuccess { _state.value = _state.value.copy(courses = it) }
        }
    }

    private fun loadLogs() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            adminRepository.getScanLogs().fold(
                onSuccess = { _state.value = _state.value.copy(scanLogs = it, isLoading = false) },
                onFailure = { _state.value = _state.value.copy(isLoading = false, error = it.message) }
            )
        }
    }
    fun scanAll() {
        if (_state.value.isScanning) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isScanning = true, error = null, scanMessage = null)
            adminRepository.scanAllCourses().fold(
                onSuccess = { msg ->
                    _state.value = _state.value.copy(isScanning = false, scanMessage = msg["message"])
                    loadLogs()
                },
                onFailure = { _state.value = _state.value.copy(isScanning = false, error = it.message, scanMessage = null) }
            )
        }
    }

    fun scan(courseId: Long, query: String) {
        if (_state.value.isScanning) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isScanning = true, error = null)
            adminRepository.scanGithub(GithubScanRequest(courseId = courseId, query = query)).fold(
                onSuccess = { result -> _state.value = _state.value.copy(scanResult = result, isScanning = false); loadLogs() },
                onFailure = { _state.value = _state.value.copy(isScanning = false, error = it.message) }
            )
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            adminRepository.clearScanLogs().fold(
                onSuccess = { _state.value = _state.value.copy(scanLogs = emptyList()) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun clearScanMessage() {
        _state.value = _state.value.copy(scanMessage = null)
    }
}

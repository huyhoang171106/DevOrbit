package vn.edu.uit.devorbit.admin.ui.github

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.CourseSummaryResponse
import vn.edu.uit.devorbit.admin.data.remote.dto.GithubScanRequest
import vn.edu.uit.devorbit.admin.data.remote.dto.RepoCandidateResponse
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

sealed class GithubUiState {
    data object Loading : GithubUiState()
    data class Idle(
        val scanLogs: List<String> = emptyList(),
        val allCourses: List<CourseSummaryResponse> = emptyList(),
    ) : GithubUiState()
    data class Scanning(
        val courseId: Long? = null,
        val query: String? = null,
        val isScanAll: Boolean = false,
        val scanLogs: List<String> = emptyList(),
        val allCourses: List<CourseSummaryResponse> = emptyList(),
    ) : GithubUiState()
    data class ScanResult(
        val results: List<RepoCandidateResponse>,
        val scanLogs: List<String> = emptyList(),
        val allCourses: List<CourseSummaryResponse> = emptyList(),
        val scanMessage: String? = null,
    ) : GithubUiState()
    data class Error(
        val message: String,
        val scanLogs: List<String> = emptyList(),
        val allCourses: List<CourseSummaryResponse> = emptyList(),
    ) : GithubUiState()
}

@HiltViewModel
class GithubViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow<GithubUiState>(GithubUiState.Loading)
    val state: StateFlow<GithubUiState> = _state.asStateFlow()

    private var scanJob: Job? = null

    init { loadInitialData() }

    private fun loadInitialData() {
        viewModelScope.launch {
            val logsResult = adminRepository.getScanLogs()
            val coursesResult = adminRepository.getAllCourses()

            val courses = coursesResult.getOrNull() ?: emptyList()

            logsResult.fold(
                onSuccess = { logs ->
                    _state.value = GithubUiState.Idle(
                        scanLogs = logs,
                        allCourses = courses,
                    )
                },
                onFailure = { e ->
                    _state.value = GithubUiState.Error(
                        message = e.message ?: "Không thể tải nhật ký quét",
                        allCourses = courses,
                    )
                }
            )
        }
    }

    private fun loadLogs() {
        viewModelScope.launch {
            adminRepository.getScanLogs().fold(
                onSuccess = { logs ->
                    _state.update { current ->
                        when (current) {
                            is GithubUiState.Idle -> current.copy(scanLogs = logs)
                            is GithubUiState.Scanning -> current.copy(scanLogs = logs)
                            is GithubUiState.ScanResult -> current.copy(scanLogs = logs)
                            is GithubUiState.Error -> {
                                GithubUiState.Idle(
                                    scanLogs = logs,
                                    allCourses = current.allCourses,
                                )
                            }
                            else -> current
                        }
                    }
                },
                onFailure = { /* best-effort */ }
            )
        }
    }

    /**
     * Scan a single course with 300ms debounce.
     */
    fun scan(courseId: Long, query: String) {
        if (query.isBlank()) return
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            delay(300L)
            executeScan(courseId, query)
        }
    }

    private fun executeScan(courseId: Long, query: String) {
        val (logs, courses) = extractCurrent()
        _state.value = GithubUiState.Scanning(
            courseId = courseId,
            query = query,
            scanLogs = logs,
            allCourses = courses,
        )
        viewModelScope.launch {
            adminRepository.scanGithub(
                GithubScanRequest(courseId = courseId, query = query)
            ).fold(
                onSuccess = { result ->
                    val (newLogs, newCourses) = extractCurrent()
                    _state.value = GithubUiState.ScanResult(
                        results = result,
                        scanLogs = newLogs,
                        allCourses = newCourses,
                    )
                    loadLogs()
                },
                onFailure = { e ->
                    val (newLogs, newCourses) = extractCurrent()
                    _state.value = GithubUiState.Error(
                        message = e.message ?: "Quét thất bại",
                        scanLogs = newLogs,
                        allCourses = newCourses,
                    )
                }
            )
        }
    }

    /**
     * Scan all courses with 300ms debounce.
     */
    fun scanAll() {
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            delay(300L)
            executeScanAll()
        }
    }

    private fun executeScanAll() {
        val (logs, courses) = extractCurrent()
        _state.value = GithubUiState.Scanning(
            isScanAll = true,
            scanLogs = logs,
            allCourses = courses,
        )
        viewModelScope.launch {
            adminRepository.scanAllCourses().fold(
                onSuccess = { msg ->
                    val (newLogs, newCourses) = extractCurrent()
                    _state.value = GithubUiState.ScanResult(
                        results = emptyList(),
                        scanLogs = newLogs,
                        allCourses = newCourses,
                        scanMessage = msg["message"],
                    )
                    loadLogs()
                },
                onFailure = { e ->
                    val (newLogs, newCourses) = extractCurrent()
                    _state.value = GithubUiState.Error(
                        message = e.message ?: "Quét tất cả thất bại",
                        scanLogs = newLogs,
                        allCourses = newCourses,
                    )
                }
            )
        }
    }

    fun clearResults() {
        val (logs, courses) = extractCurrent()
        _state.value = GithubUiState.Idle(
            scanLogs = logs,
            allCourses = courses,
        )
    }

    fun clearLogs() {
        viewModelScope.launch {
            adminRepository.clearScanLogs().fold(
                onSuccess = {
                    _state.update { current ->
                        when (current) {
                            is GithubUiState.Idle -> current.copy(scanLogs = emptyList())
                            is GithubUiState.Scanning -> current.copy(scanLogs = emptyList())
                            is GithubUiState.ScanResult -> current.copy(scanLogs = emptyList())
                            is GithubUiState.Error -> current.copy(scanLogs = emptyList())
                            else -> current
                        }
                    }
                },
                onFailure = { e ->
                    val (logs, courses) = extractCurrent()
                    _state.value = GithubUiState.Error(
                        message = e.message ?: "Không thể xoá nhật ký",
                        scanLogs = logs,
                        allCourses = courses,
                    )
                }
            )
        }
    }

    fun clearScanMessage() {
        _state.update { current ->
            if (current is GithubUiState.ScanResult) {
                current.copy(scanMessage = null)
            } else current
        }
    }

    fun clearError() {
        val (logs, courses) = extractCurrent()
        _state.value = GithubUiState.Idle(
            scanLogs = logs,
            allCourses = courses,
        )
    }

    private fun extractCurrent(): Pair<List<String>, List<CourseSummaryResponse>> = when (val s = _state.value) {
        is GithubUiState.Idle -> s.scanLogs to s.allCourses
        is GithubUiState.Scanning -> s.scanLogs to s.allCourses
        is GithubUiState.ScanResult -> s.scanLogs to s.allCourses
        is GithubUiState.Error -> s.scanLogs to s.allCourses
        else -> emptyList<String>() to emptyList()
    }
}

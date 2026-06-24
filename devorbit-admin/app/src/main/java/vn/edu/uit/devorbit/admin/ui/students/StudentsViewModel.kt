package vn.edu.uit.devorbit.admin.ui.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.AdminStudentResponse
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

sealed interface StudentsUiState {
    data object InitialLoading : StudentsUiState
    data class Error(val message: String) : StudentsUiState
    data object Empty : StudentsUiState
    data class Success(
        val students: List<AdminStudentResponse>,
        val isRefreshing: Boolean = false,
        val toggleError: String? = null
    ) : StudentsUiState
}

@HiltViewModel
class StudentsViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow<StudentsUiState>(StudentsUiState.InitialLoading)
    val state: StateFlow<StudentsUiState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var loadJob: Job? = null
    private var searchJob: Job? = null

    init {
        loadStudentsInternal(null)
        observeSearch()
    }

    private fun observeSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _searchQuery.collectLatest { query ->
                // Debounce 300ms via delay
                delay(300)
                loadStudentsInternal(query.ifBlank { null })
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun refresh() {
        loadStudentsInternal(
            search = _searchQuery.value.ifBlank { null },
            isRefresh = true
        )
    }

    fun loadStudents(search: String? = null) {
        loadStudentsInternal(
            search = search ?: _searchQuery.value.ifBlank { null },
            isRefresh = false
        )
    }


    private fun loadStudentsInternal(
        search: String? = null,
        isRefresh: Boolean = false
    ) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val current = _state.value

            when {
                current is StudentsUiState.Success && isRefresh -> {
                    // Pull-to-refresh: keep showing data with refresh indicator
                    _state.value = current.copy(isRefreshing = true)
                }
                else -> {
                    // Initial load, search, or retry from error: show loading skeleton
                    _state.value = StudentsUiState.InitialLoading
                }
            }

            adminRepository.getStudents(search).fold(
                onSuccess = { students ->
                    _state.value = if (students.isEmpty()) {
                        StudentsUiState.Empty
                    } else {
                        StudentsUiState.Success(students = students)
                    }
                },
                onFailure = { e ->
                    _state.value = StudentsUiState.Error(
                        message = e.message ?: "Không thể tải danh sách sinh viên"
                    )
                }
            )
        }
    }

    fun toggleActive(id: Long) {
        viewModelScope.launch {
            adminRepository.toggleStudentActive(id).fold(
                onSuccess = {
                    // Refresh data from server after mutation
                    loadStudentsInternal(_searchQuery.value.ifBlank { null })
                },
                onFailure = { e ->
                    val current = _state.value
                    if (current is StudentsUiState.Success) {
                        _state.value = current.copy(
                            toggleError = e.message ?: "Không thể thay đổi trạng thái"
                        )
                    }
                }
            )
        }
    }

    fun clearToggleError() {
        val current = _state.value
        if (current is StudentsUiState.Success) {
            _state.value = current.copy(toggleError = null)
        }
    }
}

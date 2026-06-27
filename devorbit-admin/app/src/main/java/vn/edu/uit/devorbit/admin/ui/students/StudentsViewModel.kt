package vn.edu.uit.devorbit.admin.ui.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.AdminStudentResponse
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

data class StudentsUiState(
    val students: List<AdminStudentResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class StudentsViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudentsUiState())
    val state: StateFlow<StudentsUiState> = _state.asStateFlow()

    init { loadStudents() }

    private var searchJob: kotlinx.coroutines.Job? = null

    fun loadStudents(search: String? = null) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            adminRepository.getStudents(search).fold(
                onSuccess = { _state.value = StudentsUiState(students = it, isLoading = false) },
                onFailure = { _state.value = _state.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun toggleActive(id: Long) {
        viewModelScope.launch {
            adminRepository.toggleStudentActive(id).fold(
                onSuccess = { updated ->
                    val list = _state.value.students.map { if (it.id == id) updated else it }
                    _state.value = _state.value.copy(students = list)
                },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun search(query: String) {
        loadStudents(query.ifBlank { null })
    }
}

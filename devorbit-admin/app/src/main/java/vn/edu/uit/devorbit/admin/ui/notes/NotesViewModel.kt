package vn.edu.uit.devorbit.admin.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.NoteResponse
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

data class NotesUiState(
    val notes: List<NoteResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotesUiState())
    val state: StateFlow<NotesUiState> = _state.asStateFlow()

    init { loadNotes() }

    private fun loadNotes() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            adminRepository.getAllNotes().fold(
                onSuccess = { _state.value = NotesUiState(notes = it, isLoading = false) },
                onFailure = { _state.value = _state.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            adminRepository.deleteNote(id).fold(
                onSuccess = { _state.value = _state.value.copy(notes = _state.value.notes.filter { it.id != id }) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }
}

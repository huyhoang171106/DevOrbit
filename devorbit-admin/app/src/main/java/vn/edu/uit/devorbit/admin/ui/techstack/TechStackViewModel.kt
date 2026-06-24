package vn.edu.uit.devorbit.admin.ui.techstack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.AdminTechStackResponse
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

data class TechStackUiState(
    val items: List<AdminTechStackResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TechStackViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {
    @Volatile
    private var submitting = false


    private val _state = MutableStateFlow(TechStackUiState())
    val state: StateFlow<TechStackUiState> = _state.asStateFlow()

    init { loadItems() }

    private fun loadItems() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            adminRepository.getTechStacks().fold(
                onSuccess = { _state.value = TechStackUiState(items = it, isLoading = false) },
                onFailure = { _state.value = _state.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun create(name: String) {
        if (submitting) return
        submitting = true
        viewModelScope.launch {
            try {
                adminRepository.createTechStack(name).fold(
                    onSuccess = { loadItems() },
                    onFailure = { _state.value = _state.value.copy(error = it.message) }
                )
            } finally {
                submitting = false
            }
        }
    }

    fun delete(id: Long) {
        if (submitting) return
        submitting = true
        viewModelScope.launch {
            try {
                adminRepository.deleteTechStack(id).fold(
                    onSuccess = { loadItems() },
                    onFailure = { _state.value = _state.value.copy(error = it.message) }
                )
            } finally {
                submitting = false
            }
        }
    }
}

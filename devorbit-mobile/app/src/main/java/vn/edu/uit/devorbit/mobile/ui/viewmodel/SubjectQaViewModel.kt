package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.domain.repository.SubjectQaRepository
import javax.inject.Inject

data class SubjectQaMessage(
    val role: String,
    val text: String,
    val sources: List<String> = emptyList()
)

data class SubjectQaUiState(
    val messages: List<SubjectQaMessage> = emptyList(),
    val sessionId: String? = null,
    val loading: Boolean = false,
    val error: String? = null
) {
    fun appendAnswer(answer: String, sessionId: String?, sources: List<String> = emptyList()): SubjectQaUiState {
        return copy(
            messages = messages + SubjectQaMessage(role = "assistant", text = answer, sources = sources),
            sessionId = sessionId ?: this.sessionId,
            loading = false,
            error = null
        )
    }
}

@HiltViewModel
class SubjectQaViewModel @Inject constructor(
    private val subjectQaRepository: SubjectQaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SubjectQaUiState())
    val state: StateFlow<SubjectQaUiState> = _state.asStateFlow()

    fun ask(message: String) {
        val text = message.trim()
        if (text.isBlank()) return

        val before = _state.value.copy(
            messages = _state.value.messages + SubjectQaMessage(role = "user", text = text),
            loading = true,
            error = null
        )
        _state.value = before

        viewModelScope.launch {
            try {
                val answer = subjectQaRepository.ask(text, before.sessionId)
                _state.value = before.appendAnswer(
                    answer = answer.answer,
                    sessionId = answer.sessionId,
                    sources = answer.sources
                )
            } catch (e: Exception) {
                _state.value = before.copy(
                    loading = false,
                    error = e.message ?: "Khong hoi duoc AI"
                )
            }
        }
    }
}

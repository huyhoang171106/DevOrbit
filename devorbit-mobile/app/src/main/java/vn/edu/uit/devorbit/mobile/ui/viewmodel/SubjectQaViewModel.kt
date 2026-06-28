package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.domain.repository.SubjectQaEvent
import vn.edu.uit.devorbit.mobile.domain.repository.SubjectQaRepository
import javax.inject.Inject

data class SubjectQaProgressStep(
    val stage: String,
    val message: String
)

data class SubjectQaMessage(
    val role: String,
    val text: String,
    val sources: List<String> = emptyList(),
    val progressSteps: List<SubjectQaProgressStep> = emptyList()
)

data class SubjectQaUiState(
    val messages: List<SubjectQaMessage> = emptyList(),
    val sessionId: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val streamingText: String = "",
    val progressSteps: List<SubjectQaProgressStep> = emptyList()
) {
    fun appendAnswer(answer: String, sessionId: String?, sources: List<String> = emptyList()): SubjectQaUiState {
        return copy(
            messages = messages + SubjectQaMessage(
                role = "assistant",
                text = streamingText.ifBlank { answer },
                sources = sources,
                progressSteps = progressSteps
            ),
            sessionId = sessionId ?: this.sessionId,
            loading = false,
            error = null,
            streamingText = "",
            progressSteps = emptyList()
        )
    }

    fun updateProgress(stage: String, message: String): SubjectQaUiState {
        val step = SubjectQaProgressStep(stage = stage, message = message)
        val index = progressSteps.indexOfFirst { it.stage == stage }
        val updated = if (index >= 0) {
            progressSteps.toMutableList().also { it[index] = step }
        } else {
            progressSteps + step
        }
        return copy(progressSteps = updated)
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
        if (text.isBlank() || _state.value.loading) return

        val before = _state.value.copy(
            messages = _state.value.messages + SubjectQaMessage(role = "user", text = text),
            loading = true,
            error = null,
            streamingText = "",
            progressSteps = emptyList()
        )
        _state.value = before

        requestAnswer(text, before)
    }

    fun retryLastQuestion() {
        if (_state.value.loading) return
        val text = _state.value.messages.lastOrNull { it.role == "user" }?.text ?: return
        val before = _state.value.copy(
            loading = true,
            error = null,
            streamingText = "",
            progressSteps = emptyList()
        )
        _state.value = before
        requestAnswer(text, before)
    }

    private fun requestAnswer(text: String, before: SubjectQaUiState) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                var completed = false
                subjectQaRepository.stream(text, before.sessionId).collect { event ->
                    when (event) {
                        is SubjectQaEvent.Status -> {
                            _state.value = _state.value.updateProgress(event.stage, event.message)
                        }
                        is SubjectQaEvent.Delta -> {
                            _state.value = _state.value.copy(
                                streamingText = _state.value.streamingText + event.content
                            )
                        }
                        is SubjectQaEvent.Complete -> {
                            completed = true
                            _state.value = _state.value.appendAnswer(
                                answer = event.answer.answer,
                                sessionId = event.answer.sessionId,
                                sources = event.answer.sources
                            )
                        }
                    }
                }
                if (!completed) {
                    error("AI stream ended before completion")
                }
            } catch (e: Exception) {
                val current = _state.value
                val messages = if (current.streamingText.isNotBlank()) {
                    current.messages + SubjectQaMessage(
                        role = "assistant",
                        text = current.streamingText,
                        progressSteps = current.progressSteps
                    )
                } else {
                    current.messages
                }
                _state.value = current.copy(
                    messages = messages,
                    loading = false,
                    error = userFacingError(e),
                    streamingText = "",
                    progressSteps = emptyList()
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun userFacingError(error: Throwable): String {
        val detail = error.message.orEmpty().lowercase()
        return when {
            "timeout" in detail || "timed out" in detail ->
                "AI đang phản hồi chậm. Hãy thử gửi lại câu hỏi."
            "unable to resolve host" in detail || "failed to connect" in detail ->
                "Không thể kết nối đến AI. Kiểm tra mạng rồi thử lại."
            else -> "Chưa thể nhận câu trả lời. Vui lòng thử lại."
        }
    }
}


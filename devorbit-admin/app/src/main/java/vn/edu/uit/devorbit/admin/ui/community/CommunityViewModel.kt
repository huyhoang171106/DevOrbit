package vn.edu.uit.devorbit.admin.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

data class CommunityUiState(
    val messages: List<CommunityMessageAdminResponse> = emptyList(),
    val chatSessions: List<ChatSessionAdminResponse> = emptyList(),
    val chatMessages: List<ChatMessageAdminResponse> = emptyList(),
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CommunityUiState())
    val state: StateFlow<CommunityUiState> = _state.asStateFlow()

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val (messagesR, sessionsR) = coroutineScope {
                    val msgDef = async { adminRepository.getCommunityMessages() }
                    val sesDef = async { adminRepository.getChatSessions() }
                    Pair(msgDef.await(), sesDef.await())
                }
                _state.value = _state.value.copy(
                    messages = messagesR.getOrDefault(emptyList()),
                    chatSessions = sessionsR.getOrDefault(emptyList()),
                    error = listOfNotNull(messagesR.exceptionOrNull(), sessionsR.exceptionOrNull())
                        .firstOrNull()?.message
                )
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun selectTab(index: Int) {
        _state.value = _state.value.copy(selectedTab = index)
    }

    fun deleteMessage(id: Long) {
        viewModelScope.launch {
            adminRepository.deleteCommunityMessage(id).fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        messages = _state.value.messages.filter { it.id != id }
                    )
                },
                onFailure = {
                    _state.value = _state.value.copy(error = it.message)
                }
            )
        }
    }

    fun loadChatMessages(sessionId: String) {
        viewModelScope.launch {
            adminRepository.getChatMessages(sessionId).fold(
                onSuccess = { _state.value = _state.value.copy(chatMessages = it) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }
}

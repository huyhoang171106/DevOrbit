package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.remote.dto.*
import vn.edu.uit.devorbit.mobile.data.datastore.SettingsDataStore
import vn.edu.uit.devorbit.mobile.domain.repository.AuthRepository
import vn.edu.uit.devorbit.mobile.domain.repository.CommunityRepository
import vn.edu.uit.devorbit.mobile.network.stomp.StompEventListener
import com.google.gson.Gson
import javax.inject.Inject

data class ChatChannel(
    val id: Long,
    val channelId: String,
    val name: String,
    val type: String,
    val referenceId: String?
)

data class ChatMessage(
    val id: Long,
    val channelId: Long,
    val studentId: Long,
    val senderName: String,
    val senderAvatar: String?,
    val content: String,
    val imageUrl: String? = null,
    val createdAt: String,
    val deleted: Boolean = false,
    val isSending: Boolean = false
)

data class OnlineMember(
    val studentId: Long?,
    val studentCode: String,
    val displayName: String,
    val avatar: String?
)

data class CommunityUiState(
    val channels: List<ChatChannel> = emptyList(),
    val activeChannel: ChatChannel? = null,
    val messages: List<ChatMessage> = emptyList(),
    val onlineMembers: List<OnlineMember> = emptyList(),
    val isConnected: Boolean = false,
    val isLoadingChannels: Boolean = false,
    val isLoadingMessages: Boolean = false,
    val currentUserId: Long? = null,
    val currentUserName: String = "",
    val error: String? = null
)

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val repository: CommunityRepository,
    private val authRepository: AuthRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    private var currentChannelId: Long? = null

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val token = settingsDataStore.token.first()
            if (token.isNullOrBlank()) {
                val name = settingsDataStore.studentName.first() ?: ""
                val id = settingsDataStore.studentId.first()
                _uiState.update { it.copy(currentUserId = id?.toLong(), currentUserName = name) }
                return@launch
            }
            var id = settingsDataStore.studentId.first()
            val name = settingsDataStore.studentName.first() ?: ""
            
            if (id == null) {
                val result = authRepository.getProfile()
                result.onSuccess {
                    id = settingsDataStore.studentId.first()
                }
            }
            
            _uiState.update { it.copy(currentUserId = id?.toLong(), currentUserName = name) }
        }
    }

    fun connect() {
        viewModelScope.launch {
            val token = settingsDataStore.token.first() ?: return@launch
            repository.connectWebSocket(token, object : StompEventListener {
                override fun onConnected() {
                    _uiState.update { it.copy(isConnected = true, error = null) }
                    currentChannelId?.let { cid -> repository.subscribeToChannel(cid) }
                }
                override fun onMessage(destination: String, body: String) {
                    handleIncomingMessage(destination, body)
                }
                override fun onError(message: String) {
                    _uiState.update { it.copy(error = message) }
                }
                override fun onDisconnected() {
                    _uiState.update { it.copy(isConnected = false) }
                }
                override fun onConnecting(attempt: Int) {
                    _uiState.update { it.copy(isConnected = false) }
                }
            })
        }
    }

    fun loadChannels() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingChannels = true) }
            try {
                val channels = repository.getChannels().map { it.toDomain() }
                _uiState.update { it.copy(channels = channels, isLoadingChannels = false) }
                if (_uiState.value.activeChannel == null) {
                    val general = channels.find { it.type == "GENERAL" } ?: channels.firstOrNull()
                    general?.let { selectChannel(it) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingChannels = false, error = e.message) }
            }
        }
    }

    fun selectChannel(channel: ChatChannel) {
        if (channel.id == currentChannelId) return
        currentChannelId?.let { repository.unsubscribeFromChannel(it) }
        _uiState.update { it.copy(activeChannel = channel, messages = emptyList(), onlineMembers = emptyList()) }
        currentChannelId = channel.id
        if (_uiState.value.isConnected) {
            repository.subscribeToChannel(channel.id)
        }
        loadMessages(channel.id)
    }

    private fun loadMessages(channelId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMessages = true) }
            try {
                val allMessages = mutableListOf<ChatMessageResponse>()
                var page = 0
                var totalPages = 1
                while (page < totalPages) {
                    val response = repository.getChannelMessages(channelId, page, 50)
                    allMessages.addAll(response.content)
                    totalPages = response.totalPages
                    page++
                }
                val messages = allMessages.reversed().map { it.toDomain() }
                _uiState.update { it.copy(messages = messages, isLoadingMessages = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingMessages = false, error = e.message) }
            }
        }
    }

    fun sendMessage(content: String) {
        val channel = _uiState.value.activeChannel ?: return
        val trimmed = content.trim()
        if (trimmed.isBlank()) return

        val tempId = -(System.currentTimeMillis())
        val optimistic = ChatMessage(
            id = tempId, channelId = channel.id, studentId = _uiState.value.currentUserId ?: 0,
            senderName = _uiState.value.currentUserName.ifBlank { "Bạn" }, senderAvatar = null, content = trimmed,
            createdAt = java.time.Instant.now().toString(), isSending = true
        )
        _uiState.update { it.copy(messages = it.messages + optimistic) }

        repository.sendMessage(channel.id, trimmed)
    }

    fun sendImage(imageUrl: String) {
        val channel = _uiState.value.activeChannel ?: return

        val tempId = -(System.currentTimeMillis())
        val optimistic = ChatMessage(
            id = tempId, channelId = channel.id, studentId = _uiState.value.currentUserId ?: 0,
            senderName = _uiState.value.currentUserName.ifBlank { "Bạn" }, senderAvatar = null,
            content = "", imageUrl = imageUrl,
            createdAt = java.time.Instant.now().toString(), isSending = true
        )
        _uiState.update { it.copy(messages = it.messages + optimistic) }

        repository.sendMessage(channel.id, "", imageUrl)
    }

    fun uploadAndSendImage(uri: android.net.Uri, context: android.content.Context) {
        val channel = _uiState.value.activeChannel ?: return
        viewModelScope.launch {
            try {
                val url = repository.uploadImage(channel.id, uri, context)
                if (url != null) {
                    sendImage(url)
                } else {
                    _uiState.update { it.copy(error = "Tải ảnh lên thất bại") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Tải ảnh lên thất bại: ${e.message}") }
            }
        }
    }

    private fun handleIncomingMessage(destination: String, body: String) {
        try {
            if (destination.contains("/presence")) {
                val presence = Gson().fromJson(body, ChannelPresenceResponse::class.java)
                _uiState.update { it.copy(onlineMembers = presence.members.map { m -> m.toDomain() }) }
            } else {
                val msg = Gson().fromJson(body, ChatMessageResponse::class.java)
                if (msg.channelId == currentChannelId) {
                    _uiState.update { state ->
                        val myId = state.currentUserId
                        val filtered = state.messages.filterNot { m ->
                            m.isSending && m.studentId == myId && m.content == msg.content
                        }
                        val exists = filtered.indexOfFirst { it.id == msg.id }
                        val updated = if (exists >= 0) {
                            filtered.toMutableList().apply { set(exists, msg.toDomain()) }
                        } else {
                            filtered + msg.toDomain()
                        }
                        state.copy(messages = updated)
                    }
                }
            }
        } catch (_: Exception) {}
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }

    override fun onCleared() {
        super.onCleared()
        repository.disconnectWebSocket()
    }

    private fun ChatChannelResponse.toDomain() = ChatChannel(id, channelId, name, type, referenceId)
    private fun ChatMessageResponse.toDomain() = ChatMessage(id, channelId, studentId, senderName, senderAvatar, content, imageUrl, createdAt, deleted)
    private fun OnlineMemberResponse.toDomain() = OnlineMember(studentId, studentCode, displayName, avatar)
}

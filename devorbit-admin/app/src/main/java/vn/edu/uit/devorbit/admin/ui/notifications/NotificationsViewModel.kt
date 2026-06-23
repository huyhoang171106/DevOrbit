package vn.edu.uit.devorbit.admin.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.NotificationResponse
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

data class NotificationsUiState(
    val notifications: List<NotificationResponse> = emptyList(),
    val unreadCount: Long = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsUiState())
    val state: StateFlow<NotificationsUiState> = _state.asStateFlow()

    init { loadNotifications() }

    private fun loadNotifications() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                adminRepository.getNotifications().onSuccess { notifs ->
                    _state.value = _state.value.copy(notifications = notifs)
                }
                adminRepository.getUnreadCount().onSuccess { count ->
                    _state.value = _state.value.copy(unreadCount = count)
                }
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun markRead(id: Long) {
        viewModelScope.launch {
            adminRepository.markNotificationRead(id).fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        notifications = _state.value.notifications.map {
                            if (it.id == id) it.copy(isRead = true) else it
                        },
                        unreadCount = (_state.value.unreadCount - 1).coerceAtLeast(0)
                    )
                },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            adminRepository.markAllNotificationsRead().fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        notifications = _state.value.notifications.map { it.copy(isRead = true) },
                        unreadCount = 0
                    )
                },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }
}

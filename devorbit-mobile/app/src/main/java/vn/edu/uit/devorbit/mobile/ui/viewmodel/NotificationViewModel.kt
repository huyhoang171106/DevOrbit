package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.remote.dto.StudentNotificationResponse
import vn.edu.uit.devorbit.mobile.network.ApiService
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<StudentNotificationResponse>>(emptyList())
    val notifications: StateFlow<List<StudentNotificationResponse>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadNotifications()
        startPolling()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _notifications.value = apiService.getNotifications()
                val countResponse = apiService.getUnreadNotificationCount()
                _unreadCount.value = countResponse.count
            } catch (e: Exception) {
                _error.value = "Không thể tải thông báo"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                delay(60_000)
                try {
                    val countResponse = apiService.getUnreadNotificationCount()
                    _unreadCount.value = countResponse.count
                    loadNotifications()
                } catch (_: Exception) { }
            }
        }
    }

    fun markAsRead(id: Long) {
        viewModelScope.launch {
            try {
                apiService.markNotificationRead(id)
                val current = _unreadCount.value
                if (current > 0) _unreadCount.value = current - 1
                loadNotifications()
            } catch (_: Exception) { }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                apiService.markAllNotificationsRead()
                _unreadCount.value = 0
                loadNotifications()
            } catch (_: Exception) { }
        }
    }
}

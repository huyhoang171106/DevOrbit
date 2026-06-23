package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.remote.dto.ApproveDeleteRequest
import vn.edu.uit.devorbit.mobile.data.remote.dto.RespondInviteRequest
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

    private val _actionLoadingId = MutableStateFlow<Long?>(null)
    val actionLoadingId: StateFlow<Long?> = _actionLoadingId.asStateFlow()

    private var pollingJob: kotlinx.coroutines.Job? = null

    init {
        loadNotifications()
        startPolling()
    }

    fun resumePolling() {
        if (pollingJob?.isActive != true) startPolling()
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
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
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(15_000)
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
            } catch (e: Exception) {
                _error.value = "Không thể đánh dấu đã đọc"
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                apiService.markAllNotificationsRead()
                _unreadCount.value = 0
                loadNotifications()
            } catch (e: Exception) {
                _error.value = "Không thể đánh dấu tất cả đã đọc"
            }
        }
    }

    fun acceptInvite(notificationId: Long, planId: Long) {
        viewModelScope.launch {
            _actionLoadingId.value = notificationId
            try {
                apiService.respondInvite(planId, RespondInviteRequest("accept"))
                markAsRead(notificationId)
            } catch (e: Exception) {
                _error.value = "Không thể chấp nhận lời mời"
            }
            _actionLoadingId.value = null
        }
    }

    fun declineInvite(notificationId: Long, planId: Long) {
        viewModelScope.launch {
            _actionLoadingId.value = notificationId
            try {
                apiService.respondInvite(planId, RespondInviteRequest("decline"))
                markAsRead(notificationId)
            } catch (e: Exception) {
                _error.value = "Không thể từ chối lời mời"
            }
            _actionLoadingId.value = null
        }
    }

    fun approveDelete(notificationId: Long, taskId: Long) {
        viewModelScope.launch {
            _actionLoadingId.value = notificationId
            try {
                apiService.approveDeleteTask(taskId, ApproveDeleteRequest("APPROVE"))
                markAsRead(notificationId)
            } catch (e: Exception) {
                _error.value = "Không thể phê duyệt xoá"
            }
            _actionLoadingId.value = null
        }
    }

    fun rejectDelete(notificationId: Long, taskId: Long) {
        viewModelScope.launch {
            _actionLoadingId.value = notificationId
            try {
                apiService.approveDeleteTask(taskId, ApproveDeleteRequest("REJECT"))
                markAsRead(notificationId)
            } catch (e: Exception) {
                _error.value = "Không thể từ chối xoá"
            }
            _actionLoadingId.value = null
        }
    }
    fun approvePlanDelete(notificationId: Long, planId: Long) {
        viewModelScope.launch {
            _actionLoadingId.value = notificationId
            try {
                apiService.approveDeletePlan(planId, ApproveDeleteRequest("APPROVE"))
                markAsRead(notificationId)
            } catch (e: Exception) {
                _error.value = "Không thể phê duyệt xoá kế hoạch"
            }
            _actionLoadingId.value = null
        }
    }

    fun rejectPlanDelete(notificationId: Long, planId: Long) {
        viewModelScope.launch {
            _actionLoadingId.value = notificationId
            try {
                apiService.approveDeletePlan(planId, ApproveDeleteRequest("REJECT"))
                markAsRead(notificationId)
            } catch (e: Exception) {
                _error.value = "Không thể từ chối xoá kế hoạch"
            }
            _actionLoadingId.value = null
        }
    }

}

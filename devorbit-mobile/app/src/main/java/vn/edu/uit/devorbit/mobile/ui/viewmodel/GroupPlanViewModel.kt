package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.remote.dto.*
import vn.edu.uit.devorbit.mobile.network.ApiService
import javax.inject.Inject

data class GroupPlanDetailState(
    val plan: GroupPlanResponse? = null,
    val tasks: List<GroupTaskResponse> = emptyList(),
    val members: List<GroupPlanMemberResponse> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val inviteCode: String = "",
    val inviteLoading: Boolean = false,
    val inviteError: String? = null,
    val actionLoading: Boolean = false,
    val actionError: String? = null
)

@HiltViewModel
class GroupPlanViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _myPlans = MutableStateFlow<List<GroupPlanResponse>>(emptyList())
    val myPlans: StateFlow<List<GroupPlanResponse>> = _myPlans.asStateFlow()

    private val _plansLoading = MutableStateFlow(false)
    val plansLoading: StateFlow<Boolean> = _plansLoading.asStateFlow()

    private val _detail = MutableStateFlow(GroupPlanDetailState())
    val detail: StateFlow<GroupPlanDetailState> = _detail.asStateFlow()

    fun loadMyPlans() {
        viewModelScope.launch {
            _plansLoading.value = true
            try {
                _myPlans.value = apiService.getMyGroupPlans()
            } catch (_: Exception) { }
            _plansLoading.value = false
        }
    }

    fun loadPlan(planId: Long) {
        viewModelScope.launch {
            _detail.update { it.copy(loading = true, error = null) }
            try {
                val plan = apiService.getGroupPlanDetail(planId)
                val tasks = apiService.getGroupTasks(planId)
                val members = apiService.getGroupPlanMembers(planId)
                _detail.update { it.copy(plan = plan, tasks = tasks, members = members, loading = false) }
            } catch (e: Exception) {
                _detail.update { it.copy(loading = false, error = "Không thể tải kế hoạch") }
            }
        }
    }

    fun addTask(planId: Long, title: String, description: String?, assignedTo: String?, deadline: String?) {
        viewModelScope.launch {
            _detail.update { it.copy(actionLoading = true, actionError = null) }
            try {
                apiService.addGroupTask(planId, AddGroupTaskRequest(title, description, assignedTo, deadline))
                val tasks = apiService.getGroupTasks(planId)
                _detail.update { it.copy(tasks = tasks, actionLoading = false) }
            } catch (e: Exception) {
                _detail.update { it.copy(actionLoading = false, actionError = "Không thể thêm nhiệm vụ") }
            }
        }
    }

    fun toggleTask(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            try {
                apiService.updateGroupTask(taskId, UpdateGroupTaskRequest(null, null, null, null, completed))
                val planId = _detail.value.plan?.id ?: return@launch
                val tasks = apiService.getGroupTasks(planId)
                _detail.update { it.copy(tasks = tasks) }
            } catch (_: Exception) { }
        }
    }

    fun inviteMember(planId: Long, studentCode: String) {
        viewModelScope.launch {
            _detail.update { it.copy(inviteLoading = true, inviteError = null) }
            try {
                apiService.inviteMember(planId, InviteMemberRequest(studentCode))
                val members = apiService.getGroupPlanMembers(planId)
                _detail.update { it.copy(members = members, inviteCode = "", inviteLoading = false) }
            } catch (e: Exception) {
                _detail.update { it.copy(inviteLoading = false, inviteError = "Không thể mời thành viên") }
            }
        }
    }

    fun updateInviteCode(code: String) {
        _detail.update { it.copy(inviteCode = code) }
    }

    fun clearInviteError() {
        _detail.update { it.copy(inviteError = null) }
    }

    fun clearActionError() {
        _detail.update { it.copy(actionError = null) }
    }

    fun requestDeleteTask(taskId: Long) {
        viewModelScope.launch {
            _detail.update { it.copy(actionLoading = true, actionError = null) }
            try {
                apiService.requestDeleteTask(taskId)
                val planId = _detail.value.plan?.id ?: return@launch
                val tasks = apiService.getGroupTasks(planId)
                _detail.update { it.copy(tasks = tasks, actionLoading = false) }
            } catch (e: Exception) {
                _detail.update { it.copy(actionLoading = false, actionError = "Không thể yêu cầu xoá") }
            }
        }
    }

    fun respondToInvite(planId: Long, action: String) {
        viewModelScope.launch {
            _detail.update { it.copy(actionLoading = true) }
            try {
                apiService.respondInvite(planId, RespondInviteRequest(action))
                _detail.update { it.copy(actionLoading = false) }
            } catch (_: Exception) {
                _detail.update { it.copy(actionLoading = false) }
            }
        }
    }
}

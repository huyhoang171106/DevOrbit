package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.datastore.SettingsDataStore
import vn.edu.uit.devorbit.mobile.data.remote.dto.*
import vn.edu.uit.devorbit.mobile.network.ApiService
import java.io.IOException
import javax.inject.Inject
import retrofit2.HttpException

data class GroupPlanDetailState(
    val plan: GroupPlanResponse? = null,
    val tasks: List<GroupTaskResponse> = emptyList(),
    val members: List<GroupPlanMemberResponse> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val isCreator: Boolean = false,
    val currentUserCode: String = "",
    val inviteCode: String = "",
    val inviteLoading: Boolean = false,
    val inviteError: String? = null,
    val inviteSuccessCode: String? = null,
    val actionLoading: Boolean = false,
    val actionError: String? = null
)

@HiltViewModel
class GroupPlanViewModel @Inject constructor(
    private val apiService: ApiService,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _myPlans = MutableStateFlow<List<GroupPlanResponse>>(emptyList())
    val myPlans: StateFlow<List<GroupPlanResponse>> = _myPlans.asStateFlow()

    private val _plansLoading = MutableStateFlow(false)
    val plansLoading: StateFlow<Boolean> = _plansLoading.asStateFlow()

    private val _detail = MutableStateFlow(GroupPlanDetailState())
    val detail: StateFlow<GroupPlanDetailState> = _detail.asStateFlow()

    init {
        viewModelScope.launch {
            val code = settingsDataStore.studentCode.first().orEmpty()
            _detail.update { it.copy(currentUserCode = code) }
        }
    }

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
                val currentUserCode = _detail.value.currentUserCode
                _detail.update { it.copy(
                    plan = plan, tasks = tasks, members = members, loading = false,
                    isCreator = plan.creatorStudentCode == currentUserCode
                ) }
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
            } catch (_: Exception) {
                _detail.update { it.copy(actionError = "Không thể cập nhật nhiệm vụ") }
             }
        }
    }

    fun inviteMember(planId: Long, studentCode: String) {
        viewModelScope.launch {
            _detail.update { it.copy(inviteLoading = true, inviteError = null) }
            try {
                apiService.inviteMember(planId, InviteMemberRequest(studentCode))
                val members = apiService.getGroupPlanMembers(planId)
                _detail.update { it.copy(members = members, inviteCode = "", inviteLoading = false, inviteSuccessCode = studentCode) }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val msg = parseInviteError(errorBody) ?: "Không thể mời thành viên (lỗi ${e.code()})"
                _detail.update { it.copy(inviteLoading = false, inviteError = msg) }
            } catch (e: IOException) {
                _detail.update { it.copy(inviteLoading = false, inviteError = "Mất kết nối mạng, vui lòng thử lại") }
            } catch (e: Exception) {
                _detail.update { it.copy(inviteLoading = false, inviteError = "Không thể mời thành viên") }
            }
        }
    }

    private fun parseInviteError(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            val json = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
            val error = json.get("error")?.asString ?: return null
            when {
                error.contains("Cannot invite yourself") -> "Không thể mời chính mình"
                error.contains("already a member") -> "Sinh viên đã là thành viên của kế hoạch này"
                error.contains("Invitation already sent") -> "Đã gửi lời mời đến sinh viên này"
                error.contains("Only the creator can invite") -> "Chỉ người tạo mới có thể mời thành viên"
                error.contains("Student not found") -> "Không tìm thấy sinh viên này"
                error.contains("Group plan not found") -> "Không tìm thấy kế hoạch"
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    fun updateInviteCode(code: String) {
        _detail.update { it.copy(inviteCode = code) }
    }

    fun clearInviteError() {
        _detail.update { it.copy(inviteError = null) }
    }

    fun clearInviteSuccess() {
        _detail.update { it.copy(inviteSuccessCode = null) }
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
                loadMyPlans()
                _detail.update { it.copy(actionLoading = false) }
            } catch (_: Exception) {
                _detail.update { it.copy(actionLoading = false, actionError = "Không thể xử lý lời mời") }
            }
        }
    }

    fun leavePlan(planId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _detail.update { it.copy(actionLoading = true, actionError = null) }
            try {
                apiService.leavePlan(planId)
                onSuccess()
            } catch (e: Exception) {
                _detail.update { it.copy(actionError = "Không thể rời kế hoạch") }
            } finally {
                _detail.update { it.copy(actionLoading = false) }
            }
        }
    }
    fun deletePlan(planId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _detail.update { it.copy(actionLoading = true, actionError = null) }
            try {
                apiService.deleteGroupPlan(planId)
                onSuccess()
            } catch (e: Exception) {
                _detail.update { it.copy(actionError = "Không thể xoá kế hoạch") }
            } finally {
                _detail.update { it.copy(actionLoading = false) }
            }
        }
    }

    fun requestDeletePlan(planId: Long) {
        viewModelScope.launch {
            _detail.update { it.copy(actionLoading = true, actionError = null) }
            try {
                apiService.requestDeletePlan(planId)
                loadPlan(planId)
            } catch (e: HttpException) {
                val msg = when (e.code()) {
                    400 -> "Chỉ người tạo mới có thể xoá trực tiếp"
                    else -> "Không thể yêu cầu xoá (lỗi ${e.code()})"
                }
                _detail.update { it.copy(actionError = msg) }
            } catch (e: IOException) {
                _detail.update { it.copy(actionError = "Mất kết nối mạng") }
            } catch (e: Exception) {
                _detail.update { it.copy(actionError = "Không thể yêu cầu xoá") }
            } finally {
                _detail.update { it.copy(actionLoading = false) }
            }
        }
    }

    fun approveDeletePlan(planId: Long, approved: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _detail.update { it.copy(actionLoading = true, actionError = null) }
            try {
                apiService.approveDeletePlan(planId, ApproveDeleteRequest(if (approved) "APPROVE" else "REJECT"))
                onSuccess()
            } catch (e: Exception) {
                _detail.update { it.copy(actionLoading = false, actionError = "Không thể xử lý yêu cầu") }
            }
        }
    }
}

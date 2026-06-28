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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import retrofit2.HttpException
import vn.edu.uit.devorbit.mobile.ui.viewmodel.TaskFilter
import vn.edu.uit.devorbit.mobile.ui.viewmodel.WeekDay

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
    val actionError: String? = null,
    val timeFilter: TaskFilter = TaskFilter.ALL,
    val memberFilter: String? = null,
    val showTransferDialog: Boolean = false,
    val transferLoading: Boolean = false,
    val selectedDate: String? = null,
    val currentYear: Int = LocalDate.now().year,
    val currentMonth: Int = LocalDate.now().monthValue,
    val currentWeekOffset: Int = 0,
    val maxWeekOffset: Int = 0,
    val weekDates: List<WeekDay> = emptyList()
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

    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val minDate: LocalDate = LocalDate.now().minusMonths(6).with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    init {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            val code = settingsDataStore.studentCode.first().orEmpty()
            _detail.update { it.copy(currentUserCode = code) }
        }
        loadWeekDays()
    }

    fun loadMyPlans() {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            _plansLoading.value = true
            try {
                _myPlans.value = apiService.getMyGroupPlans()
            } catch (_: Exception) { }
            _plansLoading.value = false
        }
    }

    fun loadPlan(planId: Long) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            _detail.update { it.copy(loading = true, error = null) }
            try {
                val currentUserCode = settingsDataStore.studentCode.first().orEmpty()
                _detail.update { it.copy(currentUserCode = currentUserCode) }
                val plan = apiService.getGroupPlanDetail(planId)
                val tasks = apiService.getGroupTasks(planId)
                val members = apiService.getGroupPlanMembers(planId)
                _detail.update { it.copy(
                    plan = plan, tasks = tasks, members = members, loading = false,
                    isCreator = plan.creatorStudentCode == currentUserCode
                ) }
            } catch (e: Exception) {
                _detail.update { it.copy(loading = false, error = "Không thể tải kế hoạch") }
            }
        }
    }

    fun addTask(planId: Long, title: String, description: String?, assignedTo: String?, deadline: String?, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            _detail.update { it.copy(actionLoading = true, actionError = null) }
            try {
                val created = apiService.addGroupTask(planId, AddGroupTaskRequest(title, description, assignedTo, deadline))
                _detail.update { it.copy(tasks = listOf(created) + it.tasks.filter { it.id != created.id }, actionLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                _detail.update { it.copy(actionLoading = false, actionError = "Không thể thêm nhiệm vụ") }
            }
        }
    }

    fun updateTask(taskId: Long, title: String, description: String?, assignedTo: String?, deadline: String?, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            _detail.update { it.copy(actionLoading = true, actionError = null) }
            try {
                apiService.updateGroupTask(taskId, UpdateGroupTaskRequest(title, description, assignedTo, deadline, null))
                val planId = _detail.value.plan?.id ?: return@launch
                val tasks = apiService.getGroupTasks(planId)
                _detail.update { it.copy(tasks = tasks, actionLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                _detail.update { it.copy(actionLoading = false, actionError = "Không thể cập nhật nhiệm vụ") }
            }
        }
    }

    fun toggleTask(taskId: Long, completed: Boolean) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
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
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
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

    fun setTimeFilter(filter: TaskFilter) {
        _detail.update { it.copy(timeFilter = filter) }
    }

    fun setMemberFilter(memberCode: String?) {
        _detail.update { it.copy(memberFilter = memberCode) }
    }

    fun requestDeleteTask(taskId: Long) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
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
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
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

    fun removeMember(planId: Long, memberId: Long) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            _detail.update { it.copy(actionLoading = true, actionError = null) }
            try {
                apiService.removeMember(planId, memberId)
                val members = apiService.getGroupPlanMembers(planId)
                _detail.update { it.copy(members = members, actionLoading = false) }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val msg = parseInviteError(errorBody) ?: "Không thể xoá thành viên (lỗi ${e.code()})"
                _detail.update { it.copy(actionLoading = false, actionError = msg) }
            } catch (e: IOException) {
                _detail.update { it.copy(actionLoading = false, actionError = "Mất kết nối mạng, vui lòng thử lại") }
            } catch (e: Exception) {
                _detail.update { it.copy(actionLoading = false, actionError = "Không thể xoá thành viên") }
            }
        }
    }

    fun leavePlan(planId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
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
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
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

    fun showTransferDialog() {
        _detail.update { it.copy(showTransferDialog = true) }
    }

    fun hideTransferDialog() {
        _detail.update { it.copy(showTransferDialog = false, actionError = null) }
    }

    fun transferOwnership(planId: Long, newOwnerCode: String, onSuccess: () -> Unit) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            _detail.update { it.copy(transferLoading = true, actionError = null) }
            try {
                apiService.transferOwnership(planId, TransferOwnershipRequest(newOwnerCode))
                onSuccess()
            } catch (e: Exception) {
                _detail.update { it.copy(actionError = "Không thể chuyển quyền trưởng nhóm") }
            } finally {
                _detail.update { it.copy(transferLoading = false, showTransferDialog = false) }
            }
        }
    }

    fun requestDeletePlan(planId: Long) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
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
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            _detail.update { it.copy(actionLoading = true, actionError = null) }
            try {
                apiService.approveDeletePlan(planId, ApproveDeleteRequest(if (approved) "APPROVE" else "REJECT"))
                onSuccess()
            } catch (e: Exception) {
                _detail.update { it.copy(actionLoading = false, actionError = "Không thể xử lý yêu cầu") }
            } finally {
                _detail.update { it.copy(actionLoading = false) }
            }
        }
    }


    private fun computeMaxWeekOffset(): Int {
        val now = LocalDate.now()
        val monday = now.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val diff = monday.toEpochDay() - minDate.toEpochDay()
        return if (diff < 0) 0 else (diff / 7).toInt()
    }

    fun selectDate(date: String?) {
        _detail.update { it.copy(selectedDate = date) }
    }

    fun navigateMonth(delta: Int) {
        val s = _detail.value
        var newMonth = s.currentMonth + delta
        var newYear = s.currentYear
        if (newMonth < 1) { newMonth = 12; newYear-- }
        else if (newMonth > 12) { newMonth = 1; newYear++ }
        if (newYear < 2026 || (newYear == 2026 && newMonth < 6)) return
        _detail.update { it.copy(currentYear = newYear, currentMonth = newMonth, selectedDate = null) }
    }

    fun navigateWeek(delta: Int) {
        val current = _detail.value.currentWeekOffset
        val maxOffset = computeMaxWeekOffset()
        val proposed = (current + delta).coerceIn(0, maxOffset)
        _detail.update { it.copy(currentWeekOffset = proposed, selectedDate = null) }
        loadWeekDays()
    }

    private fun loadWeekDays() {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            val offset = _detail.value.currentWeekOffset
            val maxOffset = computeMaxWeekOffset()
            var date = LocalDate.now().minusWeeks(offset.toLong()).with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val todayStr = LocalDate.now().format(dateFormat)
            val days = mutableListOf<WeekDay>()

            for (i in 0..6) {
                val dateStr = date.format(dateFormat)
                val dayLabel = when (date.dayOfWeek) {
                    DayOfWeek.MONDAY -> "T2"
                    DayOfWeek.TUESDAY -> "T3"
                    DayOfWeek.WEDNESDAY -> "T4"
                    DayOfWeek.THURSDAY -> "T5"
                    DayOfWeek.FRIDAY -> "T6"
                    DayOfWeek.SATURDAY -> "T7"
                    DayOfWeek.SUNDAY -> "CN"
                }
                days.add(WeekDay(
                    date = dateStr,
                    label = dayLabel,
                    activity = null,
                    isToday = dateStr == todayStr,
                    qualifiesForStreak = false
                ))
                date = date.plusDays(1)
            }
            _detail.update { it.copy(weekDates = days, maxWeekOffset = maxOffset) }
        }
    }
}


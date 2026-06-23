package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.remote.dto.CreateGroupPlanRequest
import vn.edu.uit.devorbit.mobile.data.remote.dto.CreatePersonalTaskRequest
import vn.edu.uit.devorbit.mobile.data.remote.dto.UpdatePersonalTaskRequest
import vn.edu.uit.devorbit.mobile.domain.model.TaskItem
import vn.edu.uit.devorbit.mobile.domain.model.millisToIso
import vn.edu.uit.devorbit.mobile.domain.model.toTaskItem
import vn.edu.uit.devorbit.mobile.network.ApiService
import android.util.Log
import java.io.IOException
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import retrofit2.HttpException

data class TaskManagementUiState(
    val tasks: List<TaskItem> = emptyList(),
    val filter: TaskFilter = TaskFilter.TODAY,
    val searchQuery: String = "",
    val isEditing: Boolean = false,
    val editingTaskId: Long? = null,
    val inputTitle: String = "",
    val inputDescription: String = "",
    val inputDeadline: Long? = null,
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val inputRecurrence: String? = null,
    val inputRecurrenceDays: Int? = null,
    val inputRecurrenceStartDate: Long? = null,
    val inputRecurrenceEndDate: Long? = null,
    val showRecurrenceStartPicker: Boolean = false,
    val showRecurrenceEndPicker: Boolean = false,
    val creatingPlan: Boolean = false,
    val planTitle: String = "",
    val showCreatePlanDialog: Boolean = false,
    val planError: String? = null
)

@HiltViewModel
class TaskManagementViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _state = MutableStateFlow(TaskManagementUiState())
    val state: StateFlow<TaskManagementUiState> = _state.asStateFlow()

    init {
        observeTasks()
    }

    private fun observeTasks() {
        refreshTasks()
    }

    private fun refreshTasks() {
        viewModelScope.launch {
            try {
                val s = _state.value
                val filterParam = when (s.filter) {
                    TaskFilter.TODAY -> "today"
                    TaskFilter.WEEK -> "week"
                    TaskFilter.ALL -> "all"
                }
                var tasks = apiService.getPersonalTasks(filterParam).map { it.toTaskItem() }
                if (s.searchQuery.isNotBlank()) {
                    tasks = tasks.filter { it.title.contains(s.searchQuery, ignoreCase = true) }
                }
                _state.update { it.copy(tasks = tasks.sortedBy { it.completed }) }
            } catch (_: Exception) { }
        }
    }

    fun setFilter(filter: TaskFilter) {
        _state.update { it.copy(filter = filter) }
        refreshTasks()
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
        refreshTasks()
    }

    fun updateDescription(desc: String) {
        _state.update { it.copy(inputDescription = desc) }
    }

    fun resetInput() {
        _state.update { it.copy(inputTitle = "", inputDescription = "", inputDeadline = null, inputRecurrence = null, inputRecurrenceDays = null, inputRecurrenceStartDate = null, inputRecurrenceEndDate = null, showRecurrenceStartPicker = false, showRecurrenceEndPicker = false, isEditing = false, editingTaskId = null) }
    }

    fun startEdit(task: TaskItem) {
        _state.update { it.copy(
            isEditing = true,
            editingTaskId = task.id,
            inputTitle = task.title,
            inputDescription = task.description,
            inputDeadline = task.deadline,
            inputRecurrence = task.recurrence,
            inputRecurrenceDays = task.recurrenceDaysOfWeek,
            inputRecurrenceStartDate = task.recurrenceStartDate,
            inputRecurrenceEndDate = task.recurrenceEndDate
        ) }
    }

    fun updateTitle(title: String) {
        _state.update { it.copy(inputTitle = title) }
    }

    fun updateDeadline(millis: Long?) {
        if (millis != null) {
            _state.update { it.copy(inputDeadline = millis, showDatePicker = false, showTimePicker = true) }
        } else {
            _state.update { it.copy(inputDeadline = null, showDatePicker = false) }
        }
    }

    fun showDatePicker() {
        _state.update { it.copy(showDatePicker = true) }
    }

    fun hideDatePicker() {
        _state.update { it.copy(showDatePicker = false) }
    }

    fun showTimePicker() {
        _state.update { it.copy(showTimePicker = true) }
    }

    fun hideTimePicker() {
        _state.update { it.copy(showTimePicker = false) }
    }

    fun updateTime(hour: Int, minute: Int) {
        val s = _state.value
        if (s.inputRecurrence != null) {
            val today = LocalDate.now()
            val millis = today.atTime(hour, minute).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            _state.update { it.copy(inputDeadline = millis, showTimePicker = false) }
        } else {
            val dateMillis = s.inputDeadline ?: return
            val date = Instant.ofEpochMilli(dateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            val combined = date.atTime(hour, minute).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            _state.update { it.copy(inputDeadline = combined, showTimePicker = false) }
        }
    }

    fun updateRecurrence(recurrence: String?) {
        _state.update { state ->
            state.copy(
                inputRecurrence = recurrence,
                inputRecurrenceDays = when {
                    recurrence == "WEEKLY" && state.inputRecurrenceDays == null -> 0
                    recurrence != "WEEKLY" -> null
                    else -> state.inputRecurrenceDays
                }
            )
        }
    }

    fun showRecurrenceStartPicker() {
        _state.update { it.copy(showRecurrenceStartPicker = true) }
    }

    fun hideRecurrenceStartPicker() {
        _state.update { it.copy(showRecurrenceStartPicker = false) }
    }

    fun updateRecurrenceStartDate(millis: Long?) {
        _state.update { it.copy(inputRecurrenceStartDate = millis, showRecurrenceStartPicker = false) }
    }

    fun showRecurrenceEndPicker() {
        _state.update { it.copy(showRecurrenceEndPicker = true) }
    }

    fun hideRecurrenceEndPicker() {
        _state.update { it.copy(showRecurrenceEndPicker = false) }
    }

    fun updateRecurrenceEndDate(millis: Long?) {
        _state.update { it.copy(inputRecurrenceEndDate = millis, showRecurrenceEndPicker = false) }
    }

    fun toggleRecurrenceDay(dayOfWeek: DayOfWeek) {
        val bit = 1 shl (dayOfWeek.value - 1)
        val current = _state.value.inputRecurrenceDays ?: 0
        val updated = current xor bit
        _state.update { it.copy(inputRecurrenceDays = if (updated != 0) updated else null) }
    }

    fun saveTask() {
        val s = _state.value
        val title = s.inputTitle.trim()
        if (title.isBlank()) return
        viewModelScope.launch {
            try {
                val deadline = if (s.inputRecurrence != null) {
                    val startDate = s.inputRecurrenceStartDate?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    } ?: LocalDate.now()
                    val time = s.inputDeadline?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalTime()
                    } ?: LocalTime.now()
                    startDate.atTime(time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                } else {
                    s.inputDeadline
                }
                if (s.isEditing && s.editingTaskId != null) {
                    apiService.updatePersonalTask(s.editingTaskId, UpdatePersonalTaskRequest(
                        title = title,
                        description = s.inputDescription.ifBlank { null },
                        deadline = millisToIso(deadline),
                        recurrence = s.inputRecurrence,
                        recurrenceDaysOfWeek = s.inputRecurrenceDays,
                        recurrenceStartDate = s.inputRecurrenceStartDate?.let {
                            java.time.Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                        },
                        recurrenceEndDate = s.inputRecurrenceEndDate?.let {
                            java.time.Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                        }
                    ))
                } else {
                    apiService.createPersonalTask(CreatePersonalTaskRequest(
                        title = title,
                        description = s.inputDescription.ifBlank { null },
                        deadline = millisToIso(deadline),
                        recurrence = s.inputRecurrence,
                        recurrenceDaysOfWeek = s.inputRecurrenceDays,
                        recurrenceStartDate = s.inputRecurrenceStartDate?.let {
                            java.time.Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                        },
                        recurrenceEndDate = s.inputRecurrenceEndDate?.let {
                            java.time.Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                        }
                    ))
                }
                _state.update { it.copy(inputTitle = "", inputDescription = "", inputDeadline = null, inputRecurrence = null, inputRecurrenceDays = null, inputRecurrenceStartDate = null, inputRecurrenceEndDate = null, showRecurrenceStartPicker = false, showRecurrenceEndPicker = false, isEditing = false, editingTaskId = null) }
                refreshTasks()
            } catch (_: Exception) { }
        }
    }

    fun toggleTask(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            try {
                if (completed) {
                    val currentTask = _state.value.tasks.find { it.id == taskId }
                    apiService.togglePersonalTask(taskId, mapOf("completed" to true))
                    if (currentTask?.recurrence != null) {
                        val nextDeadline = computeNextDeadline(currentTask)
                        if (nextDeadline != null) {
                            apiService.createPersonalTask(CreatePersonalTaskRequest(
                                title = currentTask.title,
                                description = currentTask.description.ifBlank { null },
                                deadline = millisToIso(nextDeadline),
                                recurrence = currentTask.recurrence,
                                recurrenceDaysOfWeek = currentTask.recurrenceDaysOfWeek,
                                recurrenceStartDate = currentTask.recurrenceStartDate?.let {
                                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                                },
                                recurrenceEndDate = currentTask.recurrenceEndDate?.let {
                                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                                }
                            ))
                        }
                    }
                } else {
                    apiService.togglePersonalTask(taskId, mapOf("completed" to false))
                }
                refreshTasks()
            } catch (_: Exception) { }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            try {
                apiService.deletePersonalTask(taskId)
                refreshTasks()
            } catch (_: Exception) { }
        }
    }

    // ── Group Plan ──

    fun showCreatePlanDialog() {
        _state.update { it.copy(showCreatePlanDialog = true, planTitle = "", planError = null) }
    }

    fun hideCreatePlanDialog() {
        _state.update { it.copy(showCreatePlanDialog = false, planError = null) }
    }

    fun updatePlanTitle(title: String) {
        _state.update { it.copy(planTitle = title) }
    }

    fun createGroupPlan(onSuccess: (Long) -> Unit) {
        val title = _state.value.planTitle.trim()
        if (title.isBlank()) {
            _state.update { it.copy(planError = "Vui lòng nhập tên kế hoạch") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(creatingPlan = true, planError = null) }
            try {
                val response = apiService.createGroupPlan(
                    CreateGroupPlanRequest(
                        title = title,
                        description = null,
                        deadline = null
                    )
                )
                _state.update { it.copy(creatingPlan = false, showCreatePlanDialog = false) }
                onSuccess(response.id)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("GroupPlan", "HTTP ${e.code()}: $errorBody")
                val msg = when (e.code()) {
                    400 -> "Tên kế hoạch không hợp lệ"
                    401, 403 -> "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại"
                    else -> "Không thể tạo kế hoạch (lỗi ${e.code()})"
                }
                _state.update { it.copy(creatingPlan = false, planError = msg) }
            } catch (e: IOException) {
                Log.e("GroupPlan", "Network: ${e.message}")
                _state.update { it.copy(creatingPlan = false, planError = "Mất kết nối mạng, vui lòng thử lại") }
            } catch (e: Exception) {
                Log.e("GroupPlan", "Unexpected", e)
                _state.update { it.copy(creatingPlan = false, planError = "Không thể tạo kế hoạch") }
            }
        }
    }

}

private fun computeNextDeadline(task: TaskItem): Long? {
    val deadline = task.deadline ?: return null
    val recurrence = task.recurrence ?: return null
    val zdt = Instant.ofEpochMilli(deadline).atZone(ZoneId.systemDefault())
    val currentDate = zdt.toLocalDate()
    val currentTime = zdt.toLocalTime()
    // Stop recurring if past end date
    if (task.recurrenceEndDate != null && currentDate >= Instant.ofEpochMilli(task.recurrenceEndDate).atZone(ZoneId.systemDefault()).toLocalDate()) {
        return null
    }
    val nextDate = when (recurrence) {
        "DAILY" -> currentDate.plusDays(1)
        "WEEKLY" -> {
            val bitmask = task.recurrenceDaysOfWeek
            if (bitmask != null) {
                val currentDow = currentDate.dayOfWeek.value
                var nextLocalDate: LocalDate? = null
                for (offset in 1..7) {
                    val dowValue = ((currentDow - 1 + offset) % 7) + 1
                    val bit = 1 shl (dowValue - 1)
                    if (bitmask and bit != 0) {
                        nextLocalDate = currentDate.plusDays(offset.toLong())
                        break
                    }
                }
                nextLocalDate ?: currentDate.plusWeeks(1)
            } else {
                currentDate.plusWeeks(1)
            }
        }
        "MONTHLY" -> currentDate.plusMonths(1)
        else -> return null
    }
    return nextDate.atTime(currentTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.local.entity.TaskEntity
import vn.edu.uit.devorbit.mobile.data.remote.dto.CreateGroupPlanRequest
import vn.edu.uit.devorbit.mobile.data.repository.AcademicRepository
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
    val tasks: List<TaskEntity> = emptyList(),
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
    private val academicRepository: AcademicRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _state = MutableStateFlow(TaskManagementUiState())
    val state: StateFlow<TaskManagementUiState> = _state.asStateFlow()

    init {
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            combine(
                academicRepository.allTasks,
                _state.map { it.filter }.distinctUntilChanged(),
                _state.map { it.searchQuery }.distinctUntilChanged()
            ) { tasks, filter, query ->
                var filtered = when (filter) {
                    TaskFilter.TODAY -> tasks.filter { isTaskToday(it) }
                    TaskFilter.WEEK -> tasks.filter { isTaskThisWeek(it) }
                    TaskFilter.ALL -> tasks
                }
                if (query.isNotBlank()) {
                    filtered = filtered.filter { it.title.contains(query, ignoreCase = true) }
                }
                filtered.sortedBy { it.completed }
            }.collect { filtered ->
                _state.update { it.copy(tasks = filtered) }
            }
        }
    }

    fun setFilter(filter: TaskFilter) {
        _state.update { it.copy(filter = filter) }
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun updateDescription(desc: String) {
        _state.update { it.copy(inputDescription = desc) }
    }

    fun resetInput() {
        _state.update { it.copy(inputTitle = "", inputDescription = "", inputDeadline = null, inputRecurrence = null, inputRecurrenceDays = null, inputRecurrenceStartDate = null, inputRecurrenceEndDate = null, showRecurrenceStartPicker = false, showRecurrenceEndPicker = false, isEditing = false, editingTaskId = null) }
    }

    fun startEdit(task: TaskEntity) {
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
            val task = if (s.isEditing && s.editingTaskId != null) {
                TaskEntity(
                    id = s.editingTaskId,
                    title = title,
                    description = s.inputDescription,
                    deadline = deadline,
                    taskType = "general",
                    recurrence = s.inputRecurrence,
                    recurrenceDaysOfWeek = s.inputRecurrenceDays,
                    recurrenceStartDate = s.inputRecurrenceStartDate,
                    recurrenceEndDate = s.inputRecurrenceEndDate,
                    completed = _state.value.tasks.find { it.id == s.editingTaskId }?.completed ?: false,
                    createdAt = _state.value.tasks.find { it.id == s.editingTaskId }?.createdAt ?: System.currentTimeMillis()
                )
            } else {
                TaskEntity(
                    title = title,
                    description = s.inputDescription,
                    deadline = deadline,
                    taskType = "general",
                    recurrence = s.inputRecurrence,
                    recurrenceDaysOfWeek = s.inputRecurrenceDays,
                    recurrenceStartDate = s.inputRecurrenceStartDate,
                    recurrenceEndDate = s.inputRecurrenceEndDate
                )
            }
            academicRepository.saveTask(task)
            _state.update { it.copy(inputTitle = "", inputDescription = "", inputDeadline = null, inputRecurrence = null, inputRecurrenceDays = null, inputRecurrenceStartDate = null, inputRecurrenceEndDate = null, showRecurrenceStartPicker = false, showRecurrenceEndPicker = false, isEditing = false, editingTaskId = null) }
        }
    }

    fun toggleTask(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            if (completed) {
                val currentTask = _state.value.tasks.find { it.id == taskId }
                academicRepository.setTaskCompleted(taskId, true)
                if (currentTask?.recurrence != null) {
                    val nextDeadline = computeNextDeadline(currentTask)
                    if (nextDeadline != null) {
                        val nextTask = currentTask.copy(
                            id = 0,
                            completed = false,
                            deadline = nextDeadline,
                            createdAt = System.currentTimeMillis()
                        )
                        academicRepository.saveTask(nextTask)
                    }
                }
            } else {
                academicRepository.setTaskCompleted(taskId, false)
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            val task = _state.value.tasks.find { it.id == taskId } ?: return@launch
            academicRepository.deleteTask(task)
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

    private fun isTaskToday(task: TaskEntity): Boolean {
        if (task.deadline == null) return false
        val date = Instant.ofEpochMilli(task.deadline).atZone(ZoneId.systemDefault()).toLocalDate()
        return date == LocalDate.now()
    }

    private fun isTaskThisWeek(task: TaskEntity): Boolean {
        if (task.deadline == null) return false
        val date = Instant.ofEpochMilli(task.deadline).atZone(ZoneId.systemDefault()).toLocalDate()
        val now = LocalDate.now()
        val monday = now.with(DayOfWeek.MONDAY)
        val sunday = now.with(DayOfWeek.SUNDAY)
        return date >= monday && date <= sunday
    }
}

private fun computeNextDeadline(task: TaskEntity): Long? {
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

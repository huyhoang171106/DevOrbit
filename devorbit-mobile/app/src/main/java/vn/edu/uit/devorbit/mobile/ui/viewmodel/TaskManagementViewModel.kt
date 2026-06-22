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
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

data class TaskManagementUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val filter: TaskFilter = TaskFilter.TODAY,
    val searchQuery: String = "",
    val inputTitle: String = "",
    val inputDescription: String = "",
    val inputDeadline: Long? = null,
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val inputRecurrence: String? = null,
    val inputRecurrenceDays: Int? = null,
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
        _state.update { it.copy(inputTitle = "", inputDescription = "", inputDeadline = null, inputRecurrence = null, inputRecurrenceDays = null) }
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
        val dateMillis = _state.value.inputDeadline ?: return
        val date = Instant.ofEpochMilli(dateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        val dateTime = date.atTime(hour, minute)
        val combined = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        _state.update { it.copy(inputDeadline = combined, showTimePicker = false) }
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

    fun toggleRecurrenceDay(dayOfWeek: DayOfWeek) {
        val bit = 1 shl (dayOfWeek.value - 1)
        val current = _state.value.inputRecurrenceDays ?: 0
        val updated = current xor bit
        _state.update { it.copy(inputRecurrenceDays = if (updated != 0) updated else null) }
    }

    fun addTask() {
        val title = _state.value.inputTitle.trim()
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = TaskEntity(
                title = title,
                description = _state.value.inputDescription,
                deadline = _state.value.inputDeadline,
                taskType = "general",
                recurrence = _state.value.inputRecurrence,
                recurrenceDaysOfWeek = _state.value.inputRecurrenceDays
            )
            academicRepository.saveTask(task)
            _state.update { it.copy(inputTitle = "", inputDescription = "", inputDeadline = null, inputRecurrence = null, inputRecurrenceDays = null) }
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
        if (title.isBlank()) return
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
            } catch (e: Exception) {
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

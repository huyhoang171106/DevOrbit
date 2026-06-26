package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.remote.dto.CreateGroupPlanRequest
import vn.edu.uit.devorbit.mobile.data.remote.dto.CreatePersonalTaskRequest
import vn.edu.uit.devorbit.mobile.data.remote.dto.GroupPlanResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.UpdatePersonalTaskRequest
import vn.edu.uit.devorbit.mobile.domain.model.TaskItem
import vn.edu.uit.devorbit.mobile.domain.model.millisToIso
import vn.edu.uit.devorbit.mobile.domain.model.toTaskItem
import vn.edu.uit.devorbit.mobile.domain.repository.AuthRepository
import vn.edu.uit.devorbit.mobile.network.ApiService
import vn.edu.uit.devorbit.mobile.ui.viewmodel.WeekDay
import android.util.Log
import java.io.IOException
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
    val planError: String? = null,
    val groupPlans: List<GroupPlanResponse> = emptyList(),
    val groupPlansLoading: Boolean = false,
    val error: String? = null,
    val taskLoading: Boolean = false,
    val saveLoading: Boolean = false,
    val deleteLoading: Boolean = false,
    val continuedDates: Set<LocalDate> = emptySet(),
    val selectedDate: String? = null,
    val currentYear: Int = LocalDate.now().year,
    val currentMonth: Int = LocalDate.now().monthValue,
    val currentWeekOffset: Int = 0,
    val maxWeekOffset: Int = 0,
    val weekDates: List<WeekDay> = emptyList()
)

@HiltViewModel
class TaskManagementViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _state = MutableStateFlow(TaskManagementUiState())
    val state: StateFlow<TaskManagementUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        observeTasks()
        loadGroupPlans()
        loadWeekDays()
    }

    private fun observeTasks() {
        refreshTasks()
    }

    private fun refreshTasks(filter: TaskFilter = _state.value.filter, searchQuery: String = _state.value.searchQuery) {
        viewModelScope.launch {
            try {
                val filterParam = when (filter) {
                    TaskFilter.TODAY -> "today"
                    TaskFilter.WEEK -> "week"
                    TaskFilter.ALL -> "all"
                }
                var tasks = apiService.getPersonalTasks(filterParam).map { it.toTaskItem() }
                if (searchQuery.isNotBlank()) {
                    tasks = tasks.filter { it.title.contains(searchQuery, ignoreCase = true) }
                }
                val now = System.currentTimeMillis()
                val sorted = tasks.sortedWith(compareBy<TaskItem> {
                    when {
                        it.completed -> 2
                        it.deadline != null && it.deadline < now -> 1
                        else -> 0
                    }
                }.thenBy {
                    it.deadline ?: Long.MAX_VALUE
                })
                _state.update { it.copy(tasks = sorted) }
            } catch (_: Exception) {
                _state.update { it.copy(error = "Không thể tải nhiệm vụ") }
            }
        }
    }

    fun setFilter(filter: TaskFilter) {
        _state.update { it.copy(filter = filter) }
        refreshTasks()
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            refreshTasks()
        }
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
        if (_state.value.saveLoading) return
        val s = _state.value
        val title = s.inputTitle.trim()
        if (title.isBlank()) return
        if (s.inputRecurrence == null && s.inputDeadline == null) {
            _state.update { it.copy(error = "Vui lòng chọn deadline hoặc thiết lập lặp lại") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(saveLoading = true) }
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
                if (deadline != null) {
                    val deadlineZdt = Instant.ofEpochMilli(deadline).atZone(ZoneId.systemDefault())
                    if (deadlineZdt.toLocalDate() == LocalDate.now(ZoneId.systemDefault()) && deadline <= System.currentTimeMillis()) {
                        _state.update { it.copy(saveLoading = false, error = "Giờ mà bạn chọn đã qua, xin vui lòng chọn giờ khác") }
                        return@launch
                    }
                }
                if (s.inputRecurrence != null) {
                    val today = LocalDate.now(ZoneId.systemDefault())
                    val minEndDate = today.plusWeeks(1)
                    val startDate = s.inputRecurrenceStartDate?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    } ?: today
                    if (startDate.isBefore(today)) {
                        _state.update { it.copy(saveLoading = false, error = "Ngày bắt đầu không thể trước hôm nay") }
                        return@launch
                    }
                    if (s.inputRecurrenceEndDate == null) {
                        _state.update { it.copy(saveLoading = false, error = "Vui lòng chọn ngày kết thúc") }
                        return@launch
                    }
                    val endDate = Instant.ofEpochMilli(s.inputRecurrenceEndDate).atZone(ZoneId.systemDefault()).toLocalDate()
                    if (endDate.isBefore(minEndDate)) {
                        _state.update { it.copy(saveLoading = false, error = "Ngày kết thúc phải cách hôm nay ít nhất 1 tuần") }
                        return@launch
                    }
                }
                if (s.isEditing && s.editingTaskId != null) {
                    val updated = apiService.updatePersonalTask(s.editingTaskId, UpdatePersonalTaskRequest(
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
                    val updatedTaskItem = updated.toTaskItem()
                    _state.update { state ->
                        state.copy(
                            tasks = state.tasks.map { if (it.id == updated.id) updatedTaskItem else it },
                            inputTitle = "", inputDescription = "", inputDeadline = null,
                            inputRecurrence = null, inputRecurrenceDays = null,
                            inputRecurrenceStartDate = null, inputRecurrenceEndDate = null,
                            showRecurrenceStartPicker = false, showRecurrenceEndPicker = false,
                            isEditing = false, editingTaskId = null,
                            saveLoading = false
                        )
                    }
                } else {
                    val created = apiService.createPersonalTask(CreatePersonalTaskRequest(
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
                    val newTaskItem = created.toTaskItem()
                    _state.update { state ->
                        state.copy(
                            tasks = listOf(newTaskItem) + state.tasks.filter { it.id != newTaskItem.id },
                            inputTitle = "", inputDescription = "", inputDeadline = null,
                            inputRecurrence = null, inputRecurrenceDays = null,
                            inputRecurrenceStartDate = null, inputRecurrenceEndDate = null,
                            showRecurrenceStartPicker = false, showRecurrenceEndPicker = false,
                            isEditing = false, editingTaskId = null,
                            saveLoading = false
                        )
                    }
                }
            } catch (_: Exception) {
                _state.update { it.copy(error = "Không thể lưu nhiệm vụ", saveLoading = false) }
            }
        }
    }

    fun toggleTask(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(taskLoading = true) }
            try {
                if (completed) {
                    val toggled = apiService.togglePersonalTask(taskId, mapOf("completed" to true))
                    if (toggled.recurrence != null) {
                        val taskItem = toggled.toTaskItem()
                        val nextDeadline = computeNextDeadline(taskItem)
                        if (nextDeadline != null) {
                            apiService.createPersonalTask(CreatePersonalTaskRequest(
                                title = taskItem.title,
                                description = taskItem.description.ifBlank { null },
                                deadline = millisToIso(nextDeadline),
                                recurrence = taskItem.recurrence,
                                recurrenceDaysOfWeek = taskItem.recurrenceDaysOfWeek,
                                recurrenceStartDate = taskItem.recurrenceStartDate?.let {
                                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                                },
                                recurrenceEndDate = taskItem.recurrenceEndDate?.let {
                                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                                }
                            ))
                        }
                    }
                } else {
                    apiService.togglePersonalTask(taskId, mapOf("completed" to false))
                }
                _state.update { it.copy(taskLoading = false) }
                refreshTasks()
            } catch (_: Exception) {
                _state.update { it.copy(error = "Không thể cập nhật nhiệm vụ", taskLoading = false) }
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(deleteLoading = true) }
            try {
                apiService.deletePersonalTask(taskId)
                _state.update { it.copy(deleteLoading = false) }
                refreshTasks()
            } catch (_: Exception) {
                _state.update { it.copy(error = "Không thể xoá nhiệm vụ", deleteLoading = false) }
            }
        }
    }
    fun clearError() { _state.update { it.copy(error = null) } }


    fun continueDateTasks(date: LocalDate) {
        viewModelScope.launch {
            _state.update { it.copy(taskLoading = true) }
            val tasksToContinue = _state.value.tasks.filter { task ->
                if (task.completed || task.recurrence != null || task.deadline == null) return@filter false
                val deadline = Instant.ofEpochMilli(task.deadline!!).atZone(ZoneId.systemDefault()).toLocalDate()
                deadline == date
            }
            var hasError = false
            var anySucceeded = false
            for (task in tasksToContinue) {
                try {
                    val zdt = Instant.ofEpochMilli(task.deadline!!).atZone(ZoneId.systemDefault())
                    val today = LocalDate.now(ZoneId.systemDefault())
                    val newDeadline = today.atTime(zdt.toLocalTime()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    apiService.createPersonalTask(CreatePersonalTaskRequest(
                        title = task.title,
                        description = task.description.ifBlank { null },
                        deadline = millisToIso(newDeadline),
                        recurrence = null,
                        recurrenceDaysOfWeek = null
                    ))
                    anySucceeded = true
                } catch (_: Exception) {
                    hasError = true
                }
            }
            if (anySucceeded) {
                _state.update { it.copy(continuedDates = it.continuedDates + date) }
            }
            _state.update { it.copy(taskLoading = false) }
            if (hasError) {
                _state.update { it.copy(error = "Không thể tiếp tục một số nhiệm vụ") }
            }
            refreshTasks()
        }
    }

    fun loadGroupPlans() {
        viewModelScope.launch {
            _state.update { it.copy(groupPlansLoading = true) }
            try {
                val plans = apiService.getMyGroupPlans()
                _state.update { it.copy(groupPlans = plans, groupPlansLoading = false) }
            } catch (_: Exception) {
                _state.update { it.copy(groupPlansLoading = false) }
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

    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val minDate: LocalDate = LocalDate.now().minusMonths(6).with(DayOfWeek.MONDAY)

    private fun computeMaxWeekOffset(): Int {
        val now = LocalDate.now()
        val monday = now.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val minDate = LocalDate.now().minusMonths(6).with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val diff = monday.toEpochDay() - minDate.toEpochDay()
        return if (diff < 0) 0 else (diff / 7).toInt()
    }

    fun selectDate(date: String?) {
        _state.update { it.copy(selectedDate = date) }
    }

    fun navigateMonth(delta: Int) {
        val s = _state.value
        var newMonth = s.currentMonth + delta
        var newYear = s.currentYear
        if (newMonth < 1) { newMonth = 12; newYear-- }
        else if (newMonth > 12) { newMonth = 1; newYear++ }
        if (newYear < 2026 || (newYear == 2026 && newMonth < 6)) return
        _state.update { it.copy(currentYear = newYear, currentMonth = newMonth, selectedDate = null) }
    }

    fun navigateWeek(delta: Int) {
        val current = _state.value.currentWeekOffset
        val maxOffset = computeMaxWeekOffset()
        val proposed = (current + delta).coerceIn(0, maxOffset)
        _state.update { it.copy(currentWeekOffset = proposed, selectedDate = null) }
        loadWeekDays()
    }

    private fun loadWeekDays() {
        viewModelScope.launch {
            val offset = _state.value.currentWeekOffset
            val maxOffset = computeMaxWeekOffset()
            var date = LocalDate.now().minusWeeks(offset.toLong()).with(DayOfWeek.MONDAY)
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
            _state.update { it.copy(weekDates = days, maxWeekOffset = maxOffset) }
        }
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

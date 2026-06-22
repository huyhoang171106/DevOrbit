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
import java.time.ZoneId
import javax.inject.Inject

data class TaskManagementUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val filter: TaskFilter = TaskFilter.TODAY,
    val inputTitle: String = "",
    val inputDeadline: Long? = null,
    val showDatePicker: Boolean = false,
    val creatingPlan: Boolean = false,
    val planTitle: String = "",
    val planDeadline: String = "",
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
            combine(academicRepository.allTasks, _state.map { it.filter }.distinctUntilChanged()) { tasks, filter ->
                val filtered = when (filter) {
                    TaskFilter.TODAY -> tasks.filter { isTaskToday(it) }
                    TaskFilter.WEEK -> tasks.filter { isTaskThisWeek(it) }
                    TaskFilter.ALL -> tasks
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

    fun updateTitle(title: String) {
        _state.update { it.copy(inputTitle = title) }
    }

    fun updateDeadline(millis: Long?) {
        _state.update { it.copy(inputDeadline = millis, showDatePicker = false) }
    }

    fun showDatePicker() {
        _state.update { it.copy(showDatePicker = true) }
    }

    fun hideDatePicker() {
        _state.update { it.copy(showDatePicker = false) }
    }

    fun addTask() {
        val title = _state.value.inputTitle.trim()
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = TaskEntity(
                title = title,
                deadline = _state.value.inputDeadline,
                taskType = "general"
            )
            academicRepository.saveTask(task)
            _state.update { it.copy(inputTitle = "", inputDeadline = null) }
        }
    }

    fun toggleTask(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            academicRepository.completeTask(taskId)
        }
    }

    // ── Group Plan ──

    fun showCreatePlanDialog() {
        _state.update { it.copy(showCreatePlanDialog = true, planTitle = "", planDeadline = "", planError = null) }
    }

    fun hideCreatePlanDialog() {
        _state.update { it.copy(showCreatePlanDialog = false, planError = null) }
    }

    fun updatePlanTitle(title: String) {
        _state.update { it.copy(planTitle = title) }
    }

    fun updatePlanDeadline(deadline: String) {
        _state.update { it.copy(planDeadline = deadline) }
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
                        deadline = _state.value.planDeadline.ifBlank { null }
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

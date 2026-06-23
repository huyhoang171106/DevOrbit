package vn.edu.uit.devorbit.mobile.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import vn.edu.uit.devorbit.mobile.data.local.dao.CourseDao
import vn.edu.uit.devorbit.mobile.data.local.dao.DailyActivityDao
import vn.edu.uit.devorbit.mobile.data.local.dao.SemesterCourseDao
import vn.edu.uit.devorbit.mobile.data.local.dao.TechStackDao
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.DailyActivityEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.SemesterCourseEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.TechStackEntity
import vn.edu.uit.devorbit.mobile.data.datastore.SettingsDataStore
import vn.edu.uit.devorbit.mobile.domain.model.TaskItem
import vn.edu.uit.devorbit.mobile.domain.model.toTaskItem
import vn.edu.uit.devorbit.mobile.network.ApiService
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

data class DashboardUiState(
    val studentName: String = "",
    val greeting: String = "",
    val dateText: String = "",
    val studyHoursToday: Int = 0,
    val semesterCourses: List<CourseEntity> = emptyList(),
    val allCourses: List<CourseEntity> = emptyList(),
    val sortedTasks: List<TaskItem> = emptyList(),
    val completedTaskCount: Int = 0,
    val totalTaskCount: Int = 0,
    val streakCount: Int = 0,
    val weekDates: List<WeekDay> = emptyList(),
    val currentWeekOffset: Int = 0,
    val maxWeekOffset: Int = 0,
    val techStacks: List<TechStackEntity> = emptyList(),
    val allTechStacks: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class TaskFilter { TODAY, WEEK, ALL }

data class WeekDay(
    val date: String,
    val label: String,
    val activity: DailyActivityEntity?,
    val isToday: Boolean,
    val qualifiesForStreak: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val courseDao: CourseDao,
    private val semesterCourseDao: SemesterCourseDao,
    private val dailyActivityDao: DailyActivityDao,
    private val techStackDao: TechStackDao,
    private val apiService: ApiService
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    private val _taskFilter = MutableStateFlow(TaskFilter.TODAY)
    val taskFilter: StateFlow<TaskFilter> = _taskFilter.asStateFlow()

    fun setTaskFilter(filter: TaskFilter) { _taskFilter.value = filter }

    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val displayDateFormat = DateTimeFormatter.ofPattern("EEEE, dd/MM", Locale("vi", "VN"))

    private val minDate: LocalDate = LocalDate.now().minusMonths(6).with(DayOfWeek.MONDAY)

    private fun computeMaxWeekOffset(): Int {
        val now = LocalDate.now().with(DayOfWeek.MONDAY)
        val diff = now.toEpochDay() - minDate.toEpochDay()
        if (diff < 0) return 0
        return (diff / 7).toInt()
    }

    private var currentStudentCode: String = ""
    private val dailyUpdateMutex = Mutex()

    init {
        observeProfile()
        observeSemesterCourses()
        observeTasks()
        observeTechStacks()
        loadAllTechStacks()
        startStudyTimer()
    }

    override fun onCleared() {
        super.onCleared()
        stopStudyTimer()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            settingsDataStore.studentName.collect { name ->
                updateGreeting(name.orEmpty())
            }
        }
        viewModelScope.launch {
            settingsDataStore.studentCode.collect { code ->
                val c = code.orEmpty()
                if (c.isNotBlank()) {
                    currentStudentCode = c
                    val streak = settingsDataStore.getStreakCount(c)
                    _state.update { it.copy(streakCount = streak) }
                    checkStreak()
                    loadWeekDays()
                    loadTodayStudyMinutes()
                }
            }
        }
    }

    private suspend fun loadTodayStudyMinutes() {
        val today = LocalDate.now().format(dateFormat)
        val activity = dailyActivityDao.getActivity(currentStudentCode, today)
        val hours = (activity?.studyMinutes ?: 0) / 60
        _state.update { it.copy(studyHoursToday = hours) }
    }

    private fun updateGreeting(name: String) {
        val now = LocalTime.now()
        val today = LocalDate.now()
        val hour = now.hour
        val greeting = when (hour) {
            in 5..11 -> "Chào buổi sáng"
            in 12..17 -> "Chào buổi chiều"
            else -> "Chào buổi tối"
        }
        val displayName = name.ifBlank { "bạn" }
        val dateText = today.format(displayDateFormat)
            .replaceFirstChar { it.uppercase() }
        _state.update { it.copy(
            studentName = name,
            greeting = "$greeting,\n$displayName",
            dateText = dateText
        ) }
    }

    private fun observeSemesterCourses() {
        viewModelScope.launch {
            combine(
                semesterCourseDao.getAllSemesterCourses(),
                courseDao.getAllCourses()
            ) { semesterCourses, allCourses ->
                val courseIds = semesterCourses.map { it.courseId }
                if (courseIds.isEmpty()) {
                    _state.update { it.copy(semesterCourses = emptyList()) }
                } else {
                    val selected = allCourses.filter { it.id in courseIds }
                    _state.update { it.copy(semesterCourses = selected) }
                }
            }.collect()
        }
    }

    private fun observeTasks() {
        viewModelScope.launch {
            _taskFilter.collect { filter ->
                try {
                    loadTasksForFilter(filter)
                } catch (_: Exception) {
                    _state.update { it.copy(error = "Không thể tải nhiệm vụ") }
                 }
            }
        }
    }

    private suspend fun loadTasksForFilter(filter: TaskFilter) {
        val filterParam = when (filter) {
            TaskFilter.TODAY -> "today"
            TaskFilter.WEEK -> "week"
            TaskFilter.ALL -> "all"
        }
        val personalTasks = apiService.getPersonalTasks(filterParam).map { it.toTaskItem() }
        val allGroupTasks = apiService.getAssignedGroupTasks().map { it.toTaskItem() }
        val groupTasks = when (filter) {
            TaskFilter.TODAY -> allGroupTasks.filter { isTaskItemToday(it) }
            TaskFilter.WEEK -> allGroupTasks.filter { isTaskItemInWeek(it) }
            TaskFilter.ALL -> allGroupTasks
        }
        val allTasks = (personalTasks + groupTasks).sortedBy { it.completed }
        _state.update { it.copy(
            sortedTasks = allTasks,
            totalTaskCount = allTasks.size,
            completedTaskCount = allTasks.count { it.completed }
        ) }
        val todayTasks = allTasks.filter { isTaskItemToday(it) }
        recordTaskProgress(todayTasks.count { it.completed }, todayTasks.size)
    }

    private fun isTaskItemToday(task: TaskItem): Boolean {
        if (task.deadline == null) return false
        val date = Instant.ofEpochMilli(task.deadline).atZone(ZoneId.systemDefault()).toLocalDate()
        return date == LocalDate.now()
    }

    private fun isTaskItemInWeek(task: TaskItem): Boolean {
        if (task.deadline == null) return false
        val date = Instant.ofEpochMilli(task.deadline).atZone(ZoneId.systemDefault()).toLocalDate()
        val now = LocalDate.now()
        val monday = now.with(java.time.DayOfWeek.MONDAY)
        val sunday = monday.plusDays(6)
        return date in monday..sunday
    }

    private fun observeTechStacks() {
        viewModelScope.launch {
            techStackDao.getAllTechStacks().collect { stacks ->
                _state.update { it.copy(techStacks = stacks) }
            }
        }
    }

    fun loadAllTechStacks() {
        viewModelScope.launch {
            val stacks = runCatching { apiService.getTechStacks().map { it.name } }
                .getOrDefault(emptyList())
            _state.update { it.copy(allTechStacks = stacks) }
        }
    }

    fun refreshDate() {
        val name = _state.value.studentName
        updateGreeting(name)
        loadWeekDays()
    }

    fun addTechStack(name: String) {
        viewModelScope.launch {
            if (!techStackDao.isTechStackAdded(name)) {
                techStackDao.insertTechStack(TechStackEntity(name = name))
            }
        }
    }

    fun removeTechStack(id: Int) {
        viewModelScope.launch {
            techStackDao.deleteTechStack(id)
        }
    }

    fun loadAllCourses() {
        viewModelScope.launch {
            val courses = courseDao.getAllCourses().first()
            _state.update { it.copy(allCourses = courses) }
        }
    }

    fun addSemesterCourse(courseId: Long) {
        viewModelScope.launch {
            semesterCourseDao.addCourse(SemesterCourseEntity(courseId = courseId))
            loadWeekDays()
        }
    }

    fun removeSemesterCourse(courseId: Long) {
        viewModelScope.launch {
            semesterCourseDao.removeCourse(courseId)
        }
    }

    fun navigateWeek(delta: Int) {
        val current = _state.value.currentWeekOffset
        val maxOffset = computeMaxWeekOffset()
        val proposed = (current + delta).coerceIn(0, maxOffset)
        _state.update { it.copy(currentWeekOffset = proposed) }
        loadWeekDays()
    }

    fun addReposViewed(count: Int = 1) {
        viewModelScope.launch {
            dailyUpdateMutex.withLock {
                if (currentStudentCode.isBlank()) return@withLock
                val today = LocalDate.now().format(dateFormat)
                val existing = dailyActivityDao.getActivity(currentStudentCode, today)
                dailyActivityDao.upsertActivity(DailyActivityEntity(
                    studentCode = currentStudentCode,
                    date = today,
                    reposViewed = (existing?.reposViewed ?: 0) + count,
                    tasksCompleted = existing?.tasksCompleted ?: 0,
                    tasksTotal = existing?.tasksTotal ?: 0
                ))
                loadWeekDays()
            }
            checkStreak()
        }
    }

    fun recordTaskProgress(completedCount: Int, totalCount: Int) {
        viewModelScope.launch {
            dailyUpdateMutex.withLock {
                if (currentStudentCode.isBlank()) return@withLock
                val today = LocalDate.now().format(dateFormat)
                val existing = dailyActivityDao.getActivity(currentStudentCode, today)
                dailyActivityDao.upsertActivity(DailyActivityEntity(
                    studentCode = currentStudentCode,
                    date = today,
                    reposViewed = existing?.reposViewed ?: 0,
                    tasksCompleted = completedCount,
                    tasksTotal = totalCount
                ))
                loadWeekDays()
            }
        }
    }

    private suspend fun checkStreak() {
        if (currentStudentCode.isBlank()) return
        val today = LocalDate.now().format(dateFormat)
        val todayActivity = dailyActivityDao.getActivity(currentStudentCode, today)
        val todayRepos = todayActivity?.reposViewed ?: 0

        val qualifiesForToday = todayRepos >= 3
        if (!qualifiesForToday) return

        val lastStreakDate = settingsDataStore.getLastStreakDate(currentStudentCode)
        if (lastStreakDate == today) return

        val yesterday = LocalDate.now().minusDays(1).format(dateFormat)
        val yesterdayActivity = dailyActivityDao.getActivity(currentStudentCode, yesterday)
        val yesterdayRepos = yesterdayActivity?.reposViewed ?: 0
        val qualifiesForYesterday = yesterdayRepos >= 3
        val currentStreak = settingsDataStore.getStreakCount(currentStudentCode)

        val newStreak = if (qualifiesForYesterday && lastStreakDate == yesterday) {
            currentStreak + 1
        } else {
            1
        }
        settingsDataStore.setStreak(currentStudentCode, newStreak, today)
        _state.update { it.copy(streakCount = newStreak) }
    }

    private fun loadWeekDays() {
        viewModelScope.launch {
            val offset = _state.value.currentWeekOffset
            val maxOffset = computeMaxWeekOffset()
            var date = LocalDate.now().minusWeeks(offset.toLong()).with(DayOfWeek.MONDAY)

            val days = mutableListOf<WeekDay>()
            val todayStr = LocalDate.now().format(dateFormat)

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
                val activity = if (currentStudentCode.isNotBlank()) {
                    dailyActivityDao.getActivity(currentStudentCode, dateStr)
                } else null
                days.add(WeekDay(
                    date = dateStr,
                    label = dayLabel,
                    activity = activity,
                    isToday = dateStr == todayStr,
                    qualifiesForStreak = activity != null && activity.reposViewed >= 3
                ))
                date = date.plusDays(1)
            }
            _state.update { it.copy(weekDates = days, maxWeekOffset = maxOffset) }
        }
    }

    private var studyTimerJob: kotlinx.coroutines.Job? = null

    private fun startStudyTimer() {
        studyTimerJob?.cancel()
        studyTimerJob = viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(60_000)
                dailyUpdateMutex.withLock {
                    if (currentStudentCode.isNotBlank()) {
                        try {
                            val today = LocalDate.now().format(dateFormat)
                            val existing = dailyActivityDao.getActivity(currentStudentCode, today)
                            dailyActivityDao.upsertActivity(DailyActivityEntity(
                                studentCode = currentStudentCode,
                                date = today,
                                reposViewed = existing?.reposViewed ?: 0,
                                tasksCompleted = existing?.tasksCompleted ?: 0,
                                tasksTotal = existing?.tasksTotal ?: 0,
                                studyMinutes = (existing?.studyMinutes ?: 0) + 1
                            ))
                            val hours = ((existing?.studyMinutes ?: 0) + 1) / 60
                            _state.update { it.copy(studyHoursToday = hours) }
                        } catch (e: Exception) {
                            Log.e("DashboardVM", "Study timer DB error", e)
                        }
                    }
                }
            }
        }
    }

    private fun stopStudyTimer() {
        studyTimerJob?.cancel()
        studyTimerJob = null
    }

    fun toggleTask(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            try {
                apiService.togglePersonalTask(taskId, mapOf("completed" to completed))
                refreshTasks()
            } catch (_: Exception) {
                _state.update { it.copy(error = "Không thể cập nhật nhiệm vụ") }
            }
        }
    }

    fun clearError() { _state.update { it.copy(error = null) } }

    private suspend fun refreshTasks() {
        try {
            loadTasksForFilter(_taskFilter.value)
        } catch (_: Exception) { }
    }
}

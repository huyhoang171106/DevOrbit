package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.local.dao.DailyActivityDao
import vn.edu.uit.devorbit.mobile.data.local.dao.SemesterCourseDao
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.DailyActivityEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.SemesterCourseEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.TaskEntity
import vn.edu.uit.devorbit.mobile.data.repository.AcademicRepository
import vn.edu.uit.devorbit.mobile.data.datastore.SettingsDataStore
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class DashboardUiState(
    val studentName: String = "",
    val greeting: String = "",
    val dateText: String = "",
    val studyHoursToday: Int = 0,
    val semesterCourses: List<CourseEntity> = emptyList(),
    val allCourses: List<CourseEntity> = emptyList(),
    val sortedTasks: List<TaskEntity> = emptyList(),
    val completedTaskCount: Int = 0,
    val totalTaskCount: Int = 0,
    val streakCount: Int = 0,
    val weekDates: List<WeekDay> = emptyList(),
    val currentWeekOffset: Int = 0,
    val maxWeekOffset: Int = 0,
    val isLoading: Boolean = false
)

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
    private val semesterCourseDao: SemesterCourseDao,
    private val dailyActivityDao: DailyActivityDao,
    private val academicRepository: AcademicRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("vi", "VN"))
    private val displayDateFormat = SimpleDateFormat("EEEE, dd/MM", Locale("vi", "VN"))

    private val minDate: Calendar = Calendar.getInstance().apply {
        set(2026, Calendar.JUNE, 8, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private fun computeMaxWeekOffset(): Int {
        val now = Calendar.getInstance()
        now.set(Calendar.DAY_OF_WEEK, now.firstDayOfWeek)
        now.set(Calendar.HOUR_OF_DAY, 0)
        now.set(Calendar.MINUTE, 0)
        now.set(Calendar.SECOND, 0)
        now.set(Calendar.MILLISECOND, 0)
        val diff = now.timeInMillis - minDate.timeInMillis
        if (diff < 0) return 0
        return (diff / (7L * 24 * 60 * 60 * 1000)).toInt()
    }

    private var currentStudentCode: String = ""

    init {
        observeProfile()
        observeSemesterCourses()
        observeTasks()
        observeStudyHours()
        loadWeekDays()
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
                    launch {
                        loadWeekDays()
                    }
                }
            }
        }
    }

    private fun updateGreeting(name: String) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 5..11 -> "Chào buổi sáng"
            in 12..17 -> "Chào buổi chiều"
            else -> "Chào buổi tối"
        }
        val displayName = name.ifBlank { "bạn" }
        val dateText = displayDateFormat.format(calendar.time)
            .replaceFirstChar { it.uppercase() }
        _state.update { it.copy(
            studentName = name,
            greeting = "$greeting,\n$displayName",
            dateText = dateText
        ) }
    }

    private fun observeStudyHours() {
        viewModelScope.launch {
            settingsDataStore.studyHoursToday.collect { hours ->
                _state.update { it.copy(studyHoursToday = hours) }
            }
        }
    }

    private fun observeSemesterCourses() {
        viewModelScope.launch {
            semesterCourseDao.getAllSemesterCourses().collect { semesterCourses ->
                val courseIds = semesterCourses.map { it.courseId }
                if (courseIds.isEmpty()) {
                    _state.update { it.copy(semesterCourses = emptyList()) }
                } else {
                    val allCourses = academicRepository.allCourses.first()
                    val selected = allCourses.filter { it.id in courseIds }
                    _state.update { it.copy(semesterCourses = selected) }
                }
            }
        }
    }

    private fun observeTasks() {
        viewModelScope.launch {
            academicRepository.allTasks.collect { tasks ->
                val total = tasks.size
                val completed = tasks.count { it.completed }
                val sorted = tasks.sortedBy { it.completed }
                _state.update { it.copy(
                    sortedTasks = sorted,
                    totalTaskCount = total,
                    completedTaskCount = completed
                ) }
            }
        }
    }

    fun loadAllCourses() {
        viewModelScope.launch {
            val courses = academicRepository.allCourses.first()
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
            if (currentStudentCode.isBlank()) return@launch
            val today = dateFormat.format(Date())
            val existing = dailyActivityDao.getActivity(currentStudentCode, today)
            val current = existing?.reposViewed ?: 0
            dailyActivityDao.upsertActivity(DailyActivityEntity(
                studentCode = currentStudentCode,
                date = today,
                reposViewed = current + count,
                tasksCompleted = existing?.tasksCompleted ?: 0,
                tasksTotal = existing?.tasksTotal ?: 0
            ))
            loadWeekDays()
            checkStreak()
        }
    }

    fun recordTaskProgress(completedCount: Int, totalCount: Int) {
        viewModelScope.launch {
            if (currentStudentCode.isBlank()) return@launch
            val today = dateFormat.format(Date())
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

    private suspend fun checkStreak() {
        if (currentStudentCode.isBlank()) return
        val today = dateFormat.format(Date())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = dateFormat.format(calendar.time)

        val yesterdayActivity = dailyActivityDao.getActivity(currentStudentCode, yesterday)
        val yesterdayRepos = yesterdayActivity?.reposViewed ?: 0

        val lastStreakDate = settingsDataStore.getLastStreakDate(currentStudentCode)
        val currentStreak = settingsDataStore.getStreakCount(currentStudentCode)

        if (yesterdayRepos >= 3) {
            if (lastStreakDate != today) {
                val newStreak = currentStreak + 1
                settingsDataStore.setStreak(currentStudentCode, newStreak, today)
                _state.update { it.copy(streakCount = newStreak) }
            }
        } else if (yesterdayActivity != null) {
            settingsDataStore.setStreak(currentStudentCode, 0, today)
            _state.update { it.copy(streakCount = 0) }
        }
    }

    private fun loadWeekDays() {
        viewModelScope.launch {
            val offset = _state.value.currentWeekOffset
            val maxOffset = computeMaxWeekOffset()
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.WEEK_OF_YEAR, -offset)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            calendar.add(Calendar.DAY_OF_YEAR, -(dayOfWeek - Calendar.MONDAY + 7) % 7)

            val days = mutableListOf<WeekDay>()
            val todayStr = dateFormat.format(Date())

            for (i in 0..6) {
                val dateStr = dateFormat.format(calendar.time)
                val dayLabel = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> "T2"
                    Calendar.TUESDAY -> "T3"
                    Calendar.WEDNESDAY -> "T4"
                    Calendar.THURSDAY -> "T5"
                    Calendar.FRIDAY -> "T6"
                    Calendar.SATURDAY -> "T7"
                    Calendar.SUNDAY -> "CN"
                    else -> ""
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
                calendar.add(Calendar.DAY_OF_YEAR, 1)
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
                settingsDataStore.addAccumulatedSeconds(60)
            }
        }
    }

    private fun stopStudyTimer() {
        studyTimerJob?.cancel()
        studyTimerJob = null
    }

    fun createTask(title: String, courseId: Long?) {
        viewModelScope.launch {
            val task = TaskEntity(
                title = title,
                courseId = courseId,
                taskType = "general"
            )
            academicRepository.saveTask(task)
        }
    }
}

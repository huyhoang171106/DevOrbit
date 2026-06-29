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
import vn.edu.uit.devorbit.mobile.data.repository.StreakTracker
import vn.edu.uit.devorbit.mobile.domain.model.TaskItem
import vn.edu.uit.devorbit.mobile.domain.model.toTaskItem
import vn.edu.uit.devorbit.mobile.data.remote.dto.CreatePersonalTaskRequest
import vn.edu.uit.devorbit.mobile.network.ApiService
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
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
    val selectedDate: String? = null,
    val currentYear: Int = 2026,
    val currentMonth: Int = 6,
    val isLoading: Boolean = false,
    val isSavingOnboarding: Boolean = false,
    val error: String? = null,
    val planActiveMajor: String = "",
    val planTotalCourses: Int = 0,
    val planTotalCredits: Int = 0,
    val planSemesterCount: Int = 0
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
    private val apiService: ApiService,
    private val streakTracker: StreakTracker
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()



    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val displayDateFormat = DateTimeFormatter.ofPattern("EEEE, dd/MM", Locale("vi", "VN"))

    private val minDate: LocalDate = LocalDate.now().minusMonths(6).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    private fun computeMaxWeekOffset(): Int {
        val now = LocalDate.now()
        val monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val diff = monday.toEpochDay() - minDate.toEpochDay()
        return if (diff < 0) 0 else (diff / 7).toInt()
    }

    private val MAJOR_OPTIONS = listOf(
        "IT" to "Công nghệ Thông tin",
        "IS" to "Hệ thống Thông tin",
        "CS" to "Khoa học Máy tính",
        "SE" to "Kỹ thuật Phần mềm",
        "AI" to "Trí tuệ Nhân tạo",
        "CE" to "Kỹ thuật Máy tính",
        "IC" to "Thiết kế Vi mạch",
        "MM" to "Truyền thông Đa phương tiện",
        "NT" to "Mạng máy tính",
        "ATTT" to "An toàn Thông tin",
        "EC" to "Thương mại Điện tử",
        "DS" to "Khoa học Dữ liệu"
    )

    private var currentStudentCode: String = ""
    private val dailyUpdateMutex = Mutex()

    init {
        observeProfile()
        observeSemesterCourses()
        observeTechStacks()
        loadAllTechStacks()
        startStudyTimer()
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            streakTracker.streakUpdated.collect { newStreak ->
                _state.update { it.copy(streakCount = newStreak) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopStudyTimer()
    }

    private fun observeProfile() {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            settingsDataStore.studentName.collect { name ->
                updateGreeting(name.orEmpty())
            }
        }
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            settingsDataStore.studentCode.collect { code ->
                val c = code.orEmpty()
                if (c.isNotBlank()) {
                    currentStudentCode = c
                    val streak = settingsDataStore.getStreakCount(c)
                    _state.update { it.copy(streakCount = streak) }
                    checkStreak()
                    loadWeekDays()
                    loadTodayStudyMinutes()
                    syncStudentTechStacks()
                    loadTodayTasks()
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
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            settingsDataStore.planActiveMajor.first().let { major ->
                val majorLabel = MAJOR_OPTIONS.firstOrNull { it.first == major }?.second ?: major
                combine(
                    semesterCourseDao.getSemesterCoursesByMajor(major),
                    courseDao.getAllCourses()
                ) { semesterCourses, allCourses ->
                    val courseIds = semesterCourses.map { it.courseId }
                    if (courseIds.isEmpty()) {
                        _state.update { it.copy(
                            semesterCourses = emptyList(),
                            planActiveMajor = majorLabel,
                            planTotalCourses = 0,
                            planTotalCredits = 0,
                            planSemesterCount = 0
                        ) }
                    } else {
                        val selected = allCourses.filter { it.id in courseIds }
                        val bySemester = semesterCourses.groupBy { it.semester }
                        val semCount = bySemester.size
                        val totalCredits = selected.sumOf { it.credits }
                        _state.update { it.copy(
                            semesterCourses = selected,
                            planActiveMajor = majorLabel,
                            planTotalCourses = selected.size,
                            planTotalCredits = totalCredits,
                            planSemesterCount = semCount
                        ) }
                    }
                }.collect()
            }
        }
    }

    private fun syncSemesterCourses() {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                val major = settingsDataStore.currentMajor.first()
                val remote = apiService.getSemesterCourses()
                val localIds = semesterCourseDao.getSemesterCourseIds(major).toSet()
                val remoteIds = remote.map { it.courseId }.toSet()
                for (r in remote) {
                    if (r.courseId !in localIds) {
                        semesterCourseDao.addCourse(SemesterCourseEntity(courseId = r.courseId, majorCode = major))
                    }
                }
                for (lid in localIds) {
                    if (lid !in remoteIds) {
                        semesterCourseDao.removeCourse(lid, major)
                    }
                }
            } catch (_: Exception) {
                // offline - use local data
            }
        }
    }

    private fun syncStudentTechStacks() {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                val remote = apiService.getStudentTechStacks()
                val localNames = techStackDao.getTechStackNames().toSet()
                val remoteNames = remote.map { it.techStackName }.toSet()
                for (r in remote) {
                    if (r.techStackName !in localNames) {
                        techStackDao.insertTechStack(TechStackEntity(name = r.techStackName))
                    }
                }
                for (ln in localNames) {
                    if (ln !in remoteNames) {
                        val local = techStackDao.getAllTechStacks().first().find { it.name == ln }
                        if (local != null) techStackDao.deleteTechStack(local.id)
                    }
                }
            } catch (_: Exception) {
                // offline - use local data
            }
        }
    }


    private suspend fun loadTodayTasks() {
        val personalTasks = apiService.getPersonalTasks("today").map { it.toTaskItem() }
        val allGroupTasks = apiService.getAssignedGroupTasks().map { it.toTaskItem() }
        val groupTasks = allGroupTasks.filter { isTaskItemToday(it) }
        val now = System.currentTimeMillis()
        val allTasks = (personalTasks + groupTasks).sortedWith(compareBy<TaskItem> {
            when {
                it.completed -> 2
                it.deadline != null && it.deadline < now -> 1
                else -> 0
            }
        }.thenBy {
            it.deadline ?: Long.MAX_VALUE
        })
        _state.update { it.copy(
            sortedTasks = allTasks,
            totalTaskCount = allTasks.size,
            completedTaskCount = allTasks.count { it.completed }
        ) }
        recordTaskProgress(allTasks.count { it.completed }, allTasks.size)
    }

    private fun isTaskItemToday(task: TaskItem): Boolean {
        if (task.deadline == null) return false
        val date = Instant.ofEpochMilli(task.deadline).atZone(ZoneId.systemDefault()).toLocalDate()
        return date == LocalDate.now()
    }


    private fun observeTechStacks() {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            techStackDao.getAllTechStacks().collect { stacks ->
                _state.update { it.copy(techStacks = stacks) }
            }
        }
    }

    fun loadAllTechStacks() {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
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
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                apiService.addStudentTechStack(mapOf("name" to name))
                if (!techStackDao.isTechStackAdded(name)) {
                    techStackDao.insertTechStack(TechStackEntity(name = name))
                }
            } catch (_: Exception) {
                _state.update { it.copy(error = "Không thể đồng bộ tech stack lên server") }
            }
        }
    }

    fun removeTechStack(id: Int) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            val entity = techStackDao.getAllTechStacks().first().find { it.id == id } ?: return@launch
            try {
                apiService.removeStudentTechStackByName(entity.name)
                techStackDao.deleteTechStack(id)
            } catch (_: Exception) {
                _state.update { it.copy(error = "Không thể đồng bộ tech stack lên server") }
            }
        }
    }

    fun loadAllCourses() {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            val courses = courseDao.getAllCourses().first()
            _state.update { it.copy(allCourses = courses) }
        }
    }

    fun addSemesterCourse(courseId: Long) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                val major = settingsDataStore.currentMajor.first()
                apiService.addSemesterCourse(mapOf("courseId" to courseId))
                semesterCourseDao.addCourse(SemesterCourseEntity(courseId = courseId, majorCode = major))
                loadWeekDays()
            } catch (_: Exception) {
                _state.update { it.copy(error = "Không thể đồng bộ môn học lên server") }
            }
        }
    }

    fun removeSemesterCourse(courseId: Long) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                val major = settingsDataStore.currentMajor.first()
                apiService.removeSemesterCourse(courseId)
                semesterCourseDao.removeCourse(courseId, major)
            } catch (_: Exception) {
                _state.update { it.copy(error = "Không thể đồng bộ môn học lên server") }
            }
        }
    }

    fun saveOnboardingPreferences(
        courseIds: Set<Long>,
        techStackNames: Set<String>,
        onSuccess: () -> Unit
    ) {
        if (courseIds.isEmpty() || techStackNames.isEmpty()) {
            _state.update { it.copy(error = "Hãy chọn ít nhất một môn học và một tech stack") }
            return
        }

        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            _state.update { it.copy(isSavingOnboarding = true, error = null) }
            try {
                val major = settingsDataStore.currentMajor.first()
                val existingCourseIds = _state.value.semesterCourses.mapTo(mutableSetOf()) { it.id }
                for (courseId in courseIds - existingCourseIds) {
                    apiService.addSemesterCourse(mapOf("courseId" to courseId))
                    semesterCourseDao.addCourse(SemesterCourseEntity(courseId = courseId, majorCode = major))
                }

                val existingStacks = _state.value.techStacks.mapTo(mutableSetOf()) { it.name }
                for (name in techStackNames - existingStacks) {
                    apiService.addStudentTechStack(mapOf("name" to name))
                    if (!techStackDao.isTechStackAdded(name)) {
                        techStackDao.insertTechStack(TechStackEntity(name = name))
                    }
                }

                loadWeekDays()
                onSuccess()
            } catch (_: Exception) {
                _state.update {
                    it.copy(error = "Không thể lưu lựa chọn. Vui lòng kiểm tra kết nối và thử lại")
                }
            } finally {
                _state.update { it.copy(isSavingOnboarding = false) }
            }
        }
    }

    fun navigateWeek(delta: Int) {
        val current = _state.value.currentWeekOffset
        val maxOffset = computeMaxWeekOffset()
        val proposed = (current + delta).coerceIn(0, maxOffset)
        _state.update { it.copy(currentWeekOffset = proposed, selectedDate = null) }
        loadWeekDays()
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

    fun addReposViewed(count: Int = 1) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
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
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
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
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            val offset = _state.value.currentWeekOffset
            val maxOffset = computeMaxWeekOffset()
            var date = LocalDate.now().minusWeeks(offset.toLong()).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

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
        studyTimerJob = viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
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
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                apiService.togglePersonalTask(taskId, mapOf("completed" to completed))
                refreshTasks()
            } catch (_: Exception) {
                _state.update { it.copy(error = "Không thể cập nhật nhiệm vụ") }
            }
        }
    }

    fun createQuickTask(title: String, deadline: Long) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                apiService.createPersonalTask(CreatePersonalTaskRequest(
                    title = title,
                    deadline = Instant.ofEpochMilli(deadline).toString()
                ))
                refreshTasks()
            } catch (_: Exception) {
                _state.update { it.copy(error = "Không thể tạo nhiệm vụ") }
            }
        }
    }

    fun clearError() { _state.update { it.copy(error = null) } }

    private suspend fun refreshTasks() {
        try {
            loadTodayTasks()
        } catch (_: Exception) { }
    }
}


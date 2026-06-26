package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.local.dao.SemesterCourseDao
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.SemesterCourseEntity
import vn.edu.uit.devorbit.mobile.data.repository.AcademicRepository
import javax.inject.Inject

data class SemesterUiState(
    val courses: List<CourseEntity> = emptyList(),
    val semesterCourses: Map<Int, List<SemesterCourseEntity>> = emptyMap(),
    val loading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SemesterPlannerViewModel @Inject constructor(
    private val repository: AcademicRepository,
    private val semesterCourseDao: SemesterCourseDao
) : ViewModel() {

    private val _state = MutableStateFlow(SemesterUiState())
    val state: StateFlow<SemesterUiState> = _state.asStateFlow()

    init {
        loadCourses()
        observePlannedCourses()
    }

    private fun loadCourses() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            repository.allCourses.collect { courses ->
                _state.update { it.copy(courses = courses, loading = false) }
            }
        }
    }

    private fun observePlannedCourses() {
        viewModelScope.launch {
            semesterCourseDao.getAllSemesterCourses().collect { planned ->
                val map = planned.groupBy { it.semester }
                _state.update { it.copy(semesterCourses = map) }
            }
        }
    }

    fun addCourseToSemester(courseId: Long, semester: Int) {
        viewModelScope.launch {
            val exists = semesterCourseDao.isCourseAdded(courseId)
            if (!exists) {
                semesterCourseDao.addCourse(SemesterCourseEntity(courseId = courseId, semester = semester))
            }
        }
    }

    fun moveCourse(courseId: Long, newSemester: Int) {
        viewModelScope.launch {
            semesterCourseDao.moveCourse(courseId, newSemester)
        }
    }

    fun removeCourse(courseId: Long) {
        viewModelScope.launch {
            semesterCourseDao.removeCourse(courseId)
        }
    }

    fun getCoursesForSemester(semester: Int): List<CourseEntity> {
        val courseIds = _state.value.semesterCourses[semester]?.map { it.courseId } ?: emptyList()
        return _state.value.courses.filter { it.id in courseIds }
    }

    fun getTotalCredits(semester: Int): Int {
        return getCoursesForSemester(semester).sumOf { it.credits }
    }

    fun getCourseCount(semester: Int): Int {
        return getCoursesForSemester(semester).size
    }

    fun getAvailableCourses(): List<CourseEntity> {
        val plannedIds = _state.value.semesterCourses.values.flatten().map { it.courseId }.toSet()
        return _state.value.courses.filter { it.id !in plannedIds }
    }
}

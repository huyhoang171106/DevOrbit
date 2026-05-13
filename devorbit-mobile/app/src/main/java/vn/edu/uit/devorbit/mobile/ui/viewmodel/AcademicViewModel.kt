package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.repository.AcademicRepository
import vn.edu.uit.devorbit.mobile.data.local.entity.*
import vn.edu.uit.devorbit.mobile.model.domain.*
import vn.edu.uit.devorbit.mobile.engine.*
import javax.inject.Inject

@HiltViewModel
class AcademicViewModel @Inject constructor(
    private val repository: AcademicRepository
) : ViewModel() {

    // ─── Data Flows ───
    val courses: StateFlow<List<CourseEntity>> = repository.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTasks: StateFlow<List<TaskEntity>> = repository.incompleteTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ─── Derived UI State (Reactive) ───
    val academicHealth = combine(courses, tasks) { _, _ ->
        // Simplified for now, will implement full engine logic later
        AcademicHealth(0.85, emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AcademicHealth(1.0, emptyList()))

    val recommendations = MutableStateFlow<List<StudyRecommendation>>(emptyList())
    
    val nextAction = MutableStateFlow<BreakdownStep?>(null)
    
    private val _burnout = mutableStateOf(BurnoutStatus(BurnoutRisk.NONE, emptyList()))
    val burnout: State<BurnoutStatus> = _burnout

    private val _focusTask = mutableStateOf<TaskEntity?>(null)
    val focusTask: State<TaskEntity?> = _focusTask

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            repository.refreshCourses()
            repository.refreshRelationships()
        }
    }

    fun toggleTaskComplete(taskId: Long) {
        viewModelScope.launch {
            repository.completeTask(taskId)
        }
    }

    fun setFocusTask(task: TaskEntity) {
        _focusTask.value = task
    }
}

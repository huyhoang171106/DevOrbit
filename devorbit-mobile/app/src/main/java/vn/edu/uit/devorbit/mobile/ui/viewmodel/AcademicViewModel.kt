package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.repository.AcademicRepository
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.domain.model.BreakdownStep
// Engines — reserved for future integration
import javax.inject.Inject

@HiltViewModel
class AcademicViewModel @Inject constructor(
    private val repository: AcademicRepository
) : ViewModel() {

    // ─── Data Flows ───
    val courses: StateFlow<List<CourseEntity>> = repository.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ─── Derived UI State (Reactive) ───
    val nextAction = MutableStateFlow<BreakdownStep?>(null)

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            repository.refreshCourses()
            repository.refreshRelationships()
        }
    }
}

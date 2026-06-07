package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.domain.model.StudyItem
import vn.edu.uit.devorbit.mobile.domain.model.StudyPhase
import vn.edu.uit.devorbit.mobile.domain.model.StudyPlan
import vn.edu.uit.devorbit.mobile.domain.repository.StudyPlanRepository
import javax.inject.Inject

data class StudyPlanUiState(
    val plan: StudyPlan? = null,
    val loading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class StudyPlanViewModel @Inject constructor(
    private val studyPlanRepository: StudyPlanRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StudyPlanUiState())
    val state: StateFlow<StudyPlanUiState> = _state.asStateFlow()

    fun generateRoadmap(learningGoals: String, careerPath: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val plan = studyPlanRepository.generateRoadmap(learningGoals, careerPath)
                _state.value = StudyPlanUiState(plan = plan)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Khong tao duoc lo trinh"
                )
            }
        }
    }

    fun toggleItem(itemId: String) {
        val plan = _state.value.plan ?: return
        _state.value = _state.value.copy(plan = plan.copy(phases = plan.phases.map { phase ->
            phase.copy(items = phase.items.map { item ->
                if (item.id == itemId) item.copy(completed = !item.completed) else item
            })
        }))
    }
}

package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.domain.repository.*
import javax.inject.Inject

data class ExploreUiState(
    val recentRepos: List<RecentRepo> = emptyList(),
    val topStacks: List<TopStack> = emptyList(),
    val techStacks: List<TechStackInfo> = emptyList(),
    val loading: Boolean = false
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val discoveryRepository: DiscoveryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ExploreUiState())
    val state: StateFlow<ExploreUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            val recentRepos = discoveryRepository.getRecentRepos()
            val topStacks = discoveryRepository.getTopStacks()
            val techStacks = discoveryRepository.getTechStacks()
            _state.value = ExploreUiState(
                recentRepos = recentRepos,
                topStacks = topStacks,
                techStacks = techStacks,
                loading = false
            )
        }
    }
}

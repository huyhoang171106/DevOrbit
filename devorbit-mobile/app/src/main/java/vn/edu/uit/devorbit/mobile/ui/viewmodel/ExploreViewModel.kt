package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.data.datastore.SettingsDataStore
import vn.edu.uit.devorbit.mobile.data.repository.StreakTracker
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.data.remote.dto.TechStack
import vn.edu.uit.devorbit.mobile.domain.repository.*
import vn.edu.uit.devorbit.mobile.ui.screen.explore.ExploreFilterState
import javax.inject.Inject

data class ExploreUiState(
    val recentRepos: List<RecentRepo> = emptyList(),
    val topStacks: List<TopStack> = emptyList(),
    val techStacks: List<TechStackInfo> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val filterState: ExploreFilterState = ExploreFilterState(),
    val selectedRepo: RepoSummary? = null
) {
    val visibleRepos: List<RecentRepo> = filterState.filterRepos(recentRepos)
}

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val discoveryRepository: DiscoveryRepository,
    private val streakTracker: StreakTracker,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(ExploreUiState())
    val state: StateFlow<ExploreUiState> = _state.asStateFlow()

    private var currentStudentCode: String = ""

    init {
        load()
        viewModelScope.launch {
            settingsDataStore.studentCode.collect { code ->
                currentStudentCode = code.orEmpty()
            }
        }
    }


    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val recentRepos = discoveryRepository.getRecentRepos()
                val topStacks = discoveryRepository.getTopStacks()
                val techStacks = discoveryRepository.getTechStacks()
                _state.value = _state.value.copy(
                    recentRepos = recentRepos,
                    topStacks = topStacks,
                    techStacks = techStacks,
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Khong tai duoc du lieu kham pha"
                )
            }
        }
    }

    fun updateSearch(query: String) {
        val nextFilter = _state.value.filterState.updateQuery(query)
        _state.value = _state.value.copy(filterState = nextFilter)
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val repos = if (nextFilter.normalizedQuery == null) {
                    discoveryRepository.getRecentRepos()
                } else {
                    discoveryRepository.searchRepos(nextFilter.normalizedQuery!!)
                }
                _state.value = _state.value.copy(recentRepos = repos, loading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Khong tim duoc repo"
                )
            }
        }
    }

    fun selectTechStack(stack: String?) {
        _state.value = _state.value.copy(
            filterState = _state.value.filterState.selectTechStack(stack)
        )
    }

    fun openRepo(repo: RecentRepo) {
        _state.value = _state.value.copy(selectedRepo = repo.toRepoSummary())
        if (currentStudentCode.isNotBlank()) {
            streakTracker.incrementReposViewed(currentStudentCode)
        }
    }

    fun closeRepo() {
        _state.value = _state.value.copy(selectedRepo = null)
    }

    private fun RecentRepo.toRepoSummary(): RepoSummary {
        return RepoSummary(
            id = id,
            displayName = name,
            description = description,
            githubUrl = githubUrl,
            primaryLanguage = language,
            stars = stars,
            techStacks = techStacks.map { TechStack(name = it) },
            courseName = courseName
        )
    }
}

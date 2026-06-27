package vn.edu.uit.devorbit.admin.ui.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.CourseReviewAdminResponse
import vn.edu.uit.devorbit.admin.data.remote.dto.RepoReviewAdminResponse
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

data class ReviewsUiState(
    val courseReviews: List<CourseReviewAdminResponse> = emptyList(),
    val repoReviews: List<RepoReviewAdminResponse> = emptyList(),
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReviewsUiState())
    val state: StateFlow<ReviewsUiState> = _state.asStateFlow()

    init { loadReviews() }

    private fun loadReviews() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val (courseR, repoR) = coroutineScope {
                    val courseDef = async { adminRepository.getCourseReviews() }
                    val repoDef = async { adminRepository.getRepoReviews() }
                    Pair(courseDef.await(), repoDef.await())
                }
                _state.value = _state.value.copy(
                    courseReviews = courseR.getOrDefault(emptyList()),
                    repoReviews = repoR.getOrDefault(emptyList()),
                    error = listOfNotNull(courseR.exceptionOrNull(), repoR.exceptionOrNull())
                        .firstOrNull()?.message
                )
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun selectTab(index: Int) { _state.value = _state.value.copy(selectedTab = index) }

    fun deleteCourseReview(id: Long) {
        viewModelScope.launch {
            adminRepository.deleteCourseReview(id).fold(
                onSuccess = { _state.value = _state.value.copy(courseReviews = _state.value.courseReviews.filter { it.id != id }) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun deleteRepoReview(id: Long) {
        viewModelScope.launch {
            adminRepository.deleteRepoReview(id).fold(
                onSuccess = { _state.value = _state.value.copy(repoReviews = _state.value.repoReviews.filter { it.id != id }) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }
}

package vn.edu.uit.devorbit.admin.ui.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import javax.inject.Inject

data class CourseDetailUiState(
    val courseId: Long = 0,
    val course: CourseDetailResponse? = null,
    val tutorials: List<TutorialItem> = emptyList(),
    val playlists: List<YoutubePlaylistResponse> = emptyList(),
    val articles: List<ArticleItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTab: Int = 0
)

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CourseDetailUiState())
    val state: StateFlow<CourseDetailUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    fun loadCourse(courseId: Long) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val activeId = courseId
            _state.value = _state.value.copy(courseId = activeId, isLoading = true, error = null)
            try {
                // Run all 4 requests in parallel
                val courseDef = async { adminRepository.getCourseDetail(activeId) }
                val tuteDef = async { adminRepository.getTutorials(activeId) }
                val plDef = async { adminRepository.getYoutubePlaylists(activeId) }
                val artDef = async { adminRepository.getArticles(activeId) }

                // Await all within coroutineScope so failure cancels siblings
                val courseResult = courseDef.await()
                val tuteResult = tuteDef.await()
                val plResult = plDef.await()
                val artResult = artDef.await()

                if (activeId != _state.value.courseId) return@launch

                // Only course failure is blocking; resource failures → empty lists
                val course = courseResult.getOrNull()
                if (course == null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = courseResult.exceptionOrNull()?.message
                            ?: "Không thể tải thông tin môn học"
                    )
                } else {
                    _state.value = _state.value.copy(
                        course = course,
                        tutorials = tuteResult.getOrDefault(emptyList()),
                        playlists = plResult.getOrDefault(emptyList()),
                        articles = artResult.getOrDefault(emptyList()),
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                if (activeId == _state.value.courseId) {
                    _state.value = _state.value.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    fun selectTab(index: Int) {
        _state.value = _state.value.copy(selectedTab = index)
    }

    fun addTutorial(title: String, url: String) {
        viewModelScope.launch {
            val courseId = _state.value.courseId
            if (courseId == 0L) return@launch
            adminRepository.createTutorial(courseId, TutorialRequest(title = title, url = url)).fold(
                onSuccess = { _state.value = _state.value.copy(tutorials = _state.value.tutorials + it) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun deleteTutorial(id: Long) {
        viewModelScope.launch {
            val courseId = _state.value.courseId
            if (courseId == 0L) return@launch
            adminRepository.deleteTutorial(courseId, id).fold(
                onSuccess = { _state.value = _state.value.copy(tutorials = _state.value.tutorials.filter { it.id != id }) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun addPlaylist(title: String, url: String) {
        viewModelScope.launch {
            val courseId = _state.value.courseId
            if (courseId == 0L) return@launch
            adminRepository.createYoutubePlaylist(courseId, YoutubePlaylistRequest(title = title, url = url)).fold(
                onSuccess = { _state.value = _state.value.copy(playlists = _state.value.playlists + it) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun deletePlaylist(id: Long) {
        viewModelScope.launch {
            val courseId = _state.value.courseId
            if (courseId == 0L) return@launch
            adminRepository.deleteYoutubePlaylist(courseId, id).fold(
                onSuccess = { _state.value = _state.value.copy(playlists = _state.value.playlists.filter { it.id != id }) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun addArticle(title: String, url: String) {
        viewModelScope.launch {
            val courseId = _state.value.courseId
            if (courseId == 0L) return@launch
            adminRepository.createArticle(courseId, ArticleRequest(title = title, url = url)).fold(
                onSuccess = { _state.value = _state.value.copy(articles = _state.value.articles + it) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun deleteArticle(id: Long) {
        viewModelScope.launch {
            val courseId = _state.value.courseId
            if (courseId == 0L) return@launch
            adminRepository.deleteArticle(courseId, id).fold(
                onSuccess = { _state.value = _state.value.copy(articles = _state.value.articles.filter { it.id != id }) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun updateTutorial(item: TutorialItem, newTitle: String, newUrl: String) {
        viewModelScope.launch {
            val courseId = _state.value.courseId
            if (courseId == 0L) return@launch
            adminRepository.updateTutorial(courseId, item.id, TutorialRequest(
                title = newTitle,
                url = newUrl,
                type = item.type,
                description = item.description
            )).fold(
                onSuccess = { updated ->
                    _state.value = _state.value.copy(tutorials = _state.value.tutorials.map { if (it.id == item.id) updated else it })
                },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun updatePlaylist(item: YoutubePlaylistResponse, newTitle: String, newUrl: String) {
        viewModelScope.launch {
            val courseId = _state.value.courseId
            if (courseId == 0L) return@launch
            adminRepository.updateYoutubePlaylist(courseId, item.id, YoutubePlaylistRequest(
                title = newTitle,
                url = newUrl,
                channelName = item.channelName,
                description = item.description
            )).fold(
                onSuccess = { updated ->
                    _state.value = _state.value.copy(playlists = _state.value.playlists.map { if (it.id == item.id) updated else it })
                },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun updateArticle(item: ArticleItem, newTitle: String, newUrl: String) {
        viewModelScope.launch {
            val courseId = _state.value.courseId
            if (courseId == 0L) return@launch
            adminRepository.updateArticle(courseId, item.id, ArticleRequest(
                title = newTitle,
                url = newUrl,
                author = item.author,
                description = item.description
            )).fold(
                onSuccess = { updated ->
                    _state.value = _state.value.copy(articles = _state.value.articles.map { if (it.id == item.id) updated else it })
                },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun updateCourse(request: AdminCourseUpsertRequest) {
        viewModelScope.launch {
            val courseId = _state.value.courseId
            if (courseId == 0L) return@launch
            adminRepository.updateCourse(courseId, request).fold(
                onSuccess = { _state.value = _state.value.copy(course = it) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun deleteCourse(courseId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            adminRepository.deleteCourse(courseId).fold(
                onSuccess = { onDone() },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun deleteRepo(repoId: Long) {
        viewModelScope.launch {
            adminRepository.deleteRepo(repoId).fold(
                onSuccess = { loadCourse(_state.value.courseId) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }

    fun syncRepos(courseId: Long) {
        viewModelScope.launch {
            adminRepository.syncCourseRepos(courseId).fold(
                onSuccess = { loadCourse(courseId) },
                onFailure = { _state.value = _state.value.copy(error = it.message) }
            )
        }
    }
}

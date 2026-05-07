package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import vn.edu.uit.devorbit.mobile.model.CourseSummary
import vn.edu.uit.devorbit.mobile.model.RepoSummary
import vn.edu.uit.devorbit.mobile.repository.DevOrbitRepository
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val repository: DevOrbitRepository
) : ViewModel() {

    private val _courses = mutableStateOf<List<CourseSummary>>(emptyList())
    val courses: State<List<CourseSummary>> = _courses

    private val _repos = mutableStateOf<List<RepoSummary>>(emptyList())
    val repos: State<List<RepoSummary>> = _repos

    private val _tutorials = mutableStateOf<List<CourseTutorial>>(emptyList())
    val tutorials: State<List<CourseTutorial>> = _tutorials

    private val _videos = mutableStateOf<List<CourseYoutubePlaylist>>(emptyList())
    val videos: State<List<CourseYoutubePlaylist>> = _videos

    private val _articles = mutableStateOf<List<CourseArticle>>(emptyList())
    val articles: State<List<CourseArticle>> = _articles

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun loadCourses() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getCourses()
                .onSuccess { _courses.value = it }
                .onFailure { _error.value = it.message ?: "Failed to load courses" }
            _isLoading.value = false
        }
    }

    fun loadRepos(courseId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _repos.value = emptyList()
            _tutorials.value = emptyList()
            _videos.value = emptyList()
            _articles.value = emptyList()

            repository.getRepos(courseId).onSuccess { _repos.value = it }
            repository.getTutorials(courseId).onSuccess { _tutorials.value = it }
            repository.getVideos(courseId).onSuccess { _videos.value = it }
            repository.getArticles(courseId).onSuccess { _articles.value = it }
            
            _isLoading.value = false
        }
    }
}

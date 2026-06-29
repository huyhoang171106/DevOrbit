package vn.edu.uit.devorbit.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import vn.edu.uit.devorbit.mobile.data.datastore.SettingsDataStore
import vn.edu.uit.devorbit.mobile.data.repository.StreakTracker
import vn.edu.uit.devorbit.mobile.data.local.CurriculumLoader
import vn.edu.uit.devorbit.mobile.data.local.dao.SemesterCourseDao
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.SemesterCourseEntity
import vn.edu.uit.devorbit.mobile.data.repository.AcademicRepository
import vn.edu.uit.devorbit.mobile.data.remote.dto.AiResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSocialInfoResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.data.remote.dto.ReviewResponse
import vn.edu.uit.devorbit.mobile.domain.engine.SemesterValidationService
import vn.edu.uit.devorbit.mobile.domain.model.GraphNode
import vn.edu.uit.devorbit.mobile.domain.model.GraphLink
import vn.edu.uit.devorbit.mobile.domain.repository.Bookmark
import vn.edu.uit.devorbit.mobile.domain.repository.BookmarkRepository
import vn.edu.uit.devorbit.mobile.ui.screen.courses.CourseHubNavigationState
import vn.edu.uit.devorbit.mobile.ui.screen.courses.CourseSearchFilterState
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val repository: AcademicRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val streakTracker: StreakTracker,
    private val settingsDataStore: SettingsDataStore,
    private val semesterCourseDao: SemesterCourseDao,
    private val curriculumLoader: CurriculumLoader
) : ViewModel() {

    val courses: StateFlow<List<CourseEntity>> = repository.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val validationService = SemesterValidationService()

    private val _currentMajor = MutableStateFlow("SE")
    val currentMajor: StateFlow<String> = _currentMajor.asStateFlow()

    private val _prerequisiteMap = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val prerequisiteMap: StateFlow<Map<String, List<String>>> = _prerequisiteMap.asStateFlow()

    private fun buildPrerequisiteMap(nodes: List<GraphNode>, links: List<GraphLink>): Map<String, List<String>> {
        val idToCode = nodes.associate { it.id to it.code }
        val prereqMap = mutableMapOf<String, MutableList<String>>()
        for (link in links) {
            if (link.type != "PREREQUISITE") continue
            val targetCode = idToCode[link.targetId] ?: continue
            val sourceCode = idToCode[link.sourceId] ?: continue
            prereqMap.getOrPut(targetCode) { mutableListOf() }.add(sourceCode)
        }
        return prereqMap
    }

    private val _graphNodes = MutableStateFlow<List<GraphNode>>(emptyList())
    val graphNodes: StateFlow<List<GraphNode>> = _graphNodes.asStateFlow()

    private val _graphLinks = MutableStateFlow<List<GraphLink>>(emptyList())
    val graphLinks: StateFlow<List<GraphLink>> = _graphLinks.asStateFlow()

    private val _semesterPlan = MutableStateFlow<Map<Int, List<GraphNode>>>(emptyMap())
    val semesterPlan: StateFlow<Map<Int, List<GraphNode>>> = _semesterPlan.asStateFlow()

    private val _totalProgramCredits = MutableStateFlow(0)
    val totalProgramCredits: StateFlow<Int> = _totalProgramCredits.asStateFlow()

    private val _courseCatalogCredits = MutableStateFlow<Map<String, Int>>(emptyMap())
    val courseCatalogCredits: StateFlow<Map<String, Int>> = _courseCatalogCredits.asStateFlow()

    private fun CourseEntity.toGraphNode(semester: Int) = GraphNode(
        id = this.id,
        name = this.tenMH,
        code = this.maMH,
        description = this.description,
        level = 0,
        impactScore = 0.0,
        semester = semester
    )

    private suspend fun loadFromRoom() {
        val majorCode = _currentMajor.value
        val semesterCourses = semesterCourseDao.getSemesterCoursesByMajor(majorCode).first()
        if (semesterCourses.isEmpty()) return
        val courseMap = courses.value.associateBy { it.id }
        val plan = mutableMapOf<Int, MutableList<GraphNode>>()
        for (sc in semesterCourses) {
            val course = courseMap[sc.courseId] ?: continue
            plan.getOrPut(sc.semester) { mutableListOf() }.add(course.toGraphNode(sc.semester))
        }
        if (plan.isNotEmpty()) {
            _semesterPlan.value = plan.toSortedMap()
        }
    }

    private val _graphLoading = MutableStateFlow(false)
    val graphLoading: StateFlow<Boolean> = _graphLoading.asStateFlow()

    private val _graphError = MutableStateFlow<String?>(null)
    val graphError: StateFlow<String?> = _graphError.asStateFlow()

    private val _courseHubNavigationState = MutableStateFlow(CourseHubNavigationState())
    val courseHubNavigationState: StateFlow<CourseHubNavigationState> = _courseHubNavigationState.asStateFlow()

    private val _courseSearchFilterState = MutableStateFlow(CourseSearchFilterState())
    val courseSearchFilterState: StateFlow<CourseSearchFilterState> = _courseSearchFilterState.asStateFlow()

    private val _selectedCourse = MutableStateFlow<CourseEntity?>(null)
    val selectedCourse: StateFlow<CourseEntity?> = _selectedCourse.asStateFlow()

    private val _selectedRepo = MutableStateFlow<RepoSummary?>(null)
    val selectedRepo: StateFlow<RepoSummary?> = _selectedRepo.asStateFlow()

    private val _detailRepos = MutableStateFlow<List<RepoSummary>>(emptyList())
    val detailRepos: StateFlow<List<RepoSummary>> = _detailRepos.asStateFlow()

    private val _detailLoading = MutableStateFlow(false)
    val detailLoading: StateFlow<Boolean> = _detailLoading.asStateFlow()

    private val _detailError = MutableStateFlow<String?>(null)
    val detailError: StateFlow<String?> = _detailError.asStateFlow()

    private val _repoSummary = MutableStateFlow<AiResponse?>(null)
    val repoSummary: StateFlow<AiResponse?> = _repoSummary.asStateFlow()

    private val _repoAdvice = MutableStateFlow<AiResponse?>(null)
    val repoAdvice: StateFlow<AiResponse?> = _repoAdvice.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _repoSocialInfo = MutableStateFlow<RepoSocialInfoResponse?>(null)
    val repoSocialInfo: StateFlow<RepoSocialInfoResponse?> = _repoSocialInfo.asStateFlow()

    private val _userReview = MutableStateFlow<ReviewResponse?>(null)
    val userReview: StateFlow<ReviewResponse?> = _userReview.asStateFlow()

    private val _userVote = MutableStateFlow(0)
    val userVote: StateFlow<Int> = _userVote.asStateFlow()

    private val _socialLoading = MutableStateFlow(false)
    val socialLoading: StateFlow<Boolean> = _socialLoading.asStateFlow()

    private val _bookmarkedCourseIds = MutableStateFlow<Set<Long>>(emptySet())
    val bookmarkedCourseIds: StateFlow<Set<Long>> = _bookmarkedCourseIds.asStateFlow()

    private val _bookmarkedRepoIds = MutableStateFlow<Set<Long>>(emptySet())
    val bookmarkedRepoIds: StateFlow<Set<Long>> = _bookmarkedRepoIds.asStateFlow()

    private var currentStudentCode: String = ""
    init {
        refreshCourses()
        loadAllBookmarkState()
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            settingsDataStore.studentCode.collect { code ->
                currentStudentCode = code.orEmpty()
            }
        }
        viewModelScope.launch {
            settingsDataStore.currentMajor.first().let { saved ->
                _currentMajor.value = saved
                // Load curriculum credits for display even when restoring from Room
                val curriculum = curriculumLoader.load(saved)
                if (curriculum != null) {
                    _courseCatalogCredits.value = curriculum.courseCatalog.associate { it.code to it.credits }
                }
            }
            loadFromRoom()
        }
    }

    fun refreshCourses() {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            val filter = _courseSearchFilterState.value
            repository.refreshCourses(
                query = filter.normalizedQuery,
                subjectType = filter.subjectType,
                semester = filter.semester
            )
        }
    }

    fun updateCourseSearch(query: String) {
        _courseSearchFilterState.value = _courseSearchFilterState.value.updateQuery(query)
        refreshCourses()
    }

    fun selectCourseSubjectType(subjectType: String?) {
        _courseSearchFilterState.value = _courseSearchFilterState.value.selectSubjectType(subjectType)
        refreshCourses()
    }

    fun selectSemester(semester: Int?) {
        _courseSearchFilterState.value = _courseSearchFilterState.value.selectSemester(semester)
        refreshCourses()
    }

    fun selectMajor(major: String) {
        _currentMajor.value = major
        viewModelScope.launch {
            settingsDataStore.saveCurrentMajor(major)
        }
    }

    fun loadGraph(major: String? = null) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            _graphLoading.value = true
            _graphNodes.value = emptyList()
            _graphLinks.value = emptyList()
            _prerequisiteMap.value = emptyMap()
            _semesterPlan.value = emptyMap()
            _graphError.value = null

            val majorCode = major ?: _currentMajor.value
            semesterCourseDao.clearByMajor(majorCode)

            try {
                _currentMajor.value = majorCode
                settingsDataStore.saveCurrentMajor(majorCode)

                val curriculum = curriculumLoader.load(majorCode)
                if (curriculum != null) {
                    _totalProgramCredits.value = curriculum.courseCatalog.sumOf { it.credits }
                    val catalogCredits = curriculum.courseCatalog.associate { it.code to it.credits }
                    _courseCatalogCredits.value = catalogCredits
                    val courseMap = courses.value.associateBy { it.maMH }
                    val plan = mutableMapOf<Int, MutableList<GraphNode>>()
                    var nextId = -1L

                    for (sd in curriculum.semesters) {
                        val nodes = mutableListOf<GraphNode>()
                        for (code in sd.courses) {
                            if (code !in catalogCredits) {
                                Log.w("CourseViewModel", "loadGraph: course $code not found in curriculum courseCatalog")
                            }
                            val dbCourse = courseMap[code]
                            val id = dbCourse?.id ?: nextId--
                            nodes.add(GraphNode(
                                id = id,
                                name = dbCourse?.tenMH ?: code,
                                code = code,
                                description = null,
                                level = 0,
                                impactScore = 0.0,
                                semester = sd.semester
                            ))
                            if (dbCourse != null) {
                                semesterCourseDao.addCourse(
                                    SemesterCourseEntity(courseId = dbCourse.id, majorCode = majorCode, semester = sd.semester)
                                )
                            }
                        }
                        plan[sd.semester] = nodes
                    }
                    _semesterPlan.value = plan.toSortedMap()
                } else {
                    _totalProgramCredits.value = 0
                    _courseCatalogCredits.value = emptyMap()
                    val kg = repository.getCourseGraph(majorCode)
                    _graphNodes.value = kg.nodes
                    _graphLinks.value = kg.links
                    _prerequisiteMap.value = buildPrerequisiteMap(kg.nodes, kg.links)
                    _semesterPlan.value = kg.nodes
                        .filter { it.semester != null && it.semester in 1..8 }
                        .groupBy { it.semester!! }
                        .toSortedMap()
                    for (node in kg.nodes) {
                        val sem = node.semester ?: continue
                        if (sem !in 1..8) continue
                        semesterCourseDao.addCourse(SemesterCourseEntity(courseId = node.id, majorCode = majorCode, semester = sem))
                    }
                }
            } catch (e: Exception) {
                _graphError.value = e.message ?: "Không thể tải kế hoạch"
            } finally {
                _graphLoading.value = false
            }
        }
    }

    fun clearGraphError() {
        _graphError.value = null
    }

    fun addCourseToSemester(semester: Int, courseCode: String) {
        val result = validationService.validateAddCourse(
            semester, courseCode, courses.value, _semesterPlan.value, _prerequisiteMap.value
        )
        if (!result.isValid) {
            _graphError.value = result.errors.joinToString("\n")
            return
        }
        val existing = courses.value.find { it.maMH == courseCode } ?: return
        _semesterPlan.value = _semesterPlan.value.toMutableMap().also { map ->
            val current = map[semester]?.toMutableList() ?: mutableListOf()
            current.add(existing.toGraphNode(semester))
            map[semester] = current
        }
        val majorCode = _currentMajor.value
        viewModelScope.launch {
            if (!semesterCourseDao.isCourseAdded(existing.id, majorCode)) {
                semesterCourseDao.addCourse(SemesterCourseEntity(courseId = existing.id, majorCode = majorCode, semester = semester))
            }
        }
    }

    fun removeCourseFromSemester(semester: Int, courseCode: String) {
        val existing = courses.value.find { it.maMH == courseCode }
        val course = existing ?: return
        val result = validationService.validateRemoveCourse(
            semester, courseCode, courses.value, _semesterPlan.value
        )
        _semesterPlan.value = _semesterPlan.value.toMutableMap().also { map ->
            map[semester] = (map[semester] ?: emptyList()).filter { it.code != courseCode }
        }
        if (result.warnings.isNotEmpty()) {
            _graphError.value = result.warnings.joinToString("\n")
        }
        val majorCode = _currentMajor.value
        viewModelScope.launch {
            semesterCourseDao.removeCourse(course.id, majorCode)
        }
    }

    fun moveCourse(fromSemester: Int, toSemester: Int, courseCode: String) {
        val course = courses.value.find { it.maMH == courseCode } ?: return
        val currentPlan = _semesterPlan.value
        val targetNodes = currentPlan[toSemester].orEmpty()
        if (targetNodes.any { it.code == courseCode }) {
            _graphError.value = "Môn $courseCode đã tồn tại ở học kỳ $toSemester"
            return
        }
        val targetCredits = targetNodes.sumOf { node ->
            courses.value.find { it.maMH == node.code }?.credits ?: 0
        }
        if (targetCredits + course.credits > SemesterValidationService.MAX_CREDITS_PER_SEMESTER) {
            _graphError.value = "Học kỳ $toSemester đã đạt tối đa ${SemesterValidationService.MAX_CREDITS_PER_SEMESTER} tín chỉ"
            return
        }
        val prereqs = _prerequisiteMap.value[courseCode].orEmpty()
        if (prereqs.isNotEmpty()) {
            val plannedBefore = currentPlan.entries
                .filter { it.key < toSemester }
                .flatMap { it.value.map { n -> n.code } }
                .toSet()
            val missing = prereqs.filter { it !in plannedBefore }
            if (missing.isNotEmpty()) {
                _graphError.value = "Không thể chuyển: thiếu môn tiên quyết ${missing.joinToString(", ")}"
                return
            }
        }
        _semesterPlan.value = currentPlan.toMutableMap().also { map ->
            map[fromSemester] = (map[fromSemester] ?: emptyList()).filter { it.code != courseCode }
            val updated = map[toSemester]?.toMutableList() ?: mutableListOf()
            updated.add(course.toGraphNode(toSemester))
            map[toSemester] = updated
        }
        val majorCode = _currentMajor.value
        viewModelScope.launch {
            semesterCourseDao.moveCourse(course.id, toSemester, majorCode)
        }
    }

    fun getAvailableCourses(query: String = ""): List<CourseEntity> {
        val plannedCodes = _semesterPlan.value.values.flatten().map { it.code }.toSet()
        return courses.value
            .filter { it.maMH !in plannedCodes }
            .filter {
                query.isBlank() || it.maMH.contains(query, ignoreCase = true) || it.tenMH.contains(query, ignoreCase = true)
            }
            .sortedBy { it.maMH }
    }

    fun getNodesGroupedBySemester(): Map<Int, List<GraphNode>> {
        return _graphNodes.value
            .filter { it.semester != null && it.semester in 1..8 }
            .groupBy { it.semester!! }
            .toSortedMap()
    }

    fun openCourse(course: CourseEntity) {
        _selectedCourse.value = course
        _selectedRepo.value = null
        _courseHubNavigationState.value = _courseHubNavigationState.value.openCourse(course.id)
        loadCourseDetail(course.id)
        loadCourseBookmarkState(course.id)
    }

    fun openRepo(repo: RepoSummary) {
        _selectedRepo.value = repo
        _courseHubNavigationState.value = _courseHubNavigationState.value.openRepo(repo.id)
        if (currentStudentCode.isNotBlank()) {
            streakTracker.incrementReposViewed(currentStudentCode)
        }
        loadRepoAiData(repo.id)
        loadRepoSocialInfo(repo.id)
        if (repo.lastPushedAt == null) {
            fetchGithubPushedAt(repo)
        }
    }

    fun navigateToRepoFromBookmark(repoId: Long) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                val repo = repository.getRepo(repoId)
                val courseId = repo.courseId ?: return@launch
                // Open the parent course first
                val course = courses.value.find { it.id == courseId }
                if (course != null) {
                    openCourse(course)
                    // Wait briefly for course detail to load, then open repo
                    kotlinx.coroutines.delay(1500)
                    // Check if repo is now in the loaded repos
                    val loadedRepo = _detailRepos.value.find { it.id == repoId } ?: repo
                    openRepo(loadedRepo)
                }
            } catch (_: Exception) {}
        }
    }

    private fun fetchGithubPushedAt(repo: RepoSummary) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO + kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                val (owner, name) = parseGithubSlug(repo.githubUrl) ?: return@launch
                val url = URL("https://api.github.com/repos/$owner/$name")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Accept", "application/vnd.github+json")
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                val code = conn.responseCode
                if (code == 200) {
                    val json = conn.inputStream.bufferedReader().readText()
                    val obj = org.json.JSONObject(json)
                    val pushedAt = obj.optString("pushed_at", null) ?: obj.optString("updated_at", null)
                    if (pushedAt != null) {
                        withContext(Dispatchers.Main) {
                            _selectedRepo.value = repo.copy(lastPushedAt = pushedAt)
                        }
                    }
                }
                conn.disconnect()
            } catch (_: Exception) { }
        }
    }

    private fun parseGithubSlug(url: String?): Pair<String, String>? {
        if (url == null) return null
        val regex = Regex("github\\.com/([^/]+)/([^/?#]+)")
        val match = regex.find(url) ?: return null
        val owner = match.groupValues[1]
        val name = match.groupValues[2].replace(Regex("\\.git$"), "")
        return Pair(owner, name)
    }

    fun backFromRepo() {
        _selectedRepo.value = null
        _repoSummary.value = null
        _repoAdvice.value = null
        _repoSocialInfo.value = null
        _userReview.value = null
        _userVote.value = 0
        _courseHubNavigationState.value = _courseHubNavigationState.value.back()
    }

    fun closeCourseDetail() {
        _selectedCourse.value = null
        _selectedRepo.value = null
        _courseHubNavigationState.value = CourseHubNavigationState()
        _detailRepos.value = emptyList()
        _detailError.value = null
    }

    private fun loadCourseDetail(courseId: Long) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            _detailLoading.value = true
            _detailError.value = null
            try {
                val detail = repository.loadCourseDetail(courseId)
                _detailRepos.value = detail.repos
            } catch (e: Exception) {
                _detailError.value = e.message ?: "Failed to load course detail"
                _detailRepos.value = emptyList()
            } finally {
                _detailLoading.value = false
            }
        }
    }

    private fun loadCourseBookmarkState(courseId: Long) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                if (bookmarkRepository.isBookmarked("COURSE", courseId)) {
                    _bookmarkedCourseIds.value = _bookmarkedCourseIds.value + courseId
                } else {
                    _bookmarkedCourseIds.value = _bookmarkedCourseIds.value - courseId
                }
            } catch (_: Exception) {}
        }
    }

    private fun loadAllBookmarkState() {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                val all = bookmarkRepository.getAllBookmarks().first()
                val courseIds = mutableSetOf<Long>()
                val repoIds = mutableSetOf<Long>()
                for (bm in all) {
                    when (bm.targetType) {
                        "COURSE" -> courseIds.add(bm.targetId)
                        "REPO" -> repoIds.add(bm.targetId)
                    }
                }
                _bookmarkedCourseIds.value = courseIds
                _bookmarkedRepoIds.value = repoIds
            } catch (e: Exception) {
                android.util.Log.e("CourseViewModel", "Failed to load bookmarks", e)
            }
        }
    }

    private fun loadRepoAiData(repoId: Long) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            _aiLoading.value = true
            _repoSummary.value = null
            _repoAdvice.value = null
            try {
                val summary = repository.getRepoSummary(repoId)
                _repoSummary.value = summary
            } catch (_: Exception) {}
            try {
                val advice = repository.getRepoAdvice(repoId)
                _repoAdvice.value = advice
            } catch (_: Exception) {}
            _aiLoading.value = false
        }
    }

    private fun loadRepoSocialInfo(repoId: Long) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            _socialLoading.value = true
            try {
                val info = repository.getRepoSocialInfo(repoId)
                _repoSocialInfo.value = info
            } catch (_: Exception) {}
            _socialLoading.value = false
        }
    }

    fun submitReview(repoId: Long, rating: Int, comment: String?) {
        val optimisticReview = ReviewResponse(
            id = -(System.currentTimeMillis()),
            targetId = repoId,
            studentId = 0,
            studentName = "Bạn",
            rating = rating,
            comment = comment,
            createdAt = null,
            updatedAt = null
        )
        val previousInfo = _repoSocialInfo.value
        // Build new social info with optimistically added review
        _repoSocialInfo.value = if (previousInfo != null) {
            previousInfo.copy(
                reviews = previousInfo.reviews + optimisticReview,
                averageRating = (previousInfo.averageRating * previousInfo.reviews.size + rating) / (previousInfo.reviews.size + 1)
            )
        } else {
            RepoSocialInfoResponse(
                repoId = repoId,
                voteScore = 0,
                averageRating = rating.toDouble(),
                reviews = listOf(optimisticReview)
            )
        }
        _userReview.value = optimisticReview

        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                val review = repository.submitRepoReview(repoId, rating, comment)
                // Replace optimistic review with real one from server
                val current = _repoSocialInfo.value
                if (current != null) {
                    val updated = current.reviews.map { if (it.id == optimisticReview.id) review else it }
                    _repoSocialInfo.value = current.copy(reviews = updated)
                }
                _userReview.value = review
                loadRepoSocialInfo(repoId)
            } catch (_: Exception) {
                // Revert optimistic update on failure
                _repoSocialInfo.value = previousInfo
                _userReview.value = null
            }
        }
    }

    fun deleteReview(repoId: Long) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                repository.deleteRepoReview(repoId)
                _userReview.value = null
                loadRepoSocialInfo(repoId)
            } catch (_: Exception) {}
        }
    }

    fun voteRepo(repoId: Long, voteValue: Int) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                val result = repository.voteRepo(repoId, voteValue)
                _userVote.value = result.voteValue
                loadRepoSocialInfo(repoId)
            } catch (_: Exception) {}
        }
    }

    fun toggleCourseBookmark(course: CourseEntity) {
        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            val existing = bookmarkRepository.getAllBookmarks().first()
                .firstOrNull { it.targetType == "COURSE" && it.targetId == course.id }

            if (existing != null) {
                bookmarkRepository.removeBookmark(existing.id)
                _bookmarkedCourseIds.value = _bookmarkedCourseIds.value - course.id
            } else {
                bookmarkRepository.addBookmark(
                    Bookmark(
                        id = 0,
                        targetType = "COURSE",
                        targetId = course.id,
                        title = course.tenMH
                    )
                )
                _bookmarkedCourseIds.value = _bookmarkedCourseIds.value + course.id
            }
        }
    }

    fun toggleRepoBookmark(repo: RepoSummary) {
        val repoId = repo.id
        val currentlyBookmarked = repoId in _bookmarkedRepoIds.value

        if (currentlyBookmarked) {
            _bookmarkedRepoIds.value = _bookmarkedRepoIds.value - repoId
        } else {
            _bookmarkedRepoIds.value = _bookmarkedRepoIds.value + repoId
        }

        viewModelScope.launch(kotlinx.coroutines.CoroutineExceptionHandler { _, e -> e.printStackTrace() }) {
            try {
                if (currentlyBookmarked) {
                    val all = bookmarkRepository.getAllBookmarks().first()
                    all.firstOrNull { it.targetType == "REPO" && it.targetId == repoId }?.let {
                        bookmarkRepository.removeBookmark(it.id)
                    }
                } else {
                    bookmarkRepository.addBookmark(
                        Bookmark(id = 0, targetType = "REPO", targetId = repoId, title = repo.displayName.orEmpty())
                    )
                }
            } catch (e: Exception) {
                if (currentlyBookmarked) {
                    _bookmarkedRepoIds.value = _bookmarkedRepoIds.value + repoId
                } else {
                    _bookmarkedRepoIds.value = _bookmarkedRepoIds.value - repoId
                }
                android.util.Log.e("CourseViewModel", "Failed to toggle repo bookmark", e)
            }
        }
    }
}


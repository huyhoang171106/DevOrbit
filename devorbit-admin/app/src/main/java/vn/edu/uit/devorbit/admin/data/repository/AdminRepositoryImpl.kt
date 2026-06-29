package vn.edu.uit.devorbit.admin.data.repository

import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import vn.edu.uit.devorbit.admin.data.datastore.AdminSettingsDataStore
import vn.edu.uit.devorbit.admin.data.remote.HttpErrorMapper
import vn.edu.uit.devorbit.admin.data.remote.dto.*
import vn.edu.uit.devorbit.admin.data.remote.interceptor.AuthInterceptor
import vn.edu.uit.devorbit.admin.domain.repository.AdminAuthResult
import vn.edu.uit.devorbit.admin.domain.repository.AdminRepository
import vn.edu.uit.devorbit.admin.network.AdminApiService
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val apiService: AdminApiService,
    private val settingsDataStore: AdminSettingsDataStore,
    private val authInterceptor: AuthInterceptor
) : AdminRepository {

    /** Hydrate the in-memory token cache from DataStore on first access. */
    suspend fun initToken() {
        val storedToken = settingsDataStore.token.first()
        val storedRefreshToken = settingsDataStore.refreshToken.first()
        authInterceptor.updateTokens(storedToken, storedRefreshToken)
    }

    override suspend fun login(username: String, password: String): Result<AdminAuthResult> {
        return safeApiCall {
            val response = apiService.login(AdminLoginRequest(username, password))
            settingsDataStore.saveToken(response.token)
            settingsDataStore.saveRefreshToken(response.refreshToken)
            authInterceptor.updateTokens(response.token, response.refreshToken)
            settingsDataStore.saveUsername(username)
            AdminAuthResult(response.token, response.refreshToken)
        }
    }

    override suspend fun logout() {
        val token = authInterceptor.token
        if (!token.isNullOrBlank()) {
            try {
                apiService.logout("Bearer $token")
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Best-effort server logout; proceed with local cleanup
            }
        }
        authInterceptor.clear()
        settingsDataStore.clear()
    }

    override suspend fun getToken(): String? {
        if (authInterceptor.token == null) {
            initToken()
        }
        return authInterceptor.token
    }

    override suspend fun getStats(sortBy: String): Result<AdminStatsResponse> = safeApiCall {
        apiService.getStats(sortBy)
    }

    override suspend fun getStudents(search: String?): Result<List<AdminStudentResponse>> = safeApiCall {
        apiService.getStudents(search)
    }

    override suspend fun toggleStudentActive(id: Long): Result<AdminStudentResponse> = safeApiCall {
        apiService.toggleStudentActive(id)
    }

    override suspend fun getAllCourses(): Result<List<CourseSummaryResponse>> = safeApiCall {
        apiService.getAllCourses()
    }

    override suspend fun getCourseDetail(id: Long): Result<CourseDetailResponse> = safeApiCall {
        apiService.getCourseDetail(id)
    }

    override suspend fun createCourse(request: AdminCourseUpsertRequest): Result<CourseDetailResponse> = safeApiCall {
        apiService.createCourse(request)
    }

    override suspend fun updateCourse(id: Long, request: AdminCourseUpsertRequest): Result<CourseDetailResponse> = safeApiCall {
        apiService.updateCourse(id, request)
    }

    override suspend fun deleteCourse(id: Long): Result<Unit> = safeApiCall {
        apiService.deleteCourse(id)
    }

    override suspend fun getTutorials(courseId: Long): Result<List<TutorialItem>> = safeApiCall {
        apiService.getTutorials(courseId)
    }

    override suspend fun createTutorial(courseId: Long, request: TutorialRequest): Result<TutorialItem> = safeApiCall {
        apiService.createTutorial(courseId, request)
    }
    override suspend fun updateTutorial(courseId: Long, tutorialId: Long, request: TutorialRequest): Result<TutorialItem> = safeApiCall {
        apiService.updateTutorial(courseId, tutorialId, request)
    }


    override suspend fun deleteTutorial(courseId: Long, tutorialId: Long): Result<Unit> = safeApiCall {
        apiService.deleteTutorial(courseId, tutorialId)
    }

    override suspend fun getYoutubePlaylists(courseId: Long): Result<List<YoutubePlaylistResponse>> = safeApiCall {
        apiService.getYoutubePlaylists(courseId)
    }

    override suspend fun createYoutubePlaylist(courseId: Long, request: YoutubePlaylistRequest): Result<YoutubePlaylistResponse> = safeApiCall {
        apiService.createYoutubePlaylist(courseId, request)
    }
    override suspend fun updateYoutubePlaylist(courseId: Long, playlistId: Long, request: YoutubePlaylistRequest): Result<YoutubePlaylistResponse> = safeApiCall {
        apiService.updateYoutubePlaylist(courseId, playlistId, request)
    }


    override suspend fun deleteYoutubePlaylist(courseId: Long, playlistId: Long): Result<Unit> = safeApiCall {
        apiService.deleteYoutubePlaylist(courseId, playlistId)
    }

    override suspend fun getArticles(courseId: Long): Result<List<ArticleItem>> = safeApiCall {
        apiService.getArticles(courseId)
    }

    override suspend fun createArticle(courseId: Long, request: ArticleRequest): Result<ArticleItem> = safeApiCall {
        apiService.createArticle(courseId, request)
    }

    override suspend fun updateArticle(courseId: Long, articleId: Long, request: ArticleRequest): Result<ArticleItem> = safeApiCall {
        apiService.updateArticle(courseId, articleId, request)
    }

    override suspend fun deleteArticle(courseId: Long, articleId: Long): Result<Unit> = safeApiCall {
        apiService.deleteArticle(courseId, articleId)
    }

    override suspend fun getAllRelationships(): Result<List<CourseRelationshipResponse>> = safeApiCall {
        apiService.getAllRelationships()
    }

    override suspend fun getCourseRelationships(courseId: Long): Result<List<CourseRelationshipResponse>> = safeApiCall {
        apiService.getCourseRelationships(courseId)
    }

    override suspend fun createRelationship(request: CourseRelationshipRequest): Result<CourseRelationshipResponse> = safeApiCall {
        apiService.createRelationship(request)
    }

    override suspend fun deleteRelationship(id: Long): Result<Unit> = safeApiCall {
        apiService.deleteRelationship(id)
    }

    override suspend fun getAllRepos(): Result<List<RepoSummaryResponse>> = safeApiCall {
        apiService.getAllRepos()
    }

    override suspend fun updateRepo(repoId: Long, request: ApprovedRepoUpdateRequest): Result<RepoSummaryResponse> = safeApiCall {
        apiService.updateRepo(repoId, request)
    }

    override suspend fun deleteRepo(repoId: Long): Result<Unit> = safeApiCall {
        apiService.deleteRepo(repoId)
    }

    override suspend fun syncRepo(repoId: Long): Result<RepoSummaryResponse> = safeApiCall {
        apiService.syncRepo(repoId)
    }
    override suspend fun syncCourseRepos(courseId: Long): Result<Unit> = safeApiCall {
        apiService.syncCourseRepos(courseId)
    }

    override suspend fun evaluateAllRepos(): Result<Unit> = safeApiCall {
        apiService.evaluateAllRepos()
    }

    override suspend fun getPendingCandidates(reviewer: String): Result<List<RepoCandidateResponse>> = safeApiCall {
        apiService.getPendingCandidates(reviewer)
    }

    override suspend fun getReviewerStats(): Result<List<ReviewerStatsResponse>> = safeApiCall {
        apiService.getReviewerStats()
    }

    override suspend fun approveCandidate(candidateId: Long, request: CandidateReviewRequest): Result<RepoCandidateResponse> = safeApiCall {
        apiService.approveCandidate(candidateId, request)
    }

    override suspend fun rejectCandidate(candidateId: Long): Result<RepoCandidateResponse> = safeApiCall {
        apiService.rejectCandidate(candidateId)
    }

    override suspend fun getRepoReviews(): Result<List<RepoReviewAdminResponse>> = safeApiCall {
        apiService.getRepoReviews()
    }

    override suspend fun deleteRepoReview(id: Long): Result<Unit> = safeApiCall {
        apiService.deleteRepoReview(id)
    }

    override suspend fun getCourseReviews(): Result<List<CourseReviewAdminResponse>> = safeApiCall {
        apiService.getCourseReviews()
    }

    override suspend fun deleteCourseReview(id: Long): Result<Unit> = safeApiCall {
        apiService.deleteCourseReview(id)
    }

    override suspend fun scanGithub(request: GithubScanRequest): Result<List<RepoCandidateResponse>> = safeApiCall {
        apiService.scanGithub(request)
    }

    override suspend fun scanAllCourses(): Result<Map<String, String>> = safeApiCall {
        apiService.scanAllCourses()
    }

    override suspend fun getScanLogs(): Result<List<String>> = safeApiCall {
        apiService.getScanLogs()
    }

    override suspend fun getGithubAutomationStatus(): Result<GithubAutomationStatus> = safeApiCall {
        apiService.getGithubAutomationStatus()
    }

    override suspend fun getAutoApprovedRepos(): Result<List<RepoCandidateResponse>> = safeApiCall {
        apiService.getAutoApprovedRepos()
    }

    override suspend fun runAutoApproval(): Result<AutoApprovalRun> = safeApiCall {
        apiService.runAutoApproval()
    }

    override suspend fun clearScanLogs(): Result<Unit> = safeApiCall {
        apiService.clearScanLogs()
    }

    override suspend fun getChannels(): Result<List<ChatChannel>> = safeApiCall {
        apiService.getChannels()
    }

    override suspend fun getCommunityMessages(): Result<List<CommunityMessageAdminResponse>> = safeApiCall {
        apiService.getCommunityMessages()
    }

    override suspend fun deleteCommunityMessage(id: Long): Result<Unit> = safeApiCall {
        apiService.deleteCommunityMessage(id)
    }

    override suspend fun getChatSessions(): Result<List<ChatSessionAdminResponse>> = safeApiCall {
        apiService.getChatSessions()
    }

    override suspend fun getChatMessages(sessionId: String): Result<List<ChatMessageAdminResponse>> = safeApiCall {
        apiService.getChatMessages(sessionId)
    }

    override suspend fun getTechStacks(): Result<List<AdminTechStackResponse>> = safeApiCall {
        apiService.getTechStacks()
    }

    override suspend fun createTechStack(name: String): Result<AdminTechStackResponse> = safeApiCall {
        apiService.createTechStack(mapOf("name" to name))
    }

    override suspend fun deleteTechStack(id: Long): Result<Unit> = safeApiCall {
        apiService.deleteTechStack(id)
    }

    override suspend fun getAllNotes(): Result<List<NoteResponse>> = safeApiCall {
        apiService.getAllNotes()
    }

    override suspend fun deleteNote(id: Long): Result<Unit> = safeApiCall {
        apiService.deleteNote(id)
    }

    override suspend fun getNotifications(): Result<List<NotificationResponse>> = safeApiCall {
        apiService.getNotifications()
    }

    override suspend fun getUnreadCount(): Result<Long> = safeApiCall {
        apiService.getUnreadCount()["count"] ?: 0L
    }

    override suspend fun markNotificationRead(id: Long): Result<Unit> = safeApiCall {
        apiService.markNotificationRead(id)
    }

    override suspend fun markAllNotificationsRead(): Result<Unit> = safeApiCall {
        apiService.markAllNotificationsRead()
    }

    /** Wraps an API call with proper cancellation propagation and user-friendly error messages. */
    private suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(IOException(HttpErrorMapper.toUserMessage(e), e))
        }
    }
}

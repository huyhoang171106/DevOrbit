package vn.edu.uit.devorbit.admin.domain.repository

import vn.edu.uit.devorbit.admin.data.remote.dto.*

data class AdminAuthResult(val token: String, val refreshToken: String? = null)

interface AdminRepository {
    // Auth
    suspend fun login(username: String, password: String): Result<AdminAuthResult>
    suspend fun logout()
    suspend fun getToken(): String?

    // Dashboard
    suspend fun getStats(sortBy: String = "bookmarks"): Result<AdminStatsResponse>

    // Students
    suspend fun getStudents(search: String? = null): Result<List<AdminStudentResponse>>
    suspend fun toggleStudentActive(id: Long): Result<AdminStudentResponse>

    // Courses
    suspend fun getAllCourses(): Result<List<CourseSummaryResponse>>
    suspend fun getCourseDetail(id: Long): Result<CourseDetailResponse>
    suspend fun createCourse(request: AdminCourseUpsertRequest): Result<CourseDetailResponse>
    suspend fun updateCourse(id: Long, request: AdminCourseUpsertRequest): Result<CourseDetailResponse>
    suspend fun deleteCourse(id: Long): Result<Unit>

    // Course Resources — Tutorials
    suspend fun getTutorials(courseId: Long): Result<List<TutorialItem>>
    suspend fun createTutorial(courseId: Long, request: TutorialRequest): Result<TutorialItem>
    suspend fun updateTutorial(courseId: Long, tutorialId: Long, request: TutorialRequest): Result<TutorialItem>
    suspend fun deleteTutorial(courseId: Long, tutorialId: Long): Result<Unit>

    // Course Resources — YouTube Playlists (was "videos")
    suspend fun getYoutubePlaylists(courseId: Long): Result<List<YoutubePlaylistResponse>>
    suspend fun createYoutubePlaylist(courseId: Long, request: YoutubePlaylistRequest): Result<YoutubePlaylistResponse>
    suspend fun updateYoutubePlaylist(courseId: Long, playlistId: Long, request: YoutubePlaylistRequest): Result<YoutubePlaylistResponse>
    suspend fun deleteYoutubePlaylist(courseId: Long, playlistId: Long): Result<Unit>
    // Course Resources — Articles
    suspend fun getArticles(courseId: Long): Result<List<ArticleItem>>
    suspend fun createArticle(courseId: Long, request: ArticleRequest): Result<ArticleItem>
    suspend fun updateArticle(courseId: Long, articleId: Long, request: ArticleRequest): Result<ArticleItem>
    suspend fun deleteArticle(courseId: Long, articleId: Long): Result<Unit>

    // Course Relationships
    suspend fun getAllRelationships(): Result<List<CourseRelationshipResponse>>
    suspend fun getCourseRelationships(courseId: Long): Result<List<CourseRelationshipResponse>>
    suspend fun createRelationship(request: CourseRelationshipRequest): Result<CourseRelationshipResponse>
    suspend fun deleteRelationship(id: Long): Result<Unit>

    // Repos
    suspend fun getAllRepos(): Result<List<RepoSummaryResponse>>
    suspend fun updateRepo(repoId: Long, request: ApprovedRepoUpdateRequest): Result<RepoSummaryResponse>
    suspend fun deleteRepo(repoId: Long): Result<Unit>
    suspend fun syncRepo(repoId: Long): Result<RepoSummaryResponse>
    suspend fun syncCourseRepos(courseId: Long): Result<Unit>
    suspend fun evaluateAllRepos(): Result<Unit>

    // Candidates
    suspend fun getPendingCandidates(reviewer: String = "all"): Result<List<RepoCandidateResponse>>
    suspend fun getReviewerStats(): Result<List<ReviewerStatsResponse>>
    suspend fun approveCandidate(candidateId: Long, request: CandidateReviewRequest): Result<RepoCandidateResponse>
    suspend fun rejectCandidate(candidateId: Long): Result<RepoCandidateResponse>

    // Reviews
    suspend fun getRepoReviews(): Result<List<RepoReviewAdminResponse>>
    suspend fun deleteRepoReview(id: Long): Result<Unit>
    suspend fun getCourseReviews(): Result<List<CourseReviewAdminResponse>>
    suspend fun deleteCourseReview(id: Long): Result<Unit>

    // GitHub
    suspend fun scanGithub(request: GithubScanRequest): Result<List<RepoCandidateResponse>>
    suspend fun scanAllCourses(): Result<Map<String, String>>
    suspend fun getScanLogs(): Result<List<String>>
    suspend fun clearScanLogs(): Result<Unit>
    suspend fun getGithubAutomationStatus(): Result<GithubAutomationStatus>
    suspend fun getAutoApprovedRepos(): Result<List<RepoCandidateResponse>>
    suspend fun runAutoApproval(): Result<AutoApprovalRun>

    // Community
    suspend fun getChannels(): Result<List<ChatChannel>>
    suspend fun getCommunityMessages(): Result<List<CommunityMessageAdminResponse>>
    suspend fun deleteCommunityMessage(id: Long): Result<Unit>

    // Chat
    suspend fun getChatSessions(): Result<List<ChatSessionAdminResponse>>
    suspend fun getChatMessages(sessionId: String): Result<List<ChatMessageAdminResponse>>

    // TechStack
    suspend fun getTechStacks(): Result<List<AdminTechStackResponse>>
    suspend fun createTechStack(name: String): Result<AdminTechStackResponse>
    suspend fun deleteTechStack(id: Long): Result<Unit>

    // Notes
    suspend fun getAllNotes(): Result<List<NoteResponse>>
    suspend fun deleteNote(id: Long): Result<Unit>

    // Notifications
    suspend fun getNotifications(): Result<List<NotificationResponse>>
    suspend fun getUnreadCount(): Result<Long>
    suspend fun markNotificationRead(id: Long): Result<Unit>
    suspend fun markAllNotificationsRead(): Result<Unit>
}

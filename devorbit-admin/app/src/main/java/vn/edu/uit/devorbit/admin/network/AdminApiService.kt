package vn.edu.uit.devorbit.admin.network

import retrofit2.http.*
import vn.edu.uit.devorbit.admin.data.remote.dto.*

interface AdminApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────
    @POST("/api/admin/auth/login")
    suspend fun login(@Body body: AdminLoginRequest): AdminLoginResponse

    @POST("/api/admin/auth/logout")
    suspend fun logout(@Header("Authorization") authHeader: String): Map<String, String>

    @POST("/api/auth/refresh")
    suspend fun refreshAccessToken(@Body request: RefreshTokenRequest): TokenPairResponse

    // ── Dashboard Stats ───────────────────────────────────────────────────────
    @GET("/api/admin/stats")
    suspend fun getStats(@Query("sortBy") sortBy: String = "bookmarks"): AdminStatsResponse

    // ── Students ──────────────────────────────────────────────────────────────
    @GET("/api/admin/students")
    suspend fun getStudents(@Query("search") search: String? = null): List<AdminStudentResponse>

    @PUT("/api/admin/students/{id}/toggle-active")
    suspend fun toggleStudentActive(@Path("id") id: Long): AdminStudentResponse

    // ── Courses ───────────────────────────────────────────────────────────────
    @GET("/api/admin/courses")
    suspend fun getAllCourses(): List<CourseSummaryResponse>

    /** Public endpoint — returns full CourseDetailResponse with repos, hours, etc. */
    @GET("/api/courses/{id}")
    suspend fun getCourseDetail(@Path("id") id: Long): CourseDetailResponse

    @POST("/api/admin/courses")
    suspend fun createCourse(@Body body: AdminCourseUpsertRequest): CourseDetailResponse

    @PUT("/api/admin/courses/{id}")
    suspend fun updateCourse(@Path("id") id: Long, @Body body: AdminCourseUpsertRequest): CourseDetailResponse

    @DELETE("/api/admin/courses/{id}")
    suspend fun deleteCourse(@Path("id") id: Long): CourseDeleteResult

    // ── Course Resources: Tutorials ───────────────────────────────────────────
    @GET("/api/admin/courses/{id}/resources/tutorials")
    suspend fun getTutorials(@Path("id") courseId: Long): List<TutorialItem>

    @POST("/api/admin/courses/{id}/resources/tutorials")
    suspend fun createTutorial(@Path("id") courseId: Long, @Body body: TutorialRequest): TutorialItem

    @PUT("/api/admin/courses/{courseId}/resources/tutorials/{tutorialId}")
    suspend fun updateTutorial(@Path("courseId") courseId: Long, @Path("tutorialId") tutorialId: Long, @Body body: TutorialRequest): TutorialItem

    @DELETE("/api/admin/courses/{id}/resources/tutorials/{tutorialId}")
    suspend fun deleteTutorial(@Path("id") courseId: Long, @Path("tutorialId") tutorialId: Long)

    // ── Course Resources: YouTube Playlists ───────────────────────────────────
    @GET("/api/admin/courses/{id}/resources/youtube-playlists")
    suspend fun getYoutubePlaylists(@Path("id") courseId: Long): List<YoutubePlaylistResponse>

    @POST("/api/admin/courses/{id}/resources/youtube-playlists")
    suspend fun createYoutubePlaylist(@Path("id") courseId: Long, @Body body: YoutubePlaylistRequest): YoutubePlaylistResponse

    @PUT("/api/admin/courses/{courseId}/resources/youtube-playlists/{playlistId}")
    suspend fun updateYoutubePlaylist(@Path("courseId") courseId: Long, @Path("playlistId") playlistId: Long, @Body body: YoutubePlaylistRequest): YoutubePlaylistResponse

    @DELETE("/api/admin/courses/{id}/resources/youtube-playlists/{playlistId}")
    suspend fun deleteYoutubePlaylist(@Path("id") courseId: Long, @Path("playlistId") playlistId: Long)

    // ── Course Resources: Articles ────────────────────────────────────────────
    @GET("/api/admin/courses/{id}/resources/articles")
    suspend fun getArticles(@Path("id") courseId: Long): List<ArticleItem>

    @POST("/api/admin/courses/{id}/resources/articles")
    suspend fun createArticle(@Path("id") courseId: Long, @Body body: ArticleRequest): ArticleItem

    @PUT("/api/admin/courses/{courseId}/resources/articles/{articleId}")
    suspend fun updateArticle(@Path("courseId") courseId: Long, @Path("articleId") articleId: Long, @Body body: ArticleRequest): ArticleItem

    @DELETE("/api/admin/courses/{id}/resources/articles/{articleId}")
    suspend fun deleteArticle(@Path("id") courseId: Long, @Path("articleId") articleId: Long)

    // ── Course Relationships ──────────────────────────────────────────────────
    @GET("/api/admin/courses/relationships")
    suspend fun getAllRelationships(): List<CourseRelationshipResponse>

    @GET("/api/admin/courses/relationships/course/{courseId}")
    suspend fun getCourseRelationships(@Path("courseId") courseId: Long): List<CourseRelationshipResponse>

    @POST("/api/admin/courses/relationships")
    suspend fun createRelationship(@Body body: CourseRelationshipRequest): CourseRelationshipResponse

    @DELETE("/api/admin/courses/relationships/{id}")
    suspend fun deleteRelationship(@Path("id") id: Long)

    // ── Repos ─────────────────────────────────────────────────────────────────
    @GET("/api/admin/repos")
    suspend fun getAllRepos(): List<RepoSummaryResponse>

    @PUT("/api/admin/repos/{repoId}")
    suspend fun updateRepo(@Path("repoId") repoId: Long, @Body body: ApprovedRepoUpdateRequest): RepoSummaryResponse

    @DELETE("/api/admin/repos/{repoId}")
    suspend fun deleteRepo(@Path("repoId") repoId: Long)

    @POST("/api/admin/repos/{repoId}/sync")
    suspend fun syncRepo(@Path("repoId") repoId: Long): RepoSummaryResponse

    @POST("/api/admin/repos/evaluate-all")
    suspend fun evaluateAllRepos()

    // ── Repo Candidates ───────────────────────────────────────────────────────
    @GET("/api/admin/repo-candidates")
    suspend fun getPendingCandidates(@Query("reviewer") reviewer: String = "all"): List<RepoCandidateResponse>

    @GET("/api/admin/repo-candidates/stats")
    suspend fun getReviewerStats(): List<ReviewerStatsResponse>

    @POST("/api/admin/repo-candidates/{candidateId}/approve")
    suspend fun approveCandidate(@Path("candidateId") candidateId: Long, @Body body: CandidateReviewRequest): RepoCandidateResponse

    @POST("/api/admin/repo-candidates/{candidateId}/reject")
    suspend fun rejectCandidate(@Path("candidateId") candidateId: Long): RepoCandidateResponse

    // ── Reviews ───────────────────────────────────────────────────────────────
    @GET("/api/admin/reviews/repos")
    suspend fun getRepoReviews(): List<RepoReviewAdminResponse>

    @DELETE("/api/admin/reviews/repos/{id}")
    suspend fun deleteRepoReview(@Path("id") id: Long)

    @GET("/api/admin/reviews/courses")
    suspend fun getCourseReviews(): List<CourseReviewAdminResponse>

    @DELETE("/api/admin/reviews/courses/{id}")
    suspend fun deleteCourseReview(@Path("id") id: Long)

    // ── GitHub ────────────────────────────────────────────────────────────────
    @POST("/api/admin/github/scan")
    suspend fun scanGithub(@Body body: GithubScanRequest): List<RepoCandidateResponse>

    @POST("/api/admin/github/scan-all")
    suspend fun scanAllCourses(): Map<String, String>

    @GET("/api/admin/github/scan-logs")
    suspend fun getScanLogs(): List<String>

    @DELETE("/api/admin/github/scan-logs")
    suspend fun clearScanLogs()

    @GET("/api/admin/github/automation-status")
    suspend fun getGithubAutomationStatus(): GithubAutomationStatus

    @GET("/api/admin/github/auto-approvals")
    suspend fun getAutoApprovedRepos(): List<RepoCandidateResponse>

    @POST("/api/admin/github/auto-approve")
    suspend fun runAutoApproval(): AutoApprovalRun

    // ── Community ─────────────────────────────────────────────────────────────
    @GET("/api/admin/community/channels")
    suspend fun getChannels(): List<ChatChannel>

    @GET("/api/admin/community/messages")
    suspend fun getCommunityMessages(): List<CommunityMessageAdminResponse>

    @DELETE("/api/admin/community/messages/{id}")
    suspend fun deleteCommunityMessage(@Path("id") id: Long)

    // ── Chat Sessions ─────────────────────────────────────────────────────────
    @GET("/api/admin/chat/sessions")
    suspend fun getChatSessions(): List<ChatSessionAdminResponse>

    @GET("/api/admin/chat/sessions/{id}/messages")
    suspend fun getChatMessages(@Path("id") id: String): List<ChatMessageAdminResponse>

    // ── Tech Stack ────────────────────────────────────────────────────────────
    @GET("/api/admin/techstack")
    suspend fun getTechStacks(): List<AdminTechStackResponse>

    @POST("/api/admin/techstack")
    suspend fun createTechStack(@Body body: Map<String, String>): AdminTechStackResponse

    @DELETE("/api/admin/techstack/{id}")
    suspend fun deleteTechStack(@Path("id") id: Long): Map<String, Any>

    // ── Notes ─────────────────────────────────────────────────────────────────
    @GET("/api/admin/notes")
    suspend fun getAllNotes(): List<NoteResponse>

    @DELETE("/api/admin/notes/{id}")
    suspend fun deleteNote(@Path("id") id: Long)

    // ── Notifications ─────────────────────────────────────────────────────────
    @GET("/api/admin/notifications")
    suspend fun getNotifications(): List<NotificationResponse>

    @GET("/api/admin/notifications/unread-count")
    suspend fun getUnreadCount(): Map<String, Long>

    @PUT("/api/admin/notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: Long)

    @PUT("/api/admin/notifications/read-all")
    suspend fun markAllNotificationsRead()
}

data class RefreshTokenRequest(val refreshToken: String)

data class TokenPairResponse(val accessToken: String, val refreshToken: String, val tokenType: String)

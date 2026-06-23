package vn.edu.uit.devorbit.mobile.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.PATCH
import vn.edu.uit.devorbit.mobile.data.remote.dto.*

interface ApiService {
    @GET("/api/courses")
    suspend fun getCourses(
        @Query("q") query: String? = null,
        @Query("subjectType") subjectType: String? = null,
        @Query("semester") semester: Int? = null,
        @Query("managementUnit") managementUnit: String? = null
    ): List<CourseSummary>

    @GET("/api/courses/{id}")
    suspend fun getCourseDetail(@Path("id") courseId: Long): CourseSummary

    @GET("/api/courses/{courseId}/repos")
    suspend fun getRepos(
        @Path("courseId") courseId: Long,
        @Query("techStack") techStack: String? = null
    ): List<RepoSummary>

    @GET("/api/courses/{id}/tutorials")
    suspend fun getTutorials(@Path("id") courseId: Long): List<CourseTutorial>

    @GET("/api/courses/{id}/videos")
    suspend fun getVideos(@Path("id") courseId: Long): List<CourseYoutubePlaylist>

    @GET("/api/courses/{id}/articles")
    suspend fun getArticles(@Path("id") courseId: Long): List<CourseArticle>

    @GET("/api/courses/relationships")
    suspend fun getRelationships(): List<CourseRelationshipResponse>

    @GET("/api/courses/relationships/course/{courseId}")
    suspend fun getCourseRelationships(@Path("courseId") courseId: Long): List<CourseRelationshipResponse>

    @GET("/api/courses/graph")
    suspend fun getKnowledgeGraph(): GraphResponse

    // Student Auth
    @POST("/api/student/login")
    suspend fun login(@Body body: Map<String, String>): Map<String, Any>

    @POST("/api/student/register")
    suspend fun register(@Body body: Map<String, String>): Map<String, Any>

    @POST("/api/student/verify-otp")
    suspend fun verifyOtp(@Body body: Map<String, String>): Map<String, Any>

    @POST("/api/student/forgot-password")
    suspend fun forgotPassword(@Body body: Map<String, String>): Map<String, Any>

    @POST("/api/student/reset-password")
    suspend fun resetPassword(@Body body: Map<String, String>): Map<String, Any>

    @POST("/api/student/resend-otp")
    suspend fun resendOtp(@Body body: Map<String, String>): Map<String, Any>

    @POST("/api/student/logout")
    suspend fun studentLogout(): Map<String, Any>

    @GET("/api/student/me")
    suspend fun getStudentProfile(): Map<String, Any>

    @GET("/api/student/bookmarks")
    suspend fun getBookmarks(): List<StudentBookmarkResponse>

    @POST("/api/student/bookmarks")
    suspend fun addBookmark(@Body body: StudentBookmarkRequest): StudentBookmarkResponse

    @DELETE("/api/student/bookmarks/{id}")
    suspend fun deleteBookmark(@Path("id") id: Long)

    // Tech & Discovery
    @GET("/api/tech-stacks")
    suspend fun getTechStacks(): List<TechStack>

    @GET("/api/discovery/recent-repos")
    suspend fun getRecentDiscoveryRepos(): List<RepoSummary>

    @GET("/api/discovery/repos")
    suspend fun searchDiscoveryRepos(@Query("q") query: String? = null): List<RepoSummary>

    @GET("/api/discovery/top-stacks")
    suspend fun getTopStacks(): List<String>

    @GET("/api/repos/{repoId}")
    suspend fun getRepo(@Path("repoId") repoId: Long): RepoSummary

    // AI
    @GET("/api/ai/repo/{repoId}/summary")
    suspend fun getRepoSummary(@Path("repoId") repoId: Long): Map<String, Any>

    @GET("/api/ai/repo/{repoId}/advice")
    suspend fun getRepoAdvice(@Path("repoId") repoId: Long): Map<String, Any>

    @POST("/api/ai/knowledge-graph/query")
    suspend fun queryKnowledgeGraph(@Body body: Map<String, String>): Map<String, Any>

    @POST("/api/ai/subject-qa/query")
    suspend fun querySubjectQa(@Body body: SubjectQaRequest): SubjectQaResponse

    @POST("/api/ai/generate-roadmap")
    suspend fun generateRoadmap(@Body body: RoadmapGenerationRequest): RoadmapRecommendationResponse

    // Student Notifications
    @GET("/api/student/notifications")
    suspend fun getNotifications(): List<StudentNotificationResponse>

    @GET("/api/student/notifications/unread-count")
    suspend fun getUnreadNotificationCount(): UnreadCountResponse

    @PUT("/api/student/notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: Long): Map<String, Any>

    @PUT("/api/student/notifications/read-all")
    suspend fun markAllNotificationsRead(): Map<String, Any>

    // Group Plans
    @POST("/api/student/group-plans")
    suspend fun createGroupPlan(@Body request: CreateGroupPlanRequest): GroupPlanResponse

    @GET("/api/student/group-plans")
    suspend fun getMyGroupPlans(): List<GroupPlanResponse>

    @GET("/api/student/group-plans/{id}")
    suspend fun getGroupPlanDetail(@Path("id") id: Long): GroupPlanResponse

    @DELETE("/api/student/group-plans/{id}")
    suspend fun deleteGroupPlan(@Path("id") id: Long)

    @POST("/api/student/group-plans/{id}/invite")
    suspend fun inviteMember(@Path("id") planId: Long, @Body request: InviteMemberRequest)

    @GET("/api/student/group-plans/{id}/members")
    suspend fun getGroupPlanMembers(@Path("id") planId: Long): List<GroupPlanMemberResponse>

    @POST("/api/student/group-plans/{id}/respond")
    suspend fun respondInvite(@Path("id") planId: Long, @Body request: RespondInviteRequest)

    @POST("/api/student/group-plans/{id}/leave")
    suspend fun leavePlan(@Path("id") planId: Long)

    @GET("/api/student/group-plans/{id}/tasks")
    suspend fun getGroupTasks(@Path("id") planId: Long): List<GroupTaskResponse>

    @POST("/api/student/group-plans/{id}/tasks")
    suspend fun addGroupTask(@Path("id") planId: Long, @Body request: AddGroupTaskRequest): GroupTaskResponse

    @PUT("/api/student/group-plans/tasks/{taskId}")
    suspend fun updateGroupTask(
        @Path("taskId") taskId: Long,
        @Body request: UpdateGroupTaskRequest
    ): GroupTaskResponse

    @POST("/api/student/group-plans/tasks/{taskId}/request-delete")
    suspend fun requestDeleteTask(@Path("taskId") taskId: Long)

    @POST("/api/student/group-plans/tasks/{taskId}/approve-delete")
    suspend fun approveDeleteTask(@Path("taskId") taskId: Long, @Body request: ApproveDeleteRequest)

    // Personal Tasks
    @GET("/api/student/tasks")
    suspend fun getPersonalTasks(
        @Query("filter") filter: String = "all"
    ): List<PersonalTaskResponse>

    @POST("/api/student/tasks")
    suspend fun createPersonalTask(@Body request: CreatePersonalTaskRequest): PersonalTaskResponse

    @PUT("/api/student/tasks/{taskId}")
    suspend fun updatePersonalTask(
        @Path("taskId") taskId: Long,
        @Body request: UpdatePersonalTaskRequest
    ): PersonalTaskResponse

    @DELETE("/api/student/tasks/{taskId}")
    suspend fun deletePersonalTask(@Path("taskId") taskId: Long)

    @PATCH("/api/student/tasks/{taskId}/toggle")
    suspend fun togglePersonalTask(
        @Path("taskId") taskId: Long,
        @Body body: Map<String, Boolean>
    ): PersonalTaskResponse
}

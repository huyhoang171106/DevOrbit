package vn.edu.uit.devorbit.mobile.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
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
    suspend fun getTechStacks(): List<Map<String, String>>

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
}

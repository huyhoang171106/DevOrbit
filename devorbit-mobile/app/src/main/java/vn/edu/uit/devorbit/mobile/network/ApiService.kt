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
    suspend fun getCourses(): List<CourseSummary>

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

    @GET("/api/courses/graph")
    suspend fun getKnowledgeGraph(): GraphResponse

    // Student Auth
    @POST("/api/student/login")
    suspend fun login(@Body body: Map<String, String>): Map<String, Any>

    @POST("/api/student/register")
    suspend fun register(@Body body: Map<String, String>): Map<String, Any>

    @GET("/api/student/me")
    suspend fun getStudentProfile(): Map<String, Any>

    // Tech & Discovery
    @GET("/api/tech-stacks")
    suspend fun getTechStacks(): List<Map<String, String>>

    @GET("/api/discovery/recent-repos")
    suspend fun getRecentDiscoveryRepos(): List<Map<String, Any>>

    @GET("/api/discovery/top-stacks")
    suspend fun getTopStacks(): List<Map<String, Any>>

    // AI
    @GET("/api/ai/repo/{repoId}/summary")
    suspend fun getRepoSummary(@Path("repoId") repoId: Long): Map<String, Any>

    @GET("/api/ai/repo/{repoId}/advice")
    suspend fun getRepoAdvice(@Path("repoId") repoId: Long): Map<String, Any>

    @POST("/api/ai/knowledge-graph/query")
    suspend fun queryKnowledgeGraph(@Body body: Map<String, String>): Map<String, Any>
}

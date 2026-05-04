package vn.edu.uit.devorbit.mobile.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import vn.edu.uit.devorbit.mobile.model.BookmarkRequest
import vn.edu.uit.devorbit.mobile.model.BookmarkResponse
import vn.edu.uit.devorbit.mobile.model.BookmarkTargetType
import vn.edu.uit.devorbit.mobile.model.CourseSummary
import vn.edu.uit.devorbit.mobile.model.RepoSummary
import vn.edu.uit.devorbit.mobile.model.StudentAuthResponse
import vn.edu.uit.devorbit.mobile.model.StudentLoginRequest
import vn.edu.uit.devorbit.mobile.model.StudentRegisterRequest

interface ApiService {
    @GET("/api/courses")
    suspend fun getCourses(): List<CourseSummary>

    @GET("/api/courses/{courseId}/repos")
    suspend fun getRepos(
        @Path("courseId") courseId: Long,
    ): List<RepoSummary>

    @POST("/api/student/login")
    suspend fun studentLogin(@Body request: StudentLoginRequest): StudentAuthResponse

    @POST("/api/student/register")
    suspend fun studentRegister(@Body request: StudentRegisterRequest): StudentAuthResponse

    @GET("/api/student/bookmarks")
    suspend fun getBookmarks(@Header("Authorization") authorization: String): List<BookmarkResponse>

    @POST("/api/student/bookmarks")
    suspend fun saveBookmark(@Header("Authorization") authorization: String, @Body request: BookmarkRequest): BookmarkResponse

    @DELETE("/api/student/bookmarks/{targetType}/{targetId}")
    suspend fun deleteBookmark(
        @Header("Authorization") authorization: String,
        @Path("targetType") targetType: BookmarkTargetType,
        @Path("targetId") targetId: Long,
    )
}

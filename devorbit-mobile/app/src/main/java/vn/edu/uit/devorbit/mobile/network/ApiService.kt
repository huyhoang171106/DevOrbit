package vn.edu.uit.devorbit.mobile.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.POST
import vn.edu.uit.devorbit.mobile.model.CourseSummary
import vn.edu.uit.devorbit.mobile.model.RepoSummary
import vn.edu.uit.devorbit.mobile.model.StudentAuthResponse
import vn.edu.uit.devorbit.mobile.model.StudentLoginRequest

interface ApiService {
    @GET("/api/courses")
    suspend fun getCourses(): List<CourseSummary>

    @GET("/api/courses/{courseId}/repos")
    suspend fun getRepos(
        @Path("courseId") courseId: Long,
    ): List<RepoSummary>

    @POST("/api/student/login")
    suspend fun studentLogin(@Body request: StudentLoginRequest): StudentAuthResponse
}

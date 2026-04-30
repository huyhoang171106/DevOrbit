package vn.edu.uit.devorbit.mobile.repository

import android.content.Context
import vn.edu.uit.devorbit.mobile.model.CourseSummary
import vn.edu.uit.devorbit.mobile.model.RepoSummary
import vn.edu.uit.devorbit.mobile.model.StudentAuthResponse
import vn.edu.uit.devorbit.mobile.model.StudentLoginRequest
import vn.edu.uit.devorbit.mobile.network.NetworkModule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DevOrbitRepository(context: Context) {
    private val api = NetworkModule.apiService
    private val cacheStore = CacheStore(context)
    private val authSessionStore = AuthSessionStore(context)
    private val gson = Gson()

    suspend fun getCourses(): Result<List<CourseSummary>> = runCatching {
        val courses = api.getCourses()
        cacheStore.saveCoursesJson(gson.toJson(courses))
        courses
    }.recoverCatching {
        cacheStore.getCoursesJson()?.let { json ->
            gson.fromJson<List<CourseSummary>>(json, object : TypeToken<List<CourseSummary>>() {}.type)
        } ?: throw it
    }

    suspend fun getRepos(courseId: Long): Result<List<RepoSummary>> = runCatching {
        val repos = api.getRepos(courseId)
        cacheStore.saveReposJson(courseId, gson.toJson(repos))
        repos
    }.recoverCatching {
        cacheStore.getReposJson(courseId)?.let { json ->
            gson.fromJson<List<RepoSummary>>(json, object : TypeToken<List<RepoSummary>>() {}.type)
        } ?: throw it
    }

    suspend fun loginStudent(studentCode: String, password: String): Result<StudentAuthResponse> = runCatching {
        val session = api.studentLogin(StudentLoginRequest(studentCode, password))
        authSessionStore.saveStudentSession(session.token, session.studentCode, session.fullName, session.email)
        session
    }
}

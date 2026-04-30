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
    private val cache = CacheStore(context)
    private val auth = AuthSessionStore(context)
    private val gson = Gson()

    suspend fun getCourses(): Result<List<CourseSummary>> =
        fetchWithCache(
            fetch = { api.getCourses() },
            saveCache = { cache.saveCoursesJson(gson.toJson(it)) },
            loadCache = { cache.getCoursesJson() },
        )

    suspend fun getRepos(courseId: Long): Result<List<RepoSummary>> =
        fetchWithCache(
            fetch = { api.getRepos(courseId) },
            saveCache = { cache.saveReposJson(courseId, gson.toJson(it)) },
            loadCache = { cache.getReposJson(courseId) },
        )

    suspend fun loginStudent(studentCode: String, password: String): Result<StudentAuthResponse> = runCatching {
        val session = api.studentLogin(StudentLoginRequest(studentCode, password))
        auth.saveStudentSession(session.token, session.studentCode, session.fullName, session.email)
        session
    }

    private inline fun <reified T> fetchWithCache(
        fetch: suspend () -> T,
        saveCache: (T) -> Unit,
        loadCache: () -> String?,
    ): Result<T> = runCatching {
        val data = fetch()
        saveCache(data)
        data
    }.recoverCatching {
        val json = loadCache()
        if (json != null) gson.fromJson<T>(json, object : TypeToken<T>() {}.type) else throw it
    }
}

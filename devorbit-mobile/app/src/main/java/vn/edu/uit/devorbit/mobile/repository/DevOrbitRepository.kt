package vn.edu.uit.devorbit.mobile.repository

import android.content.Context
import vn.edu.uit.devorbit.mobile.model.CourseSummary
import vn.edu.uit.devorbit.mobile.model.BookmarkRequest
import vn.edu.uit.devorbit.mobile.model.BookmarkResponse
import vn.edu.uit.devorbit.mobile.model.BookmarkTargetType
import vn.edu.uit.devorbit.mobile.model.RepoSummary
import vn.edu.uit.devorbit.mobile.model.StudentAuthResponse
import vn.edu.uit.devorbit.mobile.model.StudentLoginRequest
import vn.edu.uit.devorbit.mobile.model.StudentRegisterRequest
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

    suspend fun registerStudent(studentCode: String, fullName: String, email: String, password: String): Result<StudentAuthResponse> = runCatching {
        val session = api.studentRegister(StudentRegisterRequest(studentCode, fullName, email, password))
        auth.saveStudentSession(session.token, session.studentCode, session.fullName, session.email)
        session
    }

    suspend fun getBookmarks(): Result<List<BookmarkResponse>> = runCatching {
        api.getBookmarks(bearerToken())
    }

    suspend fun saveBookmark(targetType: BookmarkTargetType, targetId: Long): Result<BookmarkResponse> = runCatching {
        api.saveBookmark(bearerToken(), BookmarkRequest(targetType, targetId))
    }

    suspend fun deleteBookmark(targetType: BookmarkTargetType, targetId: Long): Result<Unit> = runCatching {
        api.deleteBookmark(bearerToken(), targetType, targetId)
    }

    private fun bearerToken(): String = "Bearer ${auth.getToken() ?: error("Missing student session")}" 

    private suspend inline fun <reified T> fetchWithCache(
        noinline fetch: suspend () -> T,
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

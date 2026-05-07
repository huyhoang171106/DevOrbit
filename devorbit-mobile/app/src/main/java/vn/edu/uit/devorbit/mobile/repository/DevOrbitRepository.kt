package vn.edu.uit.devorbit.mobile.repository

import android.content.Context
import vn.edu.uit.devorbit.mobile.model.CourseSummary
import vn.edu.uit.devorbit.mobile.model.RepoSummary
import vn.edu.uit.devorbit.mobile.network.NetworkModule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DevOrbitRepository(
    private val context: Context,
    private val api: vn.edu.uit.devorbit.mobile.network.ApiService,
    private val cache: CacheStore,
) {
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

    suspend fun getAiSummary(repoId: Long): Result<vn.edu.uit.devorbit.mobile.model.AiResponse> = runCatching {
        api.getAiSummary(repoId)
    }

    suspend fun getAiAdvice(repoId: Long): Result<vn.edu.uit.devorbit.mobile.model.AiResponse> = runCatching {
        api.getAiAdvice(repoId)
    }

    suspend fun getTutorials(courseId: Long): Result<List<CourseTutorial>> = runCatching {
        api.getTutorials(courseId)
    }

    suspend fun getVideos(courseId: Long): Result<List<CourseYoutubePlaylist>> = runCatching {
        api.getVideos(courseId)
    }

    suspend fun getArticles(courseId: Long): Result<List<CourseArticle>> = runCatching {
        api.getArticles(courseId)
    }

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

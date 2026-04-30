package vn.edu.uit.devorbit.mobile.repository

import android.content.Context

class CacheStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveCoursesJson(json: String) = prefs.edit().putString(KEY_COURSES, json).apply()

    fun getCoursesJson(): String? = prefs.getString(KEY_COURSES, null)

    fun saveReposJson(courseId: Long, json: String) = prefs.edit().putString(keyForRepos(courseId), json).apply()

    fun getReposJson(courseId: Long): String? = prefs.getString(keyForRepos(courseId), null)

    private fun keyForRepos(courseId: Long) = "repos_$courseId"

    companion object {
        private const val PREFS_NAME = "devorbit-cache"
        private const val KEY_COURSES = "courses"
    }
}
package vn.edu.uit.devorbit.mobile.repository

import android.content.Context

class AuthSessionStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveStudentSession(token: String, studentCode: String, fullName: String, email: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_STUDENT_CODE, studentCode)
            .putString(KEY_FULL_NAME, fullName)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getDisplayName(): String? = prefs.getString(KEY_FULL_NAME, null)

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "devorbit-auth"
        private const val KEY_TOKEN = "token"
        private const val KEY_STUDENT_CODE = "studentCode"
        private const val KEY_FULL_NAME = "fullName"
        private const val KEY_EMAIL = "email"
    }
}
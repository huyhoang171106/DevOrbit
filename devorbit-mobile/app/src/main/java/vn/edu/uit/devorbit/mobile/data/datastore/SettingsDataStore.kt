package vn.edu.uit.devorbit.mobile.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "devorbit_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val JWT_TOKEN = stringPreferencesKey("jwt_token")
        val STUDENT_NAME = stringPreferencesKey("student_name")
        val STUDENT_CODE = stringPreferencesKey("student_code")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val token: Flow<String?> = context.settingsStore.data.map { it[Keys.JWT_TOKEN] }
    val studentName: Flow<String?> = context.settingsStore.data.map { it[Keys.STUDENT_NAME] }
    val studentCode: Flow<String?> = context.settingsStore.data.map { it[Keys.STUDENT_CODE] }
    val darkMode: Flow<Boolean> = context.settingsStore.data.map { it[Keys.DARK_MODE] ?: true }

    suspend fun saveToken(token: String) {
        context.settingsStore.edit { it[Keys.JWT_TOKEN] = token }
    }

    suspend fun clearToken() {
        context.settingsStore.edit { it.remove(Keys.JWT_TOKEN) }
    }

    suspend fun saveStudentName(name: String) {
        context.settingsStore.edit { it[Keys.STUDENT_NAME] = name }
    }

    suspend fun saveStudentCode(code: String) {
        context.settingsStore.edit { it[Keys.STUDENT_CODE] = code }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.settingsStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun clearAll() {
        context.settingsStore.edit { it.clear() }
    }
}

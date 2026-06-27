package vn.edu.uit.devorbit.admin.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.adminDataStore by preferencesDataStore(name = "admin_settings")

@Singleton
class AdminSettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("admin_token")
        private val USERNAME_KEY = stringPreferencesKey("admin_username")
    }

    val token: Flow<String?> = context.adminDataStore.data.map { it[TOKEN_KEY] }
    val username: Flow<String?> = context.adminDataStore.data.map { it[USERNAME_KEY] }

    suspend fun saveToken(token: String) {
        context.adminDataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun saveUsername(username: String) {
        context.adminDataStore.edit { it[USERNAME_KEY] = username }
    }

    suspend fun clear() {
        context.adminDataStore.edit { it.clear() }
    }
}

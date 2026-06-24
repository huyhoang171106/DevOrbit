package vn.edu.uit.devorbit.admin.data.datastore

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.adminDataStore by preferencesDataStore(name = "admin_settings")

@Singleton
class AdminSettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val USERNAME_KEY = stringPreferencesKey("admin_username")
    }

    private val sharedPrefs: SharedPreferences by lazy {
        try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                "admin_secure_prefs",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Robust fallback if Keystore initialization fails on older devices
            context.getSharedPreferences("admin_secure_prefs_fallback", Context.MODE_PRIVATE)
        }
    }

    /** Expose token as a flow by reading from Keystore-encrypted preferences. */
    val token: Flow<String?> = flow {
        emit(sharedPrefs.getString("admin_token", null))
    }

    val username: Flow<String?> = context.adminDataStore.data.map { it[USERNAME_KEY] }

    suspend fun saveToken(token: String) {
        sharedPrefs.edit().putString("admin_token", token).apply()
    }

    suspend fun saveUsername(username: String) {
        context.adminDataStore.edit { it[USERNAME_KEY] = username }
    }

    suspend fun clear() {
        context.adminDataStore.edit { it.clear() }
        sharedPrefs.edit().remove("admin_token").apply()
    }
}

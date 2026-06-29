package vn.edu.uit.devorbit.mobile.data.auth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import vn.edu.uit.devorbit.mobile.BuildConfig
import vn.edu.uit.devorbit.mobile.data.datastore.SettingsDataStore
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefreshManager @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {
    @Volatile var accessToken: String? = null
    @Volatile var refreshToken: String? = null

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val refreshMutex = Mutex()

    suspend fun init() {
        accessToken = settingsDataStore.token.first()
        refreshToken = settingsDataStore.refreshToken.first()
    }

    suspend fun refresh(): Boolean {
        // Single-flight: only one coroutine does the actual refresh at a time.
        // Subsequent callers wait and then use the updated tokens.
        return refreshMutex.withLock {
            val rt = refreshToken ?: return@withLock false
            try {
                val requestBody = """{"refreshToken":"$rt"}""".toByteArray()
                    .toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("${BuildConfig.API_BASE_URL}api/auth/refresh")
                    .post(requestBody)
                    .build()
                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return@withLock false
                    val json = JSONObject(body)
                    val newAccess = json.getString("accessToken")
                    val newRefresh = json.getString("refreshToken")
                    accessToken = newAccess
                    refreshToken = newRefresh
                    settingsDataStore.saveToken(newAccess)
                    settingsDataStore.saveRefreshToken(newRefresh)
                    true
                } else {
                    accessToken = null
                    refreshToken = null
                    settingsDataStore.clearToken()
                    settingsDataStore.clearRefreshToken()
                    false
                }
            } catch (_: Exception) {
                false
            }
        }
    }

    fun clear() {
        accessToken = null
        refreshToken = null
    }
}

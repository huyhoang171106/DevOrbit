package vn.edu.uit.devorbit.admin.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import vn.edu.uit.devorbit.admin.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {

    @Volatile
    var token: String? = null
        private set

    @Volatile
    var refreshToken: String? = null
        private set

    private val lock = Any()
    private var refreshing = false

    fun updateTokens(newToken: String?, newRefreshToken: String?) {
        token = newToken
        refreshToken = newRefreshToken
    }

    fun clear() {
        token = null
        refreshToken = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val currentToken = token
        val request = if (!currentToken.isNullOrBlank()) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $currentToken")
                .build()
        } else {
            chain.request()
        }

        val response = chain.proceed(request)

        // Auto-refresh on 401 (single-flight)
        if (response.code == 401 && currentToken != null) {
            response.close()

            synchronized(lock) {
                // Another thread already refreshed — retry with the new token
                if (token != currentToken) {
                    val newAccess = token
                    if (newAccess != null) {
                        val retryRequest = chain.request().newBuilder()
                            .header("Authorization", "Bearer $newAccess")
                            .build()
                        return chain.proceed(retryRequest)
                    }
                    return response
                }
                // Wait for an ongoing refresh from another thread
                while (refreshing) {
                    try { @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN") (lock as java.lang.Object).wait() } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                        return response
                    }
                }
                // Token may have been updated while we waited
                if (token != currentToken) {
                    val newAccess = token
                    if (newAccess != null) {
                        val retryRequest = chain.request().newBuilder()
                            .header("Authorization", "Bearer $newAccess")
                            .build()
                        return chain.proceed(retryRequest)
                    }
                    return response
                }
                // No refresh token available — can't recover
                if (refreshToken.isNullOrBlank()) return response
                // Become the refresh leader
                refreshing = true
            }

            // Perform actual refresh outside the lock so waiters don't block on HTTP
            var success = false
            var newAccess: String? = null
            var newRefresh: String? = null
            try {
                val baseUrl = BuildConfig.API_BASE_URL.trimEnd('/')
                val rt = refreshToken!!
                val jsonBody = JSONObject().apply { put("refreshToken", rt) }

                val refreshRequest = Request.Builder()
                    .url("$baseUrl/api/auth/refresh")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                // Use a clean client without interceptors (avoids circular refresh)
                val cleanClient = OkHttpClient.Builder().build()
                val refreshResponse = cleanClient.newCall(refreshRequest).execute()

                if (refreshResponse.isSuccessful) {
                    val body = refreshResponse.body?.string()
                    if (body != null) {
                        val json = JSONObject(body)
                        newAccess = json.getString("accessToken")
                        newRefresh = json.getString("refreshToken")
                        success = true
                    }
                }
            } catch (e: Exception) { /* fall through to release */ }

            synchronized(lock) {
                if (success) {
                    token = newAccess
                    refreshToken = newRefresh
                } else {
                    token = null
                    refreshToken = null
                }
                refreshing = false
                @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
                (lock as java.lang.Object).notifyAll()
            }

            if (success) {
                val retryRequest = chain.request().newBuilder()
                    .header("Authorization", "Bearer $newAccess")
                    .build()
                return chain.proceed(retryRequest)
            }
        }

        return response
    }
}

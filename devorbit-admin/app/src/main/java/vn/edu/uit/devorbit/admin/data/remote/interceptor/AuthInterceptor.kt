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

        // Auto-refresh on 401 (try once)
        if (response.code == 401 && currentToken != null && !refreshToken.isNullOrBlank()) {
            response.close()
            try {
                val baseUrl = BuildConfig.API_BASE_URL.trimEnd('/')
                val rt = refreshToken!!
                val jsonBody = JSONObject().apply { put("refreshToken", rt) }

                val refreshRequest = Request.Builder()
                    .url("$baseUrl/api/auth/refresh")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                // Use a basic client without interceptors (avoids circularity with this interceptor)
                val client = OkHttpClient.Builder().build()
                val refreshResponse = client.newCall(refreshRequest).execute()

                if (refreshResponse.isSuccessful) {
                    val body = refreshResponse.body?.string()
                    if (body != null) {
                        val json = JSONObject(body)
                        token = json.getString("accessToken")
                        refreshToken = json.getString("refreshToken")

                        // Retry original request with new token
                        val retryRequest = chain.request().newBuilder()
                            .header("Authorization", "Bearer $token")
                            .build()
                        return chain.proceed(retryRequest)
                    }
                }

                // Refresh failed — clear everything
                clear()
            } catch (_: Exception) {
                clear()
            }
        }

        return response
    }
}

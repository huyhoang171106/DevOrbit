package vn.edu.uit.devorbit.mobile.data.remote.interceptor

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import vn.edu.uit.devorbit.mobile.data.auth.TokenRefreshManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenRefreshManager: TokenRefreshManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var token = tokenRefreshManager.accessToken
        if (token.isNullOrBlank()) {
            runBlocking { tokenRefreshManager.init() }
            token = tokenRefreshManager.accessToken
        }

        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        val response = chain.proceed(request)

        // If 401 and we had a token, try refresh once
        if (response.code == 401 && token != null) {
            response.close()
            val refreshed = runBlocking { tokenRefreshManager.refresh() }
            if (refreshed) {
                val newToken = tokenRefreshManager.accessToken
                val retryRequest = if (!newToken.isNullOrBlank()) {
                    chain.request().newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                } else {
                    chain.request()
                }
                return chain.proceed(retryRequest)
            }
        }

        return response
    }
}

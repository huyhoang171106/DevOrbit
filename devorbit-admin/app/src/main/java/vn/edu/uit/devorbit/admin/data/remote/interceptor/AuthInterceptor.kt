package vn.edu.uit.devorbit.admin.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {

    @Volatile
    var token: String? = null
        private set

    fun updateToken(newToken: String?) {
        token = newToken
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
        return chain.proceed(request)
    }
}

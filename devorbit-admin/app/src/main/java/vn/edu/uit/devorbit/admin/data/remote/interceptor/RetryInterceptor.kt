package vn.edu.uit.devorbit.admin.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * OkHttp interceptor that retries idempotent GET/HEAD requests
 * on transient failures with bounded exponential backoff.
 */
class RetryInterceptor(
    private val maxRetries: Int = 2,
    private val baseDelayMs: Long = 500
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // Only retry idempotent read-only methods
        if (request.method !in setOf("GET", "HEAD")) {
            return chain.proceed(request)
        }

        var lastException: IOException? = null
        var attempt = 0

        while (attempt <= maxRetries) {
            try {
                val response = chain.proceed(request)
                // Retry on server errors (5xx)
                if (response.code in 500..599 && attempt < maxRetries) {
                    response.close()
                    val delayMs = baseDelayMs * (1 shl attempt)
                    Thread.sleep(delayMs)
                    attempt++
                    continue
                }
                return response
            } catch (e: IOException) {
                lastException = e
                if (attempt >= maxRetries) throw e
                val delayMs = baseDelayMs * (1 shl attempt)
                Thread.sleep(delayMs)
                attempt++
            }
        }

        throw lastException ?: IOException("Retry failed after $maxRetries attempts")
    }
}

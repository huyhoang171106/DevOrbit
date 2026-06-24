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
                // Retry on transient server errors (5xx, except non-retryable ones)
                if (response.code in 500..599 && response.code != 501 && response.code != 505 && attempt < maxRetries) {
                    response.close()
                    if (chain.call().isCanceled()) {
                        throw IOException("Canceled")
                    }
                    val delayMs = baseDelayMs * (1 shl attempt)
                    try {
                        Thread.sleep(delayMs)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw IOException("Retry sleep interrupted", e)
                    }
                    attempt++
                    continue
                }
                return response
            } catch (e: IOException) {
                lastException = e
                if (attempt >= maxRetries || chain.call().isCanceled()) throw e
                val delayMs = baseDelayMs * (1 shl attempt)
                try {
                    Thread.sleep(delayMs)
                } catch (ie: InterruptedException) {
                    Thread.currentThread().interrupt()
                    throw IOException("Retry sleep interrupted", ie)
                }
                attempt++
            }
        }

        throw lastException ?: IOException("Retry failed after $maxRetries attempts")
    }
}

package vn.edu.uit.devorbit.mobile.data.repository

import com.google.gson.JsonParser
import retrofit2.HttpException

object AuthErrorMessageParser {
    fun requireToken(response: Map<String, Any>, fallback: String): String {
        val token = response["token"] as? String
        if (!token.isNullOrBlank()) return token
        throw Exception((response["message"] as? String)?.ifBlank { null } ?: fallback)
    }

    fun exceptionFrom(error: Throwable, fallback: String): Exception {
        if (error is HttpException) {
            val body = error.response()?.errorBody()?.string()
            return Exception(messageFromErrorBody(body, fallback), error)
        }
        return if (error is Exception) error else Exception(error.message ?: fallback, error)
    }

    fun messageFromErrorBody(body: String?, fallback: String): String {
        if (body.isNullOrBlank()) return fallback
        return runCatching {
            JsonParser.parseString(body)
                .asJsonObject
                .get("message")
                ?.asString
                ?.ifBlank { null }
                ?: fallback
        }.getOrDefault(fallback)
    }
}

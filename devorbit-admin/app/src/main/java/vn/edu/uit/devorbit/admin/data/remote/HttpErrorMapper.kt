package vn.edu.uit.devorbit.admin.data.remote

import com.google.gson.Gson
import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Converts HTTP / network exceptions into user-friendly Vietnamese messages.
 * Priority:
 *  1. Translated known backend error messages (English → Vietnamese)
 *  2. Backend JSON response body (`error` or `detail` field)
 *  3. HTTP-status-code-specific Vietnamese message
 *  4. Exception-type-specific message (network errors, timeouts, …)
 *  5. Raw exception message as last resort
 */
object HttpErrorMapper {

    /** Known backend English error messages mapped to Vietnamese. */
    private val knownMessages = mapOf(
        "Invalid username or password"           to "Sai tên đăng nhập hoặc mật khẩu",
        "Account is deactivated"                 to "Tài khoản đã bị vô hiệu hoá",
        "Too many login attempts. Try again later." to "Quá nhiều lần đăng nhập thất bại, vui lòng thử lại sau",
        "Refresh token has already been used"    to "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại",
        "Invalid refresh token"                  to "Phiên đăng nhập không hợp lệ",
        "Not a refresh token"                    to "Phiên đăng nhập không hợp lệ",
        "Internal server error"                  to "Máy chủ gặp lỗi, vui lòng thử lại sau",
    )

    private val gson = Gson()

    fun toUserMessage(e: Exception): String {
        // ── Retrofit HTTP errors ──────────────────────────────────────
        if (e is HttpException) {
            // Try to extract and translate backend error JSON first
            val bodyMessage = parseErrorBody(e)
            if (bodyMessage != null) return bodyMessage
            // Fall back to status-code message
            return statusMessage(e.code())
        }

        // ── Network / connectivity errors ──────────────────────────────
        return when (e) {
            is SocketTimeoutException -> "Kết nối bị timeout, vui lòng thử lại sau"
            is ConnectException       -> "Không thể kết nối đến máy chủ"
            is UnknownHostException   -> "Không tìm thấy máy chủ, vui lòng kiểm tra kết nối mạng"
            is IOException            -> "Lỗi mạng, vui lòng kiểm tra kết nối và thử lại"
            else                      -> e.message ?: "Đã xảy ra lỗi không xác định"
        }
    }

    /**
     * Maps an HTTP status code to a user-friendly Vietnamese message.
     */
    private fun statusMessage(code: Int): String = when (code) {
        400 -> "Dữ liệu gửi lên không hợp lệ"
        401 -> "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại"
        403 -> "Bạn không có quyền thực hiện thao tác này"
        404 -> "Không tìm thấy dữ liệu yêu cầu"
        405 -> "Phương thức không được hỗ trợ"
        409 -> "Dữ liệu đã bị thay đổi, vui lòng tải lại và thử lại"
        413 -> "Dữ liệu tải lên quá lớn"
        422 -> "Dữ liệu gửi lên không hợp lệ"
        429 -> "Quá nhiều yêu cầu, vui lòng thử lại sau"
        500 -> "Máy chủ gặp lỗi, vui lòng thử lại sau"
        502 -> "Máy chủ tạm thời không hoạt động, vui lòng thử lại sau"
        503 -> "Dịch vụ tạm thời ngừng hoạt động, vui lòng thử lại sau"
        504 -> "Máy chủ quá thời gian phản hồi, vui lòng thử lại sau"
        else -> "Yêu cầu thất bại (mã lỗi $code)"
    }

    /**
     * Tries to parse the HTTP error body JSON for `error` or `detail` fields
     * returned by the backend's [ApiExceptionHandler].
     *
     * Returns `null` when the body is empty or not valid JSON.
     */
    private fun parseErrorBody(e: HttpException): String? {
        val bodyStr = e.response()?.errorBody()?.string() ?: return null
        if (bodyStr.isBlank()) return null
        return try {
            val obj = JsonParser.parseString(bodyStr).asJsonObject
            val error = obj.get("error")?.asString
            val detail = obj.get("detail")?.asString
            // Prefer known translated message, then backend text, then detail
            val raw = error ?: detail ?: return bodyStr.takeIf { it.length < 200 }
            knownMessages[raw] ?: raw
        } catch (_: Exception) {
            // Not JSON — return raw body only if short enough
            bodyStr.takeIf { it.length < 200 }
        }
    }
}

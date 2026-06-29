package vn.edu.uit.devorbit.mobile.data.repository

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import vn.edu.uit.devorbit.mobile.data.datastore.SettingsDataStore
import vn.edu.uit.devorbit.mobile.data.auth.TokenRefreshManager
import vn.edu.uit.devorbit.mobile.domain.repository.AuthRepository
import vn.edu.uit.devorbit.mobile.domain.repository.AuthResult
import vn.edu.uit.devorbit.mobile.domain.repository.StudentInfo
import vn.edu.uit.devorbit.mobile.network.ApiService
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService,
    private val settingsDataStore: SettingsDataStore,
    private val tokenRefreshManager: TokenRefreshManager
) : AuthRepository {
    private suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: HttpException) {
            val msg = try {
                val body = e.response()?.errorBody()?.string()
                if (body != null) {
                    JSONObject(body).optString("error", e.message ?: "Lỗi không xác định")
                } else {
                    e.message ?: "Lỗi không xác định"
                }
            } catch (_: Exception) {
                e.message ?: "Lỗi không xác định"
            }
            Result.failure(Exception(msg))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun register(
        studentCode: String, fullName: String, email: String,
        password: String
    ): Result<StudentInfo> = safeApiCall {
        val response = apiService.register(mapOf(
            "studentCode" to studentCode,
            "fullName" to fullName,
            "email" to email,
            "password" to password
        ))
        StudentInfo(
            studentCode = response["studentCode"] as? String ?: studentCode,
            fullName = response["fullName"] as? String ?: fullName,
            email = response["email"] as? String ?: email,
            active = false
        )
    }

    override suspend fun verifyOtp(email: String, otpCode: String): Result<AuthResult> = safeApiCall {
        val response = apiService.verifyOtp(mapOf("email" to email, "otpCode" to otpCode))
        val token = response["token"] as? String ?: throw Exception("OTP verification failed")
        val studentCode = response["studentCode"] as? String ?: ""
        val fullName = response["fullName"] as? String ?: ""
        val verifiedEmail = response["email"] as? String ?: email
        val refreshToken = response["refreshToken"] as? String ?: ""
        settingsDataStore.saveToken(token)
        if (refreshToken.isNotBlank()) settingsDataStore.saveRefreshToken(refreshToken)
        settingsDataStore.saveStudentName(fullName)
        settingsDataStore.saveStudentCode(studentCode)
        tokenRefreshManager.accessToken = token
        if (refreshToken.isNotBlank()) tokenRefreshManager.refreshToken = refreshToken
        AuthResult(token, studentCode, fullName, verifiedEmail, refreshToken)
    }

    override suspend fun login(studentCode: String, password: String): Result<AuthResult> = safeApiCall {
        val response = apiService.login(mapOf("studentCode" to studentCode, "password" to password))
        val token = response["token"] as? String ?: throw Exception("Login failed")
        val fullName = response["fullName"] as? String ?: ""
        val email = response["email"] as? String ?: ""
        val refreshToken = response["refreshToken"] as? String ?: ""
        settingsDataStore.saveToken(token)
        if (refreshToken.isNotBlank()) settingsDataStore.saveRefreshToken(refreshToken)
        settingsDataStore.saveStudentName(fullName)
        settingsDataStore.saveStudentCode(studentCode)
        tokenRefreshManager.accessToken = token
        if (refreshToken.isNotBlank()) tokenRefreshManager.refreshToken = refreshToken
        AuthResult(token, studentCode, fullName, email, refreshToken)
    }

    override suspend fun forgotPassword(studentCode: String): Result<Unit> = safeApiCall {
        apiService.forgotPassword(mapOf("studentCode" to studentCode))
        Unit
    }

    override suspend fun resetPassword(
        studentCode: String, otpCode: String, newPassword: String
    ): Result<AuthResult> = safeApiCall {
        val response = apiService.resetPassword(mapOf(
            "studentCode" to studentCode,
            "otpCode" to otpCode,
            "newPassword" to newPassword
        ))
        val token = response["token"] as? String ?: throw Exception("Reset password failed")
        val fullName = response["fullName"] as? String ?: ""
        val email = response["email"] as? String ?: ""
        val refreshToken = response["refreshToken"] as? String ?: ""
        settingsDataStore.saveToken(token)
        if (refreshToken.isNotBlank()) settingsDataStore.saveRefreshToken(refreshToken)
        settingsDataStore.saveStudentName(fullName)
        settingsDataStore.saveStudentCode(studentCode)
        tokenRefreshManager.accessToken = token
        if (refreshToken.isNotBlank()) tokenRefreshManager.refreshToken = refreshToken
        AuthResult(token, studentCode, fullName, email, refreshToken)
    }

    override suspend fun resendOtp(email: String, purpose: String?): Result<Unit> = safeApiCall {
        val body = mutableMapOf("email" to email)
        purpose?.let { body["purpose"] = it }
        apiService.resendOtp(body)
        Unit
    }

    override suspend fun getProfile(): Result<StudentInfo> = safeApiCall {
        val response = apiService.getStudentProfile()
        val id = response["id"] as? Number
        id?.let { settingsDataStore.saveStudentId(it.toInt()) }
        StudentInfo(
            studentCode = response["studentCode"] as? String ?: "",
            fullName = response["fullName"] as? String ?: "",
            email = response["email"] as? String ?: "",
            active = response["active"] as? Boolean ?: true,
            avatar = response["avatar"] as? String
        )
    }

    override suspend fun uploadAvatar(uri: Uri): Result<String> = safeApiCall {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open image")
        
        val tempFile = File.createTempFile("avatar_", ".jpg", context.cacheDir)
        tempFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        inputStream.close()

        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
        val multipart = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)

        val response = apiService.uploadAvatar(multipart)
        tempFile.delete()

        response["avatar"] as? String ?: throw Exception("No avatar URL in response")
    }

    override suspend fun updateFullName(fullName: String): Result<StudentInfo> = safeApiCall {
        val response = apiService.updateFullName(mapOf("fullName" to fullName))
        settingsDataStore.saveStudentName(fullName)
        StudentInfo(
            studentCode = response["studentCode"] as? String ?: "",
            fullName = response["fullName"] as? String ?: fullName,
            email = response["email"] as? String ?: "",
            active = response["active"] as? Boolean ?: true,
            avatar = response["avatar"] as? String
        )
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = safeApiCall {
        apiService.changePassword(mapOf(
            "currentPassword" to currentPassword,
            "newPassword" to newPassword
        ))
    }

    override suspend fun getToken(): String? {
        return settingsDataStore.token.first()
    }

    override suspend fun logout() {
        safeApiCall { apiService.studentLogout(); Unit }
        settingsDataStore.clearToken()
        settingsDataStore.clearRefreshToken()
        tokenRefreshManager.clear()
    }

    override suspend fun clearToken() {
        settingsDataStore.clearToken()
        settingsDataStore.clearRefreshToken()
        tokenRefreshManager.clear()
    }
}

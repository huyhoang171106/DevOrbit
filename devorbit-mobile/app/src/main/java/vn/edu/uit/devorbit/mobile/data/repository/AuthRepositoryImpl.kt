package vn.edu.uit.devorbit.mobile.data.repository

import kotlinx.coroutines.flow.first
import vn.edu.uit.devorbit.mobile.data.datastore.SettingsDataStore
import vn.edu.uit.devorbit.mobile.domain.repository.AuthRepository
import vn.edu.uit.devorbit.mobile.domain.repository.AuthResult
import vn.edu.uit.devorbit.mobile.domain.repository.StudentInfo
import vn.edu.uit.devorbit.mobile.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val settingsDataStore: SettingsDataStore
) : AuthRepository {

    override suspend fun register(
        studentCode: String, fullName: String, email: String,
        password: String
    ): Result<StudentInfo> = runCatching {
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

    override suspend fun verifyOtp(email: String, otpCode: String): Result<AuthResult> = runCatching {
        val response = apiService.verifyOtp(mapOf("email" to email, "otpCode" to otpCode))
        val token = response["token"] as? String ?: throw Exception("OTP verification failed")
        val studentCode = response["studentCode"] as? String ?: ""
        val fullName = response["fullName"] as? String ?: ""
        val verifiedEmail = response["email"] as? String ?: email
        settingsDataStore.saveToken(token)
        settingsDataStore.saveStudentName(fullName)
        settingsDataStore.saveStudentCode(studentCode)
        AuthResult(token, studentCode, fullName, verifiedEmail)
    }

    override suspend fun login(studentCode: String, password: String): Result<AuthResult> = runCatching {
        val response = apiService.login(mapOf("studentCode" to studentCode, "password" to password))
        val token = response["token"] as? String ?: throw Exception("Login failed")
        val fullName = response["fullName"] as? String ?: ""
        val email = response["email"] as? String ?: ""
        settingsDataStore.saveToken(token)
        settingsDataStore.saveStudentName(fullName)
        settingsDataStore.saveStudentCode(studentCode)
        AuthResult(token, studentCode, fullName, email)
    }

    override suspend fun forgotPassword(studentCode: String): Result<Unit> = runCatching {
        apiService.forgotPassword(mapOf("studentCode" to studentCode))
        Unit
    }

    override suspend fun resetPassword(
        studentCode: String, otpCode: String, newPassword: String
    ): Result<AuthResult> = runCatching {
        val response = apiService.resetPassword(mapOf(
            "studentCode" to studentCode,
            "otpCode" to otpCode,
            "newPassword" to newPassword
        ))
        val token = response["token"] as? String ?: throw Exception("Reset password failed")
        val fullName = response["fullName"] as? String ?: ""
        val email = response["email"] as? String ?: ""
        settingsDataStore.saveToken(token)
        settingsDataStore.saveStudentName(fullName)
        settingsDataStore.saveStudentCode(studentCode)
        AuthResult(token, studentCode, fullName, email)
    }

    override suspend fun resendOtp(email: String, purpose: String?): Result<Unit> = runCatching {
        val body = mutableMapOf("email" to email)
        purpose?.let { body["purpose"] = it }
        apiService.resendOtp(body)
        Unit
    }

    override suspend fun getProfile(): Result<StudentInfo> = runCatching {
        val response = apiService.getStudentProfile()
        StudentInfo(
            studentCode = response["studentCode"] as? String ?: "",
            fullName = response["fullName"] as? String ?: "",
            email = response["email"] as? String ?: "",
            active = response["active"] as? Boolean ?: true
        )
    }

    override suspend fun getToken(): String? {
        return settingsDataStore.token.first()
    }

    override suspend fun logout() {
        runCatching { apiService.studentLogout() }
        settingsDataStore.clearToken()
    }
}

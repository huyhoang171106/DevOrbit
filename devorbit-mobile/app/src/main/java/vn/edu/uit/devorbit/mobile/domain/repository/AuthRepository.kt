package vn.edu.uit.devorbit.mobile.domain.repository

import android.net.Uri

data class AuthResult(val token: String, val studentCode: String, val fullName: String, val email: String)
data class StudentInfo(val studentCode: String, val fullName: String, val email: String, val active: Boolean, val avatar: String? = null)

interface AuthRepository {
    suspend fun register(studentCode: String, fullName: String, email: String, password: String): Result<StudentInfo>
    suspend fun verifyOtp(email: String, otpCode: String): Result<AuthResult>
    suspend fun login(studentCode: String, password: String): Result<AuthResult>
    suspend fun forgotPassword(studentCode: String): Result<Unit>
    suspend fun resetPassword(studentCode: String, otpCode: String, newPassword: String): Result<AuthResult>
    suspend fun resendOtp(email: String, purpose: String?): Result<Unit>
    suspend fun getProfile(): Result<StudentInfo>
    suspend fun uploadAvatar(uri: Uri): Result<String>
    suspend fun updateFullName(fullName: String): Result<StudentInfo>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>
    suspend fun getToken(): String?
    suspend fun logout()
}

package vn.edu.uit.devorbit.mobile.domain.repository

data class AuthResult(val token: String, val studentCode: String, val fullName: String, val email: String)
data class StudentInfo(val studentCode: String, val fullName: String, val email: String, val active: Boolean)

interface AuthRepository {
    suspend fun register(studentCode: String, fullName: String, email: String, password: String): Result<AuthResult>
    suspend fun login(studentCode: String, password: String): Result<AuthResult>
    suspend fun getProfile(): Result<StudentInfo>
    suspend fun getToken(): String?
    suspend fun logout()
}

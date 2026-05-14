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
    ): Result<AuthResult> = runCatching {
        val response = apiService.register(mapOf(
            "studentCode" to studentCode,
            "fullName" to fullName,
            "email" to email,
            "password" to password
        ))
        val token = response["token"] as? String ?: ""
        settingsDataStore.saveToken(token)
        settingsDataStore.saveStudentName(fullName)
        settingsDataStore.saveStudentCode(studentCode)
        AuthResult(token, studentCode, fullName, email)
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
        settingsDataStore.clearToken()
    }
}

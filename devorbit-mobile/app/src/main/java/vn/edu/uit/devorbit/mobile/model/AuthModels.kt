package vn.edu.uit.devorbit.mobile.model

data class StudentLoginRequest(
    val studentCode: String,
    val password: String,
)

data class StudentAuthResponse(
    val token: String,
    val id: Long,
    val studentCode: String,
    val fullName: String,
    val email: String,
)
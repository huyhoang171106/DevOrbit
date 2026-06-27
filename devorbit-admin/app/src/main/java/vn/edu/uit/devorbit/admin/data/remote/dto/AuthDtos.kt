package vn.edu.uit.devorbit.admin.data.remote.dto

data class AdminLoginRequest(
    val username: String,
    val password: String
)

/** Backend returns only { token }. No username in response. */
data class AdminLoginResponse(
    val token: String
)

package vn.edu.uit.devorbit.admin.data.remote.dto

data class AdminLoginRequest(
    val username: String,
    val password: String
)

/** Backend returns token + refreshToken pair on login. */
data class AdminLoginResponse(
    val token: String,
    val refreshToken: String
)

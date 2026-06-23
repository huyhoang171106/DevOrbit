package vn.edu.uit.devorbit.admin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminStudentResponse(
    val id: Long,
    @SerializedName("studentCode") val studentCode: String,
    @SerializedName("fullName") val fullName: String,
    val email: String,
    val active: Boolean,
    @SerializedName("emailVerified") val emailVerified: Boolean = false
)

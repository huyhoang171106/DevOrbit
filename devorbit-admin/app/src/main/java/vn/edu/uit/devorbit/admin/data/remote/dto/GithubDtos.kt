package vn.edu.uit.devorbit.admin.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Backend requires courseId (non-null) and query (non-null). No limit field. */
data class GithubScanRequest(
    @SerializedName("courseId") val courseId: Long,
    val query: String
)

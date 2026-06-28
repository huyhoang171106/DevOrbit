package vn.edu.uit.devorbit.admin.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Backend requires courseId (non-null) and query (non-null). No limit field. */
data class GithubScanRequest(
    @SerializedName("courseId") val courseId: Long,
    val query: String
)

data class GithubAutomationStatus(
    val enabled: Boolean = false,
    val cron: String = "",
    val zone: String = "UTC",
    val description: String = ""
)

data class AutoApprovalRun(
    val checked: Int = 0,
    val approved: Int = 0,
    val leftForManualReview: Int = 0
)

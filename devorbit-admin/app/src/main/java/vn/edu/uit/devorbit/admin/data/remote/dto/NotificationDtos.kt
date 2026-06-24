package vn.edu.uit.devorbit.admin.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Backend has no "title" field. Has "message", "type", "targetUrl", "isRead". */
data class NotificationResponse(
    val id: Long,
    val type: String? = null,
    val message: String? = null,
    @SerializedName("targetUrl") val targetUrl: String? = null,
    @SerializedName("isRead") val isRead: Boolean = false,
    @SerializedName("createdAt") val createdAt: String? = null
)

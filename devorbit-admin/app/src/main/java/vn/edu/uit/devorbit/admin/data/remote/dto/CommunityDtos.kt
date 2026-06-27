package vn.edu.uit.devorbit.admin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatSessionAdminResponse(
    val id: String,                   // UUID serialized as String by Gson
    @SerializedName("studentName") val studentName: String,
    val title: String? = null,
    @SerializedName("messageCount") val messageCount: Long = 0,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class ChatMessageAdminResponse(
    val id: Long,
    val sender: String,
    val content: String,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class CommunityMessageAdminResponse(
    val id: Long,
    @SerializedName("channelName") val channelName: String?,
    @SerializedName("studentName") val studentName: String,
    val content: String,
    @SerializedName("createdAt") val createdAt: String? = null
)

/** Backend returns the ChatChannel entity directly (not a DTO). */
data class ChatChannel(
    val id: Long,
    @SerializedName("channelId") val channelId: String,
    val name: String,
    val type: String,                 // "GENERAL", "COURSE", "TECH_STACK"
    @SerializedName("referenceId") val referenceId: String? = null,
    val active: Boolean = true,
    @SerializedName("createdAt") val createdAt: String? = null
)

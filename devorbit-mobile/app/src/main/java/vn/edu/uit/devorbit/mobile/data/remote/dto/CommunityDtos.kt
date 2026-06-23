package vn.edu.uit.devorbit.mobile.data.remote.dto

data class ChatChannelResponse(
    val id: Long,
    val channelId: String,
    val name: String,
    val type: String,
    val referenceId: String?
)

data class ChatMessageResponse(
    val id: Long,
    val channelId: Long,
    val studentId: Long,
    val senderName: String,
    val senderAvatar: String?,
    val content: String,
    val createdAt: String,
    val deleted: Boolean = false
)

data class OnlineMemberResponse(
    val studentId: Long?,
    val studentCode: String,
    val displayName: String,
    val avatar: String?
)

data class ChannelPresenceResponse(
    val channelId: Long,
    val members: List<OnlineMemberResponse>
)

data class PaginatedMessagesResponse(
    val content: List<ChatMessageResponse>,
    val totalPages: Int,
    val totalElements: Long,
    val number: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean
)

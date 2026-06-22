package vn.edu.uit.devorbit.mobile.data.remote.dto

data class StudentNotificationResponse(
    val id: Long,
    val studentCode: String,
    val title: String,
    val body: String,
    val type: String,
    val repoId: Long?,
    val courseId: Long?,
    val groupPlanId: Long?,
    val techStackName: String?,
    val isRead: Boolean,
    val createdAt: String,
    val readAt: String?
)

data class UnreadCountResponse(
    val count: Int
)

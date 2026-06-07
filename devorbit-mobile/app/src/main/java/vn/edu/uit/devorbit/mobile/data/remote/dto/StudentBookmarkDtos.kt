package vn.edu.uit.devorbit.mobile.data.remote.dto

data class StudentBookmarkRequest(
    val targetType: String,
    val targetId: Long,
    val title: String,
    val subtitle: String? = null,
    val url: String
)

data class StudentBookmarkResponse(
    val id: Long,
    val targetType: String,
    val targetId: Long,
    val title: String,
    val subtitle: String? = null,
    val url: String? = null,
    val createdAt: String? = null
)

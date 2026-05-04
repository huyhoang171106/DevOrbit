package vn.edu.uit.devorbit.mobile.model

data class StudentLoginRequest(
    val studentCode: String,
    val password: String,
)

data class StudentRegisterRequest(
    val studentCode: String,
    val fullName: String,
    val email: String,
    val password: String,
)

data class StudentAuthResponse(
    val token: String,
    val id: Long,
    val studentCode: String,
    val fullName: String,
    val email: String,
)

enum class BookmarkTargetType { COURSE, REPO }

data class BookmarkRequest(
    val targetType: BookmarkTargetType,
    val targetId: Long,
)

data class BookmarkResponse(
    val id: Long,
    val targetType: BookmarkTargetType,
    val targetId: Long,
    val title: String,
    val subtitle: String?,
    val url: String,
    val createdAt: String,
)

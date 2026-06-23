package vn.edu.uit.devorbit.mobile.data.remote.dto

data class GroupPlanResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val creatorStudentCode: String,
    val deadline: String?,
    val active: Boolean,
    val createdAt: String?,
    val deleteRequested: Boolean = false,
    val deleteRequestedBy: String? = null
)

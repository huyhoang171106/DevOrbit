package vn.edu.uit.devorbit.mobile.data.remote.dto

data class GroupPlanMemberResponse(
    val id: Long,
    val groupPlanId: Long,
    val studentCode: String,
    val status: String,
    val invitedAt: String?,
    val respondedAt: String?
)

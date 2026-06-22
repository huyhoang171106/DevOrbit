package vn.edu.uit.devorbit.mobile.data.remote.dto

data class CreateGroupPlanRequest(
    val title: String,
    val description: String?,
    val deadline: String?
)

data class InviteMemberRequest(
    val studentCode: String
)

data class RespondInviteRequest(
    val action: String
)

data class AddGroupTaskRequest(
    val title: String,
    val description: String?,
    val assignedTo: String?,
    val deadline: String?
)

data class UpdateGroupTaskRequest(
    val title: String?,
    val description: String?,
    val assignedTo: String?,
    val deadline: String?,
    val completed: Boolean?
)

data class ApproveDeleteRequest(
    val action: String
)

package vn.edu.uit.devorbit.mobile.data.remote.dto

data class GroupTaskResponse(
    val id: Long,
    val groupPlanId: Long,
    val planTitle: String?,
    val title: String,
    val description: String?,
    val assignedTo: String?,
    val deadline: String?,
    val completed: Boolean,
    val createdBy: String,
    val deleteRequested: Boolean,
    val deleteRequestedBy: String?,
    val createdAt: String?,
    val updatedAt: String?
)

package vn.edu.uit.devorbit.mobile.data.remote.dto

data class PersonalTaskResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val deadline: String?,
    val completed: Boolean,
    val recurrence: String?,
    val recurrenceDaysOfWeek: Int?,
    val recurrenceStartDate: String?,
    val recurrenceEndDate: String?,
    val createdAt: String?,
    val updatedAt: String?
)

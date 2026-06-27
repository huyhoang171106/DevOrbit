package vn.edu.uit.devorbit.mobile.data.remote.dto

data class UpdatePersonalTaskRequest(
    val title: String? = null,
    val description: String? = null,
    val deadline: String? = null,
    val completed: Boolean? = null,
    val recurrence: String? = null,
    val recurrenceDaysOfWeek: Int? = null,
    val recurrenceStartDate: String? = null,
    val recurrenceEndDate: String? = null
)

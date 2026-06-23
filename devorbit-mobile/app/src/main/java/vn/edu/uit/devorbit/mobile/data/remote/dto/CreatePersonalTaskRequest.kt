package vn.edu.uit.devorbit.mobile.data.remote.dto

data class CreatePersonalTaskRequest(
    val title: String,
    val description: String? = null,
    val deadline: String? = null,
    val recurrence: String? = null,
    val recurrenceDaysOfWeek: Int? = null,
    val recurrenceStartDate: String? = null,
    val recurrenceEndDate: String? = null
)

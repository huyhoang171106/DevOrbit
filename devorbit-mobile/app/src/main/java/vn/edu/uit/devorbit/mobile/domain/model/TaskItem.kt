package vn.edu.uit.devorbit.mobile.domain.model

import vn.edu.uit.devorbit.mobile.data.remote.dto.PersonalTaskResponse
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class TaskItem(
    val id: Long,
    val title: String,
    val description: String,
    val deadline: Long?,
    val completed: Boolean,
    val recurrence: String?,
    val recurrenceDaysOfWeek: Int?,
    val recurrenceStartDate: Long?,
    val recurrenceEndDate: Long?,
    val createdAt: Long
)

fun PersonalTaskResponse.toTaskItem(): TaskItem {
    return TaskItem(
        id = id,
        title = title,
        description = description ?: "",
        deadline = deadline?.let { parseIsoToMillis(it) },
        completed = completed,
        recurrence = recurrence,
        recurrenceDaysOfWeek = recurrenceDaysOfWeek,
        recurrenceStartDate = recurrenceStartDate?.let { parseIsoDateToMillis(it) },
        recurrenceEndDate = recurrenceEndDate?.let { parseIsoDateToMillis(it) },
        createdAt = createdAt?.let { parseIsoToMillis(it) } ?: System.currentTimeMillis()
    )
}

fun parseIsoToMillis(iso: String): Long {
    return LocalDateTime.parse(iso, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

fun millisToIso(millis: Long?): String? {
    return millis?.let {
        Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}

fun parseIsoDateOnly(iso: String): String {
    return iso.substringBefore("T")
}

fun parseIsoDateToMillis(iso: String): Long {
    return LocalDate.parse(iso)
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

package vn.edu.uit.devorbit.mobile.model.domain

data class SemesterTimeline(
    val semesterName: String,
    val startDate: Long,
    val endDate: Long,
    val weeks: List<TimelineWeek>,
    val milestones: List<Milestone> = emptyList()
)

data class TimelineWeek(
    val weekNumber: Int,
    val label: String,
    val events: List<TimelineEvent>
)

data class TimelineEvent(
    val title: String,
    val date: Long,
    val type: EventType,
    val subjectName: String = ""
)

enum class EventType { EXAM, ASSIGNMENT, DEADLINE, BREAK, MILESTONE }

data class Milestone(
    val title: String,
    val date: Long,
    val description: String = ""
)

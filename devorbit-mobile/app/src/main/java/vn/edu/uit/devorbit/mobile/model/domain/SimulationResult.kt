package vn.edu.uit.devorbit.mobile.model.domain

data class SimulationResult(
    val failedSubjectId: Long,
    val failedSubjectName: String,
    val blockedSubjects: List<BlockedSubject>,
    val graduationDelay: Int = 0,
    val timelineImpact: List<TimelineEntry>
)

data class BlockedSubject(
    val id: Long,
    val name: String,
    val reason: String
)

data class TimelineEntry(
    val semesterName: String,
    val subjects: List<String>,
    val note: String = ""
)

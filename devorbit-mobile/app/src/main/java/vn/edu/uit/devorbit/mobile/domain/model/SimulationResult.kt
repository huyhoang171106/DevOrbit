package vn.edu.uit.devorbit.mobile.domain.model

data class BlockedSubject(
    val id: Long,
    val name: String,
    val reason: String
)

data class TimelineEntry(
    val semesterName: String,
    val subjects: List<String> = emptyList(),
    val note: String = ""
)

data class SimulationResult(
    val failedSubjectId: Long,
    val failedSubjectName: String,
    val blockedSubjects: List<BlockedSubject> = emptyList(),
    val graduationDelay: Int = 0,
    val timelineImpact: List<TimelineEntry> = emptyList()
)

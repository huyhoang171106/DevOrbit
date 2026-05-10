package vn.edu.uit.devorbit.mobile.model.domain

data class WorkloadProfile(
    val weeks: List<WeekWorkload>,
    val peakWeeks: List<Int>,
    val overallStatus: WorkloadStatus
)

data class WeekWorkload(
    val weekNumber: Int,
    val label: String = "",
    val totalHours: Double,
    val taskCount: Int = 0,
    val deadlineIds: List<String> = emptyList()
)

enum class WorkloadStatus { UNDERLOADED, BALANCED, HEAVY, CRITICAL }

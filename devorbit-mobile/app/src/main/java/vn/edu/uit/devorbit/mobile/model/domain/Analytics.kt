package vn.edu.uit.devorbit.mobile.model.domain

data class StudyAnalytics(
    val consistencyScore: Double,
    val strongestSubjects: List<String>,
    val weakestSubjects: List<String>,
    val procrastinationPattern: ProcrastinationPattern,
    val focusDuration: FocusStats,
    val studyEfficiency: Double
)

data class ProcrastinationPattern(
    val score: Double,
    val peakDelayHours: Double,
    val commonTriggers: List<String> = emptyList()
)

data class FocusStats(
    val averageMinutes: Int,
    val bestSessionMinutes: Int,
    val dailyBreakdown: List<DailyFocus> = emptyList()
)

data class DailyFocus(
    val date: Long,
    val focusMinutes: Int,
    val sessions: Int
)

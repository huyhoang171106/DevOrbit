package vn.edu.uit.devorbit.mobile.domain.model

data class StudyPlan(
    val id: String = "",
    val title: String,
    val phases: List<StudyPhase>,
    val totalHours: Double = 0.0,
    val generatedAt: Long = System.currentTimeMillis()
)

data class StudyPhase(
    val title: String,
    val items: List<StudyItem>,
    val startDay: Int,
    val endDay: Int
)

data class StudyItem(
    val id: String = "",
    val title: String,
    val subjectId: Long,
    val estimatedHours: Double,
    val difficulty: String = "medium",
    val completed: Boolean = false,
    val prerequisiteIds: List<String> = emptyList()
)

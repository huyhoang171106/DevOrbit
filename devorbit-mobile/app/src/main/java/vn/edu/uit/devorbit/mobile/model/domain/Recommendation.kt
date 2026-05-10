package vn.edu.uit.devorbit.mobile.model.domain

data class StudyRecommendation(
    val id: String = "",
    val title: String,
    val description: String,
    val priority: Int,
    val category: RecommendationCategory,
    val relatedSubjectId: Long? = null
)

enum class RecommendationCategory {
    PREREQUISITE_REVIEW,
    FOCUS_SUBJECT,
    TIME_MANAGEMENT,
    WORKLOAD_REDUCTION,
    CONSISTENCY_IMPROVEMENT,
    BREAK_SUGGESTION
}

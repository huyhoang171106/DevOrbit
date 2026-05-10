package vn.edu.uit.devorbit.mobile.model.domain

data class GpaImpact(
    val currentGpa: Double = 0.0,
    val forecastGpa: Double = 0.0,
    val bySubject: List<SubjectGpaImpact> = emptyList(),
    val recommendations: List<String> = emptyList()
)

data class SubjectGpaImpact(
    val subjectId: Long,
    val subjectName: String,
    val credits: Int,
    val currentScore: Double? = null,
    val weight: Double = 0.0,
    val potentialDrop: Double = 0.0
)

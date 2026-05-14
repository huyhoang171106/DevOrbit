package vn.edu.uit.devorbit.mobile.domain.model

data class AcademicHealth(
    val score: Double = 0.0,
    val recommendations: List<StudyRecommendation> = emptyList(),
    val nextAction: String = ""
)

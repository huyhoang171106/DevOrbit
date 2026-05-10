package vn.edu.uit.devorbit.mobile.model.domain

data class AcademicTwin(
    val projectedGpa: Double,
    val graduationYear: Int,
    val onTrack: Boolean,
    val overloadProbability: Double,
    val academicHealthScore: Double,
    val insights: List<String>
)

data class AcademicHealth(
    val score: Double,
    val categories: List<HealthCategory>
)

data class HealthCategory(
    val name: String,
    val score: Double,
    val maxScore: Double
)

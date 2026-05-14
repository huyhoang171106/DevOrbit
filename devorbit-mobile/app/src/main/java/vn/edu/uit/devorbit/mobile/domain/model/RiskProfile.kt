package vn.edu.uit.devorbit.mobile.domain.model

data class RiskProfile(
    val subjectRisks: List<SubjectRisk> = emptyList(),
    val overallRisk: RiskLevel = RiskLevel.LOW,
    val riskFactors: List<RiskFactor> = emptyList(),
    val academicRisk: Double = 0.0,
    val healthRisk: Double = 0.0,
    val consistencyRisk: Double = 0.0,
    val socialRisk: Double = 0.0,
    val mentalRisk: Double = 0.0
)

data class SubjectRisk(
    val subjectId: Long,
    val subjectName: String,
    val riskLevel: RiskLevel,
    val weakPrerequisites: List<String>,
    val overdueTasks: Int = 0,
    val consistency: Double = 1.0
)

enum class RiskLevel { LOW, MEDIUM, HIGH, CRITICAL }

data class RiskFactor(
    val type: String,
    val description: String,
    val severity: RiskLevel
)

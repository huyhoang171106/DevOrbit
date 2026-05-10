package vn.edu.uit.devorbit.mobile.model.domain

data class BurnoutStatus(
    val riskLevel: BurnoutRisk,
    val indicators: List<BurnoutIndicator>,
    val recommendation: String = ""
)

enum class BurnoutRisk { NONE, LOW, MODERATE, HIGH }

data class BurnoutIndicator(
    val name: String,
    val value: Double,
    val threshold: Double,
    val triggered: Boolean
)

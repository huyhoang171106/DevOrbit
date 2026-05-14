package vn.edu.uit.devorbit.mobile.domain.model

data class TaskBreakdown(
    val goal: String,
    val steps: List<BreakdownStep>,
    val totalEstimatedMinutes: Int = 0
)

data class BreakdownStep(
    val id: String = "",
    val title: String,
    val estimatedMinutes: Int,
    val difficulty: String = "medium",
    val isNextAction: Boolean = false
)

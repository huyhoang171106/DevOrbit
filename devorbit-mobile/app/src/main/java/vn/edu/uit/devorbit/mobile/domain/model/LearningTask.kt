package vn.edu.uit.devorbit.mobile.domain.model

data class LearningTask(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val subjectId: Long? = null,
    val estimatedMinutes: Int = 0,
    val achievedMinutes: Int = 0,
    val deadline: Long? = null,
    val priority: Int = 0,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val taskType: String = "general",
    val parentTaskId: Long? = null,
    val relatedUrl: String? = null
)

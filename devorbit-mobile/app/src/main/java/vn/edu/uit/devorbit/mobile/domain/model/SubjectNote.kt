package vn.edu.uit.devorbit.mobile.domain.model

data class SubjectNote(
    val id: Long = 0,
    val title: String,
    val contentMarkdown: String = "",
    val targetType: String = "NONE",
    val targetId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

package vn.edu.uit.devorbit.admin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NoteResponse(
    val id: Long,
    @SerializedName("studentId") val studentId: Long,
    @SerializedName("studentCode") val studentCode: String,
    @SerializedName("studentName") val studentName: String,
    val title: String?,
    @SerializedName("contentMarkdown") val contentMarkdown: String?,
    @SerializedName("targetType") val targetType: String?,  // COURSE, REPO, NONE
    @SerializedName("targetId") val targetId: Long? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    val snippets: List<NoteCodeSnippet> = emptyList()
)

data class NoteCodeSnippet(
    val id: Long = 0,
    @SerializedName("noteId") val noteId: Long = 0,
    val language: String? = null,
    val code: String,
    val caption: String? = null,
    @SerializedName("sortOrder") val sortOrder: Int = 0
)

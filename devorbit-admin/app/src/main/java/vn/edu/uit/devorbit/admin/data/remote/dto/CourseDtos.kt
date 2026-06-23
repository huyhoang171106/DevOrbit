package vn.edu.uit.devorbit.admin.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Summary (list view) ──────────────────────────────────────────────────────
data class CourseSummaryResponse(
    val id: Long,
    val code: String?,
    val name: String?,                // Backend field: "name" (Vietnamese)
    val description: String? = null,
    @SerializedName("repoCount") val repoCount: Long = 0,
    val semester: Int? = null,
    val credits: Int = 0,             // Backend field: "credits" (not tinChi)
    @SerializedName("loaiMonHoc") val loaiMonHoc: String? = null,
    val managementUnit: String? = null
)

// ── Detail ───────────────────────────────────────────────────────────────────
data class CourseDetailResponse(
    val id: Long,
    val code: String?,
    val name: String?,                // "name" not "tenMH"
    @SerializedName("nameEn") val nameEn: String? = null,
    val description: String? = null,  // "description" not "moTa"
    @SerializedName("theoryHours") val theoryHours: Int? = null,
    @SerializedName("practiceHours") val practiceHours: Int? = null,
    val credits: Int = 0,
    @SerializedName("subjectType") val subjectType: String? = null,
    @SerializedName("isOpen") val isOpen: Boolean = true,
    val managementUnit: String? = null,
    @SerializedName("codeOld") val codeOld: String? = null,
    @SerializedName("equivalentMH") val equivalentMH: String? = null,
    @SerializedName("prerequisiteMH") val prerequisiteMH: String? = null,
    @SerializedName("previousMH") val previousMH: String? = null,
    @SerializedName("learningObjectives") val learningObjectives: String? = null,
    @SerializedName("gradingCriteria") val gradingCriteria: String? = null,
    val repos: List<RepoSummaryResponse> = emptyList()
)

// ── Upsert Request ───────────────────────────────────────────────────────────
data class AdminCourseUpsertRequest(
    val code: String,                 // Required: "SE101"
    val name: String,                 // Required: Vietnamese name
    @SerializedName("nameEn") val nameEn: String? = null,
    val credits: Int,                 // Required: credit hours
    @SerializedName("lectureHours") val lectureHours: Int? = null,
    @SerializedName("practiceHours") val practiceHours: Int? = null,
    @SerializedName("subjectType") val subjectType: String,  // Required: DAI_CUONG/CHUYEN_NGANH/CO_SO
    @SerializedName("isOpen") val isOpen: Boolean = true,
    val managementUnit: String? = null,
    @SerializedName("codeOld") val codeOld: String? = null,
    @SerializedName("equivalentMH") val equivalentMH: String? = null,
    @SerializedName("prerequisiteMH") val prerequisiteMH: String? = null,
    @SerializedName("previousMH") val previousMH: String? = null,
    val description: String? = null,
    @SerializedName("learningObjectives") val learningObjectives: String? = null,
    @SerializedName("gradingCriteria") val gradingCriteria: String? = null
)

// ── Resources (from separate endpoints, not nested in detail) ────────────────

// YouTube Playlists (replaces old VideoItem)
data class YoutubePlaylistResponse(
    val id: Long,
    @SerializedName("courseId") val courseId: Long,
    val title: String,
    val url: String,
    val description: String? = null,
    @SerializedName("channelName") val channelName: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class YoutubePlaylistRequest(
    val title: String,
    val url: String,
    val description: String? = null,
    @SerializedName("channelName") val channelName: String? = null
)

// Tutorials
data class TutorialItem(
    val id: Long,
    @SerializedName("courseId") val courseId: Long,
    val title: String,
    val url: String,
    val type: String? = null,
    val description: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class TutorialRequest(
    val title: String,
    val url: String,
    val type: String? = null,
    val description: String? = null
)

// Articles
data class ArticleItem(
    val id: Long,
    @SerializedName("courseId") val courseId: Long,
    val title: String,
    val url: String,
    val author: String? = null,
    val description: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class ArticleRequest(
    val title: String,
    val url: String,
    val author: String? = null,
    val description: String? = null
)

// ── Course Relationships ─────────────────────────────────────────────────────
data class CourseRelationshipResponse(
    val id: Long,
    @SerializedName("courseId") val courseId: Long,
    @SerializedName("courseCode") val courseCode: String?,
    @SerializedName("courseName") val courseName: String?,
    @SerializedName("courseNameEn") val courseNameEn: String? = null,
    @SerializedName("relatedCourseId") val relatedCourseId: Long,
    @SerializedName("relatedCourseCode") val relatedCourseCode: String?,
    @SerializedName("relatedCourseName") val relatedCourseName: String?,
    @SerializedName("relatedCourseNameEn") val relatedCourseNameEn: String? = null,
    @SerializedName("relationType") val relationType: String,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class CourseRelationshipRequest(
    @SerializedName("courseId") val courseId: Long,
    @SerializedName("relatedCourseId") val relatedCourseId: Long,
    @SerializedName("relationType") val relationType: String
)

// ── Delete Result ────────────────────────────────────────────────────────────
data class CourseDeleteResult(
    @SerializedName("channelDeactivated") val channelDeactivated: Boolean = false
)

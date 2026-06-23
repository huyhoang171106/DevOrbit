package vn.edu.uit.devorbit.admin.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Tech Stack (wrapper for name) ────────────────────────────────────────────
data class TechStackResponse(
    val name: String
)

// ── Repo Summary (approved repos) ────────────────────────────────────────────
data class RepoSummaryResponse(
    val id: Long,
    @SerializedName("displayName") val displayName: String,
    val description: String? = null,
    @SerializedName("githubUrl") val githubUrl: String,
    @SerializedName("primaryLanguage") val primaryLanguage: String? = null,
    val stars: Int? = null,
    @SerializedName("techStacks") val techStacks: List<TechStackResponse> = emptyList(),
    @SerializedName("courseId") val courseId: Long? = null,
    @SerializedName("courseCode") val courseCode: String? = null,
    @SerializedName("courseName") val courseName: String? = null,
    @SerializedName("readmeExcerpt") val readmeExcerpt: String? = null,
    @SerializedName("fileTree") val fileTree: String? = null,
    @SerializedName("hasReadme") val hasReadme: Boolean? = null,
    @SerializedName("lastPushedAt") val lastPushedAt: String? = null,
    @SerializedName("approvedAt") val approvedAt: String? = null,
    @SerializedName("repoType") val repoType: String? = null,
    @SerializedName("usefulnessRating") val usefulnessRating: String? = null,
    @SerializedName("usefulnessScore") val usefulnessScore: Int? = null,
    @SerializedName("readyToUseLevel") val readyToUseLevel: String? = null,
    @SerializedName("reviewCount") val reviewCount: Int? = null,
    @SerializedName("averageRating") val averageRating: Double? = null
)

// ── Repo Update Request ──────────────────────────────────────────────────────
data class ApprovedRepoUpdateRequest(
    @SerializedName("displayName") val displayName: String? = null,
    val description: String? = null,
    @SerializedName("githubUrl") val githubUrl: String? = null,
    @SerializedName("primaryLanguage") val primaryLanguage: String? = null,
    val stars: Int? = null,
    @SerializedName("techStacks") val techStacks: List<String>? = null,
    val active: Boolean? = null,
    @SerializedName("courseId") val courseId: Long? = null
)

// ── Repo Candidate ───────────────────────────────────────────────────────────
data class RepoCandidateResponse(
    val id: Long,
    @SerializedName("githubOwner") val githubOwner: String? = null,
    @SerializedName("githubName") val githubName: String? = null,
    @SerializedName("githubUrl") val githubUrl: String? = null,
    val status: String? = null,
    val description: String? = null,
    @SerializedName("primaryLanguage") val primaryLanguage: String? = null,
    val topics: String? = null,        // Comma-separated on backend
    val stars: Int = 0,
    val forks: Int = 0,
    @SerializedName("lastPushedAt") val lastPushedAt: String? = null,
    @SerializedName("readmeExcerpt") val readmeExcerpt: String? = null,
    @SerializedName("hasReadme") val hasReadme: Boolean? = null,
    @SerializedName("fileTree") val fileTree: String? = null,
    @SerializedName("assignedReviewer") val assignedReviewer: String? = null,
    @SerializedName("courseId") val courseId: Long? = null,
    @SerializedName("courseCode") val courseCode: String? = null,
    @SerializedName("courseName") val courseName: String? = null,
    @SerializedName("reviewNote") val reviewNote: String? = null,
    @SerializedName("approvedAt") val approvedAt: String? = null
)

// ── Candidate Review Request ─────────────────────────────────────────────────
data class CandidateReviewRequest(
    val description: String? = null,
    @SerializedName("techStacks") val techStacks: List<String>? = null,
    @SerializedName("reviewNote") val reviewNote: String? = null
)

// ── Reviewer Stats ───────────────────────────────────────────────────────────
data class ReviewerStatsResponse(
    val reviewer: String?,
    val remaining: Long = 0,
    val completed: Long = 0
)

// ── Reviews ──────────────────────────────────────────────────────────────────
data class RepoReviewAdminResponse(
    val id: Long,
    @SerializedName("studentName") val studentName: String,
    @SerializedName("repoName") val repoName: String,
    val rating: Int? = null,
    val comment: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class CourseReviewAdminResponse(
    val id: Long,
    @SerializedName("studentName") val studentName: String,
    @SerializedName("courseName") val courseName: String,
    val rating: Int? = null,
    val comment: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

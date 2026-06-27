package vn.edu.uit.devorbit.admin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AdminStatsResponse(
    @SerializedName("totalStudents") val totalStudents: Long = 0,
    @SerializedName("totalCourses") val totalCourses: Long = 0,
    @SerializedName("totalRepos") val totalRepos: Long = 0,
    @SerializedName("pendingCandidates") val pendingCandidates: Long = 0,
    @SerializedName("recentStudents") val recentStudents: List<StudentSummary> = emptyList(),
    @SerializedName("recentCourseReviews") val recentCourseReviews: List<ReviewSummary> = emptyList(),
    @SerializedName("recentSubmissions") val recentSubmissions: List<SubmissionSummary> = emptyList(),
    @SerializedName("topFavoritedRepos") val topFavoritedRepos: List<RepoStatsEntry> = emptyList(),
    @SerializedName("topViewedRepos") val topViewedRepos: List<RepoStatsEntry> = emptyList()
)

/** Backend: only id, fullName, studentCode — no email, active, or registeredAt. */
data class StudentSummary(
    val id: Long,
    val fullName: String,
    val studentCode: String
)

/** Backend: id, studentName, courseName, rating, comment, createdAt. */
data class ReviewSummary(
    val id: Long,
    @SerializedName("studentName") val studentName: String,
    @SerializedName("courseName") val courseName: String,
    val rating: Int,
    val comment: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

/** Backend: id, githubUrl, courseName, status — not repoName/studentName/repoUrl. */
data class SubmissionSummary(
    val id: Long,
    @SerializedName("githubUrl") val githubUrl: String,
    @SerializedName("courseName") val courseName: String,
    val status: String
)

/** Backend: repoId, repoName, githubUrl, courseName, primaryLanguage, count, bookmarkCount, averageRating, reviewCount, voteScore, viewCount, popularityScore. */
data class RepoStatsEntry(
    @SerializedName("repoId") val repoId: Long,
    @SerializedName("repoName") val repoName: String,
    @SerializedName("githubUrl") val githubUrl: String? = null,
    @SerializedName("courseName") val courseName: String? = null,
    @SerializedName("primaryLanguage") val primaryLanguage: String? = null,
    val count: Long = 0,
    @SerializedName("bookmarkCount") val bookmarkCount: Long = 0,
    @SerializedName("averageRating") val averageRating: Double = 0.0,
    @SerializedName("reviewCount") val reviewCount: Long = 0,
    @SerializedName("voteScore") val voteScore: Long = 0,
    @SerializedName("viewCount") val viewCount: Long = 0,
    @SerializedName("popularityScore") val popularityScore: Double = 0.0
)

package vn.edu.uit.devorbit.mobile.data.remote.dto

data class ReviewResponse(
    val id: Long,
    val targetId: Long,
    val studentId: Long,
    val studentName: String,
    val rating: Int,
    val comment: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class RepoSocialInfoResponse(
    val repoId: Long,
    val voteScore: Int,
    val averageRating: Double,
    val reviews: List<ReviewResponse>
)

data class ReviewSummaryResponse(
    val targetId: Long,
    val averageRating: Double,
    val reviews: List<ReviewResponse>
)

data class ReviewRequest(
    val rating: Int,
    val comment: String?
)

data class RepoVoteRequest(
    val voteValue: Int
)

data class RepoVoteResponse(
    val repoId: Long,
    val studentId: Long,
    val voteValue: Int,
    val voteScore: Int
)

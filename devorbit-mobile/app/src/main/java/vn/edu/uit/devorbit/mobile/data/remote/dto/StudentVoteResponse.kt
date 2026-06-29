package vn.edu.uit.devorbit.mobile.data.remote.dto

data class StudentVoteResponse(
    val repoId: Long,
    val repoName: String,
    val githubUrl: String?,
    val voteValue: Int,
    val createdAt: String?
)

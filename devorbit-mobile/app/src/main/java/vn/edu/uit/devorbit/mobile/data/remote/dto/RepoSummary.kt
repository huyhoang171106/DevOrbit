package vn.edu.uit.devorbit.mobile.data.remote.dto

data class RepoSummary(
    val id: Long,
    val displayName: String,
    val description: String,
    val githubUrl: String,
    val primaryLanguage: String,
    val stars: Int? = null,
    val techStacks: List<TechStack> = emptyList()
)

package vn.edu.uit.devorbit.mobile.data.remote.dto

data class RepoSummary(
    val id: Long,
    val displayName: String,
    val description: String? = null,
    val githubUrl: String? = null,
    val primaryLanguage: String? = null,
    val stars: Int? = null,
    val techStacks: List<TechStack>? = emptyList(),
    val courseId: Long? = null,
    val courseCode: String? = null,
    val courseName: String? = null,
    val readmeExcerpt: String? = null,
    val fileTree: String? = null,
    val hasReadme: Boolean? = null,
    val lastPushedAt: String? = null
) {
    val safeDescription: String
        get() = description.orEmpty()

    val safeGithubUrl: String
        get() = githubUrl.orEmpty()

    val safePrimaryLanguage: String
        get() = primaryLanguage.orEmpty()

    val safeTechStacks: List<TechStack>
        get() = techStacks.orEmpty()
}

package vn.edu.uit.devorbit.mobile.data.repository

import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.domain.repository.RecentRepo

internal fun RepoSummary.toRecentRepo(): RecentRepo {
    return RecentRepo(
        id = id,
        name = displayName,
        description = safeDescription,
        githubUrl = safeGithubUrl,
        language = safePrimaryLanguage,
        stars = stars ?: 0,
        courseName = courseName,
        techStacks = safeTechStacks.map { it.name }.filter { it.isNotBlank() }
    )
}

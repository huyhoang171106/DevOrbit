package vn.edu.uit.devorbit.mobile.data.repository

import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.domain.repository.RecentRepo

internal fun RepoSummary.toRecentRepo(): RecentRepo {
    return RecentRepo(
        id = id,
        name = displayName,
        description = description,
        githubUrl = githubUrl,
        language = primaryLanguage,
        stars = stars ?: 0,
        courseName = courseName,
        techStacks = techStacks.map { it.name }.filter { it.isNotBlank() }
    )
}

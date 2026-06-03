package vn.edu.uit.devorbit.mobile.ui.screen.explore

import vn.edu.uit.devorbit.mobile.domain.repository.RecentRepo

data class ExploreFilterState(
    val query: String = "",
    val selectedTechStack: String? = null
) {
    val normalizedQuery: String?
        get() = query.trim().takeIf { it.isNotBlank() }

    fun updateQuery(value: String): ExploreFilterState = copy(query = value)

    fun selectTechStack(stack: String?): ExploreFilterState {
        return copy(selectedTechStack = if (stack == selectedTechStack) null else stack)
    }

    fun filterRepos(repos: List<RecentRepo>): List<RecentRepo> {
        return selectedTechStack?.let { selected ->
            repos.filter { repo -> repo.techStacks.any { it.equals(selected, ignoreCase = true) } }
        } ?: repos
    }
}

package vn.edu.uit.devorbit.mobile.ui

import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary

data class RepoFilterState(
    val repos: List<RepoSummary>,
    val selectedTechStack: String? = null
) {
    val availableTechStacks: List<String> = repos
        .flatMap { repo -> repo.techStacks.map { it.name } }
        .filter { it.isNotBlank() }
        .distinct()
        .sorted()

    val filteredRepos: List<RepoSummary> =
        selectedTechStack?.let { selected ->
            repos.filter { repo -> repo.techStacks.any { it.name == selected } }
        } ?: repos

    fun selectTechStack(stack: String?): RepoFilterState {
        return copy(selectedTechStack = if (stack == selectedTechStack) null else stack)
    }
}

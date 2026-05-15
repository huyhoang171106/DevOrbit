package vn.edu.uit.devorbit.mobile.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.data.remote.dto.TechStack

class RepoFilterStateTest {

    @Test
    fun `availableTechStacks returns unique sorted stack names`() {
        val state = RepoFilterState(
            repos = listOf(
                repo("Mobile Lab", "Kotlin", "Compose"),
                repo("Api Lab", "Spring Boot", "Kotlin"),
                repo("Empty Lab")
            )
        )

        assertEquals(listOf("Compose", "Kotlin", "Spring Boot"), state.availableTechStacks)
    }

    @Test
    fun `filteredRepos returns only repos containing selected stack`() {
        val state = RepoFilterState(
            repos = listOf(
                repo("Mobile Lab", "Kotlin", "Compose"),
                repo("Api Lab", "Spring Boot"),
                repo("Web Lab", "React")
            ),
            selectedTechStack = "Kotlin"
        )

        assertEquals(listOf("Mobile Lab"), state.filteredRepos.map { it.displayName })
    }

    @Test
    fun `selectTechStack toggles selected stack off when tapped twice`() {
        val state = RepoFilterState(
            repos = listOf(repo("Mobile Lab", "Kotlin")),
            selectedTechStack = "Kotlin"
        )

        assertNull(state.selectTechStack("Kotlin").selectedTechStack)
    }

    private fun repo(name: String, vararg stacks: String): RepoSummary {
        return RepoSummary(
            id = name.hashCode().toLong(),
            displayName = name,
            description = "",
            githubUrl = "https://github.com/example/${name.lowercase().replace(" ", "-")}",
            primaryLanguage = "",
            stars = 0,
            techStacks = stacks.map(::TechStack)
        )
    }
}

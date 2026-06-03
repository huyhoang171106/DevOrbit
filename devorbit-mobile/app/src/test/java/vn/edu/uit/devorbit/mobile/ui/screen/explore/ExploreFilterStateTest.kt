package vn.edu.uit.devorbit.mobile.ui.screen.explore

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import vn.edu.uit.devorbit.mobile.domain.repository.RecentRepo

class ExploreFilterStateTest {

    @Test
    fun `normalizedQuery trims blank search text`() {
        assertNull(ExploreFilterState(query = " ").normalizedQuery)
        assertEquals("android", ExploreFilterState(query = " android ").normalizedQuery)
    }

    @Test
    fun `selectTechStack toggles selected stack off`() {
        val state = ExploreFilterState(selectedTechStack = "Kotlin")

        assertNull(state.selectTechStack("Kotlin").selectedTechStack)
    }

    @Test
    fun `filterRepos keeps repos matching selected tech stack`() {
        val repos = listOf(
            repo("Mobile", "Kotlin", "Compose"),
            repo("Backend", "Spring Boot"),
            repo("Web", "React")
        )

        val result = ExploreFilterState(selectedTechStack = "compose").filterRepos(repos)

        assertEquals(listOf("Mobile"), result.map { it.name })
    }

    private fun repo(name: String, vararg stacks: String): RecentRepo {
        return RecentRepo(
            id = name.hashCode().toLong(),
            name = name,
            description = "",
            githubUrl = "",
            language = "",
            stars = 0,
            courseName = null,
            techStacks = stacks.toList()
        )
    }
}

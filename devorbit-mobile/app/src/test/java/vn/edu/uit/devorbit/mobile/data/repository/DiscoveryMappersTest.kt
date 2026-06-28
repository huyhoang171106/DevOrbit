package vn.edu.uit.devorbit.mobile.data.repository

import org.junit.Assert.assertEquals
import org.junit.Test
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.data.remote.dto.TechStack

class DiscoveryMappersTest {

    @Test
    fun `RepoSummary maps to RecentRepo with tech stacks and course name`() {
        val repo = RepoSummary(
            id = 7,
            displayName = "Mobile Labs",
            description = "Compose examples",
            githubUrl = "https://github.com/uit/mobile-labs",
            primaryLanguage = "Kotlin",
            stars = 12,
            courseName = "Mobile Programming",
            techStacks = listOf(
                TechStack(name = "Kotlin"),
                TechStack(name = "Compose"),
                TechStack(name = "")
            )
        )

        val result = repo.toRecentRepo()

        assertEquals(7, result.id)
        assertEquals("Mobile Labs", result.name)
        assertEquals("Mobile Programming", result.courseName)
        assertEquals(12, result.stars)
        assertEquals(listOf("Kotlin", "Compose"), result.techStacks)
    }
}

package vn.edu.uit.devorbit.mobile.data.remote.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RepoSummaryTest {

    @Test
    fun `safe accessors tolerate missing optional repo metadata`() {
        val repo = RepoSummary(
            id = 1L,
            displayName = "demo",
            description = null,
            githubUrl = null,
            primaryLanguage = null,
            stars = null,
            techStacks = null
        )

        assertEquals("", repo.safeDescription)
        assertEquals("", repo.safeGithubUrl)
        assertEquals("", repo.safePrimaryLanguage)
        assertTrue(repo.safeTechStacks.isEmpty())
    }
}

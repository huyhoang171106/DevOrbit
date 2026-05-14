package vn.edu.uit.devorbit.mobile.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.*
import org.junit.Test

class DeserializationTest {

    private val gson = Gson()

    @Test
    fun techStack_deserialization_test() {
        val json = """{"name": "Kotlin"}"""
        val result = gson.fromJson(json, TechStack::class.java)
        assertEquals("Kotlin", result.name)
    }

    @Test
    fun `RepoSummary deserializes with tech stacks`() {
        val json = """
        {
            "id": 1,
            "displayName": "Test Repo",
            "description": "A test repository",
            "githubUrl": "https://github.com/test/repo",
            "primaryLanguage": "Kotlin",
            "stars": 42,
            "techStacks": [
                {"name": "Kotlin"},
                {"name": "Android"},
                {"name": "Compose"}
            ]
        }
        """.trimIndent()

        val result = gson.fromJson(json, RepoSummary::class.java)

        assertEquals(1L, result.id)
        assertEquals("Test Repo", result.displayName)
        assertEquals("Kotlin", result.primaryLanguage)
        assertEquals(42, result.stars)
        assertNotNull(result.techStacks)
        assertEquals(3, result.techStacks.size)
        assertEquals("Kotlin", result.techStacks[0].name)
        assertEquals("Android", result.techStacks[1].name)
        assertEquals("Compose", result.techStacks[2].name)
    }

    @Test
    fun `RepoSummary deserializes with empty techStacks list`() {
        val json = """
        {
            "id": 2,
            "displayName": "Empty Stacks",
            "description": "Repo with no tech stacks",
            "githubUrl": "https://github.com/test/empty",
            "primaryLanguage": "Java",
            "stars": 0,
            "techStacks": []
        }
        """.trimIndent()

        val result = gson.fromJson(json, RepoSummary::class.java)

        assertNotNull(result.techStacks)
        assertTrue(result.techStacks.isEmpty())
    }

    @Test
    fun `TechStack name handles special characters`() {
        val json = """{"name": "C#"}"""
        val result = gson.fromJson(json, TechStack::class.java)
        assertEquals("C#", result.name)
    }

    @Test
    fun `RepoSummary deserializes with nullable stars as null`() {
        val json = """
        {
            "id": 3,
            "displayName": "No Stars",
            "description": "Repo with null stars",
            "githubUrl": "https://github.com/test/nostars",
            "primaryLanguage": "Python",
            "techStacks": []
        }
        """.trimIndent()

        val result = gson.fromJson(json, RepoSummary::class.java)

        assertNull(result.stars)
        assertNotNull(result.techStacks)
        assertTrue(result.techStacks.isEmpty())
    }

    @Test
    fun `List of RepoSummary deserializes from array`() {
        val json = """
        [
            {
                "id": 1,
                "displayName": "Repo One",
                "description": "First repo",
                "githubUrl": "https://github.com/test/one",
                "primaryLanguage": "Java",
                "stars": 10,
                "techStacks": [{"name": "Java"}, {"name": "Spring Boot"}]
            },
            {
                "id": 2,
                "displayName": "Repo Two",
                "description": "Second repo",
                "githubUrl": "https://github.com/test/two",
                "primaryLanguage": "Kotlin",
                "stars": 5,
                "techStacks": [{"name": "Kotlin"}]
            }
        ]
        """.trimIndent()

        val type = object : TypeToken<List<RepoSummary>>() {}.type
        val results: List<RepoSummary> = gson.fromJson(json, type)

        assertEquals(2, results.size)
        assertEquals("Repo One", results[0].displayName)
        assertEquals("Repo Two", results[1].displayName)
        assertEquals(2, results[0].techStacks.size)
        assertEquals(1, results[1].techStacks.size)
    }

    @Test
    fun `Cache round-trip preserves RepoSummary data`() {
        val original = RepoSummary(
            id = 1,
            displayName = "Cache Test",
            description = "Testing cache round-trip",
            githubUrl = "https://github.com/test/cache",
            primaryLanguage = "Rust",
            stars = 99,
            techStacks = listOf(
                TechStack("Rust"),
                TechStack("WebAssembly")
            )
        )

        val json = gson.toJson(original)
        val type = object : TypeToken<RepoSummary>() {}.type
        val restored: RepoSummary = gson.fromJson(json, type)

        assertEquals(original.id, restored.id)
        assertEquals(original.displayName, restored.displayName)
        assertEquals(original.description, restored.description)
        assertEquals(original.githubUrl, restored.githubUrl)
        assertEquals(original.primaryLanguage, restored.primaryLanguage)
        assertEquals(original.stars, restored.stars)
        assertNotNull(restored.techStacks)
        assertEquals(original.techStacks.size, restored.techStacks.size)
        assertEquals(original.techStacks[0].name, restored.techStacks[0].name)
        assertEquals(original.techStacks[1].name, restored.techStacks[1].name)
    }

    @Test
    fun `List of TechStack cache round-trip`() {
        val original = listOf(
            TechStack("Java"),
            TechStack("Spring Boot"),
            TechStack("PostgreSQL")
        )

        val json = gson.toJson(original)
        val type = object : TypeToken<List<TechStack>>() {}.type
        val restored: List<TechStack> = gson.fromJson(json, type)

        assertEquals(original.size, restored.size)
        assertEquals("Java", restored[0].name)
        assertEquals("Spring Boot", restored[1].name)
        assertEquals("PostgreSQL", restored[2].name)
    }
}

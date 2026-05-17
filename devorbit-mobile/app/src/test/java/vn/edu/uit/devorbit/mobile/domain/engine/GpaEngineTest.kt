package vn.edu.uit.devorbit.mobile.domain.engine

import org.junit.Assert.*
import org.junit.Test
import vn.edu.uit.devorbit.mobile.domain.model.*

class GpaEngineTest {

    @Test
    fun `forecastGpa returns current GPA when subjects list is empty`() {
        val result = GpaEngine.forecastGpa(3.0, emptyList())
        assertEquals(3.0, result.currentGpa, 0.001)
        assertEquals(3.0, result.forecastGpa, 0.001)
        assertTrue(result.bySubject.isEmpty())
        assertTrue(result.recommendations.isNotEmpty())
    }

    @Test
    fun `forecastGpa returns current GPA when totalCredits is zero`() {
        val subjects = listOf(SubjectGpaImpact(1L, "Math", credits = 0))
        val result = GpaEngine.forecastGpa(3.5, subjects)
        assertEquals(3.5, result.forecastGpa, 0.001)
    }

    @Test
    fun `calculateWeight returns correct weight`() {
        assertEquals(0.5, GpaEngine.calculateWeight(3, 6), 0.001)
        assertEquals(1.0, GpaEngine.calculateWeight(5, 5), 0.001)
        assertEquals(0.0, GpaEngine.calculateWeight(0, 10), 0.001)
    }

    @Test
    fun `calculateWeight returns 0 when totalCredits is 0 or negative`() {
        assertEquals(0.0, GpaEngine.calculateWeight(3, 0), 0.001)
        assertEquals(0.0, GpaEngine.calculateWeight(3, -1), 0.001)
    }

    @Test
    fun `forecastGpa computes weighted drop and reduces forecast`() {
        val subjects = listOf(
            SubjectGpaImpact(1L, "Math", credits = 3, currentScore = 8.0),
            SubjectGpaImpact(2L, "Physics", credits = 2, currentScore = 7.0),
            SubjectGpaImpact(3L, "Chem", credits = 1, currentScore = 6.0)
        ) // total = 6 credits
        val result = GpaEngine.forecastGpa(3.0, subjects)
        // Math weight = 3/6 = 0.5, drop = 0.5 * 8.0 = 4.0
        // Physics weight = 2/6 = 0.333..., drop = 0.333 * 7.0 ≈ 2.333
        // Chem weight = 1/6 = 0.166..., drop = 0.166 * 6.0 ≈ 1.0
        // totalDrop ≈ 7.333, forecast = max(3.0 - 7.333, 0) = 0.0
        assertTrue(result.forecastGpa < 3.0)
        assertEquals(3, result.bySubject.size)
        // Math has weight 0.5 and potentialDrop 4.0 > 0.5 -> recommendation
        assertTrue(result.recommendations.isNotEmpty())
    }

    @Test
    fun `forecastGpa does not go below zero`() {
        val subjects = listOf(
            SubjectGpaImpact(1L, "Math", credits = 10, currentScore = 10.0)
        )
        val result = GpaEngine.forecastGpa(1.0, subjects)
        assertTrue(result.forecastGpa >= 0.0)
    }

    @Test
    fun `recommendations include heavy weight subjects with large potential drop`() {
        val subjects = listOf(
            SubjectGpaImpact(1L, "Heavy", credits = 9, currentScore = 5.0),
            SubjectGpaImpact(2L, "Light", credits = 1, currentScore = 1.0)
        ) // total = 10
        val result = GpaEngine.forecastGpa(2.5, subjects)
        // Heavy weight = 0.9, potentialDrop = 0.9 * 5.0 = 4.5 -> recommendation
        // Light weight = 0.1, potentialDrop = 0.1 * 1.0 = 0.1 -> no recommendation
        assertTrue(result.recommendations.any { it.contains("Heavy") })
        assertTrue(result.recommendations.none { it.contains("Light") })
    }

    @Test
    fun `uses currentScore when available and defaults to 0 when null`() {
        val subjects = listOf(
            SubjectGpaImpact(1L, "Scored", credits = 3, currentScore = 8.0),
            SubjectGpaImpact(2L, "Unscored", credits = 3, currentScore = null)
        )
        val result = GpaEngine.forecastGpa(3.0, subjects)
        // Weight each = 0.5
        // Scored drop = 0.5 * 8.0 = 4.0, Unscored drop = 0.5 * 0.0 = 0.0
        // totalDrop = 4.0, forecast = max(3.0 - 4.0, 0) = 0.0
        assertEquals(2, result.bySubject.size)
    }
}

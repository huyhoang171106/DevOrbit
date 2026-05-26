package vn.edu.uit.devorbit.mobile.domain.engine

import org.junit.Assert.*
import org.junit.Test
import vn.edu.uit.devorbit.mobile.domain.model.*

class RiskEngineTest {

    @Test
    fun `assessRisk returns LOW when consistency is high and no overdue`() {
        val subjects = listOf(1L to "Math")
        val result = RiskEngine.assessRisk(subjects, emptyMap(), mapOf(1L to 0.9), emptyMap())
        assertEquals(RiskLevel.LOW, result.subjectRisks[0].riskLevel)
        assertEquals(RiskLevel.LOW, result.overallRisk)
    }

    @Test
    fun `assessRisk returns CRITICAL when consistency is very low with overdue and weak prereqs`() {
        val subjects = listOf(1L to "AI")
        val result = RiskEngine.assessRisk(
            subjects,
            mapOf(1L to 3),
            mapOf(1L to 0.2),
            mapOf(1L to listOf("Math", "Statistics"))
        )
        assertEquals(RiskLevel.CRITICAL, result.subjectRisks[0].riskLevel)
    }

    @Test
    fun `assessRisk returns HIGH when consistency is below 0 dot 3`() {
        val subjects = listOf(1L to "Physics")
        val result = RiskEngine.assessRisk(subjects, emptyMap(), mapOf(1L to 0.25), emptyMap())
        assertEquals(RiskLevel.HIGH, result.subjectRisks[0].riskLevel)
    }

    @Test
    fun `assessRisk returns HIGH when consistency is between 0 dot 3 and 0 dot 5 with overdue`() {
        val subjects = listOf(1L to "Chem")
        val result = RiskEngine.assessRisk(subjects, mapOf(1L to 2), mapOf(1L to 0.4), emptyMap())
        assertEquals(RiskLevel.HIGH, result.subjectRisks[0].riskLevel)
    }

    @Test
    fun `assessRisk returns MEDIUM when consistency is between 0 dot 3 and 0 dot 5 without overdue`() {
        val subjects = listOf(1L to "Biology")
        val result = RiskEngine.assessRisk(subjects, emptyMap(), mapOf(1L to 0.5), emptyMap())
        assertEquals(RiskLevel.MEDIUM, result.subjectRisks[0].riskLevel)
    }

    @Test
    fun `assessRisk returns MEDIUM when overdue exists even with good consistency`() {
        val subjects = listOf(1L to "English")
        val result = RiskEngine.assessRisk(
            subjects, mapOf(1L to 1), mapOf(1L to 0.9), emptyMap()
        )
        assertEquals(RiskLevel.MEDIUM, result.subjectRisks[0].riskLevel)
    }

    @Test
    fun `assessRisk returns MEDIUM when weak prerequisites exist with moderate consistency`() {
        val subjects = listOf(1L to "OOP")
        val result = RiskEngine.assessRisk(
            subjects, emptyMap(), mapOf(1L to 0.6),
            mapOf(1L to listOf("C Programming"))
        )
        assertEquals(RiskLevel.MEDIUM, result.subjectRisks[0].riskLevel)
    }

    @Test
    fun `overallRisk is CRITICAL when any subject is CRITICAL`() {
        val subjects = listOf(1L to "Hard", 2L to "Easy")
        val result = RiskEngine.assessRisk(
            subjects, mapOf(1L to 5), mapOf(1L to 0.1, 2L to 0.9),
            mapOf(1L to listOf("A", "B"))
        )
        assertEquals(RiskLevel.CRITICAL, result.overallRisk)
    }

    @Test
    fun `overallRisk is CRITICAL when 2 or more subjects are HIGH`() {
        val subjects = listOf(1L to "A", 2L to "B")
        val result = RiskEngine.assessRisk(
            subjects, emptyMap(), mapOf(1L to 0.2, 2L to 0.1), emptyMap()
        )
        assertEquals(RiskLevel.CRITICAL, result.overallRisk)
    }

    @Test
    fun `overallRisk is HIGH when one subject is HIGH`() {
        val subjects = listOf(1L to "A", 2L to "B")
        val result = RiskEngine.assessRisk(
            subjects, emptyMap(), mapOf(1L to 0.2, 2L to 0.9), emptyMap()
        )
        assertEquals(RiskLevel.HIGH, result.overallRisk)
    }

    @Test
    fun `overallRisk is MEDIUM when 2 or more subjects are MEDIUM`() {
        val subjects = listOf(1L to "A", 2L to "B")
        val result = RiskEngine.assessRisk(
            subjects, mapOf(1L to 1, 2L to 1), mapOf(1L to 0.9, 2L to 0.9), emptyMap()
        )
        assertEquals(RiskLevel.MEDIUM, result.overallRisk)
    }

    @Test
    fun `riskFactors include CONSISTENCY_CRITICAL when consistency below 0 dot 3`() {
        val profile = RiskEngine.assessRisk(
            listOf(1L to "Math"), emptyMap(), mapOf(1L to 0.2), emptyMap()
        )
        assertTrue(profile.riskFactors.any { it.type == "CONSISTENCY_CRITICAL" })
    }

    @Test
    fun `riskFactors include CONSISTENCY_LOW when consistency between 0 dot 3 and 0 dot 5`() {
        val profile = RiskEngine.assessRisk(
            listOf(1L to "Math"), emptyMap(), mapOf(1L to 0.4), emptyMap()
        )
        assertTrue(profile.riskFactors.any { it.type == "CONSISTENCY_LOW" })
    }

    @Test
    fun `riskFactors include OVERDUE_TASKS when overdue exists`() {
        val profile = RiskEngine.assessRisk(
            listOf(1L to "Math"), mapOf(1L to 2), mapOf(1L to 0.9), emptyMap()
        )
        assertTrue(profile.riskFactors.any { it.type == "OVERDUE_TASKS" })
    }

    @Test
    fun `riskFactors include WEAK_PREREQUISITES when weak prereqs exist`() {
        val profile = RiskEngine.assessRisk(
            listOf(1L to "OOP"), emptyMap(), mapOf(1L to 0.9),
            mapOf(1L to listOf("C"))
        )
        assertTrue(profile.riskFactors.any { it.type == "WEAK_PREREQUISITES" })
    }

    @Test
    fun `generateRiskFactors works standalone`() {
        val profile = RiskProfile(
            subjectRisks = listOf(
                SubjectRisk(1L, "Math", RiskLevel.LOW, emptyList(), 0, 1.0)
            ),
            overallRisk = RiskLevel.LOW
        )
        val factors = RiskEngine.generateRiskFactors(profile)
        assertTrue(factors.isEmpty())
    }

    @Test
    fun `handles empty subject list`() {
        val result = RiskEngine.assessRisk(emptyList(), emptyMap(), emptyMap(), emptyMap())
        assertTrue(result.subjectRisks.isEmpty())
        assertEquals(RiskLevel.LOW, result.overallRisk)
    }

    @Test
    fun `uses default consistency of 1 dot 0 when not provided`() {
        val subjects = listOf(1L to "NoData")
        val result = RiskEngine.assessRisk(subjects, emptyMap(), emptyMap(), emptyMap())
        assertEquals(RiskLevel.LOW, result.subjectRisks[0].riskLevel)
        assertEquals(1.0, result.subjectRisks[0].consistency, 0.001)
    }
}

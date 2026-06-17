package vn.edu.uit.devorbit.mobile.domain.gpa

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GpaCalculatorTest {

    @Test
    fun `calculates weighted semester gpa on ten point scale`() {
        val result = GpaCalculator.semester(
            listOf(
                GpaCourseInput(name = "Nhap mon lap trinh", credits = "4", grade = "8.5"),
                GpaCourseInput(name = "Cau truc du lieu", credits = "3", grade = "7")
            )
        )

        assertTrue(result.valid)
        assertEquals(7, result.totalCredits)
        assertEquals(7.86, result.gpa, 0.001)
    }

    @Test
    fun `ignores invalid course rows`() {
        val result = GpaCalculator.semester(
            listOf(
                GpaCourseInput(name = "Valid", credits = "3", grade = "9"),
                GpaCourseInput(name = "No credits", credits = "0", grade = "10"),
                GpaCourseInput(name = "Bad grade", credits = "4", grade = "11")
            )
        )

        assertTrue(result.valid)
        assertEquals(3, result.totalCredits)
        assertEquals(9.0, result.gpa, 0.001)
        assertEquals(2, result.ignoredRows)
    }

    @Test
    fun `projects cumulative gpa after current semester`() {
        val result = GpaCalculator.projectCumulative(
            currentGpa = "7",
            completedCredits = "100",
            semesterGpa = 8.0,
            semesterCredits = 20
        )

        assertTrue(result.valid)
        assertEquals(7.17, result.projectedGpa, 0.001)
        assertEquals(120, result.totalCredits)
    }

    @Test
    fun `calculates required semester gpa for target cumulative gpa`() {
        val result = GpaCalculator.requiredSemesterGpa(
            currentGpa = "7",
            completedCredits = "100",
            targetGpa = "7.5",
            semesterCredits = 20
        )

        assertTrue(result.valid)
        assertFalse(result.infeasible)
        assertEquals(10.0, result.requiredGpa, 0.001)
    }

    @Test
    fun `marks target infeasible when required semester gpa is above ten`() {
        val result = GpaCalculator.requiredSemesterGpa(
            currentGpa = "5",
            completedCredits = "100",
            targetGpa = "8",
            semesterCredits = 20
        )

        assertTrue(result.valid)
        assertTrue(result.infeasible)
        assertEquals(23.0, result.requiredGpa, 0.001)
    }
}

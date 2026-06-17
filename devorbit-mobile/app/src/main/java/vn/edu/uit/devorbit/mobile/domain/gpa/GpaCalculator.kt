package vn.edu.uit.devorbit.mobile.domain.gpa

import kotlin.math.round

data class GpaCourseInput(
    val name: String,
    val credits: String,
    val grade: String
)

data class GpaSemesterResult(
    val valid: Boolean,
    val gpa: Double,
    val totalCredits: Int,
    val ignoredRows: Int
)

data class GpaCumulativeResult(
    val valid: Boolean,
    val projectedGpa: Double,
    val totalCredits: Int
)

data class GpaTargetResult(
    val valid: Boolean,
    val requiredGpa: Double,
    val infeasible: Boolean
)

object GpaCalculator {

    fun semester(courses: List<GpaCourseInput>): GpaSemesterResult {
        var totalCredits = 0
        var weighted = 0.0
        var ignoredRows = 0

        courses.forEach { course ->
            val credits = course.credits.toIntOrNull()
            val grade = course.grade.toDoubleOrNull()
            if (credits == null || credits <= 0 || grade == null || grade < 0.0 || grade > 10.0) {
                ignoredRows += 1
            } else {
                totalCredits += credits
                weighted += credits * grade
            }
        }

        if (totalCredits == 0) {
            return GpaSemesterResult(valid = false, gpa = 0.0, totalCredits = 0, ignoredRows = ignoredRows)
        }

        return GpaSemesterResult(
            valid = true,
            gpa = round2(weighted / totalCredits),
            totalCredits = totalCredits,
            ignoredRows = ignoredRows
        )
    }

    fun projectCumulative(
        currentGpa: String,
        completedCredits: String,
        semesterGpa: Double,
        semesterCredits: Int
    ): GpaCumulativeResult {
        val current = currentGpa.toDoubleOrNull()
        val completed = completedCredits.toIntOrNull()
        if (
            current == null || current < 0.0 || current > 10.0 ||
            completed == null || completed < 0 ||
            semesterGpa < 0.0 || semesterGpa > 10.0 ||
            semesterCredits <= 0
        ) {
            return GpaCumulativeResult(valid = false, projectedGpa = 0.0, totalCredits = 0)
        }

        val totalCredits = completed + semesterCredits
        val projected = ((current * completed) + (semesterGpa * semesterCredits)) / totalCredits
        return GpaCumulativeResult(valid = true, projectedGpa = round2(projected), totalCredits = totalCredits)
    }

    fun requiredSemesterGpa(
        currentGpa: String,
        completedCredits: String,
        targetGpa: String,
        semesterCredits: Int
    ): GpaTargetResult {
        val current = currentGpa.toDoubleOrNull()
        val completed = completedCredits.toIntOrNull()
        val target = targetGpa.toDoubleOrNull()
        if (
            current == null || current < 0.0 || current > 10.0 ||
            completed == null || completed < 0 ||
            target == null || target < 0.0 || target > 10.0 ||
            semesterCredits <= 0
        ) {
            return GpaTargetResult(valid = false, requiredGpa = 0.0, infeasible = false)
        }

        val totalCredits = completed + semesterCredits
        val required = ((target * totalCredits) - (current * completed)) / semesterCredits
        return GpaTargetResult(valid = true, requiredGpa = round2(required), infeasible = required > 10.0)
    }

    private fun round2(value: Double): Double = round(value * 100.0) / 100.0
}

package vn.edu.uit.devorbit.mobile.domain.engine

import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.domain.model.GraphNode

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
)

class SemesterValidationService {

    companion object {
        const val MAX_CREDITS_PER_SEMESTER = 24
        const val MIN_CREDITS_PER_SEMESTER = 14
    }

    fun validateAddCourse(
        semester: Int,
        courseCode: String,
        courses: List<CourseEntity>,
        semesterPlan: Map<Int, List<GraphNode>>,
        prerequisiteMap: Map<String, List<String>> = emptyMap()
    ): ValidationResult {
        val errors = mutableListOf<String>()

        val existingEntry = semesterPlan.entries
            .filter { it.key != semester }
            .firstOrNull { (_, nodes) -> nodes.any { it.code == courseCode } }
        if (existingEntry != null) {
            errors.add("Môn $courseCode đã tồn tại ở học kỳ ${existingEntry.key}")
            return ValidationResult(isValid = false, errors = errors)
        }

        val currentNodes = semesterPlan[semester].orEmpty()
        if (currentNodes.any { it.code == courseCode }) {
            errors.add("Môn $courseCode đã có trong học kỳ $semester")
            return ValidationResult(isValid = false, errors = errors)
        }

        val course = courses.find { it.maMH == courseCode }
        if (course == null) {
            errors.add("Không tìm thấy môn $courseCode")
            return ValidationResult(isValid = false, errors = errors)
        }

        val prereqs = prerequisiteMap[courseCode].orEmpty()
        if (prereqs.isNotEmpty()) {
            val plannedCodesBefore = semesterPlan.entries
                .filter { it.key < semester }
                .flatMap { it.value.map { n -> n.code } }
                .toSet()
            val missing = prereqs.filter { it !in plannedCodesBefore }
            if (missing.isNotEmpty()) {
                errors.add("Thiếu môn tiên quyết: ${missing.joinToString(", ")}")
                return ValidationResult(isValid = false, errors = errors)
            }
        }

        val currentCredits = currentNodes.sumOf { node ->
            courses.find { it.maMH == node.code }?.credits ?: 0
        }
        val newTotal = currentCredits + course.credits
        if (newTotal > MAX_CREDITS_PER_SEMESTER) {
            errors.add("Vượt quá $MAX_CREDITS_PER_SEMESTER tín chỉ (hiện tại: $currentCredits, thêm: ${course.credits})")
            return ValidationResult(isValid = false, errors = errors)
        }

        return ValidationResult(isValid = true)
    }

    fun validateRemoveCourse(
        semester: Int,
        courseCode: String,
        courses: List<CourseEntity>,
        semesterPlan: Map<Int, List<GraphNode>>
    ): ValidationResult {
        val warnings = mutableListOf<String>()

        val remainingNodes = semesterPlan[semester].orEmpty().filter { it.code != courseCode }
        val remainingCredits = remainingNodes.sumOf { node ->
            courses.find { it.maMH == node.code }?.credits ?: 0
        }
        if (remainingCredits < MIN_CREDITS_PER_SEMESTER && remainingNodes.isNotEmpty()) {
            warnings.add("Học kỳ $semester chỉ còn $remainingCredits/$MIN_CREDITS_PER_SEMESTER tín chỉ tối thiểu")
        }

        return ValidationResult(isValid = true, warnings = warnings)
    }

    fun getMissingPrerequisites(
        courseCode: String,
        semesterPlan: Map<Int, List<GraphNode>>,
        prerequisiteMap: Map<String, List<String>>
    ): List<String> {
        val prereqs = prerequisiteMap[courseCode].orEmpty()
        if (prereqs.isEmpty()) return emptyList()
        val plannedCodes = semesterPlan.values.flatten().map { it.code }.toSet()
        return prereqs.filter { it !in plannedCodes }
    }

    fun getSemesterCredits(
        semester: Int,
        semesterPlan: Map<Int, List<GraphNode>>,
        courses: List<CourseEntity>
    ): Int {
        return semesterPlan[semester].orEmpty().sumOf { node ->
            courses.find { it.maMH == node.code }?.credits ?: 0
        }
    }
}

package vn.edu.uit.devorbit.mobile.domain.engine

import vn.edu.uit.devorbit.mobile.domain.model.*
import java.util.UUID

object TaskBreakdownEngine {

    private val templates = mapOf(
        "study" to listOf(
            "Tìm hiểu lý thuyết nền tảng",
            "Làm bài tập cơ bản",
            "Làm bài tập nâng cao",
            "Làm đề thi thử",
            "Review và sửa lỗi sai",
        ),
        "review" to listOf(
            "Ôn nhanh lý thuyết",
            "Ghi nhớ công thức/key concepts",
            "Luyện bài tập theo chủ đề",
            "Làm flashcard",
            "Tự kiểm tra với đề mẫu",
        ),
        "practice" to listOf(
            "Khởi động với bài dễ",
            "Bài tập trung bình",
            "Bài tập khó",
            "Bài tập timed (giới hạn thời gian)",
        ),
        "project" to listOf(
            "Phân tích yêu cầu",
            "Thiết kế kiến trúc",
            "Implement core feature",
            "Testing và fix bug",
            "Viết báo cáo",
        ),
        "read" to listOf(
            "Đọc lướt để hiểu tổng quan",
            "Đọc kỹ và ghi chú",
            "Tóm tắt nội dung chính",
            "Áp dụng vào bài tập thực tế",
        ),
    )

    fun breakdown(goal: String, difficulty: String = "medium"): TaskBreakdown {
        val templateKey = detectTemplate(goal)
        val stepTitles = templates[templateKey] ?: templates["study"]!!
        val baseMinutes = when (difficulty) {
            "easy" -> 15
            "hard" -> 40
            else -> 25
        }
        val steps = stepTitles.mapIndexed { i, title ->
            val variance = (i % 3) * 5
            BreakdownStep(
                id = UUID.randomUUID().toString(),
                title = title,
                estimatedMinutes = baseMinutes + variance,
                difficulty = difficulty,
                isNextAction = i == 0
            )
        }
        return TaskBreakdown(
            goal = goal,
            steps = steps,
            totalEstimatedMinutes = steps.sumOf { it.estimatedMinutes }
        )
    }

    fun generateNextAction(breakdown: TaskBreakdown): BreakdownStep? {
        return breakdown.steps.firstOrNull { it.isNextAction }
    }

    fun markStepDone(breakdown: TaskBreakdown, stepId: String): TaskBreakdown {
        val updated = breakdown.steps.mapIndexed { i, step ->
            if (step.id == stepId) step.copy(isNextAction = false)
            else if (i > 0 && breakdown.steps[i - 1].id == stepId && step.isNextAction.not())
                step.copy(isNextAction = true)
            else step
        }
        return breakdown.copy(steps = updated)
    }

    private fun detectTemplate(goal: String): String {
        val lower = goal.lowercase()
        return when {
            lower.contains("project") || lower.contains("đồ án") || lower.contains("project") -> "project"
            lower.contains("review") || lower.contains("ôn") || lower.contains("revise") -> "review"
            lower.contains("practice") || lower.contains("luyện") || lower.contains("thực hành") -> "practice"
            lower.contains("read") || lower.contains("đọc") || lower.contains("document") -> "read"
            lower.contains("study") || lower.contains("học") || lower.startsWith("study ") -> "study"
            else -> "study"
        }
    }
}

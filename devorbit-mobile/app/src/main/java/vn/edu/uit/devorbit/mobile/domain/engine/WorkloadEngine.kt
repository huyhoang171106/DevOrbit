package vn.edu.uit.devorbit.mobile.domain.engine

import java.util.concurrent.TimeUnit
import vn.edu.uit.devorbit.mobile.domain.model.*

object WorkloadEngine {

    fun analyzeWorkload(
        tasks: List<LearningTask>,
        totalWeeks: Int = 16,
        semesterStartMs: Long = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(8 * 7L),
    ): WorkloadProfile {
        val averageMinutes = if (tasks.isEmpty()) 0.0
            else tasks.sumOf { it.estimatedMinutes }.toDouble() / totalWeeks
        val threshold = averageMinutes * 1.5

        val weeks = (0 until totalWeeks).map { weekIdx ->
            val weekStart = semesterStartMs + TimeUnit.DAYS.toMillis(weekIdx * 7L)
            val weekEnd = weekStart + TimeUnit.DAYS.toMillis(7L)
            val weekTasks = tasks.filter { task ->
                task.deadline?.let { d -> d in weekStart until weekEnd } ?: false
            }
            val totalHours = weekTasks.sumOf { it.estimatedMinutes }.toDouble() / 60.0
            WeekWorkload(
                weekNumber = weekIdx + 1,
                label = "Tuần ${weekIdx + 1}",
                totalHours = totalHours,
                taskCount = weekTasks.size,
            )
        }

        val peakWeeks = weeks.filter { it.totalHours > threshold }.map { it.weekNumber }
        val avgHours = weeks.sumOf { it.totalHours } / totalWeeks
        val status = when {
            avgHours > 40 -> WorkloadStatus.CRITICAL
            avgHours > 25 -> WorkloadStatus.HEAVY
            avgHours > 10 -> WorkloadStatus.BALANCED
            else -> WorkloadStatus.UNDERLOADED
        }

        return WorkloadProfile(weeks, peakWeeks, status)
    }

    fun detectPeakWeeks(profile: WorkloadProfile): List<Int> = profile.peakWeeks

    fun suggestBalancing(profile: WorkloadProfile, targetHoursPerWeek: Double): List<String> {
        if (profile.overallStatus != WorkloadStatus.HEAVY && profile.overallStatus != WorkloadStatus.CRITICAL)
            return emptyList()

        return profile.weeks.filter { it.totalHours > targetHoursPerWeek * 1.5 }.map { week ->
            "Tuần ${week.weekNumber}: ${String.format("%.1f", week.totalHours)}h — giảm còn ${String.format("%.1f", targetHoursPerWeek)}h bằng cách rải đều work cho các tuần lân cận"
        }
    }
}

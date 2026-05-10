package vn.edu.uit.devorbit.mobile.engine

import vn.edu.uit.devorbit.mobile.model.domain.*

object BurnoutEngine {

    fun analyzeBurnout(
        tasks: List<LearningTask>,
        nowMs: Long = System.currentTimeMillis()
    ): BurnoutStatus {
        if (tasks.isEmpty()) {
            return BurnoutStatus(
                riskLevel = BurnoutRisk.NONE,
                indicators = emptyList(),
                recommendation = "Chua co du lieu de phan tich burnout."
            )
        }

        val indicators = listOf(
            computeOverdueIndicator(tasks, nowMs),
            computeConsistencyIndicator(tasks, nowMs),
            computeLateNightIndicator(tasks, nowMs),
            computeWorkloadSpikeIndicator(tasks, nowMs)
        )

        val triggeredCount = indicators.count { it.triggered }
        val riskLevel = when (triggeredCount) {
            0 -> BurnoutRisk.NONE
            1 -> BurnoutRisk.LOW
            2 -> BurnoutRisk.MODERATE
            else -> BurnoutRisk.HIGH
        }

        val triggered = indicators.filter { it.triggered }
        val recommendation = buildRecommendation(riskLevel, triggered)

        return BurnoutStatus(
            riskLevel = riskLevel,
            indicators = indicators,
            recommendation = recommendation
        )
    }

    private fun computeOverdueIndicator(tasks: List<LearningTask>, nowMs: Long): BurnoutIndicator {
        val overdue = tasks.count { !it.completed && it.deadline != null && it.deadline < nowMs }
        val total = tasks.size
        val ratio = if (total == 0) 0.0 else overdue.toDouble() / total

        return BurnoutIndicator(
            name = "overdueRatio",
            value = ratio,
            threshold = 0.3,
            triggered = ratio > 0.3
        )
    }

    private fun computeConsistencyIndicator(tasks: List<LearningTask>, nowMs: Long): BurnoutIndicator {
        val sevenDaysMs = 7 * 24 * 60 * 60 * 1000L
        val sevenDaysAgo = nowMs - sevenDaysMs

        val dueLast7Days = tasks.filter {
            it.deadline != null && it.deadline in sevenDaysAgo..nowMs
        }
        val totalDue = dueLast7Days.size
        val completedOnTime = dueLast7Days.count { it.completed }
        val ratio = if (totalDue == 0) 1.0 else completedOnTime.toDouble() / totalDue

        return BurnoutIndicator(
            name = "consistencyScore",
            value = ratio,
            threshold = 0.4,
            triggered = ratio < 0.4
        )
    }

    private fun computeLateNightIndicator(tasks: List<LearningTask>, nowMs: Long): BurnoutIndicator {
        val sevenDaysMs = 7 * 24 * 60 * 60 * 1000L
        val sevenDaysAgo = nowMs - sevenDaysMs

        val recentCreated = tasks.filter { it.createdAt >= sevenDaysAgo }
        val totalRecent = recentCreated.size
        val lateNightCount = recentCreated.count { task ->
            val hour = java.time.Instant.ofEpochMilli(task.createdAt)
                .atZone(java.time.ZoneId.systemDefault())
                .hour
            hour >= 22
        }
        val ratio = if (totalRecent == 0) 0.0 else lateNightCount.toDouble() / totalRecent

        return BurnoutIndicator(
            name = "lateNightRatio",
            value = ratio,
            threshold = 0.5,
            triggered = ratio > 0.5
        )
    }

    private fun computeWorkloadSpikeIndicator(tasks: List<LearningTask>, nowMs: Long): BurnoutIndicator {
        val weekMs = 7 * 24 * 60 * 60 * 1000L

        val thisWeekTasks = tasks.count {
            it.deadline != null && it.deadline in (nowMs - weekMs)..nowMs
        }
        val lastWeekTasks = tasks.count {
            it.deadline != null && it.deadline in (nowMs - 2 * weekMs) until (nowMs - weekMs)
        }

        val ratio = when {
            lastWeekTasks == 0 && thisWeekTasks > 0 -> Double.POSITIVE_INFINITY
            lastWeekTasks == 0 -> 0.0
            else -> thisWeekTasks.toDouble() / lastWeekTasks
        }

        return BurnoutIndicator(
            name = "workloadSpike",
            value = if (ratio.isInfinite()) 3.0 else ratio,
            threshold = 2.0,
            triggered = ratio > 2.0
        )
    }

    private fun buildRecommendation(
        riskLevel: BurnoutRisk,
        triggeredIndicators: List<BurnoutIndicator>
    ): String {
        val base = when (riskLevel) {
            BurnoutRisk.NONE -> "Khong co dau hieu burnout. Tiep tuc duy tri nhi do hoc tap hien tai."
            BurnoutRisk.LOW -> "Phat hien dau hieu burnout nhe. Hay chu y nghi ngoi va sap xep lai lich hoc."
            BurnoutRisk.MODERATE -> "Nguy co burnout o muc trung binh. Can dieu chinh thoi gian bieu va danh thoi gian thu gian."
            BurnoutRisk.HIGH -> "Nguy co burnout cao! Can nghi ngoi ngay lap tuc, giam tai cong viec va uu tien suc khoe tinh than."
        }

        if (triggeredIndicators.isEmpty()) return base

        val details = triggeredIndicators.joinToString(". ") { ind ->
            when (ind.name) {
                "overdueRatio" -> "Ty le nhiem vu qua han: ${"%.0f".format(ind.value * 100)}%"
                "consistencyScore" -> "Do nhat quan thap: ${"%.0f".format(ind.value * 100)}%"
                "lateNightRatio" -> "Ty le hoc khuya: ${"%.0f".format(ind.value * 100)}%"
                "workloadSpike" -> "Khoi luong tang dot bien: gap ${"%.1f".format(ind.value)} lan"
                else -> ind.name
            }
        }

        return "$base\n$details"
    }
}

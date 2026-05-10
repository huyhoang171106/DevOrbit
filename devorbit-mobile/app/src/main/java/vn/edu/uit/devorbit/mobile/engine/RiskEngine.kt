package vn.edu.uit.devorbit.mobile.engine

import vn.edu.uit.devorbit.mobile.model.domain.RiskFactor
import vn.edu.uit.devorbit.mobile.model.domain.RiskLevel
import vn.edu.uit.devorbit.mobile.model.domain.RiskProfile
import vn.edu.uit.devorbit.mobile.model.domain.SubjectRisk

object RiskEngine {

    /**
     * Assess academic risk from raw subject data.
     *
     * @param subjects          Pairs of (subjectId, subjectName)
     * @param overdueTasks      Map subjectId -> number of overdue tasks
     * @param consistencyScores Map subjectId -> consistency ratio (0.0 – 1.0)
     * @param weakPrereqs       Map subjectId -> names of weak prerequisites
     */
    fun assessRisk(
        subjects: List<Pair<Long, String>>,
        overdueTasks: Map<Long, Int>,
        consistencyScores: Map<Long, Double>,
        weakPrereqs: Map<Long, List<String>>
    ): RiskProfile {
        val subjectRisks = subjects.map { (id, name) ->
            val overdue = overdueTasks[id] ?: 0
            val consistency = consistencyScores[id] ?: 1.0
            val weakPrereqList = weakPrereqs[id] ?: emptyList()
            SubjectRisk(
                subjectId = id,
                subjectName = name,
                riskLevel = subjectRiskLevel(consistency, overdue, weakPrereqList),
                weakPrerequisites = weakPrereqList,
                overdueTasks = overdue,
                consistency = consistency
            )
        }

        return RiskProfile(
            subjectRisks = subjectRisks,
            overallRisk = overallRiskLevel(subjectRisks),
            riskFactors = generateRiskFactors(subjectRisks)
        )
    }

    fun generateRiskFactors(profile: RiskProfile): List<RiskFactor> =
        generateRiskFactors(profile.subjectRisks)

    // ---- internal helpers ----

    private fun subjectRiskLevel(
        consistency: Double,
        overdue: Int,
        weakPrereqs: List<String>
    ): RiskLevel {
        val hasOverdue = overdue > 0
        val hasWeak = weakPrereqs.isNotEmpty()

        return when {
            // LOW: consistent & no overdue
            consistency > 0.8 && !hasOverdue -> RiskLevel.LOW

            // CRITICAL: multiple high-risk indicators
            consistency < 0.3 && hasOverdue && hasWeak -> RiskLevel.CRITICAL

            // HIGH: very inconsistent, OR moderately inconsistent with overdue
            consistency < 0.3 -> RiskLevel.HIGH
            consistency <= 0.5 && hasOverdue -> RiskLevel.HIGH

            // MEDIUM: borderline consistency, overdue, or weak prereqs
            consistency <= 0.5 -> RiskLevel.MEDIUM
            hasOverdue || hasWeak -> RiskLevel.MEDIUM

            else -> RiskLevel.LOW
        }
    }

    private fun overallRiskLevel(subjectRisks: List<SubjectRisk>): RiskLevel {
        val levels = subjectRisks.map { it.riskLevel }
        return when {
            levels.any { it == RiskLevel.CRITICAL } -> RiskLevel.CRITICAL
            levels.count { it == RiskLevel.HIGH } >= 2 -> RiskLevel.CRITICAL
            levels.any { it == RiskLevel.HIGH } -> RiskLevel.HIGH
            levels.count { it == RiskLevel.MEDIUM } >= 2 -> RiskLevel.MEDIUM
            levels.any { it == RiskLevel.MEDIUM } -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }
    }

    private fun generateRiskFactors(subjectRisks: List<SubjectRisk>): List<RiskFactor> {
        val factors = mutableListOf<RiskFactor>()

        subjectRisks.forEach { risk ->
            // Consistency factors
            when {
                risk.consistency < 0.3 -> factors.add(
                    RiskFactor(
                        type = "CONSISTENCY_CRITICAL",
                        description = "${risk.subjectName}: Thói quen học tập không ổn định (${(risk.consistency * 100).toInt()}%)",
                        severity = RiskLevel.HIGH
                    )
                )
                risk.consistency < 0.5 -> factors.add(
                    RiskFactor(
                        type = "CONSISTENCY_LOW",
                        description = "${risk.subjectName}: Tỷ lệ học đều đặn thấp (${(risk.consistency * 100).toInt()}%)",
                        severity = RiskLevel.MEDIUM
                    )
                )
            }

            // Overdue factors
            if (risk.overdueTasks > 0) {
                factors.add(
                    RiskFactor(
                        type = "OVERDUE_TASKS",
                        description = "${risk.subjectName}: ${risk.overdueTasks} nhiệm vụ quá hạn",
                        severity = when {
                            risk.overdueTasks >= 3 -> RiskLevel.HIGH
                            risk.overdueTasks >= 1 -> RiskLevel.MEDIUM
                            else -> RiskLevel.LOW
                        }
                    )
                )
            }

            // Weak prerequisite factors
            if (risk.weakPrerequisites.isNotEmpty()) {
                factors.add(
                    RiskFactor(
                        type = "WEAK_PREREQUISITES",
                        description = "${risk.subjectName}: Kiến thức nền yếu — ${risk.weakPrerequisites.joinToString(", ")}",
                        severity = if (risk.weakPrerequisites.size >= 2) RiskLevel.HIGH else RiskLevel.MEDIUM
                    )
                )
            }
        }

        return factors
    }
}

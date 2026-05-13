package vn.edu.uit.devorbit.mobile.engine

import vn.edu.uit.devorbit.mobile.model.domain.*

object RecommendationEngine {

    fun generate(
        tasks: List<LearningTask>,
        riskProfile: RiskProfile,
        workloadProfile: WorkloadProfile,
        burnoutStatus: BurnoutStatus? = null
    ): List<StudyRecommendation> {
        val recommendations = mutableListOf<StudyRecommendation>()

        // Rule 1: Weak prerequisites detected -> PREREQUISITE_REVIEW (priority 1)
        riskProfile.subjectRisks.forEach { subjectRisk ->
            if (subjectRisk.weakPrerequisites.isNotEmpty()) {
                recommendations.add(
                    StudyRecommendation(
                        id = "prereq_${subjectRisk.subjectId}",
                        title = "On lai kien thuc nen tang",
                        description = "Mon ${subjectRisk.subjectName} yeu cau kien thuc tu: " +
                                subjectRisk.weakPrerequisites.joinToString(", ") +
                                ". Hay danh thoi gian on lai cac khai niem co ban truoc khi hoc tiep.",
                        priority = 1,
                        category = RecommendationCategory.PREREQUISITE_REVIEW,
                        relatedSubjectId = subjectRisk.subjectId
                    )
                )
            }
        }

        // Rule 2: Risk HIGH/CRITICAL -> FOCUS_SUBJECT (priority 2)
        riskProfile.subjectRisks.forEach { subjectRisk ->
            if (subjectRisk.riskLevel == RiskLevel.HIGH || subjectRisk.riskLevel == RiskLevel.CRITICAL) {
                recommendations.add(
                    StudyRecommendation(
                        id = "focus_${subjectRisk.subjectId}",
                        title = "Tap trung vao mon co nguy co cao",
                        description = "Mon ${subjectRisk.subjectName} dang o muc rui ro ${subjectRisk.riskLevel}. " +
                                "Co ${subjectRisk.overdueTasks} nhiem vu qua han va do nhat quan chi " +
                                "${(subjectRisk.consistency * 100).toInt()}%. " +
                                "Hay uu tien giai quyet cac nhiem vu qua han truoc.",
                        priority = 2,
                        category = RecommendationCategory.FOCUS_SUBJECT,
                        relatedSubjectId = subjectRisk.subjectId
                    )
                )
            }
        }

        // Rule 3: Workload CRITICAL -> WORKLOAD_REDUCTION (priority 3)
        if (workloadProfile.overallStatus == WorkloadStatus.CRITICAL) {
            recommendations.add(
                StudyRecommendation(
                    id = "workload_reduction",
                    title = "Giam tai khoi luong cong viec",
                    description = "Khoi luong cong viec dang o muc CRITICAL. " +
                            "Hay xem xet loai bo hoac tri hoan cac nhiem vu khong quan trong.",
                    priority = 3,
                    category = RecommendationCategory.WORKLOAD_REDUCTION
                )
            )
        }

        // Rule 4: Burnout MODERATE+ -> BREAK_SUGGESTION (priority 3)
        burnoutStatus?.let { status ->
            if (status.riskLevel == BurnoutRisk.MODERATE || status.riskLevel == BurnoutRisk.HIGH) {
                recommendations.add(
                    StudyRecommendation(
                        id = "break_suggestion",
                        title = "Nghi ngoi va thu gian",
                        description = "Phat hien nguy co burnout ${status.riskLevel.name.lowercase()}. " +
                                "Hay danh thoi gian nghi ngoi, di dao hoac lam viec khac de tai tao nang luong.",
                        priority = 3,
                        category = RecommendationCategory.BREAK_SUGGESTION
                    )
                )
            }
        }

        // Rule 5: Consistency < 0.3 in any subject -> CONSISTENCY_IMPROVEMENT (priority 4)
        val hasLowConsistency = riskProfile.subjectRisks.any { it.consistency < 0.3 }
        if (hasLowConsistency) {
            recommendations.add(
                StudyRecommendation(
                    id = "consistency_improvement",
                    title = "Cai thien tinh nhat quan",
                    description = "Do nhat quan hoc tap duoi 30% o mot so mon. " +
                            "Hay thiet lap lich hoc co dinh moi ngay va cam ket thuc hien deu dan.",
                    priority = 4,
                    category = RecommendationCategory.CONSISTENCY_IMPROVEMENT
                )
            )
        }

        // Rule 6: Multiple deadlines same week -> TIME_MANAGEMENT (priority 5)
        val deadlineCounts = tasks
            .filter { it.deadline != null && !it.completed }
            .groupBy {
                java.time.Instant.ofEpochMilli(it.deadline!!)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                    .get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear())
            }
        val hasCrowdedWeek = deadlineCounts.any { it.value.size >= 3 }
        if (hasCrowdedWeek) {
            recommendations.add(
                StudyRecommendation(
                    id = "time_management",
                    title = "Quan ly thoi gian hieu qua",
                    description = "Co tuan co tu 3 deadline tro len. " +
                            "Hay lap ke hoach phan bo thoi gian hop ly giua cac mon de tranh don viec.",
                    priority = 5,
                    category = RecommendationCategory.TIME_MANAGEMENT
                )
            )
        }

        return recommendations
    }
}

package vn.edu.uit.devorbit.mobile.engine

import vn.edu.uit.devorbit.mobile.model.domain.*

object GpaEngine {

    fun forecastGpa(
        currentGpa: Double,
        subjects: List<SubjectGpaImpact>
    ): GpaImpact {
        val totalCredits = subjects.sumOf { it.credits }
        if (totalCredits == 0) {
            return GpaImpact(
                currentGpa = currentGpa,
                forecastGpa = currentGpa,
                bySubject = subjects,
                recommendations = listOf("Khong co mon hoc nao de phan tich.")
            )
        }

        val impacts = subjects.map { subj ->
            val weight = calculateWeight(subj.credits, totalCredits)
            val currentScore = subj.currentScore ?: 0.0
            val potentialDrop = weight * currentScore
            subj.copy(weight = weight, potentialDrop = potentialDrop)
        }

        val totalDrop = impacts.sumOf { it.potentialDrop }
        val forecastGpa = currentGpa - totalDrop

        val recommendations = mutableListOf<String>()
        impacts.forEach { subj ->
            if (subj.weight > 0.2 && subj.potentialDrop > 0.5) {
                val weightPct = "%.0f".format(subj.weight * 100)
                recommendations.add(
                    "Mon ${subj.subjectName} (${subj.credits} tin chi) co trong so $weightPct%" +
                            " va nguy co tut diem ${"%.2f".format(subj.potentialDrop)}. Can tap trung on tap."
                )
            }
        }

        return GpaImpact(
            currentGpa = currentGpa,
            forecastGpa = kotlin.math.max(forecastGpa, 0.0),
            bySubject = impacts,
            recommendations = recommendations
        )
    }

    fun calculateWeight(credits: Int, totalCredits: Int): Double {
        if (totalCredits <= 0) return 0.0
        return credits.toDouble() / totalCredits
    }
}

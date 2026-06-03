package vn.edu.uit.devorbit.mobile.domain.repository

import vn.edu.uit.devorbit.mobile.domain.model.StudyPlan

interface StudyPlanRepository {
    suspend fun generateRoadmap(learningGoals: String, careerPath: String): StudyPlan
}

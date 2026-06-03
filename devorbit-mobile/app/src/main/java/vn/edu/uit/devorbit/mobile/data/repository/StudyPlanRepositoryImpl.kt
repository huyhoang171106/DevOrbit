package vn.edu.uit.devorbit.mobile.data.repository

import vn.edu.uit.devorbit.mobile.data.remote.dto.RoadmapGenerationRequest
import vn.edu.uit.devorbit.mobile.domain.model.StudyPlan
import vn.edu.uit.devorbit.mobile.domain.repository.StudyPlanRepository
import vn.edu.uit.devorbit.mobile.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyPlanRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : StudyPlanRepository {

    override suspend fun generateRoadmap(learningGoals: String, careerPath: String): StudyPlan {
        return apiService.generateRoadmap(
            RoadmapGenerationRequest(
                learningGoals = learningGoals,
                careerPath = careerPath
            )
        ).toStudyPlan(careerPath.ifBlank { "Lo trinh hoc tap" })
    }
}

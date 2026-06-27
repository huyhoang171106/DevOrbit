package vn.edu.uit.devorbit.mobile.data.remote.dto

data class AiResponse(
    val content: String,
    val type: String
)

data class SubjectQaRequest(
    val message: String,
    val sessionId: String? = null
)

data class SubjectQaResponse(
    val answer: String,
    val sessionId: String? = null,
    val relevantNodeIds: List<Long> = emptyList(),
    val sources: List<String> = emptyList(),
    val type: String? = null
)

data class RoadmapGenerationRequest(
    val learningGoals: String,
    val careerPath: String
)

data class RoadmapRecommendationResponse(
    val summary: String? = null,
    val recommendedCourses: List<CourseRecommendationResponse> = emptyList(),
    val graduationTracks: List<GraduationTrackResponse> = emptyList(),
    val electivePools: List<ElectivePoolCandidatesResponse> = emptyList()
)

data class CourseRecommendationResponse(
    val courseId: Long? = null,
    val courseCode: String? = null,
    val courseName: String? = null,
    val reasoning: String? = null,
    val description: String? = null,
    val isMandatory: Boolean = false,
    val semester: Int? = null,
    val credits: Int = 0
)

data class GraduationTrackResponse(
    val type: String? = null,
    val name: String? = null,
    val description: String? = null,
    val credits: Int = 0,
    val requirements: String? = null,
    val recommendation: String? = null,
    val recommended: Boolean = false,
    val courseCodes: List<String> = emptyList()
)

data class ElectivePoolCandidatesResponse(
    val poolId: String? = null,
    val poolName: String? = null,
    val targetTC: Int = 0,
    val currentTC: Int = 0,
    val candidates: List<ElectiveCandidateResponse> = emptyList()
)

data class ElectiveCandidateResponse(
    val courseId: Long? = null,
    val courseCode: String? = null,
    val courseName: String? = null,
    val credits: Int = 0,
    val score: Int = 0,
    val isSelected: Boolean = false,
    val description: String? = null,
    val reasoning: String? = null,
    val semester: Int? = null
)

package vn.edu.uit.devorbit.mobile.data.remote.dto

data class SubjectQaRequest(
    val message: String,
    val sessionId: String? = null
)

data class SubjectQaResponse(
    val answer: String,
    val sessionId: String? = null,
    val sources: List<SubjectQaSource> = emptyList()
)

data class SubjectQaSource(
    val title: String? = null,
    val url: String? = null,
    val excerpt: String? = null
)

data class RoadmapGenerationRequest(
    val learningGoals: String,
    val careerPath: String
)

data class RoadmapRecommendationResponse(
    val summary: String? = null,
    val phases: List<RoadmapPhaseResponse> = emptyList()
)

data class RoadmapPhaseResponse(
    val title: String,
    val description: String? = null,
    val items: List<RoadmapItemResponse> = emptyList()
)

data class RoadmapItemResponse(
    val title: String,
    val description: String? = null,
    val targetType: String? = null,
    val targetId: Long? = null
)

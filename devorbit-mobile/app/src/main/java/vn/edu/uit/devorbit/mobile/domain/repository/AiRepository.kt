package vn.edu.uit.devorbit.mobile.domain.repository

data class RepoSummaryInfo(val summary: String, val keyTopics: List<String>)
data class RepoAdvice(val advice: String, val difficulty: String, val prerequisites: List<String>)
data class AiQueryResult(val answer: String, val relatedCourseIds: List<Long>)

interface AiRepository {
    suspend fun getRepoSummary(repoId: Long): Result<RepoSummaryInfo>
    suspend fun getRepoAdvice(repoId: Long): Result<RepoAdvice>
    suspend fun queryKnowledgeGraph(query: String): Result<AiQueryResult>
}

package vn.edu.uit.devorbit.mobile.data.repository

import vn.edu.uit.devorbit.mobile.domain.repository.AiRepository
import vn.edu.uit.devorbit.mobile.domain.repository.RepoSummaryInfo
import vn.edu.uit.devorbit.mobile.domain.repository.RepoAdvice
import vn.edu.uit.devorbit.mobile.domain.repository.AiQueryResult
import vn.edu.uit.devorbit.mobile.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AiRepository {

    override suspend fun getRepoSummary(repoId: Long): Result<RepoSummaryInfo> = runCatching {
        val res = apiService.getRepoSummary(repoId)
        RepoSummaryInfo(
            summary = res["summary"] as? String ?: "",
            keyTopics = (res["keyTopics"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        )
    }

    override suspend fun getRepoAdvice(repoId: Long): Result<RepoAdvice> = runCatching {
        val res = apiService.getRepoAdvice(repoId)
        RepoAdvice(
            advice = res["advice"] as? String ?: "",
            difficulty = res["difficulty"] as? String ?: "",
            prerequisites = (res["prerequisites"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        )
    }

    override suspend fun queryKnowledgeGraph(query: String): Result<AiQueryResult> = runCatching {
        val res = apiService.queryKnowledgeGraph(mapOf("query" to query))
        AiQueryResult(
            answer = res["answer"] as? String ?: "",
            relatedCourseIds = (res["relatedCourses"] as? List<*>)?.filterIsInstance<Number>()?.map { it.toLong() } ?: emptyList()
        )
    }
}

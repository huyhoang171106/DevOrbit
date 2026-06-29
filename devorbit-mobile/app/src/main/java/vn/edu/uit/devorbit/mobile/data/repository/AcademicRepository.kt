package vn.edu.uit.devorbit.mobile.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import vn.edu.uit.devorbit.mobile.data.local.dao.CourseDao
import vn.edu.uit.devorbit.mobile.data.local.dao.RelationshipDao
import vn.edu.uit.devorbit.mobile.data.local.dao.RepoDao
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.CourseRelationshipEntity
import vn.edu.uit.devorbit.mobile.data.local.entity.RepoEntity
import vn.edu.uit.devorbit.mobile.data.remote.dto.GraphNodeDto
import vn.edu.uit.devorbit.mobile.data.remote.dto.GraphLinkDto
import vn.edu.uit.devorbit.mobile.data.remote.dto.AiResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSocialInfoResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.data.remote.dto.ReviewRequest
import vn.edu.uit.devorbit.mobile.data.remote.dto.ReviewResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.ReviewSummaryResponse
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoVoteRequest
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoVoteResponse
import vn.edu.uit.devorbit.mobile.network.ApiService
import javax.inject.Inject

data class CourseDetailData(
    val repos: List<RepoSummary>
)

class AcademicRepository @Inject constructor(
    private val apiService: ApiService,
    private val courseDao: CourseDao,
    private val repoDao: RepoDao,
    private val relationshipDao: RelationshipDao
) {
    val allCourses: Flow<List<CourseEntity>> = courseDao.getAllCourses()
    val allRelationships: Flow<List<CourseRelationshipEntity>> = relationshipDao.getAllRelationships()

    fun getReposByCourse(courseId: Long): Flow<List<RepoEntity>> = 
        repoDao.getReposByCourse(courseId)

    fun getRecentRepos(): Flow<List<RepoEntity>> = 
        repoDao.getRecentRepos()

    suspend fun refreshCourses(
        query: String? = null,
        subjectType: String? = null,
        semester: Int? = null,
        managementUnit: String? = null
    ) {
        try {
            val courses = apiService.getCourses(query, subjectType, semester, managementUnit)
            val entities = courses.map {
                CourseEntity(
                    id = it.id,
                    maMH = it.code,
                    tenMH = it.name,
                    credits = it.credits,
                    description = it.description.orEmpty(),
                    semester = it.semester,
                    loaiMonHoc = it.loaiMonHoc,
                    repoCount = it.repoCount
                )
            }
            courseDao.deleteAll()
            courseDao.upsertCourses(entities)
        } catch (e: Exception) {
            android.util.Log.e("AcademicRepository", "API error", e)
        }
    }

    suspend fun refreshRepos(courseId: Long) {
        try {
            val repos = apiService.getRepos(courseId)
            val entities = repos.map {
                RepoEntity(
                    id = it.id,
                    courseId = courseId,
                    displayName = it.displayName,
                    description = it.description,
                    githubUrl = it.githubUrl,
                    primaryLanguage = it.primaryLanguage,
                    stars = it.stars ?: 0,
                    aiClassification = null
                )
            }
            repoDao.upsertRepos(entities)
        } catch (e: Exception) {
            android.util.Log.e("AcademicRepository", "API error", e)
        }
    }

    suspend fun loadCourseDetail(courseId: Long): CourseDetailData {
        val repos = try {
            val remoteRepos = apiService.getRepos(courseId)
            val entities = remoteRepos.map {
                RepoEntity(
                    id = it.id,
                    courseId = courseId,
                    displayName = it.displayName,
                    description = it.description,
                    githubUrl = it.githubUrl,
                    primaryLanguage = it.primaryLanguage,
                    stars = it.stars ?: 0,
                    aiClassification = null
                )
            }
            repoDao.upsertRepos(entities)
            remoteRepos
        } catch (e: Exception) {
            repoDao.getReposByCourse(courseId).first().map {
                RepoSummary(
                    id = it.id,
                    displayName = it.displayName,
                    description = it.description.orEmpty(),
                    githubUrl = it.githubUrl,
                    primaryLanguage = it.primaryLanguage.orEmpty(),
                    stars = it.stars,
                    techStacks = emptyList()
                )
            }
        }

        return CourseDetailData(repos = repos)
    }

    suspend fun refreshRelationships() {
        try {
            val relationships = apiService.getRelationships()
            val entities = relationships.map {
                CourseRelationshipEntity(
                    id = it.id,
                    fromCourseId = it.courseId,
                    toCourseId = it.relatedCourseId,
                    type = it.relationType
                )
            }
            relationshipDao.upsertRelationships(entities)
        } catch (e: Exception) {
            android.util.Log.e("AcademicRepository", "API error", e)
        }
    }

    suspend fun getCourseGraph(major: String? = null): vn.edu.uit.devorbit.mobile.domain.model.KnowledgeGraph {
        val response = apiService.getKnowledgeGraph(major)
        val nodes = response.nodes.map { dto ->
            vn.edu.uit.devorbit.mobile.domain.model.GraphNode(
                id = dto.id, name = dto.name, code = dto.code,
                level = dto.level ?: 0,
                impactScore = dto.impactScore ?: 0.0,
                semester = dto.semester,
                description = dto.description
            )
        }
        val links = response.links.map { dto ->
            vn.edu.uit.devorbit.mobile.domain.model.GraphLink(
                sourceId = dto.source, targetId = dto.target, type = dto.type
            )
        }
        return vn.edu.uit.devorbit.mobile.domain.model.KnowledgeGraph(nodes, links)
    }

    suspend fun getRepo(repoId: Long): RepoSummary = apiService.getRepo(repoId)

    suspend fun getRepoSummary(repoId: Long): AiResponse = apiService.getRepoSummary(repoId)

    suspend fun getRepoAdvice(repoId: Long): AiResponse = apiService.getRepoAdvice(repoId)

    suspend fun getRepoSocialInfo(repoId: Long): RepoSocialInfoResponse = apiService.getRepoSocialInfo(repoId)

    suspend fun getCourseReviews(courseId: Long): ReviewSummaryResponse = apiService.getCourseReviews(courseId)

    suspend fun submitRepoReview(repoId: Long, rating: Int, comment: String?): ReviewResponse =
        apiService.submitRepoReview(repoId, ReviewRequest(rating, comment))

    suspend fun deleteRepoReview(repoId: Long) = apiService.deleteRepoReview(repoId)

    suspend fun voteRepo(repoId: Long, voteValue: Int): RepoVoteResponse =
        apiService.voteRepo(repoId, RepoVoteRequest(voteValue))
}

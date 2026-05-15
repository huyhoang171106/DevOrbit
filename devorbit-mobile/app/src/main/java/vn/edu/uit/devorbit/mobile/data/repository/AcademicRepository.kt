package vn.edu.uit.devorbit.mobile.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import vn.edu.uit.devorbit.mobile.data.local.dao.*
import vn.edu.uit.devorbit.mobile.data.local.entity.*
import vn.edu.uit.devorbit.mobile.data.remote.dto.GraphNodeDto
import vn.edu.uit.devorbit.mobile.data.remote.dto.GraphLinkDto
import vn.edu.uit.devorbit.mobile.data.remote.dto.CourseArticle
import vn.edu.uit.devorbit.mobile.data.remote.dto.CourseTutorial
import vn.edu.uit.devorbit.mobile.data.remote.dto.CourseYoutubePlaylist
import vn.edu.uit.devorbit.mobile.data.remote.dto.RepoSummary
import vn.edu.uit.devorbit.mobile.network.ApiService
import javax.inject.Inject

data class CourseDetailData(
    val repos: List<RepoSummary>,
    val tutorials: List<CourseTutorial>,
    val videos: List<CourseYoutubePlaylist>,
    val articles: List<CourseArticle>
)

class AcademicRepository @Inject constructor(
    private val apiService: ApiService,
    private val courseDao: CourseDao,
    private val repoDao: RepoDao,
    private val relationshipDao: RelationshipDao,
    private val taskDao: TaskDao
) {
    val allCourses: Flow<List<CourseEntity>> = courseDao.getAllCourses()
    val allRelationships: Flow<List<CourseRelationshipEntity>> = relationshipDao.getAllRelationships()
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val incompleteTasks: Flow<List<TaskEntity>> = taskDao.getIncompleteTasks()

    fun getReposByCourse(courseId: Long): Flow<List<RepoEntity>> = 
        repoDao.getReposByCourse(courseId)

    fun getRecentRepos(): Flow<List<RepoEntity>> = 
        repoDao.getRecentRepos()

    suspend fun refreshCourses() {
        try {
            val courses = apiService.getCourses()
            val entities = courses.map { 
                CourseEntity(
                    id = it.id,
                    maMH = it.code,
                    tenMH = it.name,
                    credits = 0,
                    description = ""
                )
            }
            courseDao.upsertCourses(entities)
        } catch (e: Exception) {
            e.printStackTrace()
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
            e.printStackTrace()
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

        return CourseDetailData(
            repos = repos,
            tutorials = runCatching { apiService.getTutorials(courseId) }.getOrDefault(emptyList()),
            videos = runCatching { apiService.getVideos(courseId) }.getOrDefault(emptyList()),
            articles = runCatching { apiService.getArticles(courseId) }.getOrDefault(emptyList())
        )
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
            e.printStackTrace()
        }
    }

    suspend fun getCourseGraph(): vn.edu.uit.devorbit.mobile.domain.model.KnowledgeGraph {
        val response = apiService.getKnowledgeGraph()
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

    suspend fun saveTask(task: TaskEntity) = taskDao.upsertTask(task)
    suspend fun completeTask(id: Long) = taskDao.setCompleted(id, true)
    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
}

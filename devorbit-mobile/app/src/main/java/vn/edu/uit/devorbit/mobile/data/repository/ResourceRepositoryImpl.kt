package vn.edu.uit.devorbit.mobile.data.repository

import vn.edu.uit.devorbit.mobile.domain.repository.ResourceRepository
import vn.edu.uit.devorbit.mobile.domain.repository.Tutorial
import vn.edu.uit.devorbit.mobile.domain.repository.VideoPlaylist
import vn.edu.uit.devorbit.mobile.domain.repository.Article
import vn.edu.uit.devorbit.mobile.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ResourceRepository {

    override suspend fun getTutorials(courseId: Long): List<Tutorial> = try {
        apiService.getTutorials(courseId).map {
            Tutorial(it.id, it.title, it.url, it.description)
        }
    } catch (e: Exception) { emptyList() }

    override suspend fun getVideos(courseId: Long): List<VideoPlaylist> = try {
        apiService.getVideos(courseId).map {
            VideoPlaylist(it.id, it.title, it.playlistUrl, it.thumbnail)
        }
    } catch (e: Exception) { emptyList() }

    override suspend fun getArticles(courseId: Long): List<Article> = try {
        apiService.getArticles(courseId).map {
            Article(it.id, it.title, it.url, it.author)
        }
    } catch (e: Exception) { emptyList() }
}

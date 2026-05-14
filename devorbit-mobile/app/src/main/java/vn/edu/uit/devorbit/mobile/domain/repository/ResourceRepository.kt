package vn.edu.uit.devorbit.mobile.domain.repository

data class Tutorial(val id: Long, val title: String, val url: String, val description: String?)
data class VideoPlaylist(val id: Long, val title: String, val url: String, val thumbnail: String?)
data class Article(val id: Long, val title: String, val url: String, val author: String?)

interface ResourceRepository {
    suspend fun getTutorials(courseId: Long): List<Tutorial>
    suspend fun getVideos(courseId: Long): List<VideoPlaylist>
    suspend fun getArticles(courseId: Long): List<Article>
}

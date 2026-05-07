package vn.edu.uit.devorbit.mobile.model

data class CourseTutorial(
    val id: Long,
    val title: String,
    val url: String,
    val description: String?
)

data class CourseYoutubePlaylist(
    val id: Long,
    val title: String,
    val playlistUrl: String,
    val thumbnail: String?
)

data class CourseArticle(
    val id: Long,
    val title: String,
    val url: String,
    val author: String?,
    val publishedDate: String?
)
